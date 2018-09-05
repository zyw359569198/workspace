<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>txt2小说网推荐小说,2018年小说排行榜,txt2小说网</title>
<meta name="keywords" content="txt2小说网推荐小说,2018年小说排行榜,txt2小说网">
<meta name="description" content="txt2小说网是广大书友最值得收藏的小说阅读网，网站提供了当前最好看的小说推存，免费提供高质量的小说。提供高速无弹窗的阅读体验。">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<meta http-equiv="mobile-agent" content="format=html5; url=https://m.txt2.cc">
<meta http-equiv="mobile-agent" content="format=xhtml; url=https://m.txt2.cc">
<link rel="stylesheet" href="${request.contextPath}/css/index.css"/>
<link rel="shortcut icon" href="/favicon-txt2.ico">
<script type="text/javascript" src="${request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/index.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/jqPaginator.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/me/recommend.js"></script>
</head>
<body>
<#include "main.ftl" >
<div class="place">当前位置：<a href="/">txt2小说网</a> > <h2>好书推荐</h2></div>
<div class="fengtui">
<#list bil as book>
    <dl>
      <dt><a href="/book/${book.bookNameEn}/"><img src="${request.contextPath }${book.imageUrl}" alt="${book.bookName}" onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt=""/></a></dt>
       <dd>
        <h3><a href="/book/${book.bookNameEn}/">${book.bookName}</a></h3>
        <span><a target="_blank" href="/author/${book.authorNameEn}/">${book.authorName}</a></span>
		<#if  book.bookDesc?length gt 50>
        <p>&emsp;&emsp;${book.bookDesc?substring(0,50)}...</p>
        <#else>
        <p>&emsp;&emsp;${book.bookDesc}...</p>
        </#if>      </dd>
    </dl>
     </#list>
     <#if bil?size==0>
     <div class="place">当前位置：<a href="/">txt2小说网</a> > <h2></h2></div>
     <div class="fengtui">
     </#if>
    </div>
<div class="booklist">
    <h1></h1>
 <ul>
        <li class="t"><span class="sm">小说名称</span><span class="zj">最新章节</span><span class="zz">作者</span><span class="sj">更新</span><span class="zt">状态</span></li>
        <#list bul.list as store>
        <li><span class="sm"><a href="/book/${store.bookNameEn}/"><b>${store.bookName}</b></a></span><span class="zj">&nbsp;<a href="/book/${store.bookNameEn}/${store.storeId}/">${store.storeName}</a></span><span class="zz"><a target="_blank" href="/author/${store.authorNameEn}/">${store.authorName}</a></span>
        <#list store.createTime?split("-") as item>
      <#if item_index==1>
      <span class="sj">${item} <#elseif item_index==2>/${item}</span>
      </#if>
      </#list>
      <#if store.isCompletion==0>
      <span class="zt">已完结</span>
      <#else>
      <span class="zt">连载中</span>
      </#if>
        </li>
 		</#list>
 </ul> 
 </div>
<br>
<#list bul.list as store>
<#if store_index==0>
<div class="pagelink" id="pagelink" value=${bul.total}">
</div>
</#if>
</#list>
<div class="pagelink" id="page">
</div>
<#include "foots.ftl" >
</body>
</html>