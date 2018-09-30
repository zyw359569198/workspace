<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>txt2小说网_最新最全最好看的免费小说网站</title>
<meta name="keywords" content="txt2小说网,免费小说,无弹窗广告小说,完本小说">
<meta name="description" content="txt2小说网提供都市小说、玄幻小说、其它小说、武侠小说、言情小说、穿越小说、网游小说、恐怖小说、科幻小说、历史小说、耽美小说、爱情小说、修真小说在线阅读。无弹窗广告，页面简洁，访问速度快。">
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta http-equiv="Cache-Control" content="max-age=300">
<meta http-equiv="Cache-Control" content="no-transform ">
<link rel="stylesheet" type="text/css" href="${request.contextPath}/css/m.css">
</head>
<body>
 <#include "main.ftl">
<div class="article">
    <h2 class="title">
        <span><a href="/mobile/recommend">推荐</a></span>
        <a href="/mobile/recommend"  title="推荐小说">更多...</a>
    </h2>
    <div class="block">
    <#list bkl as book>
    <#if book_index==0>
        <div class="block_img">
            <a href="/mobile/book/${book.bookNameEn}/"  title="${book.bookName}"><img height="100" width="80" src="${request.contextPath }${book.imageUrl}"  onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${book.bookName}"></a>
        </div>
        <div class="block_txt">
            <h2><a href="/mobile/book/${book.bookNameEn}/" >${book.bookName}</a></h2>
            <p>作者：<a href="/mobile/author/${book.authorNameEn}/" >${book.authorName}</a></p>
            <p><a href="/mobile/book/${book.bookNameEn}/"  title="${book.bookName}小说介绍">
            <#if  book.bookDesc?length gt 200>
        ${book.bookDesc?substring(0,200)}...
        <#else>
        ${book.bookDesc}
        </#if>
            </a></p>
        </div>
        <div style="clear:both"></div>
        <#else>
        <ul>
            <li><a href="/mobile/book/${book.bookNameEn}/" >${book.bookName}</a>/<a class="list-a-text" href="/mobile/author/${book.authorNameEn}/" >${book.authorName}</a></li>
        </ul>
        </#if>
        </#list>
    </div>
    </div>
<#list tjl as catagoryItem>
<div class="article">
<#list catagoryItem as cbook>
<#if cbook_index==0>
    <h2 class="title">
        <span><a href="/mobile/catagory/${cbook.cataNameEn}/" >${cbook.cataName}</a></span>
        <a href="/mobile/catagory/${cbook.cataNameEn}/"  title="${cbook.cataName}小说">更多...</a>
    </h2>
    <div class="block">
    <div class="block_img">
            <a href="/mobile/book/${cbook.bookNameEn}/"  title="${cbook.bookName}"><img height="100" width="80" src="${request.contextPath }${cbook.imageUrl}"  onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${cbook.bookName}"></a>
        </div>
        <div class="block_txt">
            <h2><a href="/mobile/book/${cbook.bookNameEn}/" >${cbook.bookName}</a></h2>
            <p>作者：<a href="/mobile/author/${cbook.authorNameEn}/" >${cbook.authorName}</a></p>
            <p><a href="/mobile/book/${cbook.bookNameEn}/"  title="${cbook.bookName}小说介绍">
            <#if  cbook.bookDesc?length gt 200>
        ${cbook.bookDesc?substring(0,200)}...
        <#else>
        ${cbook.bookDesc}
        </#if>
            </a></p>
        </div>
        <div style="clear:both"></div>
        <#else>
        <ul>
            <li><a href="/mobile/book/${cbook.bookNameEn}/" >${cbook.bookName}</a>/<a class="list-a-text" href="/mobile/author/${cbook.authorNameEn}/" >${cbook.authorName}</a></li>
        </ul>
        </#if>
        </#list>
    </div>
</div>
</#list>
<div class="article">
<h2 class="title"><span>最近更新</span></h2>
<div class="block">
<ul>
<#list bul as bookInfo>
<li><a href="/mobile/book/${bookInfo.bookNameEn}/"  title="${bookInfo.bookName}">${bookInfo.bookName}<span>/${bookInfo.storeName}</span></a></li>
</#list>
</ul>
</div>
</div>
 <#include "foots.ftl">
    </body>
    <div></div>
</html>