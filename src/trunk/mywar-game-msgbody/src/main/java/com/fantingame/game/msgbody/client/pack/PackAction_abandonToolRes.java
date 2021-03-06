package com.fantingame.game.msgbody.client.pack;

import com.fantingame.game.msgbody.common.io.iface.IXInputStream;
import com.fantingame.game.msgbody.common.io.iface.IXOutStream;
import com.fantingame.game.msgbody.common.model.ICodeAble;
import java.io.IOException;


/**背包丢弃道具**/
public class PackAction_abandonToolRes implements ICodeAble {

		/**道具类型**/
	private Integer toolType=0;
	/**道具id**/
	private Integer toolId=0;
	/**道具数量**/
	private Integer toolNum=0;
	/**用户装备id**/
	private String userEquipId="";
	/**用户宝石id**/
	private String userGemstoneId="";

	
	@Override
	public void encode(IXOutStream outputStream) throws IOException {
		outputStream.writeInt(toolType);

		outputStream.writeInt(toolId);

		outputStream.writeInt(toolNum);

		outputStream.writeUTF(userEquipId);

		outputStream.writeUTF(userGemstoneId);


	}

	@Override
	public void decode(IXInputStream inputStream) throws IOException {
		toolType = inputStream.readInt();

		toolId = inputStream.readInt();

		toolNum = inputStream.readInt();

		userEquipId = inputStream.readUTF();

		userGemstoneId = inputStream.readUTF();


	}
	
		/**道具类型**/
    public Integer getToolType() {
		return toolType;
	}
	/**道具类型**/
    public void setToolType(Integer toolType) {
		this.toolType = toolType;
	}
	/**道具id**/
    public Integer getToolId() {
		return toolId;
	}
	/**道具id**/
    public void setToolId(Integer toolId) {
		this.toolId = toolId;
	}
	/**道具数量**/
    public Integer getToolNum() {
		return toolNum;
	}
	/**道具数量**/
    public void setToolNum(Integer toolNum) {
		this.toolNum = toolNum;
	}
	/**用户装备id**/
    public String getUserEquipId() {
		return userEquipId;
	}
	/**用户装备id**/
    public void setUserEquipId(String userEquipId) {
		this.userEquipId = userEquipId;
	}
	/**用户宝石id**/
    public String getUserGemstoneId() {
		return userGemstoneId;
	}
	/**用户宝石id**/
    public void setUserGemstoneId(String userGemstoneId) {
		this.userGemstoneId = userGemstoneId;
	}

	
	
}
