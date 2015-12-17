package com.fantingame.game.gamecenter.service.impl.yxcq;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.fantingame.game.gamecenter.bo.UserToken;
import com.fantingame.game.gamecenter.constant.OrderStatus;
import com.fantingame.game.gamecenter.constant.PartenerEnum;
import com.fantingame.game.gamecenter.dao.PaymentOrderDao;
import com.fantingame.game.gamecenter.dao.ServerListDao;
import com.fantingame.game.gamecenter.exception.ServiceException;
import com.fantingame.game.gamecenter.model.GameServer;
import com.fantingame.game.gamecenter.model.PaymentOrder;
import com.fantingame.game.gamecenter.partener.PaymentObj;
import com.fantingame.game.gamecenter.partener.fantin.util.TradeInfo;
import com.fantingame.game.gamecenter.partener.yxcq.qihoo.PayCallbackObject;
import com.fantingame.game.gamecenter.partener.yxcq.qihoo.YXCQQiHooSdk;
import com.fantingame.game.gamecenter.sdk.GameApiSdk;
import com.fantingame.game.gamecenter.service.BasePartnerService;
import com.fantingame.game.gamecenter.service.PartnerService;
import com.fantingame.game.gamecenter.util.json.Json;

public class YXCQQiHooServiceImpl extends BasePartnerService {

	private final static Logger logger = Logger.getLogger(YXCQQiHooServiceImpl.class);

	@Autowired
	private PaymentOrderDao paymentOrderDao;
	@Autowired
	private ServerListDao serverListDao;
	@Override
	public UserToken login(String token, String partnerId, String serverId, long timestamp, String sign, Map<String, String> params) {
		if (StringUtils.isBlank(token) || StringUtils.isBlank(partnerId) || StringUtils.isBlank(serverId) || timestamp == 0 || StringUtils.isBlank(sign)) {
			throw new ServiceException(PartnerService.PARAM_ERROR, "参数不正确");
		}
		checkSign(token, partnerId, serverId, timestamp, sign);

		try {
			logger.info("qihoo token:" + token);
			Map<String, String> tokenInfo = YXCQQiHooSdk.instance().assessToken(token);
			logger.info("tokenInfo:" + Json.toJson(tokenInfo));
			if (tokenInfo != null && tokenInfo.containsKey("access_token") && !StringUtils.isBlank(tokenInfo.get("access_token"))) {
				String accessToken = tokenInfo.get("access_token");
				String refreshToken = tokenInfo.get("refresh_token");
				logger.info("accessToken:" + accessToken);
				logger.info("refreshToken:" + refreshToken);
				Map<String, String> userInfoMap = YXCQQiHooSdk.instance().getUserInfo(tokenInfo.get("access_token"));
				logger.info("userInfoMap:" + userInfoMap);
				if (userInfoMap != null && userInfoMap.containsKey("id")) {
					GameServer gameServer = serverListDao.getServerByServerIdAndPartenerId(serverId, partnerId);
					UserToken userToken = GameApiSdk.getInstance().loadUserToken(userInfoMap.get("id"), partnerId, serverId, "0",gameServer, params);
					userToken.setPartnerToken(accessToken);
					userToken.setExtInfo(refreshToken);
					return userToken;
				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(PartnerService.LOGIN_VALID_FAILD, "登录验证失败");
		}

		throw new ServiceException(PartnerService.LOGIN_VALID_FAILD, "登录验证失败");
	}

	@Override
	public boolean doPayment(String orderId, String partnerUserId, boolean success, String partnerOrderId, BigDecimal finishAmount, Map<String, String> reqInfo) {
		return false;
	}

	@Override
	public boolean doPayment(PaymentObj paymentObj) {
		if (paymentObj == null) {
			logger.error("paymentObj为空");
			return false;
		}
		PayCallbackObject cb = (PayCallbackObject) paymentObj;

		if (!YXCQQiHooSdk.instance().checkPayCallbackSign(cb)) {
			logger.error("签名不正确" + Json.toJson(paymentObj));
			return false;
		}

		PaymentOrder order = paymentOrderDao.get(cb.getApp_order_id());

		logger.info("应用订单：" + Json.toJson(order));
		if (order == null) {
			logger.error("订单为空：" + Json.toJson(cb));
			return false;
		}

		if (order.getStatus() == OrderStatus.STATUS_FINISH) {
			logger.info("订单已经完成" + Json.toJson(cb));
			return true;
		}

		BigDecimal finishAmount = new BigDecimal(cb.getAmount()).divide(new BigDecimal(100));
//		if (order.getAmount().compareTo(finishAmount) != 0) {
//			logger.error("订单金额不符：" + Json.toJson(cb));
//			return false;
//		}

		if (!cb.getGateway_flag().equals("success")) {
			logger.error("充值失败：" + Json.toJson(cb));
			this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_ERROR, cb.getOrder_id(), finishAmount, "");
			return true;
		}

		int gold = (int) (finishAmount.doubleValue() * 10);
		String extInfo = cb.getApp_ext1();
		if (YXCQQiHooSdk.instance().verifyPayment(cb)) {
			// 更新订单状态
			if (this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_FINISH, cb.getOrder_id(), finishAmount, extInfo)) {
				GameServer gameServer = serverListDao.getServerByServerIdAndPartenerId(order.getServerId(), order.getPartnerId());
				// 请求游戏服，发放游戏货币
				if (!GameApiSdk.getInstance().doPayment(order.getPartnerId(), order.getServerId(), order.getPartnerUserId(), "", order.getOrderId(), finishAmount, gold, "", "",gameServer)) {
					// 如果失败，要把订单置为未支付
					this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_NOT_PAY, cb.getOrder_id(), finishAmount, extInfo);
					logger.error("发货失败：" + Json.toJson(cb));
					return false;
				} else {
					logger.info("支付成功：" + Json.toJson(cb));
					return true;
				}
			}
		}
		this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_ERROR, cb.getOrder_id(), finishAmount, "");
		logger.error("充值失败：" + Json.toJson(cb));
		return false;
	}

	@Override
	public String createOrder(String partnerId, String serverId, String partnerUserId, BigDecimal amount, String tradeName) {
		TradeInfo info = createOrderInfo(partnerId, serverId, partnerUserId, amount, tradeName);
		info.setNotifyUrl(YXCQQiHooSdk.instance().getPayCallback());
		// 360以分为单位
		info.setReqFee(Integer.toString(new BigDecimal(100).multiply(new BigDecimal(info.getReqFee())).intValue()));
		return Json.toJson(info);
	}

	@Override
	public PartenerEnum getPatenerEnum() {
		return PartenerEnum.YXCQ_QiHoo;
	}
}
