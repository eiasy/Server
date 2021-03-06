package com.log.bo;

import java.sql.Timestamp;
import java.util.Date;

/**
 * UserHeroLog entity. @author MyEclipse Persistence Tools
 */

public class UserHeroLog implements java.io.Serializable {

	// Fields

	private Long userHeroLogId;
	private String userId;
	private String userHeroId;
	private Integer heroId;
	private Integer type;
	private Long exp;
	private Integer level;
	private Short pos;
	private Integer expNum;
	private Timestamp operationTime;
	private String heroName;
	private String userName;
	private int lodoId;

	// Constructors

	/** default constructor */
	public UserHeroLog() {
	}

	/** full constructor */
	public UserHeroLog(Long userHeroLogId,String userId, String userHeroId, Integer heroId,
			Integer type, Long exp, Integer level, Short pos, Integer expNum,
			Date operationTime, String heroName,String userName,int lodoId) {
		this.userHeroLogId = userHeroLogId;
		this.userId = userId;
		this.userHeroId = userHeroId;
		this.heroId = heroId;
		this.type = type;
		this.exp = exp;
		this.level = level;
		this.pos = pos;
		this.expNum = expNum;
		this.operationTime = new Timestamp(operationTime.getTime());
		this.heroName = heroName;
		this.userName = userName;
		this.lodoId = lodoId;
	}

	// Property accessors

	public Long getUserHeroLogId() {
		return this.userHeroLogId;
	}

	public void setUserHeroLogId(Long userHeroLogId) {
		this.userHeroLogId = userHeroLogId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserHeroId() {
		return this.userHeroId;
	}

	public void setUserHeroId(String userHeroId) {
		this.userHeroId = userHeroId;
	}

	public Integer getHeroId() {
		return this.heroId;
	}

	public void setHeroId(Integer heroId) {
		this.heroId = heroId;
	}

	public Integer getType() {
		return this.type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getExp() {
		return this.exp;
	}

	public void setExp(Long exp) {
		this.exp = exp;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}


	public int getLodoId() {
		return lodoId;
	}

	public void setLodoId(int lodoId) {
		this.lodoId = lodoId;
	}

	public Integer getLevel() {
		return this.level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Short getPos() {
		return this.pos;
	}

	public void setPos(Short pos) {
		this.pos = pos;
	}

	public Integer getExpNum() {
		return this.expNum;
	}

	public void setExpNum(Integer expNum) {
		this.expNum = expNum;
	}

	public Timestamp getOperationTime() {
		return this.operationTime;
	}

	public void setOperationTime(Timestamp operationTime) {
		this.operationTime = operationTime;
	}

	public String getHeroName() {
		return this.heroName;
	}

	public void setHeroName(String heroName) {
		this.heroName = heroName;
	}

}