<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<#list bul.list as store>
<#if store_index==0>
<title>${store.cataName}小说,好看的${store.cataName}小说,2018年${store.cataName}小说排行榜,txt2小说网</title>
<meta name="keywords" content="${store.cataName}小说,好看的${store.cataName}小说,2018年${store.cataName}小说排行榜">
<meta name="description" content="txt2小说网提供最新最快的${store.cataName}小说，网站收录了当前最好看的${store.cataName}小说，是广大书友值得收藏的${store.cataName}小说阅读网。">
</#if>
</#list>
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta http-equiv="Cache-Control" content="max-age=300">
<meta http-equiv="Cache-Control" content="no-transform ">
<link rel="stylesheet" type="text/css" href="${request.contextPath}/css/m.css" >
<script type="text/javascript" src="${request.contextPath}/js/mobile/jq.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/mobile/catagory.js"></script>
</head>
<body>
<#include "main.ftl">
<#list bul.list as store>
<#if store_index==0>
<div class="toptab"  name="${store.cataNameEn}"><span class="active">${store.cataName}小说</span></div>
<div id="div_test">
</#if>
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
 </div>
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