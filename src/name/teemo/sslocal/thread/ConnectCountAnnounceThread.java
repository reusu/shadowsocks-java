package name.teemo.sslocal.thread;

import name.teemo.sslocal.mina.IoSeesionPool;

import org.apache.log4j.Logger;


public class ConnectCountAnnounceThread implements Runnable{
		private static Logger log = Logger.getLogger(ConnectCountAnnounceThread.class);

		@Override
		public void run() {
			while(true){
				try{

					log.info("Connected Count : " + IoSeesionPool.getInstance().getPoolMap().size());
					
					
					
					
					Thread.sleep(10000);
					
					
					
					
					
					
					
					
					
					
				}catch (Exception e) {
					// TODO: handle exception
				}
				
				
				
				
			}
			
		}
}
