package name.teemo.sslocal.entity;

import org.apache.mina.core.session.IoSession;
import org.bouncycastle.crypto.modes.CFBBlockCipher;

public class SessionPoolValueVo {
	private IoSession ssSession;
	private CFBBlockCipher enCipher;
	private CFBBlockCipher deCipher;
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
	
}
