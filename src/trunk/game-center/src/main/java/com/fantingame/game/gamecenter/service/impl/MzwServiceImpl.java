package com.fantingame.game.gamecenter.service.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantingame.game.gamecenter.bo.UserToken;
import com.fantingame.game.gamecenter.constant.OrderStatus;
import com.fantingame.game.gamecenter.constant.PartenerEnum;
import com.fantingame.game.gamecenter.dao.ServerListDao;
import com.fantingame.game.gamecenter.exception.ServiceException;
import com.fantingame.game.gamecenter.model.GameServer;
import com.fantingame.game.gamecenter.model.PaymentOrder;
import com.fantingame.game.gamecenter.partener.PaymentObj;
import com.fantingame.game.gamecenter.partener.fantin.util.TradeInfo;
import com.fantingame.game.gamecenter.partener.mzw.MzwPaymentObj;
import com.fantingame.game.gamecenter.partener.mzw.MzwSdk;
import com.fantingame.game.gamecenter.partener.mzw.MzwUserInfo;
import com.fantingame.game.gamecenter.sdk.GameApiSdk;
import com.fantingame.game.gamecenter.service.BasePartnerService;
import com.fantingame.game.gamecenter.service.PartnerService;
import com.fantingame.game.gamecenter.util.json.Json;

/**
 * 拇指玩
 * 
 * @author yezp
 */
public class MzwServiceImpl extends BasePartnerService {
	
	@Autowired
	private ServerListDao serverListDao;
	
	private static final Logger logger = Logger.getLogger(MzwServiceImpl.class);

	@Override
	public UserToken login(String token, String partnerId, String serverId,
			long timestamp, String sign, Map<String, String> params) {
		if (StringUtils.isBlank(token) || StringUtils.isBlank(partnerId) || StringUtils.isBlank(serverId) || timestamp == 0 || StringUtils.isBlank(sign)) {
			throw new ServiceException(PartnerService.PARAM_ERROR, "参数不正确");
		}
		checkSign(token, partnerId, serverId, timestamp, sign);
		
		try {
			MzwUserInfo userInfo = MzwSdk.instance().getUserInfo(token);
			if (userInfo != null && userInfo.getCode().equals("1")) {
				GameServer gameServer = serverListDao.getServerByServerIdAndPartenerId(serverId, partnerId);
				UserToken userToken = GameApiSdk.getInstance().loadUserToken(userInfo.getUser().getUid(), partnerId, serverId, "0", gameServer, params);
				
				return userToken;
			} else {
				if (userInfo != null) {
					logger.error("Wzw login error msg:" + userInfo.getMsg());
					throw new ServiceException(PartnerService.LOGIN_VALID_FAILD, userInfo.getMsg());
				} else {
					logger.error("Wzw login faild:" + token);
					throw new ServiceException(PartnerService.LOGIN_VALID_FAILD, "登录验证失败");
				}
			}			
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(PartnerService.PARAM_ERROR, "参数不正确");
		}
	}

	@Override
	public boolean doPayment(String orderId, String partnerUserId,
			boolean success, String partnerOrderId, BigDecimal finishAmount,
			Map<String, String> reqInfo) {
		return false;
	}

	@Override
	public boolean doPayment(PaymentObj paymentObj) {
		if (paymentObj == null) {
			logger.error("paymentObj为空");
			return false;
		}
		MzwPaymentObj rsp = (MzwPaymentObj) paymentObj;
		if (!MzwSdk.instance().checkPayCallbackSign(rsp)) {
			logger.error("签名不正确" + Json.toJson(rsp));
			return false;
		}
		
		// 将自己的订单号放在extern
		PaymentOrder order = paymentOrderDao.get(rsp.getExtern());
		if (order == null) {
			logger.error("订单为空：" + Json.toJson(rsp));
			return false;
		}

		if (order.getStatus() == OrderStatus.STATUS_FINISH) {
			logger.info("订单已经完成" + Json.toJson(rsp));
			return true;
		}

		BigDecimal finishAmount = new BigDecimal(rsp.getMoney());
		int gold = (int) (finishAmount.doubleValue() * 10);
		String extInfo = rsp.getProductDesc();
		// 更新订单状态
		if (this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_FINISH, rsp.getOrderID(), finishAmount, extInfo)) {
			GameServer gameServer = serverListDao.getServerByServerIdAndPartenerId(order.getServerId(), order.getPartnerId());
			// 请求游戏服，发放游戏货币
			if (!GameApiSdk.getInstance().doPayment(order.getPartnerId(), order.getServerId(), order.getPartnerUserId(), "", order.getOrderId(), finishAmount, gold, "", "",gameServer)) {
				// 如果失败，要把订单置为未支付
				return this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_NOT_PAY, rsp.getOrderID(), finishAmount, extInfo);
			} else {
				logger.info("支付成功：" + Json.toJson(rsp));
				return true;
			}
		}

		logger.error("充值失败：" + Json.toJson(rsp));		
		return false;
	}
	
	@Override
	public String createOrder(String partnerId, String serverId,
			String partnerUserId, BigDecimal amount, String tradeName) {
		String tradeNameDecode = tradeName;
		try {
			tradeNameDecode = new String(tradeNameDecode.getBytes("ISO_8859-1"));
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		logger.info("Mzw createOrder tradeNameDecode = " + tradeNameDecode);
		TradeInfo info = createOrderInfo(partnerId, serverId, partnerUserId,
				amount, tradeName);
		info.setNotifyUrl("http://wapi.andr.hw.fantingame.com:80/webApi/mzwPayment.do");
//		info.setNotifyUrl("http://203.195.190.121:8089/webApi/mzwPayment.do");
		logger.info("Mzw orderInfo:" + info.getNotifyUrl());
		return Json.toJson(info);
	}
	
	@Override
	public String createOrder(String partnerId, String serverId,
			String partnerUserId, BigDecimal amount, String tradeName, String qn) {
		String tradeNameDecode = tradeName;
		try {
			tradeNameDecode = URLDecoder.decode(tradeName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		TradeInfo info = createOrderInfo(partnerId, serverId, partnerUserId,
				amount, tradeName, qn);
		info.setNotifyUrl("http://wapi.andr.hw.fantingame.com:80/webApi/mzwPayment.do");
//		info.setNotifyUrl("http://203.195.190.121:8089/webApi/mzwPayment.do");
		logger.info("Mzw orderInfo:" + info.getNotifyUrl());
		return Json.toJson(info);
	}

	@Override
	public PartenerEnum getPatenerEnum() {
		return PartenerEnum.Mzw;
	}

}
