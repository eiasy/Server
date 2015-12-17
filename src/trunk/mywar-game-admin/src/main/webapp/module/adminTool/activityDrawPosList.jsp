<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>

<%@ include file="../common/taglib.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title><s:text name="drawPosListJsp.title"></s:text></title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link href="../../css/main.css" rel="stylesheet" type="text/css" />
		
	</head>
	<script type="text/javascript" src="../../js/ajax.js"></script>
	<script type="text/javascript">	
		var success = '${isCommit}';
		if (success == 'T') {
			alert("配置成功！");
		}
		
		function addRow() {
			var newRow = document.all.posTable.insertRow();
			var cssTd = "style='background: #EEF7FD; height:24px; font: normal 12px SimSun; line-height:20px; border:1px solid #fff; color:#135294; padding:2px; text-align:left;'";
			newRow.innerHTML="<td " + cssTd + "><input type='text' name='posArr' value='' /></td>" 
				+ "<td " + cssTd + "><input type='text' name='lowerNumArr' value='' /></td>"
				+ "<td " + cssTd + "><input type='text' name='upperNumArr' value='' /></td>"
				+ "<td " + cssTd + "><a href='#' onclick='reset(this)' ><s:text name='reset'></s:text></a></td>";	
		}		
		
		function reset(tableTd) {		
			var obj = tableTd.parentNode.parentNode;
			var childrenArr = obj.children;
			
			for (var i = 0; i < childrenArr.length; i++) {
				var inputNode = childrenArr[i].children[0];
				inputNode.value = '';
			}
		}
	</script>
	<body>
		<form name="f" action="addActivityDrawPos?isCommit=T" method="post" >
		<input type="button"  value="<s:text name='drawPos.addPos'></s:text>" class="button" onclick=addRow(); />
		<table id="posTable" cellpadding="3" cellspacing="1" border="0" width="100%" align=center>
			<tr class="border">
				<td class="td_title" colspan="22" align="center">
					<center>
						<s:text name="drawPosListJsp.title"></s:text>
					</center>
				</td>
			</tr>
			<tr>
				<td >
					<input type="hidden" name="activityId" value="${activityId}" />
					<s:text name="drawPos.pos"></s:text>
				</td>
				<td >
					<s:text name="drawPos.lowerNum"></s:text>
				</td>
				<td>
					<s:text name="drawPos.upperNum"></s:text>
				</td>
				<td width="35">
					<s:text name="reset"></s:text>
				</td>
			</tr>
			<s:iterator var="drawPos" value="drawPosList" >
			<input type="hidden" name="idArr" value="${drawPos.id}" />			
			<tr id="${drawPos.id}" >
				<td >
					<input type="text" name="posArr" value="${drawPos.pos}" />								
				</td>
				<td >
					<input type="text" name="lowerNumArr" value="${drawPos.drawLowerNum}" />								
				</td>
				<td >					
					<input type="text" name="upperNumArr" value="${drawPos.drawUpperNum}" />
				</td>			
				<td>
					<a href="#" onclick='reset(this)' ><s:text name="reset"></s:text></a>
				</td>
			</tr>			
			</s:iterator>
		</table>
		<table>
			<td colspan="5">
				<input type="submit" value="<s:text name='submit'></s:text>" class="button" />
			</td>
		</table>
	</body>
</html>
<script src="../../js/jquery/jquery.validate.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("form").validate({
			rules:{
				posArr:{
					required:true,
					digits:true
				},
				lowerNumArr:{
					required:true,
					digits:true
				},
				upperNumArr:{
					required:true,
					digits:true
				}
			}		
		});	
	});
</script>
