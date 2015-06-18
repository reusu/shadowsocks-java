package name.teemo.sslocal.service.impl;

import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.mina.core.session.IoSession;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.springframework.stereotype.Service;


import name.teemo.sslocal.entity.ShadowSocksKey;
import name.teemo.sslocal.mina.IoSeesionPool;
import name.teemo.sslocal.service.SsSecretService;
import name.teemo.sslocal.utils.BytesUtils;

@Service("aes256cfbCrypt")
public class AES256CFB implements SsSecretService{
	private static final int keyLength = 32;
	private static final int ivLength = 16;
	private ParametersWithIV enIV = null;
	private ParametersWithIV deIV = null;
	@Override
	public byte[] crypt(IoSession s5Session,int mode, byte[] bytes, String key) throws Exception {
		
		ShadowSocksKey ssKey = new ShadowSocksKey(key, keyLength);
		SecretKey secretKey = new SecretKeySpec(ssKey.getEncoded(), "AES");
		
		if(IoSeesionPool.getInstance().getPoolMap().get(s5Session).getEnCipher() == null){
			IoSeesionPool.getInstance().getPoolMap().get(s5Session).setEnCipher(new CFBBlockCipher(new AESFastEngine(), ivLength * 8));
		}
		if(IoSeesionPool.getInstance().getPoolMap().get(s5Session).getDeCipher() == null){
			IoSeesionPool.getInstance().getPoolMap().get(s5Session).setDeCipher(new CFBBlockCipher(new AESFastEngine(), ivLength * 8));
		}
		if(mode == 0){
			//de
			if(Arrays.equals(IoSeesionPool.getInstance().getPoolMap().get(s5Session).getDeCipher().getCurrentIV(),new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0})){
				byte[] decryptIV = new byte[ivLength];
				System.arraycopy(bytes, 0, decryptIV, 0, ivLength);
				deIV = new ParametersWithIV(new KeyParameter(secretKey.getEncoded()), decryptIV);
				IoSeesionPool.getInstance().getPoolMap().get(s5Session).getDeCipher().init(false, deIV);
				byte[] temp = new byte[bytes.length - ivLength];
	            System.arraycopy(bytes, ivLength, temp, 0, bytes.length - ivLength);
				byte[] buffer = new byte[temp.length];
				IoSeesionPool.getInstance().getPoolMap().get(s5Session).getDeCipher().processBytes(temp, 0, temp.length, buffer, 0);
				return buffer;
			}else{
				deIV = new ParametersWithIV(new KeyParameter(secretKey.getEncoded()), IoSeesionPool.getInstance().getPoolMap().get(s5Session).getDeCipher().getCurrentIV());
				byte[] buffer = new byte[bytes.length];
				IoSeesionPool.getInstance().getPoolMap().get(s5Session).getDeCipher().processBytes(bytes, 0, bytes.length, buffer, 0);
				return buffer;
			}
		}else/* if(mode == 1)*/{
			//en
			if(Arrays.equals(IoSeesionPool.getInstance().getPoolMap().get(s5Session).getEnCipher().getCurrentIV(),new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0})){
				byte[] encryptIV = new byte[ivLength];
				encryptIV = BytesUtils.randomBytes(ivLength);
				enIV = new ParametersWithIV(new KeyParameter(secretKey.getEncoded()), encryptIV);
				IoSeesionPool.getInstance().getPoolMap().get(s5Session).getEnCipher().init(true, enIV);
				byte[] buffer = new byte[bytes.length];
				IoSeesionPool.getInstance().getPoolMap().get(s5Session).getEnCipher().processBytes(bytes, 0, bytes.length, buffer, 0);
				return BytesUtils.byteMerger(encryptIV,buffer);
			}else{
				enIV = new ParametersWithIV(new KeyParameter(secretKey.getEncoded()), IoSeesionPool.getInstance().getPoolMap().get(s5Session).getEnCipher().getCurrentIV());
				byte[] buffer = new byte[bytes.length];
				IoSeesionPool.getInstance().getPoolMap().get(s5Session).getEnCipher().processBytes(bytes, 0, bytes.length, buffer, 0);
				return buffer;
			}
		}
	}
}
