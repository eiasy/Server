package com.fantingame.game.msgbody.client.pack;

import com.fantingame.game.msgbody.common.io.iface.IXInputStream;
import com.fantingame.game.msgbody.common.io.iface.IXOutStream;
import com.fantingame.game.msgbody.common.model.ICodeAble;
import java.io.IOException;

import com.fantingame.game.msgbody.client.goods.CommonGoodsBeanBO;

/**扩展背包**/
public class PackAction_extendPackRes implements ICodeAble {

		/**通用掉落对象**/
	private CommonGoodsBeanBO drop=null;

	
	@Override
	public void encode(IXOutStream outputStream) throws IOException {
		drop.encode(outputStream);


	}

	@Override
	public void decode(IXInputStream inputStream) throws IOException {
		drop=new CommonGoodsBeanBO();    
		drop.decode(inputStream);


	}
	
		/**通用掉落对象**/
    public CommonGoodsBeanBO getDrop() {
		return drop;
	}
	/**通用掉落对象**/
    public void setDrop(CommonGoodsBeanBO drop) {
		this.drop = drop;
	}

	
	
}
