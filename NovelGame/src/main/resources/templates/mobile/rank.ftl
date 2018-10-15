
<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>2018小说排行榜_txt2小说网</title>
<meta name="keywords" content="2018小说排行榜,2018热门小说排行榜,2018网络小说排行榜,经典小说排行,2018免费小说排行榜,2018完本小说推荐榜">
<meta name="description" content="txt2小说网小说排行榜提供最新、流行、经典、免费小说排行榜,涵盖:2018都市排行榜，2018玄幻排行榜，2018武侠排行榜，2018言情排行榜，2018穿越排行榜，2018网游排行榜，2018恐怖排行榜，2018科幻排行榜，2018修真排行榜，2018其它排行榜，2018全本小说排行榜">
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
<#if (hits == "week") >
<div class="toptab"><span class="active">周排行榜</span></div>
<#elseif (hits == "month") >
<div class="toptab"><span class="active">月排行榜</span></div>
<#elseif (hits == "newrank") >
<div class="toptab"><span class="active">最新入库</span></div>
<#elseif (hits == "updaterank") >
<div class="toptab"><span class="active">最近更新</span></div>
<#else>
<div class="toptab"><span class="active">总排行榜</span></div>
</#if>
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