package name.teemo.sslocal.mina.server;

import name.teemo.sslocal.biz.SsSecretBiz;
import name.teemo.sslocal.mina.IoSeesionPool;
import name.teemo.sslocal.mina.client.ShadowSocksClientThread;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import com.hisunsray.commons.res.Config;

public class Socks5ServerHandle extends IoHandlerAdapter{
	private static Logger log = Logger.getLogger(Socks5ServerHandle.class);
//	
//	private byte[] distAddr;
//	private byte[] distPort;
	@Override
	public void messageReceived(IoSession s5Session, Object message) throws Exception {
//		log.info("S5Received");
		if(bytesToHex((byte[])message).startsWith("050100")){
		
			if(((byte[])message).length == 3){
//				P1 S5Hand
//				Version identifier/method selection message:
//	            +----+----------+----------+
//	            |VER | NMETHODS | METHODS  |
//	            +----+----------+----------+
//	            | 1  |    1     | 1 to 255 |
//	            +----+----------+----------+
//				Will be ignored directly.
				
				if (((byte[])message)[0] != (byte)5) {
					System.out.println("Unknow protocol version.");
					s5Session.close(true);
				}else{
					s5Session.write(new byte[]{5, 0});
				}
				
				
			}else{
//				P2 S5Header
//				Request:
//		        +----+-----+-------+------+----------+----------+
//		        |VER | CMD |  RSV  | ATYP | DST.ADDR | DST.PORT |
//		        +----+-----+-------+------+----------+----------+
//		        | 1  |  1  | X'00' |  1   | Variable |    2     |
//		        +----+-----+-------+------+----------+----------+
//	          o  CMD
//	             o  CONNECT X'01'
//	          o  ATYP   address type of following address
//	             o  IP V4 address: X'01'
//	             o  DOMAINNAME: X'03'
//						The first octet of the address field contains 
//						the number of octets of name that follow,
//				   		there is no terminating NUL octet.
				
				byte[] req = new byte[4];
				// load VER, CMD, RSV ATYP
				for(int i=0;i<4;i++){
					req[i] = ((byte[])message)[i];
				}
				if (req[1] != 1) { 
					// Command not supported
					byte[] reply = {5, 7, 0, 1 ,0, 0, 0, 0, 1, 1};
					s5Session.write(reply); 
					s5Session.close(true);
				}
				byte addrType = req[3];
				byte[] distAddr = null;
				byte[] distPort = new byte[2];
				if (addrType == 1) { 
					// IP address
					distAddr = new byte[5];
					int i;
					for(i=1;i<5;i++){
						distAddr[i] = ((byte[])message)[4+i-1];
					}
					distPort[0] = ((byte[])message)[4+(i++)-1];
					distPort[1] = ((byte[])message)[4+i-1];
				} else if(addrType == 3) { 
					// Domain name
					int addrLen = ((byte[])message)[4];
					distAddr = new byte[addrLen + 2];
					distAddr[1] = (byte) addrLen;
					int i;
					for(i=2;i<2+addrLen;i++){
						distAddr[i] = ((byte[])message)[4+i-1];
					}
					distPort[0] = ((byte[])message)[4+(i++)-1];
					distPort[1] = ((byte[])message)[4+i-1];
				} else {
					// Address type not supported
					byte[] reply = {5, 8, 0, 1 ,0, 0, 0, 0, 1, 1};
					s5Session.write(reply); 
					s5Session.close(true);
				}
				
				distAddr[0] = addrType;
				
//				P3 SSlocal
//		        +----+-----+-------+------+----------+----------+
//		        |VER | REP |  RSV  | ATYP | BND.ADDR | BND.PORT |
//		        +----+-----+-------+------+----------+----------+
//		        | 1  |  1  | X'00' |  1   | Variable |    2     |
//		        +----+-----+-------+------+----------+----------+
//	          o  REP    Reply field:
//	              o  X'00' succeeded
//	              o  X'01' general SOCKS server failure
//	              o  X'02' connection not allowed by ruleset
//	              o  X'03' Network unreachable
//	              o  X'04' Host unreachable
//	              o  X'05' Connection refused
//	              o  X'06' TTL expired
//	              o  X'07' Command not supported
//	              o  X'08' Address type not supported
//	              o  X'09' to X'FF' unassigned
				
				
				new Thread(new ShadowSocksClientThread(s5Session, distAddr, distPort)).start();
				s5Session.write(new byte[]{5, 0, 0, 1 ,0, 0, 0, 0, 1, 1});
			}
			
		}else{
			//encode to remote
			if(IoSeesionPool.getInstance().getPoolMap().get(s5Session) == null){
				Thread.sleep(1000);
				if(IoSeesionPool.getInstance().getPoolMap().get(s5Session) == null){
					s5Session.close(true);
				}else{
					IoSession ssSession = IoSeesionPool.getInstance().getPoolMap().get(s5Session).getSsSession();
					ssSession.write(SsSecretBiz.getInstance().crypt(s5Session,Config.getProperty("METHOD").toLowerCase(), 1 , ((byte[])message), Config.getProperty("PASSWORD")));
				}
			}else{
				IoSession ssSession = IoSeesionPool.getInstance().getPoolMap().get(s5Session).getSsSession();
				ssSession.write(SsSecretBiz.getInstance().crypt(s5Session,Config.getProperty("METHOD").toLowerCase(), 1 , ((byte[])message), Config.getProperty("PASSWORD")));
			}
		}
	}
	public void messageSent(IoSession ioSession, Object message) throws Exception {
//		log.info("S5Sent");
	}
	@Override
	public void sessionClosed(IoSession s5Session) throws Exception {
		log.info("S5Close");
		if(IoSeesionPool.getInstance().getPoolMap().get(s5Session) == null){
			Thread.sleep(1000);
			if(IoSeesionPool.getInstance().getPoolMap().get(s5Session) == null){
				//Do nothing
			}else{
				IoSession ssSession = IoSeesionPool.getInstance().getPoolMap().remove(s5Session).getSsSession();
				if(ssSession.isConnected()){
					ssSession.close(true);
				}
			}
		}else{
			IoSession ssSession = IoSeesionPool.getInstance().getPoolMap().remove(s5Session).getSsSession();
			if(ssSession.isConnected()){
				ssSession.close(true);
			}
		}
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
	}

	@Override
	public void sessionOpened(IoSession s5Session) throws Exception {
	}
	@Override
	public void sessionIdle(IoSession s5Session, IdleStatus status){
		log.info("S5Idle " + status.toString());
		if(s5Session.isConnected()){
			s5Session.close(true);
		}
	}
	@Override
	public void exceptionCaught(IoSession s5Session, Throwable cause){
		log.info("S5Exceptione");
		if(s5Session.isConnected()){
			s5Session.close(true);
		}
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
