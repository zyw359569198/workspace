
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

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
<div class="toptab"><span class="active">搜索结果</span></div>
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
<div class="listpage">
    <span class="left"><a class="before">上一页</a></span><span class="middle"><select name="pageselect" onchange="self.location.href=options[selectedIndex].value"><option value="/weekrank/?p=1" selected="selected">第1页</option><option value="/weekrank/?p=2" >第2页</option><option value="/weekrank/?p=3" >第3页</option><option value="/weekrank/?p=4" >第4页</option><option value="/weekrank/?p=5" >第5页</option><option value="/weekrank/?p=6" >第6页</option><option value="/weekrank/?p=7" >第7页</option><option value="/weekrank/?p=8" >第8页</option><option value="/weekrank/?p=9" >第9页</option></select></span><span class="right"><a class="onclick" href="/weekrank/?p=2">下一页</a></span></div>
<#include "foots.ftl">
    </body>
    <div></div>
</html>