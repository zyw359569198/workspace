<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>推荐小说_2018年小说排行榜_txt2小说网</title>
<meta name="keywords" content="txt2小说网推荐小说,2018年小说排行榜,txt2小说网">
<meta name="description" content="txt2小说网网站提供了当前最好看的小说，是广大书友最值得收藏的小说阅读网，提供高速无弹窗的阅读体验，小说质量高">
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta http-equiv="Cache-Control" content="max-age=300">
<meta http-equiv="Cache-Control" content="no-transform ">
<link rel="stylesheet" type="text/css" href="${request.contextPath}/css/m.css" >
<script type="text/javascript" src="${request.contextPath}/js/mobile/jq.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/mobile/recommend.js"></script>
</head>
<body>
  <#include "main.ftl">
<div class="toptab"><span class="active">推荐小说</span></div>
<div id="div_test">
<#list bkl.list as book>
<div class="bookbox">
    <div class="bookimg">
    <a href="/mobile/book/${book.bookNameEn}/"  title="${book.bookName}"><img src="${request.contextPath }${book.imageUrl}"  width="78" onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${book.bookName}"></a>
    </div>
    <div class="bookinfo">
    <a href="/mobile/book/${book.bookNameEn}/"  class="iTit"><h2 class="bookname">${book.bookName}</h2></a>
    <p class="author">作者：<a href="/mobile/author/${book.authorNameEn}/" >${book.authorName}</a></p>
    <p class="update"><span>更新至：</span><a href="/mobile/book/${book.bookNameEn}/${(book.lastStoreId)!''}/">${(book.storeName)!''}</a></p>
    <p class="intro_line"><span>简介：</span>
    <#if  book.bookDesc?length gt 50>
        ${book.bookDesc?substring(0,50)}...
        <#else>
        ${book.bookDesc}
        </#if>
          </p>
    </div>
    </div>
    </#list>
    </div>
    <div class="listpage" value=${bkl.pages?replace(",","")}>
    <span class="left"><a href="javascript:void(0);" class="before" value=1 onclick="changeClick(this,-1)">上一页</a></span>
    <span class="middle">
    <select id="pageselect" onchange="changeClick(options[selectedIndex].value,-999)">
    <#list 1..(bkl.pages?replace(",","")) ?eval as i>
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