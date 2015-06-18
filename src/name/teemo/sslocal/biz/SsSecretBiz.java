package name.teemo.sslocal.biz;

import java.util.Map;

import name.teemo.sslocal.entity.SsSecretChannelBean;
import name.teemo.sslocal.service.SsSecretService;
import name.teemo.sslocal.service.StartSsLocalService;

import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;

public class SsSecretBiz {
	private static Logger log = Logger.getLogger(SsSecretBiz.class);
	private static SsSecretBiz ssSecretBiz;
	private Map<String, SsSecretChannelBean> ssSecretServiceImplMap;
	public Map<String, SsSecretChannelBean> getSsSecretServiceImplMap() {
		return ssSecretServiceImplMap;
	}
	public void setSsSecretServiceImplMap(
			Map<String, SsSecretChannelBean> ssSecretServiceImplMap) {
		this.ssSecretServiceImplMap = ssSecretServiceImplMap;
	}
	public static SsSecretBiz getInstance() {
		if (ssSecretBiz == null || ssSecretBiz.getSsSecretServiceImplMap().size() == 0) {
			load();
		}
		return ssSecretBiz;
	}
	public static void load() {
		String xmlName = "applicationContext-biz.xml";
		String className = "ssSecretBiz";
		log.info("开始加载处理类配置信息");
		try {
			ssSecretBiz = (SsSecretBiz) StartSsLocalService.getInstance().getContext().getBean(className);
		} catch (Exception e) {
			log.error("加载处理类配置文件:" + xmlName + "|error:" + e.getMessage());
		}
	}
	public byte[] crypt(IoSession s5Session ,String method,int mode,byte[] bytes,String key){
		SsSecretChannelBean channel = new SsSecretChannelBean();
		String className = null;
		byte[] abytes = null;
		if (method != null && method.length() > 0) {
			channel = ssSecretServiceImplMap.get(method);
			SsSecretService ssSecretService;
			if (channel != null) {
				try {
					className = channel.getChannelImpl();
					ssSecretService = (SsSecretService) StartSsLocalService.getInstance().getContext().getBean(className);
					abytes = ssSecretService.crypt(s5Session,mode, bytes, key);
				} catch (Exception e) {
					log.error("执行类途中发生错误:" + className + "|输入参数:" + method + "|" + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		return abytes;
	}
}
