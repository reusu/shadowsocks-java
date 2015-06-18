package name.teemo.sslocal.service;

import org.apache.mina.core.session.IoSession;

public interface SsSecretService {
	//mode: 0 decrypt | 1 encript
	public byte[] crypt(IoSession s5Session,int mode,byte[] bytes,String key)throws Exception;
}
