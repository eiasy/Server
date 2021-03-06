package com.stats.recollector;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.framework.servicemanager.CustomerContextHolder;
import com.framework.servicemanager.ServiceCacheFactory;
import com.framework.util.DateUtil;
import com.log.service.UserActionLogService;
import com.log.service.UserGoldLogService;
import com.stats.bo.UserLoginDrawStats;
import com.stats.service.UserLoginDrawService;

/**
 * 重新采集--大富翁
 * @author Administrator
 *
 */
public class ReUserLoginDrawStatsCollector {

	public void execute(String dateStr) {
		
		/*String[] dates = DateUtil.getOneDayStrArr(SystemStatsDate.YESTERDAY);*/
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dateTemp = null;
		try {
			dateTemp = sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String[] dates = DateUtil.getBeforeDateByDate(dateTemp, 0);
		
		UserActionLogService userActionLogService = ServiceCacheFactory.getServiceCache().getService(UserActionLogService.class);
		UserGoldLogService userGoldLogService = ServiceCacheFactory.getServiceCache().getService(UserGoldLogService.class);
		UserLoginDrawService userLoginDrawService = ServiceCacheFactory.getServiceCache().getService(UserLoginDrawService.class);
		List<Object> objList1 = userActionLogService.loginDrawActionLog(dates, 200048);
		List<Object> objList2 = userGoldLogService.countLoginDraw(dates);
		
		UserLoginDrawStats stats = new UserLoginDrawStats();
		stats.setSysNum(CustomerContextHolder.getSystemNum());
		/*stats.setTime(DateUtil.getSomeDaysDiffDate(SystemStatsDate.YESTERDAY));*/
		Date time = DateUtil.getSomeDaysDiffDateByDate(dateTemp, 0);
		stats.setTime(time);
		
		if (objList1 != null) {
			for (int i = 0; i < objList1.size(); i++) {
				Object[] obj = (Object[]) objList1.get(i);
				stats.setTotalPeopleCount(Integer.valueOf(obj[0].toString()));
				stats.setTotalCount(Integer.valueOf(obj[1].toString()));
			}
		}
		
		if (objList2 != null) {
			for (int i = 0; i < objList2.size(); i++) {
				Object[] obj = (Object[]) objList2.get(i);
				stats.setDiamondUsePeopleCount(Integer.valueOf(obj[0].toString()));
				stats.setDiamondUseCount(Integer.valueOf(obj[1].toString()));
			}
		}
		// 先删除
		userLoginDrawService.delete(dateStr);
		userLoginDrawService.save(stats);
	}
}
