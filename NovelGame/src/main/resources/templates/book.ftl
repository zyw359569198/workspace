<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>海贼之文斯莫克最新章节,海贼之文斯莫克免费在线阅读,txt2小说网</title>
<meta name="keywords" content="海贼之文斯莫克最新章节,海贼之文斯莫克免费在线阅读,老婆用我换糖的小说">
<meta name="description" content="txt2小说网提供《海贼之文斯莫克》最新章节在线免费阅读，每天更新《海贼之文斯莫克》最新章节，页面简洁无弹窗，访问速度快！">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<meta http-equiv="mobile-agent" content="format=html5; url=https://m.txt2.cc/">
<meta http-equiv="mobile-agent" content="format=xhtml; url=https://m.txt2.cc/">
<link rel="stylesheet" href="${request.contextPath}/css/index.css"/>
<link rel="shortcut icon" href="/favicon-txt2.ico">
<script type="text/javascript" src="${request.contextPath}/js/jquery.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/index.js"></script>
<script type="text/javascript" src="${request.contextPath}/js/me/hits.js"></script>
</head>
<body>
<#include "main.ftl" >
<#list bil as bookInfoData>
<div class="place" value="${bookInfoData.bookId}">
  当前位置：<a href="/" >txt2小说网</a> > <a href="/catagory/${bookInfoData.cataNameEn}" >${bookInfoData.cataName}</a> > ${bookInfoData.bookName}</div>
<div class="jieshao">
  <div class="lf">
    <img src="${request.contextPath }${bookInfoData.imageUrl}" onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${bookInfoData.bookName}">
  </div>
  <div class="rt">
    <h1>${bookInfoData.bookName}</h1>
    <div class="msg">
      <em>作者：<a target="_blank" href="/author/${bookInfoData.authorNameEn}/" >${bookInfoData.authorName}</a> </em>
      <#if bookInfoData.isCompletion==0>
      <em>状态：已完结 </em>
      <#else>
      <em>状态：连载中 </em>
      </#if>
      <em>更新时间：${bookInfoData.updateTime?string("yyyy-MM-dd")}</em>
          </div>
    <div class="info" style="padding-bottom: 5px;">
      <a href="#footer" rel="nofollow">直达底部</a><!-- <a href="javascript:Ajax.Request('',{onComplete:function(){alert(this.response.replace(/<br[^<>]*>/g,'\n'));}});" rel="nofollow">加入书架</a> -->
    </div>
    <div class="intro">   ${bookInfoData.bookDesc}</div>
  </div>
</div>
</#list>
<div class="mulu">
  <ul>
  <#list sil as storeData>
    <li><a href="/book/${storeData.bookNameEn}/${storeData.storeId}/">${storeData.storeName}</a></li>
   </#list>
 </ul>
</div>
<div class="guess">
  <h3>猜你喜欢</h3>
    <#list bkl as bookHitsData>
  <div class="image">
      <a href="/book/${bookHitsData.bookNameEn}/" title="${bookHitsData.bookName}"><img src="${bookHitsData.imageUrl}"  onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${bookHitsData.bookName} "></a><br>
      <span><a href="/book/${bookHitsData.bookNameEn}/" >${bookHitsData.bookName}</a></span>
  </div>
     </#list>
  </div>
<br>
<#include "foots.ftl" >
</body>
</html>