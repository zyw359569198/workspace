<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<style type="text/css">
BODY {
	text-align:center;
	font-size:12px;
	font-family:Tahoma,Verdana,STHeiTi,simsun,sans-serif;
}
DIV#header {
	margin:10px 10px;
	width:60%;
	border:1px solid #A0BEDC;
	padding:10px 0px;
	font-size: 16px;
	color: black;
	font-weight:700;
	text-align:center;
}
DIV#content {
	width:60%;
	border:1px solid #A0BEDC;
	margin-top:4px;
	padding:10px 0px;
}

DIV#content #payheader {
	margin-bottom:5px;
	width:60%;
	margin-top:20px;
}

DIV#content #paycontent {
	width:60%;
}

DIV#content #tips{
	border-top:1px solid #DDDDDD;
	color:#888888;
	text-align:left;
	width:600px;
	margin:40px auto;
}

DIV#content #tips UL{
	list-style:none;
}

.yue {
	background:url(/root/img/cs_navtop.png) 0px -94px no-repeat;
}

.button {
	background-color:#005EAC;
	border-color:#B8D4E8 #124680 #124680 #B8D4E8;
	border-style:solid;
	border-width:1px;
	color:#FFFFFF;
	cursor:pointer;
	font-size:12px;
	overflow:visible;
	padding:4px 15px;
	text-align:center;
	line-height:25px;
}
.ratio {
	color:#ccc;
	font-size:12px;
	
}

.txtInput {
	border:1px solid #D4D0C8;
	height:18px;
	padding:1px;
	width:100px;
}
.tips {
	background-color:#FFFBC1;
	border:1px solid #F9B965;
	color:#E80211;
	padding:7px 17px;
	width:60%;
}
A:link{
	text-decoration:none;
}
A:active{
	text-decoration:none;
}
A:visited{
	text-decoration:none;
}
#pay_table {
    color:#DC8F19; 
    font-size:14px;
    line-height:25px;
    text-align:left;
}
</style>
<title>征战四方充值中心</title>
</head>
<body>
<center>
<div id="header">
	征战四方充值中心
</div>
<div id="content">
	<div id="payheader">
		尊敬的<b>${playerName}</b>，您的账户余额：<span class="yue">&nbsp;&nbsp;&nbsp;${money}</span>&nbsp;&nbsp;<a href=${payUrl} class="button" target="_blank">前往充值人人豆</a>
	</div>
	<div id="paycontent">
		<#if (hasTips)>
		<div class="tips"><label>${tips}</label></div>
		</#if>
		<form id="pay" onsubmit="return true;" action="gateway.action?command=yxRenren@pay" method="post">
			<br />
			<h2 class="ratio">兑换比例：1人民币=1人人豆，1人人豆=10金币</h2>
			<table width="600" cellpadding="0" cellspacing="0" id="pay_table">
           <tr>
              <td><input type='radio' name='money' id='money'  value='5000' />
					5000人人豆兑换50000个金币</td>
				<td><input type='radio' name='money' id='money'  value='100' />
					100人人豆兑换1000个金币</td>
			</tr>
          <tr>
              <td><input type='radio' name='money' id='money'  value='2000' />
					2000人人豆兑换20000个金币</td>
				<td><input type='radio' name='money' id='money'  value='50' />
					50人人豆兑换500个金币</td>
				
			</tr>
          <tr>
              <td><input type='radio' name='money' id='money'  value='1000' />
					1000人人豆兑换10000个金币</td>
				<td><input type='radio' name='money' id='money'  value='20' />
					20人人豆兑换200个金币</td>				
			</tr>
			<tr>
              <td><input type='radio' name='money' id='money'  value='500' />
					500人人豆兑换5000个金币</td>
				<td><input type='radio' name='money' id='money'  value='10' />
					10人人豆兑换100个金币</td>
			</tr>
			<tr>
              <td><input type='radio' name='money' id='money'  value='200' />
					200人人豆兑换2000个金币</td>
				<td><input type='radio' name='money' id='money'  value='5' />
					5人人豆兑换50个金币</td>
			</tr>
			</table>
			<br/><br/>
			<input id="pay__rrpay" name="method:rrpay" value="充值" class="button" type="submit">
		</form>
		<div id="tips">
			<h3>温馨提示:</h3>
	        	<ul>
	            	<li>1. 充值失败后，请多次重新尝试；</li>           
	            </ul>
		</div>
	</div>
</div>

</center>
</body>
</html>