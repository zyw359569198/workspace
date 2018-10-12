<!DOCTYPE html>
<html><head><meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<meta http-equiv="Cache-Control" content="max-age=0">
<meta http-equiv="Cache-Control" content="no-siteapp">
<meta http-equiv="Cache-Control" content="no-transform">
<#list sdl as storeData>
<title>${storeData.storeName},${storeData.bookName},${storeData.authorName},txt2小说网手机版</title>
<meta name="keywords" content="${storeData.storeName},${storeData.bookName},${storeData.authorName},txt2小说网手机版">
<meta name="description" content="${storeData.storeName}是由作家${storeData.authorName}所作${storeData.bookName}的最新章节,更多小说尽在txt2小说网，好看记得告诉您的朋友哦！">
</#list>
<meta name="MobileOptimized" content="240">
<meta name="applicable-device" content="mobile">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<meta name="format-detection" content="telephone=no">
<meta name="apple-mobile-web-app-capable" content="yes">
<link rel="shortcut icon" href="/favicon-txt2.ico">
<meta property="og:url" content="/book/blpq_ssjq/">
<script src="${request.contextPath}/js/mobile/jq.js"></script>
<link rel="stylesheet" type="text/css" href="${request.contextPath}/js/mobile/m.css" >
<script src="${request.contextPath}/js/mobile/m.js" ></script>
<script src="${request.contextPath}/js/mobile/store.js" ></script>
<link href="${request.contextPath}/js/mobile/layer.css"  type="text/css" rel="styleSheet" id="layermcss">

</head>
<body id="nr_body" class="nr_all c_nr">
<div class="header">
	<div class="back">
		<a href="javascript:history.go(-1);">返回</a>
	</div>
	<#list sdl as storeData>
	<h1><a href="/mobile/book/${storeData.bookNameEn}/"  id="bookname"  bookName=${storeData.bookNameEn}  storeId=${storeData.storeId}>${storeData.bookName}</a></h1>
	</#list>
	<div class="reg">
		<a href="javascript:st();void 0;" id="st" rel="nofollow" class="login_topbtn c_index_login">繁</a><a href="/mobile" class="login_topbtn c_index_login">首页</a>
	</div>
</div>
	<div class="nr_set">
		<div id="lightdiv" class="set1" onclick="nr_setbg(&#39;light&#39;)">
			关灯
		</div>
		<div id="huyandiv" class="set1" onclick="nr_setbg(&#39;huyan&#39;)">
			护眼
		</div>
		<div class="set2">
			<div id="fontbig2" onclick="nr_setbg(&#39;big2&#39;)"> 巨 </div> 
			<div id="fontbig" onclick="nr_setbg(&#39;big&#39;)"> 大 </div>
			<div id="fontmiddle" onclick="nr_setbg(&#39;middle&#39;)"> 中 </div>
			<div id="fontsmall" onclick="nr_setbg(&#39;small&#39;)"> 小 </div>
		</div>
		<div class="clear">
		</div>
	</div>
	<#list sdl as storeData>
	<div class="nr_title" id="nr_title">${storeData.storeName}</div>
	<#list sddl as storeDataItem>
	<div class="nr_page">
	  <#if storeData.preStoreId == "0">
    <#else>
       <a id="pt_prev" href="/mobile/book/${storeData.bookNameEn}/${storeData.preStoreId}/" >上一章</a>
    </#if>
		        <a id="pt_mulu" class="mulu" href="/mobile/book/${storeData.bookNameEn}/" >目录</a>
     <#if storeData.nextStoreId == "0">
    <#else>
            <a id="pt_next" href="/mobile/book/${storeData.bookNameEn}/${storeData.nextStoreId}/" >下一章</a>
    </#if>
			</div>
	
	<div id="nr" class="nr_nr">
		<div id="nr1">
			    ${storeDataItem.vStoreContent}		
			<script>
			$(function() {  
			    eval(window.atob("dmFyIGN1cnJlbnRIcmVmPWxvY2F0aW9uLmhyZWY7aWYoL2JhaWR1Y29udGVudC5jb20vZ2kudGVzdChjdXJyZW50SHJlZikpe2xvY2F0aW9uLmhyZWY9ICQoIm1ldGFbcHJvcGVydHk9XCJvZzp1cmxcIl0iKS5hdHRyKCJjb250ZW50Iik7fQ=="));
			});
			</script>
		</div>
	</div>
	<div class="my-ad">
		<div id="c6der"></div>
	</div>
		<div class="nr_page">
  <#if storeData.preStoreId == "0">
    <#else>
       <a id="pt_prev" href="/mobile/book/${storeData.bookNameEn}/${storeData.preStoreId}/" >上一章</a>
    </#if>
		        <a id="pt_mulu" class="mulu" href="/mobile/book/${storeData.bookNameEn}/" >目录</a>
     <#if storeData.nextStoreId == "0">
    <#else>
            <a id="pt_next" href="/mobile/book/${storeData.bookNameEn}/${storeData.nextStoreId}/" >下一章</a>
    </#if>
				</div>
</#list>
</#list>
	<div class="my-ad">
		<div id="c311p">		
		</div>
	</div>
	<script>getset();</script>
	<div class="footer">
	  <p>本站所有小说由网友上传，如有侵犯版权，请来信告知，本站立即予以处理。</p>
	  <p><a href="/" >电脑版</a> | <a href="javascript:scroll(0,0)">返回顶部</a></p>
	</div>
	<div class="tfanye" onclick="tfanye()"></div>
	<div class="bfanye" onclick="bfanye()"></div>
</body>
</html>