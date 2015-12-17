package com.fantingame.game.gamecenter.service.impl;

import java.math.BigDecimal;
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
import com.fantingame.game.gamecenter.partener.chinamobile.ChinaMobilePaymentObj;
import com.fantingame.game.gamecenter.partener.chinamobile.ChinaMobileSdk;
import com.fantingame.game.gamecenter.partener.chinamobile.UserInfo;
import com.fantingame.game.gamecenter.partener.fantin.util.TradeInfo;
import com.fantingame.game.gamecenter.sdk.GameApiSdk;
import com.fantingame.game.gamecenter.service.BasePartnerService;
import com.fantingame.game.gamecenter.service.PartnerService;
import com.fantingame.game.gamecenter.util.json.Json;

public class ChinaMobileServiceImpl extends BasePartnerService {
	private static final Logger logger = Logger.getLogger(ChinaMobileServiceImpl.class);
	@Autowired
	private ServerListDao serverListDao;
	@Override
	public UserToken login(String token, String partnerId, String serverId, long timestamp, String sign, Map<String, String> params) {
		if (StringUtils.isBlank(token) || StringUtils.isBlank(partnerId) || StringUtils.isBlank(serverId) || timestamp == 0 || StringUtils.isBlank(sign)) {
			throw new ServiceException(PartnerService.PARAM_ERROR, "参数不正确");
		}
		checkSign(token, partnerId, serverId, timestamp, sign);
		try {
			UserInfo userInfo = ChinaMobileSdk.instance().verifySession(token);
			if (userInfo != null) {
				ChinaMobileSdk.instance().getUserMap().remove(token);
				GameServer gameServer = serverListDao.getServerByServerIdAndPartenerId(serverId, partnerId);
				UserToken userToken = GameApiSdk.getInstance().loadUserToken(userInfo.getUserId(), partnerId, serverId, "0",gameServer, params);
				userToken.setExtInfo(userInfo.getKey());
				return userToken;
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(PartnerService.LOGIN_VALID_FAILD, "登录验证失败");
		}

		throw new ServiceException(PartnerService.LOGIN_VALID_FAILD, "登录验证失败");
	}

	@Override
	public boolean doPayment(String orderId, String partnerUserId, boolean success, String partnerOrderId, BigDecimal finishAmount, Map<String, String> reqInfo) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doPayment(PaymentObj paymentObj) {
		if (paymentObj == null) {
			logger.error("paymentObj为空");
			return false;
		}

		ChinaMobilePaymentObj cb = (ChinaMobilePaymentObj) paymentObj;
		if (!ChinaMobileSdk.instance().checkPayCallbackSign(cb)) {
			logger.error("签名不正确" + Json.toJson(paymentObj));
			return false;
		}
		logger.info("game id:" + cb.getCpparam());
		PaymentOrder order = paymentOrderDao.get(cb.getCpparam());

		logger.info("应用订单：" + Json.toJson(order));
		if (order == null) {
			logger.error("订单为空：" + Json.toJson(cb));
			return false;
		}

		if (order.getStatus() == OrderStatus.STATUS_FINISH) {
			logger.error("订单已经完成" + Json.toJson(cb));
			return true;
		}
		// 以分为单位
		Integer money = ChinaMobileSdk.instance().getGoodsMap().get(cb.getConsumerCode());
		if (money == null) {
			logger.error("道具编号不存在：" + Json.toJson(cb));
			return false;
		}
		BigDecimal finishAmount = new BigDecimal(money).divide(new BigDecimal(100));
		if (order.getAmount().compareTo(finishAmount) != 0) {
			logger.error("订单金额不符：" + Json.toJson(cb));
			return false;
		}

		if (!cb.gethRet().equals("0")) {
			logger.error("充值失败：" + Json.toJson(cb));
			this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_ERROR, cb.getContentId(), finishAmount, "");
			return false;
		}

		int gold = (int) (order.getAmount().doubleValue() * 10);
		// 更新订单状态
		if (this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_FINISH, cb.getContentId(), finishAmount, "")) {
			GameServer gameServer = serverListDao.getServerByServerIdAndPartenerId(order.getServerId(), order.getPartnerId());
			// 请求游戏服，发放游戏货币
			if (!GameApiSdk.getInstance().doPayment(order.getPartnerId(), order.getServerId(), order.getPartnerUserId(), "", order.getOrderId(), finishAmount, gold, "", "",gameServer)) {
				// 如果失败，要把订单置为未支付
				this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_NOT_PAY, cb.getContentId(), finishAmount, "");
				logger.error("发货失败：" + Json.toJson(cb));
				return false;
			} else {
				logger.info("支付成功：" + Json.toJson(cb));
				return true;
			}
		}
		this.paymentOrderDao.updateStatus(order.getOrderId(), OrderStatus.STATUS_ERROR, cb.getContentId(), finishAmount, "");
		logger.error("充值失败：" + Json.toJson(cb));
		return false;
	}

	@Override
	public String createOrder(String partnerId, String serverId, String partnerUserId, BigDecimal amount, String tradeName) {
		TradeInfo info = createOrderInfo(partnerId, serverId, partnerUserId, amount, tradeName);
		GameServer gameServer = serverListDao.getServerByServerIdAndPartenerId(serverId, partnerId);
		int systemGoldSetId = GameApiSdk.getInstance().getSystemGoldSetId(serverId, Double.toString(amount.doubleValue()),gameServer);
		info.setExectInfo(Integer.toString(systemGoldSetId));
		return Json.toJson(info);
	}
	@Override
	public PartenerEnum getPatenerEnum() {
		return PartenerEnum.ChinaMobile;
	}
}