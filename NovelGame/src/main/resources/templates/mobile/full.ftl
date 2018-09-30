<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<title>全本小说免费在线阅读-txt2小说网</title>
<meta name="keywords" content="好看的全本小说,全本小说免费在线阅读,大结局小说">
<meta name="description" content="txt2小说网收录了大量好看的全本小说，提供全本小说免费阅读。页面清爽无弹窗，访问速度快！">
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
<div class="toptab"><span class="active">全本小说</span></div>
<#list bul.list as store>
<div class="bookbox">
    <div class="bookimg">
    <a href="/mobile/book/${store.bookNameEn}/"  title="${store.bookName}"><img src="${request.contextPath }${store.imageUrl}"  width="78" onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${store.bookName}"></a>
    </div>
    <div class="bookinfo">
    <a href="/mobile/book/${store.bookNameEn}/"  class="iTit"><h2 class="bookname">${store.bookName}</h2></a>
    <p class="author">作者：<a href="/mobile/author/${store.authorNameEn}/" >${store.authorName}</a></p>
    <p class="update"><span>更新至：</span><a href="/mobile/book/${store.bookNameEn}/${store.storeId}/" >${store.storeName}</a></p>
    <p class="intro_line"><span>简介：</span>
        <#if  store.bookDesc?length gt 200>
        ${store.bookDesc?substring(0,200)}...
        <#else>
        ${store.bookDesc}
        </#if>
    </p>
    </div>
</div>
</#list>
<div class="listpage"><span class="left"><a class="before">上一页</a></span><span class="middle"><select name="pageselect" onchange="self.location.href=options[selectedIndex].value"><option value="/full/?&p=1" selected="selected">第1页</option><option value="/full/?&p=2" >第2页</option><option value="/full/?&p=3" >第3页</option><option value="/full/?&p=4" >第4页</option><option value="/full/?&p=5" >第5页</option><option value="/full/?&p=6" >第6页</option><option value="/full/?&p=7" >第7页</option><option value="/full/?&p=8" >第8页</option><option value="/full/?&p=9" >第9页</option><option value="/full/?&p=10" >第10页</option><option value="/full/?&p=11" >第11页</option><option value="/full/?&p=12" >第12页</option><option value="/full/?&p=13" >第13页</option><option value="/full/?&p=14" >第14页</option><option value="/full/?&p=15" >第15页</option><option value="/full/?&p=16" >第16页</option><option value="/full/?&p=17" >第17页</option><option value="/full/?&p=18" >第18页</option><option value="/full/?&p=19" >第19页</option><option value="/full/?&p=20" >第20页</option><option value="/full/?&p=21" >第21页</option><option value="/full/?&p=22" >第22页</option><option value="/full/?&p=23" >第23页</option><option value="/full/?&p=24" >第24页</option><option value="/full/?&p=25" >第25页</option><option value="/full/?&p=26" >第26页</option><option value="/full/?&p=27" >第27页</option><option value="/full/?&p=28" >第28页</option><option value="/full/?&p=29" >第29页</option><option value="/full/?&p=30" >第30页</option><option value="/full/?&p=31" >第31页</option><option value="/full/?&p=32" >第32页</option><option value="/full/?&p=33" >第33页</option><option value="/full/?&p=34" >第34页</option><option value="/full/?&p=35" >第35页</option><option value="/full/?&p=36" >第36页</option><option value="/full/?&p=37" >第37页</option><option value="/full/?&p=38" >第38页</option><option value="/full/?&p=39" >第39页</option><option value="/full/?&p=40" >第40页</option><option value="/full/?&p=41" >第41页</option><option value="/full/?&p=42" >第42页</option><option value="/full/?&p=43" >第43页</option><option value="/full/?&p=44" >第44页</option><option value="/full/?&p=45" >第45页</option><option value="/full/?&p=46" >第46页</option><option value="/full/?&p=47" >第47页</option><option value="/full/?&p=48" >第48页</option><option value="/full/?&p=49" >第49页</option><option value="/full/?&p=50" >第50页</option><option value="/full/?&p=51" >第51页</option><option value="/full/?&p=52" >第52页</option><option value="/full/?&p=53" >第53页</option><option value="/full/?&p=54" >第54页</option><option value="/full/?&p=55" >第55页</option><option value="/full/?&p=56" >第56页</option><option value="/full/?&p=57" >第57页</option><option value="/full/?&p=58" >第58页</option><option value="/full/?&p=59" >第59页</option><option value="/full/?&p=60" >第60页</option><option value="/full/?&p=61" >第61页</option><option value="/full/?&p=62" >第62页</option><option value="/full/?&p=63" >第63页</option><option value="/full/?&p=64" >第64页</option><option value="/full/?&p=65" >第65页</option><option value="/full/?&p=66" >第66页</option><option value="/full/?&p=67" >第67页</option><option value="/full/?&p=68" >第68页</option><option value="/full/?&p=69" >第69页</option><option value="/full/?&p=70" >第70页</option><option value="/full/?&p=71" >第71页</option><option value="/full/?&p=72" >第72页</option><option value="/full/?&p=73" >第73页</option><option value="/full/?&p=74" >第74页</option><option value="/full/?&p=75" >第75页</option><option value="/full/?&p=76" >第76页</option><option value="/full/?&p=77" >第77页</option><option value="/full/?&p=78" >第78页</option><option value="/full/?&p=79" >第79页</option><option value="/full/?&p=80" >第80页</option><option value="/full/?&p=81" >第81页</option><option value="/full/?&p=82" >第82页</option><option value="/full/?&p=83" >第83页</option><option value="/full/?&p=84" >第84页</option><option value="/full/?&p=85" >第85页</option><option value="/full/?&p=86" >第86页</option><option value="/full/?&p=87" >第87页</option><option value="/full/?&p=88" >第88页</option><option value="/full/?&p=89" >第89页</option><option value="/full/?&p=90" >第90页</option><option value="/full/?&p=91" >第91页</option><option value="/full/?&p=92" >第92页</option><option value="/full/?&p=93" >第93页</option><option value="/full/?&p=94" >第94页</option><option value="/full/?&p=95" >第95页</option><option value="/full/?&p=96" >第96页</option><option value="/full/?&p=97" >第97页</option><option value="/full/?&p=98" >第98页</option><option value="/full/?&p=99" >第99页</option><option value="/full/?&p=100" >第100页</option></select></span><span class="right"><a class="onclick" href="-&p=2.htm" tppabs="/full/?&p=2">下一页</a></span></div>
 <#include "foots.ftl">
    </body>
    <div></div>
</html>