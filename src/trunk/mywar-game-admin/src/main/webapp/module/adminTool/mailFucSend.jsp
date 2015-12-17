<%@ page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.system.bo.GameWeb"%>
<%@page import="com.system.bo.TGameServer"%>
<%@ include file="../common/taglib.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><s:text name="发送运营邮件"></s:text></title>
	</head>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link href="../../css/main.css" rel="stylesheet" type="text/css" />
	<script language="javascript" src="../../js/json.js"></script>
	<script language="javascript" src="../../js/ajax.js"></script>
	<script language="javascript" type="text/javascript" src="../../My97DatePicker/newDate_WdatePicker.js"></script>


	<script type="text/javascript">
	var mailAttachs = "";
	var tempMailAttachs = "";
	// 存放系统中所有可以发送的东西，包括装备、道具、元宝、武将等等
	var toolArray = new Array();
	
	// 向 toolArray 中添加装备信息
	<s:iterator value="tools" status="sta">
		var equipment = new Object();
		// toolId 是 String 类型，格式是 toolType,toolId
		equipment.toolId = "${key}";
		equipment.toolName = "${value}";
		toolArray.push(equipment);
	</s:iterator>
	
	var equipmentColorArray = new Array();
	<s:generator separator="," val="%{getText('userEquipment.color_values')}">
		<s:iterator var="str">
				equipmentColorArray[${str}] = "<s:text name="%{'userEquipment.color_'+#str}"></s:text>";
		</s:iterator>
	</s:generator>
	
	function searchTool() {
		var toolInput = document.getElementById("toolInput").value;
		var toolSelect = document.getElementById("toolSelect");
		for (i = toolSelect.options.length-1; i >= 0; i--) {
			toolSelect.options[i] = null;
		}
		
		var tempArray = new Array();
		
		for (var i = 0; i < toolArray.length; i++) {
			if (toolArray[i].toolName==toolInput) {
				var tool = new Object();
				tool.toolId = toolArray[i].toolId;
				tool.toolName = toolArray[i].toolName;
				tempArray.push(tool);
			}
		}
	
	    	for (var i = 0; i < toolArray.length; i++) {
			if (toolArray[i].toolName!=toolInput&&toolArray[i].toolName.indexOf(toolInput) != -1) {
				var tool = new Object();
				tool.toolId = toolArray[i].toolId;
				tool.toolName = toolArray[i].toolName;
				tempArray.push(tool);
			}
		}
		 
		
		
		
		if (tempArray.length == 0) {
			alert("<s:text name="没有找到"></s:text>")
			return;
		}
		
		var quality = 1;
		for(var i=0; i<tempArray.length; i++){
			toolSelect.options[i] = new Option(tempArray[i].toolName, tempArray[i].toolId);
			toolSelect.options[i].style.backgroundColor = equipmentColorArray[quality];
			if (quality < 7) {
				quality++;
			} else {
				quality = 1;
			}
		}
	}
	
	function addTool() {
		var toolSelect = document.getElementById("toolSelect");
		var toolNum = document.getElementById("toolNum");
		var tools = document.getElementById("tools");
		var toolList = document.getElementById("toolList");
		var fromRank = document.getElementById("fromRank").value;
		var toRank = document.getElementById("toRank").value;
		var target = document.getElementById("target").value;
		var id = 0;
		var name = "";
		var num = toolNum.value;
		
		if (num == "") {
			alert("<s:text name="请输入购买数量"></s:text>");
			return;
		}
		if(target!=2){
			if(fromRank == "" || toRank == ""){
				alert("<s:text name="请输入排行范围"></s:text>");
				return;
			}
		}
		for (i = toolSelect.options.length-1; i >= 0; i--) {
			if (toolSelect.options[i].selected == true) {
				name = toolSelect[i].innerHTML;
			}
		}
		if(target==2){
			toolList.value += "\n" + name + " * " + num;
		}else{//有排行的显示 后面追加一个排行范围
			if(fromRank==toRank){
				toolList.value += "\n" + name + " * " + num + "(第"+fromRank+"名奖励)";
			}else{
				toolList.value += "\n" + name + " * " + num + "(第"+fromRank+"名到第"+toRank+"名奖励)";
			}
		}
		
		id = toolSelect.value;
		mailAttachs += id + "," + parseInt(num) + "|";
		if(target!=2){
			tempMailAttachs += id + "," + parseInt(num) + ","+parseInt(fromRank)+"_"+parseInt(toRank)+"|";
		}
	}
	
	function beforeSubmit() {
		var servers = document.getElementsByName("serverIds");
		var toolList = document.getElementById("toolList");
		var title = document.getElementById("title").value;
		var content = document.getElementById("content").value;
		var startDate = document.getElementById("startDate").value;
		var endDate = document.getElementById("endDate").value;
		var fromRank = document.getElementById("fromRank").value;
		var toRank = document.getElementById("toRank").value;
		var target = document.getElementById("target").value;
		var num = 0;
		for (var i = 0; i < servers.length; i++) {
			if(servers[i].checked == true) {
				num++;
			}
		}
		if (num >= 2) {
			alert("选择了多个服务器，只能发送单个服务器");
			return false;	
		}
		
		/* if (mailAttachs.length == 0) {
			alert("<s:text name="没有选择道具"></s:text>");
			return false;
		} */
		
		var mailAttachInput = document.getElementById("mailAttach");
		mailAttachInput.value = mailAttachs;

		var tempMailAttachInput = document.getElementById("tempMailAttach");
		tempMailAttachInput.value = tempMailAttachs;
		
		if (toolList.value.length != 0 && mailAttachInput.value.length == 0) {
			alert("数据错误，请按 F5 刷新页面！");
			return false;
		}
		if(target!=2){
			var mailAttachsArr = mailAttachs.split("|");
			var tempMailAttachsArr = tempMailAttachs.split("|");
			if(mailAttachsArr.length != tempMailAttachsArr.length){
				alert("数据异常，请按 F5 刷新页面！");
				return false;
			}
		}
		if (!title || !content) {
			alert("请填写标题，内容");
			return false;
		}
		if(startDate =="" || endDate == ""){
			alert("请选择时间段");
			return false;
		}
		var diamond = 0; // 钻石数
		var tool = 0; // 道具
		var mailAttachsArr = new Array();
		mailAttachsArr = mailAttachs.split("|");
		for (var i = 0; i < mailAttachsArr.length - 1; i++) {
			var temp = mailAttachsArr[i];
			var tempArr = new Array();
			tempArr = temp.split(",");
			if (!tempArr) {
			} else {
				if (tempArr.length == 3) {
					var tempArrInt = parseInt(tempArr[2]);
					if (tempArr[0] == 1) {
						diamond += tempArrInt;
					} else if (tempArr[0] == 1001 || tempArr[0] == 4001 || tempArr[0] == 5001 || tempArr[0] == 6001 || tempArr[0] == 7001) {
						tool += tempArrInt;
					}
				}
			}
		}
		
		var servIds = document.getElementsByName("serverIds");
		var flag = false;
		for (var i = 0; i < servIds.length; i++) {
			if (servIds[i].type == "checkbox") {
				if (servIds[i].checked) {
					flag = true;
				}
			}
		}
		if (flag == false) {
			alert("请选择服务器!");
			return false;
		} else {
			return true;
		}
		
		// 一次性发送用户数不宜超过100个
		/*var tempLodoIds = document.getElementById("lodoIds").value;
		var tempLodoIdsArr = tempLodoIds.split(",");
		
		if (tempLodoIdsArr.length > 100) {
			alert("一次性发送用户数，不宜超过100。");
			return false;
		} else {
			return true;
		}
		*/
	}

	/*
	function isSendAll() {
		var lodoIds = document.getElementById("lodoIds").value;
		if (lodoIds != "") {
			document.getElementById("isCheckAll").setAttribute("disabled", "disabled");
		} else {
			document.getElementById("isCheckAll").removeAttribute("disabled");
		}
		
	}
	*/
	function selectPartner(){
		var target = document.getElementById("target").value;
		var rank = document.getElementById("rank");
		if(target!=2){
			rank.style.display = '';
		}else{
			rank.style.display = 'none';
		}
		/*var target = document.getElementById("target").value;
		
		if (target == 5) {
			var startTime = document.getElementById("startTime");
			startTime.type = "hidden";
			var partner = document.getElementById("partner");
			
			partner.style.display = '';
		} else {
			var startTime = document.getElementById("startTime");
			startTime.type = "visibility";
			var partner = document.getElementById("partner");
			
			partner.style.display = 'none';
		}
		
		if (target == 1) {
			var min1 = document.getElementById("min1");
			min1.style.display = "inline";
			
			var min2 = document.getElementById("min2");
			min2.style.display = "inline";
			
		} else {
			var min1 = document.getElementById("min1");
			min1.style.display = "none";
			
			var min2 = document.getElementById("min2");
			min2.style.display = "inline";
		}
		*/
	}
	
/* 	function setTargetDate(par) {
	 	var setTime = "<input style=width:15% type=text name=date onfocus=WdatePicker({dateFmt:'yyyy-MM-dd'}) class=Wdate>";
			
		var value = par.value;
		var table = document.getElementById("table");
		var row = table.rows.item(2).cells.item(0);
		var content = row.innerHTML;
		
		if (value == 1) {
			content = content + " 注册时间 " + setTime;
		} else if (value == 3) {
			content = content + " 登录时间 " + setTime;
		} else if (value = 4) {
			content = content + " 充值时间 " + setTime;
		}
		
		row.innerHTML = content;
	} */
	
	function checkAll(obj) {
		var index = obj.value;
		var isChecked = obj.checked;
		var gameWeb = document.getElementById("gameMap_" + index).value;
		var servers = gameWeb.split("_");
		for (var i = 0; i < servers.length - 1; i++) {
			var server = servers[i];
			document.getElementById(server).checked = isChecked;
		}
		
	}
	</script>
<body>
	<form action="mailFucSend?isCommit=T" method="post" onsubmit="return beforeSubmit()">
		<table cellpadding="3" cellspacing="2" border="0" width="100%" id="table"
			align=center>
			<%
				Map<GameWeb, List<TGameServer>> gameWebMap = (Map<GameWeb, List<TGameServer>>) request.getAttribute("gameWebMap");
				// 排序
				ArrayList<Map.Entry<GameWeb, List<TGameServer>>> mappingList = new ArrayList<Map.Entry<GameWeb, List<TGameServer>>>(gameWebMap.entrySet());
				// 通过比较器实现比较排序
				Collections.sort(mappingList, new Comparator<Map.Entry<GameWeb, List<TGameServer>>>(){
					public int compare(Map.Entry<GameWeb, List<TGameServer>> mapping1,Map.Entry<GameWeb, List<TGameServer>> mapping2){
						return mapping1.getKey().getServerId().compareTo(mapping2.getKey().getServerId());
					}
				}); 
			%>
			
			<%
			for (Map.Entry<GameWeb, List<TGameServer>> mapping : mappingList) { 
  					List<TGameServer> list = mapping.getValue();
  					GameWeb gameWeb = mapping.getKey();
  			%>
  			<tr>
			<td size="2">
				选择服务器
				
			</td>
			<td>
				<input type="checkbox" name="gameWeb" value="<%=gameWeb.getServerId()%>" onclick="checkAll(this);"/>
							<%=gameWeb.getServerName()%>(全选)
  			<%
  					String str = "";
					int rowspan = list.size();
					if (list.size() == 0) {
						rowspan = 1;
					}
			
			%>
				
				<%-- <td><s:iterator var="data" value="tgameServerList">
						<input type="checkbox" name="serverIds" value="${data.serverId}" />
					${data.serverAlias}
				</s:iterator></td> --%>
				
					&nbsp;(
					<%
						for (TGameServer data : list) {
					%>
							<input type="checkbox" name="serverIds" id="<%=data.getServerAlias()%><%=data.getServerId()%>" value="<%=data.getServerId()%>" />
							<%=data.getServerAlias()%>
					<%
							str += data.getServerAlias();
							str += data.getServerId();
					%>
							<script>
								<%-- alert("<%=str%>"); --%>
							</script>
					<%
							str += "_";
						}
					%>
					)
					<script>
								<%-- alert(":"+"<%=str%>"); --%>
					</script>
				</td>
				<input type="hidden" id="gameMap_<%=gameWeb.getServerId()%>" value="<%=str%>"/>
			</tr>
			<%
			}
			
			%>
			
			<tr>
				<td rowspan="3">收件对象</td>
			</tr>
			<tr>
				<td>发放目标	
				     <!--input id="isCheckAll" type="checkbox" name="target" value=1-->
				     <select name="target" id="target" onchange="selectPartner()">
				         <option value=2>时间段内充值用户</option>
				         <option value=1>急速冲等</option> 
				         <option value=3>榜上有名</option>
				     </select>
					<!-- 
					<select name="partner" id="partner" style="display:none">
						<s:iterator var="data" value="partnerList">
							<option value=${data.pid}>${data.PName}</option>
						</s:iterator>
					</select>
					 -->
					 <s:text name="timeFrom"></s:text><s:text name="colon"></s:text>
					 <input type="text" id="startDate" name="startDate" value="<s:if test="startDate != null"><s:text name="format.date_time"><s:param value="startDate"></s:param></s:text></s:if>" class="Wdate" style="width:160px" onfocus="WdatePicker({startDate:'%y-%M-%d 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',onpicked:function(){endDate.focus();}})"/>
					 <s:text name="timeTo"></s:text><input type="text" id="endDate" name="endDate" value="<s:if test="endDate != null"><s:text name="format.date_time"><s:param value="endDate"></s:param></s:text></s:if>" class="Wdate" style="width:160px" onfocus="WdatePicker({startDate:'%y-%M-%d 00:00:00',dateFmt:'yyyy-MM-dd HH:mm:ss',minDate:'#F{$dp.$D(\'startDate\')}'})"/>
				</td>
			</tr>
			<tr>
				<td>
					<br>1.如果选择<b>『时间段内充值用户』</b>，邮件只发送给这段时间内充值过的用户；
					<br>2.如果选择<b>『急速冲等』</b>，邮件只发送给这段时间内等级排行前20的用户；
					<br>3.如果选择<b>『榜上有名』</b>，邮件只发送给这段时间内争霸排行前10的用户；
				</td>
			</tr>
			<tr>
				<td>主题</td>
				<td><input type="text" name="title" id="title"></td>
			</tr>
			<tr>
				<td>邮件内容</td>
				<td><textarea name="content" id="content" rows="4" cols="20"></textarea></td>
			</tr>
			<tr id="rank" style="display:none">
				<td>排名</td>
				<td>从<input type="text" id="fromRank" name="fromRank"/>到<input type="text" id="toRank" name="toRank"/></td>
			</tr>
			<tr>
				<td rowspan="3">道具</td>
				<td><s:text name="道具名"></s:text>
					<input type="text" id="toolInput"> 
					<input type="button" value="<s:text name="搜索"></s:text>" onclick="searchTool()"	class="button">
				</td>
			</tr>
			<tr>
				<td>
					<s:text name="道具列表"></s:text>
					 <select id="toolSelect">
						<option value="-1"><s:text name="请先搜索"></s:text></option>
					</select> 
					<s:text name="数量"></s:text>
					<input type="text" id="toolNum" size="5" onblur="value=value.replace(/[^\d]/g,'')">
					<input type="button" value="<s:text name="添加"></s:text>" onclick="addTool()" class="button">
				</td>
			</tr>
			<tr>
					<td>
					<s:text name="发放道具列表"></s:text><br/>
					<textarea name="toolList" id="toolList" rows="10" cols="40" readonly="readonly"></textarea>
					</td>
				</tr>
			<tr>
				<td align="center" colspan="2">
					<input type="hidden" id="mailAttach" name="mailAttach"> 
					<input type="hidden" id="tempMailAttach" name="tempMailAttach"> 
					<input type="submit" value="<s:text name="提交审核"></s:text>"  class="button"></td>
			</tr>
		</table>
	</form>
</body>
</html>