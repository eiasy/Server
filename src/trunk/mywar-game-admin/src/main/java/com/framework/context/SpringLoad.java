package com.framework.context;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.framework.servicemanager.IServiceCache;
import com.framework.servicemanager.ServiceCacheClassImpl;
import com.framework.servicemanager.ServiceCacheFactory;

/**
 * 
 * 应用加载类，主要负责应用spring的装载
 * 
 * @author mengchao
 * 
 */
public class SpringLoad {

	private static SpringLoad context;

	private static final String SYSTEM_CONFIG_NAME = "/applicationContext.xml";

	// spring上下文
	private ApplicationContext appContext;

	// 私有构造函数, 禁止实例化, 通过getContext()工厂方法获取单例
	private SpringLoad() {
		initApplication();
	}

	public static SpringLoad getApplicationLoad() {
		if (context == null) {
			context = new SpringLoad();
		}
		return context;
	}

	/**
	 * 
	 * 初始化应用
	 */
	private void initApplication() {

		initBean();

		initServiceFactory();
	}

	/**
	 * 初始化bean
	 */
	private void initBean() {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

		List<String> beanConfigs = new ArrayList<String>();

		// 获取applicationContext.xml文件
		String applicationContextPath = SYSTEM_CONFIG_NAME;
		beanConfigs.add(applicationContextPath);

		List<String> configPathList = new ArrayList<String>();
		configPathList.add("classpath*:com/**/bean_config/beans.xml");
//		configPathList.add("classpath*:com/**/bean_config/beans.xml");
//		configPathList
//				.add("classpath*:com/**/bean_config/beans.xml");
//		configPathList
//				.add("classpath*:com/**/bean_config/beans.xml");

		for (String configPath : configPathList) {
			try {
				Resource[] resources = resolver.getResources(configPath);
				for (Resource resource : resources) {
					try {
						String file = resource.getURL().getPath();
						String config = file
								.substring(file.lastIndexOf("com/"));
						beanConfigs.add(config);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 加载bean
		String[] beans = beanConfigs.toArray(new String[beanConfigs.size()]);
		appContext = new ClassPathXmlApplicationContext(beans);
	}

	/**
	 * 初始化serviceFactory类
	 */
	public void initServiceFactory() {
		IServiceCache serviceCache = new ServiceCacheClassImpl(appContext);
		new ServiceCacheFactory(serviceCache);
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}

}
