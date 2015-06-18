package name.teemo.sslocal.mina;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import name.teemo.sslocal.entity.SessionPoolValueVo;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;


public class IoSeesionPool {
	private static Logger log = Logger.getLogger(IoSeesionPool.class);
	private static IoSeesionPool ioSeesionPool;
	private ConcurrentHashMap<IoSession,SessionPoolValueVo> poolMap;
	public static IoSeesionPool getInstance(){
		if(ioSeesionPool==null){
			load();
		}
		return ioSeesionPool;
	}
	private static void load(){
		ioSeesionPool = new IoSeesionPool();
	}
	private void initPoolMap(){
		poolMap = new ConcurrentHashMap<IoSession,SessionPoolValueVo>();
	}
	@SuppressWarnings("rawtypes")
	public void reInitPoolMap(){
		log.info("****************PoolMap-Reset****************");
		Iterator iterator = poolMap.entrySet().iterator();
		while(iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			//强行关闭所有的IOSeesion 然后初始化Pool
			IoSession s5Sssion = (IoSession) entry.getKey();
			s5Sssion.close(true);
		}
		poolMap = new ConcurrentHashMap<IoSession,SessionPoolValueVo>();
	}
	public ConcurrentHashMap<IoSession,SessionPoolValueVo> getPoolMap(){
		if(poolMap == null){
			log.info("****************PoolMap-Init****************");
			initPoolMap();
		}
		return poolMap;
	}
}
