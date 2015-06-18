package name.teemo.sslocal.mina.client;


import java.net.InetSocketAddress;

import name.teemo.sslocal.biz.SsSecretBiz;
import name.teemo.sslocal.entity.SessionPoolValueVo;
import name.teemo.sslocal.mina.IoSeesionPool;
import name.teemo.sslocal.mina.factory.BinaryCodecFactory;
import name.teemo.sslocal.utils.BytesUtils;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.hisunsray.commons.res.Config;

public class ShadowSocksClientThread implements Runnable{
	private static Logger logger = Logger.getLogger(ShadowSocksClientThread.class);
	private IoSession s5Session;
	private byte[] distAddr;
	private byte[] distPort;
	public ShadowSocksClientThread(IoSession s5Session,byte[] distAddr,byte[] distPort){
		this.s5Session = s5Session;
		this.distAddr = distAddr;
		this.distPort = distPort;
	}
	@Override
	public void run() {
		IoConnector connector = new NioSocketConnector();
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		chain.addLast("codec", new ProtocolCodecFilter(new BinaryCodecFactory()));
		connector.setHandler(new ShadowSocksClientHandle(s5Session));
		IoSession ssSession = null;
		try{
			ConnectFuture future = connector.connect(new InetSocketAddress(Config.getProperty("SERVER"),Integer.parseInt(Config.getProperty("SERVER_PORT"))));
			future.awaitUninterruptibly();
			ssSession=future.getSession();
			//发送Init POOL
			SessionPoolValueVo spv = new SessionPoolValueVo();
			spv.setSsSession(ssSession);
			IoSeesionPool.getInstance().getPoolMap().put(s5Session, spv);
			//发送DistServerInf
			ssSession.write(SsSecretBiz.getInstance().crypt(s5Session,Config.getProperty("METHOD").toLowerCase(), 1 , BytesUtils.byteMerger(distAddr, distPort), Config.getProperty("PASSWORD")));
		}catch (Exception e) {
			logger.error("SS-SERVER连接失败!",e);
		}
	}
}
