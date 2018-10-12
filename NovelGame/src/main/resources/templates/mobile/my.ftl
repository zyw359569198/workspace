<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title></title>
<meta name="keywords" content="">
<meta name="description" content="">
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta http-equiv="Cache-Control" content="max-age=300">
<meta http-equiv="Cache-Control" content="no-transform ">
<link rel="stylesheet" type="text/css" href="${request.contextPath}/css/m.css" >
</head>
<body>
 <#include "main.ftl">
  <div class="toptab"><span class="active">最近阅读过的小说</span></div>
<div class="wrap">
        &nbsp;
    <div id="viewlog-list" class="block">   
        <ul>
        <#assign i=1>
        <#list cook as ck>
        <li><span>${i}</span>&nbsp;&nbsp;<a href="/mobile/book/${ck.bookNameEn}/${ck.storeId}/" ><em>${ck.bookName} - ${ck.storeName}</em></a></li>
        <#assign i=i+1>
        </#list>
        <#if cook?size == 0>
        对不起，您还没有阅读小说！
        </#if>
        </ul>
    </div>
         &nbsp;
</div>
 <#include "foots.ftl">
    </body>
    <div></div>
</html>