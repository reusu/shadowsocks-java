package name.teemo.sslocal.entity;

import org.apache.mina.core.session.IoSession;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class SessionPoolValueVo {
	private IoSession ssSession;
	private CFBBlockCipher enCipher;
	private CFBBlockCipher deCipher;
	private ParametersWithIV deIV;
	private ParametersWithIV enIV;
	public IoSession getSsSession() {
		return ssSession;
	}
	public void setSsSession(IoSession ssSession) {
		this.ssSession = ssSession;
	}
	public CFBBlockCipher getEnCipher() {
		return enCipher;
	}
	public void setEnCipher(CFBBlockCipher enCipher) {
		this.enCipher = enCipher;
	}
	public CFBBlockCipher getDeCipher() {
		return deCipher;
	}
	public void setDeCipher(CFBBlockCipher deCipher) {
		this.deCipher = deCipher;
	}
	public ParametersWithIV getDeIV() {
		return deIV;
	}
	public void setDeIV(ParametersWithIV deIV) {
		this.deIV = deIV;
	}
	public ParametersWithIV getEnIV() {
		return enIV;
	}
	public void setEnIV(ParametersWithIV enIV) {
		this.enIV = enIV;
	}
	
}
