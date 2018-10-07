<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<#list sdl as storeData>
<title>${storeData.storeName}-txt2小说网</title>
<meta name="keywords" content="${storeData.storeName},${storeData.authorName},txt2小说网">
<meta name="description" content="txt2小说网提供了《${storeData.bookName}》干净清爽的最新文字章节：${storeData.storeName}在线免费阅读。">
</#list>
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<meta http-equiv="mobile-agent" content="format=html5; url=https://m.txt2.cc">
<meta http-equiv="mobile-agent" content="format=xhtml; url=https://m.txt2.cc">
<link rel="stylesheet" href="${request.contextPath}/css/index.css"/>
<link rel="shortcut icon" href="/favicon-txt2.ico">
<script type="text/javascript" src="${request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/index.js"></script>
</head>
<body>
<div class="top">
  <div class="main">
    <div class="lf">
      <div style="color: rgb(147, 0, 255);">请记住网址：<b style="color: rgb(255, 0, 186);">txt2.cc</b>，欢迎常来<b style="color: rgb(255, 0, 186);">txt2小说网</b>阅读小说</div>
    </div>
    <div class="rt">
      <a href="/search/" target="_blank"><font color="#FF0000">站内搜索</font></a> |
      <!-- <a href="/index.php?m=Home&c=Feedback&a=add" target="_blank">反馈留言</a> | -->
      <a href="/mobile" target="_blank">手机版</a> | 
      <a href="javascript:void(0);" onclick="AddFavorite('txt2小说网',location.href)" target="_self" rel="nofollow">收藏本站</a>
    </div>
  </div>
</div>
<#list sdl as storeData>
<div class="read_t">
  <span>
  </span>当前位置：<a href="/">txt2小说网</a> > <a href="/catagory/${storeData.cataNameEn}/">${storeData.cataName}</a> > <a href="/book/${storeData.bookNameEn}/">${storeData.bookName}</a> >  ${storeData.storeName}</div>

<div class="novel" style="border-top: 2px solid #208181;">
  <h1> ${storeData.storeName}</h1>
  <#list sddl as storeDataItem>
  <div class="pereview">
  <#if storeData.preStoreId == "0">
    <#else>
        <a href="/book/${storeData.bookNameEn}/${storeData.preStoreId}/" target="_top" title="">上一章</a>
    </#if>
     <a class="back" href="/book/${storeData.bookNameEn}/" target="_top">返回目录</a>
       <#if storeData.nextStoreId == "0">
    <#else>
    <a  href="/book/${storeData.bookNameEn}/${storeData.nextStoreId}/"   target="_top" title="" >下一章</a>
    </#if>
      </div>
  <div class="yd_text2">
  ${storeDataItem.vStoreContent}
   </div>
  <div class="pereview">
<#if storeData.preStoreId == "0">
    <#else>
        <a href="/book/${storeData.bookNameEn}/${storeData.preStoreId}/" target="_top" title="">上一章</a>
    </#if>
     <a class="back" href="/book/${storeData.bookNameEn}/" target="_top">返回目录</a>
       <#if storeData.nextStoreId == "0">
    <#else>
    <a  href="/book/${storeData.bookNameEn}/${storeData.nextStoreId}/"   target="_top" title="" >下一章</a>
    </#if>
</div>
</#list>
</#list>
<#include "foots.ftl" >
</body>
</html>