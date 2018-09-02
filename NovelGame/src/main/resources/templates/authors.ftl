<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>网络小说作家排行_txt2小说网</title>
<meta name="keywords" content="txt2小说网网络小说作家排行,txt2小说网">
<meta name="description" content="txt2小说网收录当前最火最活跃的小说作家，提供2018年网络小说作家排行榜，是广大书友最值得收藏的小说阅读网，提供高速无弹窗的阅读体验，小说质量高。">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<meta http-equiv="mobile-agent" content="format=html5; url=https://m.txt2.cc/">
<meta http-equiv="mobile-agent" content="format=xhtml; url=https://m.txt2.cc/">
<link rel="stylesheet" href="${request.contextPath}/css/index.css"/>
<link rel="shortcut icon" href="/favicon-txt2.ico">
<script type="text/javascript" src="${request.contextPath}/js/jquery.js"></script>
</head>
<body>
<#include "main.ftl" >
<div class="ops_two">
    <div class="ops_lf">本站推出作者专栏，排序按本站热度排名，如有争议，请联系本站处理。</div><div class="rt"></div>
</div>
<div class="author-list">
	<ul class="title"><li class="num">序号</li><li class="author">作者</li><li class="bookname">代表作品</li><li class="update"></li></ul>
	<#assign i=1>
	<#list bil as book>
	<ul>
			<li class="num">${i}</li>
			<li class="author"><a href="/author/${book.authorNameEn}/"  target="_blank">${book.authorName}</a></li>
			<li class="bookname"><a class="bn vip" target="_blank" href="/book/${book.bookNameEn}/">${book.bookName}</a></li>
			<li></li>
</ul>
<#assign i=i+1>
</#list>
</div>
<div class="pagelink" id="pagelink"><div><span class="current">1</span><a class="num" href="../index.php-m=Home&c=Author&a=index&p=2.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=2">2</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=3.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=3">3</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=4.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=4">4</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=5.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=5">5</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=6.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=6">6</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=7.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=7">7</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=8.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=8">8</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=9.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=9">9</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=10.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=10">10</a><a class="num" href="../index.php-m=Home&c=Author&a=index&p=11.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=11">11</a><a class="next" href="../index.php-m=Home&c=Author&a=index&p=2.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=2">下一页</a><a class="end" href="../index.php-m=Home&c=Author&a=index&p=12.htm" tppabs="http://localhost:8080/index.php?m=Home&c=Author&a=index&p=12">末页</a><li class="rows">共<b>240</b>条记录&nbsp;第<b>1</b>页/共<b>12</b>页</li></div></div>
<#include "foots.ftl" >
</body>
</html>