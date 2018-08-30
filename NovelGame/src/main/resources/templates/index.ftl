<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>txt2小说网_最新最全最好看的免费小说网站</title>
<meta name="keywords" content="txt2小说网,免费小说,无弹窗广告小说,完本小说">
<meta name="description" content="txt2小说网提供都市小说、玄幻小说、其它小说、武侠小说、言情小说、穿越小说、网游小说、恐怖小说、科幻小说、历史小说、耽美小说、爱情小说、修真小说在线阅读。无弹窗广告，页面简洁，访问速度快。">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<meta http-equiv="mobile-agent" content="format=html5; url=https://m.txt2.cc">
<meta http-equiv="mobile-agent" content="format=xhtml; url=https://m.txt2.cc">
<link rel="stylesheet" href="${request.contextPath }/css/index.css"/>
<link rel="shortcut icon" href="/favicon-txt2.ico">
<script type="text/javascript" src="${request.contextPath }/js/jquery.js"></script>
<script type="text/javascript" src="${request.contextPath }/js/index.js"></script>
</head>
<body>
<#include "main.ftl" >
<div class="fengtui">
<#list bkl as book>
  <dl>
      <dt><a href="/book/${book.bookNameEn}/"><img src="${request.contextPath }${book.imageUrl}" alt="${book.bookName}" onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt=""/></a></dt>
      <dd>
        <h3><a href="/book/${book.bookNameEn}/">${book.bookName}</a></h3>
        <span><a target="_blank" href="/author/${book.authorNameEn}/">${book.authorName}</a></span>        
        <#if  book.bookDesc?length gt 50>
        <p>&emsp;&emsp;${book.bookDesc?substring(0,50)}...</p>
        <#else>
        <p>&emsp;&emsp;${book.bookDesc}...</p>
        </#if>
      </dd>
 </dl>
 </#list>
 </div>
 <#assign i=0>
 <#list tjl as catagoryItem>
 <#if i==0||i==4>
<div class="tuijian">
 <ul class="l">
 <#else>
  <ul>
</#if>
   <#list catagoryItem as cbook>
   <#if cbook_index==0>
    <li class="t"><h2><a href="/catagory/${cbook.cataNameEn}/">${cbook.cataName}</a></h2></li>
    </#if>
    <li><a href="/book/${cbook.bookNameEn}/">${cbook.bookName}</a>/<a target="_blank" href="/author/${cbook.authorNameEn}/">${cbook.authorName}</a></li>
</#list>
    </ul>
 <#if i==(tjl?size)||i==3||i==7>
</div>
</#if>
 <#assign i=i+1>
 </#list>
<div class="main">
  <div class="lastupdate">
    <h2>最新更新</h2>
    <ul>
      <li class="t"><span class="lx">类型</span><span class="sm">书名</span><span class="zj">最新章节</span><span class="zz">作者</span><span class="sj">时间</span></li>
      <#list bul as bookInfo>
      <li><span class="lx">[${bookInfo.cataName}]</span>
      <span class="sm"><a href="/book/${bookInfo.bookNameEn}/">${bookInfo.bookName}</a></span>
      <span class="zj">&nbsp;<a href="/book/${bookInfo.bookNameEn}/${bookInfo.orderIndex}/">${bookInfo.storeName}</a></span>
      <span class="zz"><a target="_blank" href="/author/${bookInfo.authorNameEn}/">${bookInfo.authorName}</a></span>
      <#list bookInfo.createTime?split("-") as item>
      <#if item_index==1>
      <span class="sj">${item} <#elseif item_index==2>/${item}</span></li>
      </#if>
      </#list>
      </#list>
      </ul>
  </div>
  <div class="postdate">
    <h2>最新小说</h2>
    <ul>
    <#list bcl as bookCata>
      <li><span class="lx">[${bookCata.cataName}]</span>
      <span class="rx"><a href="/book/${bookCata.bookNameEn}/">${bookCata.bookName}</a></span></li>
      </#list>
      </ul>
  </div>
</div>
<#include "link.ftl" >
<#include "foots.ftl" >
</body>
</html>