<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>小说分类_txt2小说网</title>
<meta name="keywords" content="小说分类,txt2小说网">
<meta name="description" content="txt2小说网收录了都市，玄幻，其它，武侠，言情，穿越，网游，恐怖，科幻，历史，耽美，爱情，修真等各种类型的小说，提供高质量的小说最新章节！">
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta http-equiv="Cache-Control" content="max-age=300">
<meta http-equiv="Cache-Control" content="no-transform ">
<link rel="stylesheet" type="text/css" href="${request.contextPath}/css/m.css" >
</head>
<body>
 <#include "main.ftl">
<div class="sort-content">
<#list cgl as catagory>
<#if catagory.cataId=="0">
<#else>
<#if (catagory_index-1)%2 == 0 >
    <ul>
    </#if>
        <li class="prev"><a href="/mobile/catagory/${catagory.cataNameEn}/">${catagory.cataName}</a></li>
  <#if catagory_index%2==0 >  
    </ul>
</#if>
</#if>
</#list>
</div> 
<#include "foots.ftl">
    </body>
    <div></div>
</html>