<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>全本小说免费在线阅读-txt2小说网</title>
<meta name="keywords" content="好看的全本小说,全本小说免费在线阅读,大结局小说">
<meta name="description" content="txt2小说网收录了大量好看的全本小说，提供全本小说免费阅读。页面清爽无弹窗，访问速度快！">
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta http-equiv="Cache-Control" content="max-age=300">
<meta http-equiv="Cache-Control" content="no-transform ">
<link rel="stylesheet" type="text/css" href="${request.contextPath}/css/m.css" >
<script type="text/javascript" src="${request.contextPath}/js/mobile/jq.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/mobile/full.js"></script>
</head>
<body>
<#include "main.ftl">
<div class="toptab"><span class="active">全本小说</span></div>
<#list bul.list as store>
<div class="bookbox">
    <div class="bookimg">
    <a href="/mobile/book/${store.bookNameEn}/"  title="${store.bookName}"><img src="${request.contextPath }${store.imageUrl}"  width="78" onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${store.bookName}"></a>
    </div>
    <div class="bookinfo">
    <a href="/mobile/book/${store.bookNameEn}/"  class="iTit"><h2 class="bookname">${store.bookName}</h2></a>
    <p class="author">作者：<a href="/mobile/author/${store.authorNameEn}/" >${store.authorName}</a></p>
    <p class="update"><span>更新至：</span><a href="/mobile/book/${store.bookNameEn}/${store.storeId}/" >${store.storeName}</a></p>
    <p class="intro_line"><span>简介：</span>
        <#if  store.bookDesc?length gt 50>
        ${store.bookDesc?substring(0,50)}...
        <#else>
        ${store.bookDesc}
        </#if>
    </p>
    </div>
</div>
</#list>
    <div class="listpage" value=${bul.pages?replace(",","")}>
    <span class="left"><a href="javascript:void(0);" class="before" value=1 onclick="changeClick(this,-1)">上一页</a></span>
    <span class="middle">
    <select id="pageselect" onchange="changeClick(options[selectedIndex].value,-999)">
    <#list 1..(bul.pages?replace(",","")) ?eval as i>
    <option value="${i}" >第${i}页</option>
    </#list>
    </select>
    </span>
    <span class="right"><a href="javascript:void(0);" class="onclick" value=1 onclick="changeClick(this,1)">下一页</a></span></div>
</div>
 <#include "foots.ftl">
    </body>
    <div></div>
</html>