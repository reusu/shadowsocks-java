package name.teemo.sslocal.service;

import name.teemo.sslocal.mina.server.Socks5Server;
import name.teemo.sslocal.thread.ConnectCountAnnounceThread;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.hisunsray.commons.res.Config;

public class StartSsLocalService {
	private static Logger logger = Logger.getLogger(StartSsLocalService.class);
	private static String configFilePath = Class.class.getClass().getResource("/").getPath() + "global.properties";
	private static StartSsLocalService startSsLocalService;
	public static StartSsLocalService getInstance(){
		if(startSsLocalService == null){
			startSsLocalService = new StartSsLocalService();
		}
		return startSsLocalService;
	}
	private ApplicationContext context;
	public ApplicationContext getContext() {
		return context;
	}
	public void startListener() {
		try {
			logger.info("****************FsServer-Start****************");
			// 加载配置信息
			logger.info("****************LoadSpring-Start****************");
			context=new ClassPathXmlApplicationContext("applicationContext*.xml");
			logger.info("****************LoadSpring-Done****************");
			
			logger.info("****************LoadGlobal-Start****************");
			Config.setConfigResource(configFilePath);
			logger.info("****************LoadGlobal-Done****************");
			
			logger.info("****************S5Server-Start****************");
			if(!Socks5Server.getInstance().startListener()){
				logger.info("****************S5Server-Error****************");
				startSsLocalService.stopListener();
			}
			logger.info("****************CountAnnounce****************");
			new Thread(new ConnectCountAnnounceThread()).start();
			
		}catch (Exception e) {
			logger.error("****************FsServer-Error****************");
			logger.error(e.toString());
		}
	}
	public void stopListener() {
		logger.info("****************FsServer-Stop&Exit****************");
		System.exit(1);
	}
	public static void reloadConfig(){
		logger.info("****************ReLoadGlobal-Start****************");
		Config.setConfigResource(configFilePath);
		logger.info("****************ReLoadGlobal-Done****************");
	}
	public static void main(String[] args) throws Exception {
		StartSsLocalService.getInstance().startListener();
	}
}
