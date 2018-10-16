<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<#list abl as book>
<#if book_index==0>
<title>${book.authorName}的小说专辑_txt2小说网</title>
<meta name="keywords" content="${book.authorName},网络小说作家专辑,${book.authorName}的小说专栏">
<meta name="description" content="txt2小说网收录了当前网络上最火的网络小说作者${book.authorName}的小说，提供了作者${book.authorName}的小说专栏，${book.authorName}的代表作品为《${book.bookName}》">
</#if>
</#list>
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
<#list abl as book>
<#if book_index==0>
<div class="toptab"><span class="active">特约作者“${book.authorName}”的专栏</span></div>
</#if>
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
    <div class="listpage"></div>
 <#include "foots.ftl">
    </body>
    <div></div>
</html>