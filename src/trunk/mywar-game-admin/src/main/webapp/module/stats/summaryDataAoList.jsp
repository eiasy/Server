<%@ include file="../common/taglib.jsp"%>
<%@ include file="../common/graphTools.jsp"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title><s:text name="summaryDataAoListJsp.title"></s:text></title>
		<style type="text/css">
		td {
			text-align: center;
		}
		</style>		
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link href="../../css/main.css" rel="stylesheet" type="text/css" />
		<script language="javascript" type="text/javascript" src="../../My97DatePicker/stats_WdatePicker.js"></script>  
	</head>
	<script type="text/javascript">
		function check1(){
			var pid = document.getElementById("pid").value;
			if(pid==0){
				alert("请选择一个平台");
				return false;
			}
			return true;
		}
	</script>
	<body>
		<form action="summaryDataAoList?isCommit=T" method="post" onsubmit="return check1()">
			<s:text name="searchPartner"></s:text><s:text name="colon"></s:text>
			<s:select name="channel" id="pid" list="partnerList" listKey="pid" listValue="PName" headerKey="0" headerValue="%{getText('pleaseSelect')}"/>
			<input type="submit" value="<s:text name='submit'></s:text>" class="button" />
		</form>
		<table id="table" cellpadding="3" cellspacing="1" border="0" width="100%" align=center>
			<tr class="border">
				<td class="td_title" colspan="22" align="center">
					<center>
						<s:text name="summaryDataAoListJsp.title"></s:text>
					</center>
				</td>
			</tr>
			<tr>
				<td>
					<s:text name="server"></s:text>
				</td>
				<td>
					<s:text name="summaryData.currentOnlineAmount"></s:text>
				</td>
				<td>
					<s:text name="summaryData.maxOnlineAmount"></s:text>
				</td>
				<td>
					<s:text name="summaryData.maxOnlineTime"></s:text>
				</td>
				<td>
					<s:text name="summaryData.avgOnlineAmount"></s:text>
				</td>
				<td>
					<s:text name="summaryData.payNum"></s:text>
				</td>
				<td>
					<s:text name="summaryData.payUserNum"></s:text>
				</td>
				<td>
					<s:text name="summaryData.buyToolConsume"></s:text>
				</td>
			</tr>
			<s:iterator value="statsMap">
				<tr>
					<s:iterator>
						<td>
							${key}
						</td>
						<td>
							<s:property value="value.split(',')[0]"/>
						</td>
						<td>
							<s:property value="value.split(',')[5]"/>
						</td>
						<td>
							<s:property value="value.split(',')[6]"/>
						</td>
						<td>
							<s:property value="value.split(',')[4]"/>
						</td>
						<td>
							<s:property value="value.split(',')[1]"/>
						</td>
						<td>
							<s:property value="value.split(',')[2]"/>
						</td>
						<td>
							<s:property value="value.split(',')[3]"/>
						</td>
					</s:iterator>
				</tr>
			</s:iterator>
			<tr>
				<td colspan="22">
					<aldtags:pageTag paraStr="channel,${channel},isCommit,T"/>
				</td>
			</tr>
		</table>
	</body>
</html>