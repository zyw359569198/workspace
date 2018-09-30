 <!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>不良痞妻，束手就寝最新章节,不良痞妻，束手就寝免费在线阅读,txt2小说网</title>
<meta name="keywords" content="不良痞妻，束手就寝最新章节,不良痞妻，束手就寝免费在线阅读,玉司司的小说">
<meta name="description" content="txt2小说网提供《不良痞妻，束手就寝》最新章节在线免费阅读，每天更新《不良痞妻，束手就寝》最新章节，页面简洁无弹窗，访问速度快！">
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta http-equiv="Cache-Control" content="max-age=300">
<meta http-equiv="Cache-Control" content="no-transform ">
<link rel="stylesheet" type="text/css" href="${request.contextPath}/css/m.css" >
</head>
<body>
 <#include "main.ftl" >
 <div class="cover">
<#list bil as bookInfoData>
    <div class="block">
        <div class="block_img2"><img src="${request.contextPath }${bookInfoData.imageUrl}"  border="0" width="92" height="116" onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${bookInfoData.bookName}"></div>
        <div class="block_txt2">
            <p></p><h2>${bookInfoData.bookName}</h2><p></p>
            <p>作者：<a href="/mobile/author/${bookInfoData.authorNameEn}/" >${bookInfoData.authorName}</a></p>
            <p>分类：<a href="/mobile/catagory/${bookInfoData.cataNameEn}/" >${bookInfoData.cataName}小说</a></p>           
       <#if bookInfoData.isCompletion==0>
      <p>状态：已完结</p>
      <#else>
     <p>状态：连载中</p>
      </#if>
            <p>更新：${bookInfoData.updateTime?string("yyyy-MM-dd")}</p>
            <p>最新：<a href="/mobile/book/${bookInfoData.bookNameEn}/${bookInfoData.lastStoreId}/"  title="${bookInfoData.storeName}">${bookInfoData.storeName}</a></p>
        </div>
    </div>
    <div class="intro">简介</div>
    <div class="intro_info">
        <p>${bookInfoData.bookDesc}</p>
    </div>
    <div class="intro">正文</div>
    <ul class="chapter">
    <#list sil as storeData>
        <li><a href="/mobile/book/${storeData.bookNameEn}/${storeData.storeId}/" >${storeData.storeName}</a></li>
    </#list>
    </ul>
    </#list>
    <div class="listpage"><span class="left"><a class="before">上一页</a></span><span class="middle"><select name="pageselect" onchange="self.location.href=options[selectedIndex].value"><option value="/book/blpq_ssjq/?&p=1" selected="selected">第1页 - 第2章 按我说的做</option><option value="/book/blpq_ssjq/?&p=2" >第2页 - 第102章 要杀要剐给个痛快</option></select></span><span class="right"><a class="onclick" href="-&p=2.htm" tppabs="/book/blpq_ssjq/?&p=2">下一页</a></span></div>
</div>
 <#include "foots.ftl">
    </body>
    <div></div>
</html>