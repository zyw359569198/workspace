<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>穿越小说_txt2小说网</title>
<meta name="keywords" content="穿越小说,好看的穿越小说,2018年穿越小说排行榜">
<meta name="description" content="txt2小说网提供最新最快的穿越小说，网站收录了当前最好看的穿越小说，是广大书友值得收藏的穿越小说阅读网。">
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
<div class="place">当前位置：<a href="">txt2小说网</a> > <h2>穿越</h2></div>
<div class="fengtui">
<#list bcl as book>
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
    </div>
<div class="booklist">
    <h1>穿越</h1>
    <ul>
        <li class="t"><span class="sm">小说名称</span><span class="zj">最新章节</span><span class="zz">作者</span><span class="sj">更新</span><span class="zt">状态</span></li>
        <#list bul as store>
        <li><span class="sm"><a href="/book/${store.bookNameEn}/"><b>${store.bookName}</b></a></span><span class="zj">&nbsp;<a href="/book/${store.bookNameEn}/${store.orderIndex}/">${store.storeName}</a></span><span class="zz"><a target="_blank" href="/author/${store.authorNameEn}/">${store.authorName}</a></span><span class="sj">08/07</span><span class="zt">连载中</span></li>
 		</#list>
 </ul>     
</div>
<br>
<div class="pagelink" id="pagelink"><div><span class="current">1</span><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=2">2</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=3">3</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=4">4</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=5">5</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=6">6</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=7">7</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=8">8</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=9">9</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=10">10</a><a class="num" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=11">11</a><a class="next" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=2">下一页</a><a class="end" href="/index.php?m=Home&c=Book&a=clist&pinyin=chuanyue&p=90">末页</a><li class="rows">共<b>2155</b>条记录&nbsp;第<b>1</b>页/共<b>90</b>页</li></div></div>
<#include "foots.ftl" >
<script type="text/javascript" src="/Public/js/push.js?ver=2.05"></script>
</body>
</html>