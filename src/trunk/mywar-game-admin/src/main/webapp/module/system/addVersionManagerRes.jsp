<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.adminTool.bo.Partner"%>

<%@taglib prefix="s" uri="/struts-tags" %>
<%@ include file="../common/taglib.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><s:text name="addVersionManagerResJsp.title"></s:text></title>
	</head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<link href="../../css/main.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" href="../../css/jquery.ui.themes/base/jquery.ui.all.css">
	
	<script type="text/javascript" src="../../js/ajax.js"></script>
	<script src="../../js/jquery/jquery.min.js"></script>
	<body>
		&nbsp;
		<form action="addVersionManagerRes?isCommit=T" method="post" >
			<table cellpadding="3" cellspacing="1" border="0" width="100%" align=center>
				<tr class="border">
					<td class="td_title" colspan="5">
						<center>
							<s:text name="addVersionManagerResJsp.title"></s:text>
							<font color='red'>${erroDescrip}</font>
						</center>
					</td>
				</tr>
				<tr>
					<td >
						<s:text name="versionManagerApk.version"></s:text><s:text name="colon"></s:text>
					</td>
					<td >
						<input type="hidden" name="gameWebId" value="${gameWebId}" />
						<input type="text" size="6" name="resBigVersion" value="${versionManagerRes.resBigVersion}" />
						<input type="text" size="6" name="resSmallVersion" value="${versionManagerRes.resSmallVersion}" />
					</td>
				</tr>
				<tr>
					<td >
						<s:text name="versionManagerApk.beUpdateVersion"></s:text><s:text name="colon"></s:text>
					</td>
					<td >
						<input type="text" size="6" name="beUpdateBigVersion" value="${versionManagerRes.beUpdateBigVersion}" />
						<input type="text" size="6" name="beUpdateSmallVersion" value="${versionManagerRes.beUpdateSmallVersion}" />
					</td>
				</tr>
				<tr>
					<td>
						<s:text name="versionManagerApk.partnerId"></s:text><s:text name="colon"></s:text>
					</td>							
					<td colspan="4">
						<% List<Partner> partnerList = (ArrayList<Partner>) request.getAttribute("partnerList"); %>
						<select name="partnerId" class="select">
							<option value="0"><s:text name="versionManagerApk.allPartner"></s:text>
							<% for (Partner p : partnerList) {%>
								<option value="<%=p.getPid()%>"><%=p.getPName()%>
							<%}%>
						</select>
					</td>	
				</tr>
				<tr>
					<td>
						<s:text name="versionManagerApk.isTest"></s:text><s:text name="colon"></s:text>
					</td>							
					<td colspan="4">
						<select name="isTest" class="select">
							<s:generator separator="," val="%{getText('packageInfo.isTest_valuenew')}">
								<s:iterator var="str">
									<option value="${str}" >
										<s:text name="%{'packageInfo.isTestnew_'+#str}"></s:text>
									</option>
								</s:iterator>
							</s:generator>
						</select>
						<font color="red">新增资源包默认为‘测试包’</font>
					</td>	
				</tr>
				<tr>
					<td>
						<s:text name="versionManagerApk.url"></s:text><s:text name="colon"></s:text>
					</td>							
					<td colspan="4">
						<input type="text" size="120" name="url" value="" />
					</td>	
				</tr>								
				<tr>
					<td>
						<s:text name="versionManagerApk.description"></s:text><s:text name="colon"></s:text>
					</td>							
					<td colspan="4">
						<textarea size="45" name="description" rows="6" cols="60" value="" /></textarea>
					</td>	
				</tr>
				<tr>
					<td colspan="5" align="center">
						<input type="submit"  value="<s:text name='submit'></s:text>" class="button" />
						<input type="reset" value="<s:text name='reset'></s:text>" class="button" />
					</td>
				</tr>
			</table>
		</form>
	</body>
</html>
<script src="../../js/jquery/jquery.validate.js"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("form").validate({
			rules:{
				resBigVersion:{
					required: true,
					digits:true
				},
				resSmallVersion:{
					required: true,
					digits:true
				},
				beUpdateBigVersion:{
					required: true,
					digits:true
				},
				beUpdateSmallVersion:{
					required: true,
					digits:true
				},
				partnerId:{
					required: true
				},
				url:{
					required: true
				}
			}		
		});	
	});
</script>