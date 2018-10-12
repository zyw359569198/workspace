<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>2018小说排行榜_txt2小说网</title>
<meta name="keywords" content="2018小说排行榜,2018热门小说排行榜,2018网络小说排行榜,经典小说排行,2018免费小说排行榜,2018完本小说推荐榜">
<meta name="description" content="txt2小说网小说排行榜提供最新、流行、经典、免费小说排行榜,涵盖:2018都市排行榜，2018玄幻排行榜，2018武侠排行榜，2018言情排行榜，2018穿越排行榜，2018网游排行榜，2018恐怖排行榜，2018科幻排行榜，2018修真排行榜，2018全本小说排行榜">
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta http-equiv="Cache-Control" content="max-age=300">
<meta http-equiv="Cache-Control" content="no-transform ">
<link rel="stylesheet" type="text/css" href="${request.contextPath}/css/m.css" ></head>
<body>
<#include "main.ftl">
<div class="sortcontent">
	<ul>
		<li><a id="dayvisit" href="/weekrank/index.htm">周点击榜</a></li>
		<li><a id="dayvisit" href="/monthrank/index.htm">月点击榜</a></li>
		<li><a id="dayvisit" href="/recommend/index.htm">总点击榜</a></li>
		<#list cgl as catagory>
		<#if (catagory_index >0) >
		<#if (catagory_index <10) >
		<li><a id="dayvisit" href="/mobile/catagory/${catagory.cataNameEn}/">${catagory.cataName}排行</a></li>
		</#if>
		</#if>
		</#list>
		<li><a id="dayvisit" href="/newrank/index.htm">最新入库</a></li>
		<li><a id="dayvisit" href="/updaterank/index.htm">最近更新</a></li>
		<li><a id="dayvisit" href="/full/index.htm">全本排行</a></li>
	</ul>
</div>
 <#include "foots.ftl">
    </body>
    <div></div>
</html>