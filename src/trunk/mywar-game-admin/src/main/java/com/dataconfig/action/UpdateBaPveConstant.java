package com.dataconfig.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dataconfig.bo.BaPveConstant;
import com.dataconfig.bo.BaPveConstantId;
import com.dataconfig.service.BaPveConstantService;
import com.dataconfig.service.MMapAreaService;
import com.dataconfig.service.MMonsterConstantService;
import com.dataconfig.service.TTreasureConstantService;
import com.framework.common.ALDAdminActionSupport;
import com.framework.servicemanager.ServiceCacheFactory;
import com.opensymphony.xwork2.ModelDriven;

public class UpdateBaPveConstant extends ALDAdminActionSupport implements ModelDriven<BaPveConstant> {

	private static final long serialVersionUID = -5623471239313351290L;
	
	private BaPveConstant baPveConstant = new BaPveConstant();
	
	private List<Map<String, Object>> monsterInfoList = new ArrayList<Map<String, Object>>();
	
	private List<Map<String, Integer>> monsterAppearTypeList = new ArrayList<Map<String, Integer>>();
	
	private List<Map<String, String>> rewardList = new ArrayList<Map<String, String>>();
	
	private List<Map<String, String>> vipRewardList = new ArrayList<Map<String, String>>();
	
	private List<Map<String, String>> dropTaskTreasureInfoList = new ArrayList<Map<String, String>>();
	
	private List<Map<String, String>> firstRewardList = new ArrayList<Map<String, String>>();
	
	private Map<Integer, String> treasureIDNameMap;
	
	private Map<Integer, String> mapAreaIdNameMap;
	
	private Integer pveBigId;
	
	private Integer pveSmallId;
	
	private String isCommit = "F";
	
	public String execute() {
		BaPveConstantService baPveConstantService = ServiceCacheFactory.getServiceCache().getService(BaPveConstantService.class);
		if ("F".equals(this.isCommit)) {
			TTreasureConstantService treasureConstantService = ServiceCacheFactory.getServiceCache().getService(TTreasureConstantService.class);
			treasureIDNameMap = treasureConstantService.findTreasureIdNameMap();
			MMapAreaService mapAreaService = ServiceCacheFactory.getServiceCache().getService(MMapAreaService.class);
			mapAreaIdNameMap = mapAreaService.findMapAreaIdNameMap();
			MMonsterConstantService monsterConstantService = ServiceCacheFactory.getServiceCache().getService(MMonsterConstantService.class);
			Map<Integer, String> monsterCategoryAndNameMap = monsterConstantService.findMonsterCategoryAndNameMap();
			
			baPveConstant = baPveConstantService.findOneBaPveConstant(new BaPveConstantId(pveBigId, pveSmallId));
			
			try {
//				System.out.println(baPveConstant.getId().getPveBigId()+", "+baPveConstant.getId().getPveSmallId()+" =================== ");
				monsterInfoList = baPveConstantService.parseMonsterInfo(baPveConstant, monsterCategoryAndNameMap);
				monsterAppearTypeList = baPveConstantService.parseMonsterAppearType(baPveConstant);
				rewardList = baPveConstantService.parseReward(baPveConstant, treasureIDNameMap);
				vipRewardList = baPveConstantService.parseVipReward(baPveConstant, treasureIDNameMap);
				dropTaskTreasureInfoList = baPveConstantService.parseDropTaskTreasureInfo(baPveConstant, treasureIDNameMap);
				firstRewardList = baPveConstantService.parseFirstReward(baPveConstant, treasureIDNameMap);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return INPUT;
		} else {
			baPveConstant.setDropTaskTreasureInfo("");
			baPveConstantService.updateOneBaPveConstant(baPveConstant);
			return SUCCESS;
		}
	}
	
	@Override
	public BaPveConstant getModel() {
		return baPveConstant;
	}

	public void setBaPveConstant(BaPveConstant baPveConstant) {
		this.baPveConstant = baPveConstant;
	}

	public BaPveConstant getBaPveConstant() {
		return baPveConstant;
	}

	public String getIsCommit() {
		return isCommit;
	}

	public void setIsCommit(String isCommit) {
		this.isCommit = isCommit;
	}

	public void setPveBigId(Integer pveBigId) {
		this.pveBigId = pveBigId;
	}

	public Integer getPveBigId() {
		return pveBigId;
	}

	public void setPveSmallId(Integer pveSmallId) {
		this.pveSmallId = pveSmallId;
	}

	public Integer getPveSmallId() {
		return pveSmallId;
	}

	public List<Map<String, Object>> getMonsterInfoList() {
		return monsterInfoList;
	}

	public void setMonsterInfoList(List<Map<String, Object>> monsterInfoList) {
		this.monsterInfoList = monsterInfoList;
	}

	public List<Map<String, Integer>> getMonsterAppearTypeList() {
		return monsterAppearTypeList;
	}

	public void setMonsterAppearTypeList(
			List<Map<String, Integer>> monsterAppearTypeList) {
		this.monsterAppearTypeList = monsterAppearTypeList;
	}

	public List<Map<String, String>> getRewardList() {
		return rewardList;
	}

	public void setRewardList(List<Map<String, String>> rewardList) {
		this.rewardList = rewardList;
	}

	public List<Map<String, String>> getVipRewardList() {
		return vipRewardList;
	}

	public void setVipRewardList(List<Map<String, String>> vipRewardList) {
		this.vipRewardList = vipRewardList;
	}

	public void setDropTaskTreasureInfoList(List<Map<String, String>> dropTaskTreasureInfoList) {
		this.dropTaskTreasureInfoList = dropTaskTreasureInfoList;
	}

	public List<Map<String, String>> getDropTaskTreasureInfoList() {
		return dropTaskTreasureInfoList;
	}

	public void setFirstRewardList(List<Map<String, String>> firstRewardList) {
		this.firstRewardList = firstRewardList;
	}

	public List<Map<String, String>> getFirstRewardList() {
		return firstRewardList;
	}

	public void setTreasureIDNameMap(Map<Integer, String> treasureIDNameMap) {
		this.treasureIDNameMap = treasureIDNameMap;
	}

	public Map<Integer, String> getTreasureIDNameMap() {
		return treasureIDNameMap;
	}

	public void setMapAreaIdNameMap(Map<Integer, String> mapAreaIdNameMap) {
		this.mapAreaIdNameMap = mapAreaIdNameMap;
	}

	public Map<Integer, String> getMapAreaIdNameMap() {
		return mapAreaIdNameMap;
	}
}
