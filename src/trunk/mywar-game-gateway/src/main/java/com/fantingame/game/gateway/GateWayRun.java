package com.fantingame.game.gateway;

import com.fandingame.game.framework.context.SpringLoad;
import com.fandingame.game.framework.log.LogSystem;
import com.fandingame.game.framework.plugin.AppPluginFactory;



public class GateWayRun {
		public static void main(String[] args) throws Exception {
			SpringLoad.getApplicationLoad();
			// 启动各个应用插件
			AppPluginFactory.startup();
			
			LogSystem.info("网关服务器启动成功");
		}
 
}
