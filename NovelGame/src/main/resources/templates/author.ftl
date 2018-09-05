<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>艾亭的小说专辑_txt2小说网</title>
<meta name="keywords" content="艾亭,网络小说作家专辑,艾亭的小说专栏">
<meta name="description" content="txt2小说网收录了当前网络上最火的网络小说作者艾亭的小说，提供了作者艾亭的小说专栏，艾亭的代表作品为《》">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<meta http-equiv="mobile-agent" content="format=html5; url=https://m.txt2.cc">
<meta http-equiv="mobile-agent" content="format=xhtml; url=https://m.txt2.cc">
<link rel="stylesheet" href="${request.contextPath}/css/index.css"/>
<link rel="shortcut icon" href="/favicon-txt2.ico">
<script type="text/javascript" src="${request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/index.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/jqPaginator.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/me/author.js"></script>
</head>
<body>
<#include "main.ftl" >
<#assign i=0>
<#list abl.list as bookInfoData>
<#if i==0>
<div class="ops_two">
    <div class="ops_lf">特约作者 <em>${bookInfoData.authorName}</em> 相关作品 <em>${abl.list?size}</em> 部</div><div class="rt"></div>
</div>
<#assign i=i+1>
</#if>
<div class="ops_cover">
    <div class="block">
        <div class="block_img">
            <a href="/book/${bookInfoData.bookNameEn}/" target="_blank"><img src="${request.contextPath}${bookInfoData.imageUrl}" alt="${bookInfoData.bookName}" onerror="this.src='${request.contextPath}/images/nocover.jpg'" alt=""/></a>
        </div>
        <div class="block_txt">
            <h2><a href="/book/${bookInfoData.bookNameEn}/" target="_blank">${bookInfoData.bookName}</a></h2>
            <p></p>
            <p>作者：${bookInfoData.authorName}</p>
            <p>类型：${bookInfoData.cataName}</p>
            <p>简介：${bookInfoData.bookDesc}</p>
        </div>
    </div>
    </div>
  </#list>
<#list abl.list as book>
<#if book_index==0>
<div class="pagelink" id="pagelink" value=${abl.total} name="${book.authorNameEn}">
</div>
</#if>
</#list>
<div class="pagelink" id="page">
</div>
<#include "foots.ftl" >
</body>
</html>