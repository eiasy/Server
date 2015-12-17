package com.framework.plugin;

/**
 * 
 * 应用启动插件接口
 * 
 * @author mengchao
 * 
 */
public interface IAppPlugin {

	/**
	 * 当服务器启动时，将调用该办法启动插件
	 */
	public void startup();

	/**
	 * 当服务器关闭时，将调用该办法关闭插件
	 */
	public void shutdown();

}
