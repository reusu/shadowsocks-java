package name.teemo.sslocal.mina.client;

import name.teemo.sslocal.biz.SsSecretBiz;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.hisunsray.commons.res.Config;

public class ShadowSocksClientHandle extends IoHandlerAdapter{
	private static Logger logger = Logger.getLogger(ShadowSocksClientHandle.class);
	private IoSession s5Session;
	public ShadowSocksClientHandle(IoSession s5Session){
		this.s5Session = s5Session;
	}
	public void messageReceived(IoSession ssSession, Object message) throws Exception{
//		logger.info("SSReceived");
		if(s5Session.isConnected()){
			s5Session.write(SsSecretBiz.getInstance().crypt(s5Session,Config.getProperty("METHOD").toLowerCase(), 0 , ((byte[])message), Config.getProperty("PASSWORD")));
		}else{
			ssSession.close(true);
		}
	}
	
	public void messageSent(IoSession ioSession, Object message) throws Exception {
//		logger.info("SSSent");
	}
	@Override
	public void exceptionCaught(IoSession arg0, Throwable arg1)throws Exception {
//		logger.info("SSExceptione");
	}
	@Override
	public void inputClosed(IoSession arg0) throws Exception {
		
	}

	@Override
	public void sessionClosed(IoSession ssSession) throws Exception {
		logger.info("SSClosed");
		s5Session.close(true);
	}
	@Override
	public void sessionCreated(IoSession arg0) throws Exception {
	}
	@Override
	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
	}
	@Override
	public void sessionOpened(IoSession arg0) throws Exception {
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
}
