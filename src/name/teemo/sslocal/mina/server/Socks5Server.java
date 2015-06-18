package name.teemo.sslocal.mina.server;

import java.net.InetSocketAddress;

import name.teemo.sslocal.mina.factory.BinaryCodecFactory;

import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import com.hisunsray.commons.res.Config;

public class Socks5Server {
	private static Logger log = Logger.getLogger(Socks5Server.class);
	private static Socks5Server socks5Server;
	public static Socks5Server getInstance(){
		if(socks5Server==null){
			socks5Server = new Socks5Server();
		}
		return socks5Server;
	}
	public boolean startListener() {
		try {
			IoAcceptor acceptor = new NioSocketAcceptor();
			acceptor.getFilterChain().addLast("protocolFilter",new ProtocolCodecFilter(new BinaryCodecFactory()));
			((NioSocketAcceptor)acceptor).setReuseAddress(true);
			acceptor.setHandler(new Socks5ServerHandle());        
			acceptor.bind(new InetSocketAddress(Config.getProperty("LOCAL"),Integer.parseInt(Config.getProperty("LOCAL_PORT"))));
			
			acceptor.getSessionConfig().setReaderIdleTime(30);
			acceptor.getSessionConfig().setWriterIdleTime(30);
			acceptor.getSessionConfig().setWriteTimeout(Integer.parseInt(Config.getProperty("TIMEOUT")));
			acceptor.getSessionConfig().setThroughputCalculationInterval(2);
			acceptor.getSessionConfig().setReadBufferSize(256);
			acceptor.getSessionConfig().setMaxReadBufferSize(2048);
			acceptor.getSessionConfig().setUseReadOperation(false);
			
			log.info("S5Server waiting for connections on  ["+Config.getProperty("LOCAL") + ":" +Config.getProperty("LOCAL_PORT")+"] ...");
			return true;
		} catch (Exception e) {
			log.error(e.toString());
			return false;
		} 
	}
	public void stopListener() {
	}
}
