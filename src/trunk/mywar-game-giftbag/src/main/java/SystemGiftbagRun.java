import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import com.fandingame.game.framework.log.LogSystem;

/**
 * 启动方法
 * 
 * @author yezp
 */
public class SystemGiftbagRun {
	public static void main(String[] args) throws Exception {
		Server server = buildNormalServer(9090, "/");
		server.start();
		LogSystem.info("启动成功");		
	}
	
	/**
	 * 创建用于正常运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
	 */
	public static Server buildNormalServer(int port, String contextPath) {
		Server server = new Server(port);
		WebAppContext webContext = new WebAppContext("src/main/webapp", contextPath);
		webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
		server.setHandler(webContext);
		server.setStopAtShutdown(true);
		return server;
	}
}
