package com.fantingame.game.mywar.logic.pk.action;

import com.fantingame.game.msgbody.common.model.Msg;
import com.fantingame.game.server.annotation.ISingleThreadRuler;
import com.fantingame.game.server.util.Utils;

public class EnterPkThread implements ISingleThreadRuler {

	private Long key;
	
	@Override
	public String getCmdCode() {
		return "PkAction_enterPk";
	}

	@Override
	public long getRulerKey(Msg msg) {
		// TODO 此处key不需要重复计算 是因为cmdCode不会改变 如果计算因子会改变 则不应该如此做
		if (key == null) {
			String str = msg.getMsgHead().getCmdCode();
			key = Utils.getUserHashCode(str);
		}
		
		return key;
	}

}
