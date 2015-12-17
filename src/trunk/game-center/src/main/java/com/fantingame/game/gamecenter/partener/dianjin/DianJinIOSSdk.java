package com.fantingame.game.gamecenter.partener.dianjin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import com.alibaba.fastjson.JSONObject;
import com.fantingame.game.gamecenter.partener.BaseSdk;

public class DianJinIOSSdk extends BaseSdk {
	
	private static final Logger logger = Logger.getLogger(DianJinIOSSdk.class);
	
	private static DianJinIOSSdk ins;

	private static Properties prop;

	private final static String PROTOCOL_HEAD = "http://";
	private String partnerId;
	private String appKey;
	private String appId;
	private String verifyUrl;

	public static DianJinIOSSdk instance() {
		synchronized (DianJinIOSSdk.class) {
			if (ins == null) {
				ins = new DianJinIOSSdk();
			}
		}

		return ins;
	}

	private DianJinIOSSdk() {
		loadSdkProperties();
	}

	public void reload(){
		loadSdkProperties();
	}
	
	private void loadSdkProperties() {
		try {
			prop = PropertiesLoaderUtils.loadProperties(new ClassPathResource("sdk.properties"));
			appKey = prop.getProperty("DianJinIOSSdk.appKey");
			appId = prop.getProperty("DianJinIOSSdk.appId");
			partnerId = prop.getProperty("DianJinIOSSdk.partnerId");
			verifyUrl = prop.getProperty("DianJinIOSSdk.verifyUrl");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public boolean verifySession(String token,String uid){
		try {
			if(checkUserLogin(uid,token)==1){
				return true;
			}
		} catch (Exception e) {
			logger.error("校验出现异常",e);
		}
		return false;
	}
	

	public boolean checkPayCallbackSign(DianJinPaymentObj data) {
		try {
//			String sign = MD5.MD5Encode("App_Id="+getAppId()+"&Create_Time="+data.getCreateTime()+
//					"&Extra="+data.getExtra()+"&Pay_Status="+data.getPayStatus()+
//					"&Recharge_Gold_Count="+data.getRechargeGoldCount()+
//					"&Recharge_Money="+data.getRechargeMoney()+
//					"&Uin="+data.getUin()+"&Urecharge_Id="+data.getUrechargeId()+getAppKey());
			int result = this.payResultNotify(data.getAppId(), data.getAct(), data.getProductName(), data.getConsumeStreamId(), data.getCooOrderSerial(), data.getUin(), data.getGoodsId(), data.getGoodsInfo(), data.getGoodsCount(), data.getOriginalMoney(), data.getOrderMoney(), data.getNote(), data.getPayStatus(), data.getCreateTime(), data.getSign());
			if(result==1){
				return true;
			}else{
				logger.error("点金ios MD5校验失败！错误码"+result);
				return false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
	/**
	 * 查询支付购买结果的API调用
	 * @param cooOrderSerial 商户订单号
	 * @return ERRORCODE的值
	 * @throws Exception API调用失败
	 */
	public int queryPayResult(String cooOrderSerial)  throws Exception{
		String act = "1";
		StringBuilder strSign = new StringBuilder();
		strSign.append(appId);
		strSign.append(act);
		strSign.append(cooOrderSerial);
		strSign.append(appKey);
		String sign = md5(strSign.toString());
		StringBuilder getUrl = new StringBuilder();
		getUrl.append("Appid=");
		getUrl.append(appId);
		getUrl.append("&Act=");
		getUrl.append(act);
		getUrl.append("&CooOrderSerial=");
		getUrl.append(cooOrderSerial);
		getUrl.append("&Sign=");
		getUrl.append(sign);
		return GetResult(HttpGetGo(getUrl.toString()));
	}
	
	/**
	 * 检查用户登陆SESSIONID是否有效
	 * @param uin 91账号ID
	 * @param sessionID
	 * @return
	 * @throws Exception
	 */
	public int checkUserLogin(String uin,String sessionID) throws Exception{
		String act = "4";
		StringBuilder strSign = new StringBuilder();
		strSign.append(appId);
		strSign.append(act);
		strSign.append(uin);
		strSign.append(sessionID);
		strSign.append(appKey);
		String sign = md5(strSign.toString());
		StringBuilder getUrl = new StringBuilder();
		getUrl.append("Appid=");
		getUrl.append(appId);
		getUrl.append("&Act=");
		getUrl.append(act);
		getUrl.append("&Uin=");
		getUrl.append(uin);
		getUrl.append("&SessionId=");
		getUrl.append(sessionID);
		getUrl.append("&Sign=");
		getUrl.append(sign);
		return GetResult(HttpGetGo(getUrl.toString()));
	}
	
	/**
	 * 接收支付购买结果
	 * @param appid
	 * @param act
	 * @param productName
	 * @param consumeStreamId
	 * @param cooOrderSerial
	 * @param uin
	 * @param goodsId
	 * @param goodsInfo
	 * @param goodsCount
	 * @param originalMoney
	 * @param orderMoney
	 * @param note
	 * @param payStatus
	 * @param createTime
	 * @param fromSign
	 * @return 支付结果
	 * @throws UnsupportedEncodingException 
	 */
	public int payResultNotify(String appid,String act, String productName,String consumeStreamId,
			String cooOrderSerial,String uin,String goodsId,String goodsInfo,String goodsCount,
			String originalMoney,String orderMoney,String note,
			String payStatus,String createTime,String fromSign) throws UnsupportedEncodingException{
		
		StringBuilder strSign = new StringBuilder();
		strSign.append(appid);
		strSign.append(act);
		strSign.append(productName);
		strSign.append(consumeStreamId);
		strSign.append(cooOrderSerial);
		strSign.append(uin);
		strSign.append(goodsId);
		strSign.append(goodsInfo);
		strSign.append(goodsCount);
		strSign.append(originalMoney);
		strSign.append(orderMoney);
		strSign.append(note);
		strSign.append(payStatus);
		strSign.append(createTime);
		strSign.append(appKey);
		String sign = md5(strSign.toString());
		
		if(!this.appId.equals(appid)){
			return 2; //appid无效
		}
		if(!"1".equals(act)){
			return 3; //Act无效
		}
		if(!sign.toLowerCase().equals(fromSign.toLowerCase())){
			return 5; //sign无效
		}
		int payResult = -1;
		if("1".equals(payStatus)){
			try {
				if(queryPayResult(cooOrderSerial) == 1){
					payResult = 1; //有订单
				}
				else{
					payResult = 11; //无订单
				}
			} catch (Exception e) {
				payResult = 6; //自定义：网络问题
			}
			return payResult;
		}
		return 0;  //错误
	}
	
	
	/**
	 * 获取91服务器返回的结果
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 */
	private int GetResult(String jsonStr) throws Exception{
//		Pattern p = Pattern.compile("(?<=\"ErrorCode\":\")\\d{1,3}(?=\")");
//		Matcher m = p.matcher(jsonStr);
//		m.find();
//		return Integer.parseInt(m.group());
		
		//这里需要引入JSON-LIB包内的JAR
		JSONObject jo = JSONObject.parseObject(jsonStr); 
		return Integer.parseInt(jo.getString("ErrorCode"));
	}
	
	

	/**
	 * 对字符串进行MD5并返回结果
	 * @param sourceStr
	 * @return
	 */
	private String md5(String sourceStr){
		String signStr = "";
		try {
			byte[] bytes = sourceStr.getBytes("utf-8");
			MessageDigest md5 = MessageDigest.getInstance("MD5"); md5.update(bytes);
			byte[] md5Byte = md5.digest();
			if(md5Byte != null){
			signStr = HexBin.encode(md5Byte); }
			} catch (NoSuchAlgorithmException e) { e.printStackTrace();
			} catch (UnsupportedEncodingException e) { e.printStackTrace();
			}
			return signStr;
	}
	
	/**
	 * 发送GET请求并获取结果
	 * @param getUrl
	 * @return
	 * @throws Exception
	 */
	private String HttpGetGo(String getUrl) throws Exception{   
	    StringBuffer readOneLineBuff = new StringBuffer();   
	    String content ="";   
        URL url = new URL(PROTOCOL_HEAD+verifyUrl + getUrl);   
        URLConnection conn = url.openConnection();    
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));          
        String line = "";   
        while ((line = reader.readLine()) != null) {   
            readOneLineBuff.append(line);   
        }   
        content = readOneLineBuff.toString();   
        reader.close();   
	    return content;   
	}   
	public String getPartnerId() {
		return partnerId;
	}

	public String getAppKey() {
		return appKey;
	}

	public String getVerifyUrl() {
		return verifyUrl;
	}

	public String getAppId() {
		return appId;
	}
}
