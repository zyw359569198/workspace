<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title></title>
<meta name="keywords" content="">
<meta name="description" content="">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<meta http-equiv="mobile-agent" content="format=html5; url=https://m.txt2.cc">
<meta http-equiv="mobile-agent" content="format=xhtml; url=https://m.txt2.cc">
<link rel="stylesheet" href="${request.contextPath}/css/index.css"/>
<link rel="shortcut icon" href="/favicon-txt2.ico">
<script type="text/javascript" src="${request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/index.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/jqPaginator.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/me/search.js"></script>
</head>
<body>
<#include "main.ftl" >
<#if abl.list?size gt 0>
<#assign i=0>
<#list abl.list as bookInfoData>
<#if i==0>
<div class="ops_two">
    <div class="ops_lf">与 <em>${bookInfoData.authorName}</em> 相关作品 <em>${abl.list?size}</em> 部</div><div class="rt"></div>
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
  <#else>
 <div class="ops_two">
    <div class="ops_lf">与 <em></em> 相关作品 <em>0</em> 部</div><div class="rt"></div>
</div>

<div class="ops_cover">
        <div class="ops_no">对不起本站还没有这本书，搜索时宁可少字也不要错字。</div></div></div>
<div class="pagelink" id="pagelink"></div>
  </#if>
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