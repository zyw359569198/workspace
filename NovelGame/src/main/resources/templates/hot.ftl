<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>2018年最新小说推荐-txt2小说网</title>
<meta name="keywords" content="2018年最新小说推荐,2018年最新小说免费在线阅读">
<meta name="description" content="txt2小说网收录了大量好看的小说，更新最快。页面清爽无弹窗，访问速度快！">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<meta http-equiv="mobile-agent" content="format=html5; url=https://m.txt2.cc">
<meta http-equiv="mobile-agent" content="format=xhtml; url=https://m.txt2.cc">
<link rel="stylesheet" href="${request.contextPath}/css/index.css"/>
<link rel="shortcut icon" href="/favicon-txt2.ico">
<script type="text/javascript" src="${request.contextPath}/js/jquery.js"></script>
</head>
<body>
<#include "main.ftl" >
<div class="place">当前位置：<a href="/">txt2小说网</a> > <h2>新书抢鲜</h2></div>
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
    <h1>最新小说</h1>
 <ul>
        <li class="t"><span class="sm">小说名称</span><span class="zj">最新章节</span><span class="zz">作者</span><span class="sj">更新</span><span class="zt">状态</span></li>
        <#list bul as store>
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
<div class="pagelink" id="pagelink"><div><span class="current">1</span><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=2">2</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=3">3</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=4">4</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=5">5</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=6">6</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=7">7</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=8">8</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=9">9</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=10">10</a><a class="num" href="/index.php?m=Home&c=Book&a=hot&p=11">11</a><a class="next" href="/index.php?m=Home&c=Book&a=hot&p=2">下一页</a><a class="end" href="/index.php?m=Home&c=Book&a=hot&p=1398">末页</a><li class="rows">共<b>27941</b>条记录&nbsp;第<b>1</b>页/共<b>1398</b>页</li></div></div>
<#include "foots.ftl" >
</body>
</html>