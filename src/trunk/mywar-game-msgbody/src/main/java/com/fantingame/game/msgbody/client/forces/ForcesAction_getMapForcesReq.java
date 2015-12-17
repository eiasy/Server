package com.fantingame.game.msgbody.client.forces;

import com.fantingame.game.msgbody.common.io.iface.IXInputStream;
import com.fantingame.game.msgbody.common.io.iface.IXOutStream;
import com.fantingame.game.msgbody.common.model.ICodeAble;
import java.io.IOException;


/**获取某个地图下所有用户关卡信息列表**/
public class ForcesAction_getMapForcesReq implements ICodeAble {

		/**地图id**/
	private Integer mapId=0;

	
	@Override
	public void encode(IXOutStream outputStream) throws IOException {
		outputStream.writeInt(mapId);


	}

	@Override
	public void decode(IXInputStream inputStream) throws IOException {
		mapId = inputStream.readInt();


	}
	
		/**地图id**/
    public Integer getMapId() {
		return mapId;
	}
	/**地图id**/
    public void setMapId(Integer mapId) {
		this.mapId = mapId;
	}

	
	
}
