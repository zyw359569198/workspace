<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>2018小说排行榜_txt2小说网</title>
<meta name="keywords" content="2018小说排行榜,2018热门小说排行榜,2018网络小说排行榜,经典小说排行,2018免费小说排行榜,2018完本小说推荐榜">
<meta name="description" content="txt2小说网小说排行榜提供最新、流行、经典、免费小说排行榜,涵盖:2018都市排行榜，2018玄幻排行榜，2018武侠排行榜，2018言情排行榜，2018穿越排行榜，2018网游排行榜，2018恐怖排行榜，2018科幻排行榜，2018修真排行榜，2018全本小说排行榜">
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
<div class="wrap">
  <div class="rank-box box-center cf">
    <div class="rank-nav-list fl" data-l1="4">
      <h4>热门分类排行</h4>
      <ul class="list_type_detective">
       <#list tjl as catagoryItem>
       <#list catagoryItem as cbook>
   <#if cbook_index==0>
        <li><a href="/catagory/${cbook.cataNameEn}/">${cbook.cataName}小说排行榜</a></li>
            </#if>
            </#list>
            </#list>
            <li class=""><a href="/model/full/">完本榜</a>
        </li>
      </ul>
    </div>
    <div class="main-content-wrap fl" data-l1="5">
      <div class="rank-body">
        <div class="rank-list-row cf mb20">
          
          <#list tjl as catagoryItem>
          <#if catagoryItem_index <9 >
          <#assign i=1>
       <#list catagoryItem as cbook>
   <#if cbook_index==0>
          <div class="rank-list">
              <h3 class="wrap-title lang">${cbook.cataName}小说排行榜
                <a class="more" href="/catagory/${cbook.cataNameEn}/" data-eid="qd_C45">更多
                  <em class="iconfont"></em></a>
              </h3>
              <div class="book-list">
                <ul>
                  <li class="unfold" data-rid="1">
                    <div class="book-wrap cf">
                      <div class="book-info fl">
                        <h3>NO.1</h3>
                        <h4>
                          <a href="/book/${cbook.bookNameEn}/" target="_blank">${cbook.bookName}</a></h4>
                        <p class="digital">
                          <em></em>阅读</p>
                        <p class="author"><a class="writer" target="_blank" href="/author/${cbook.authorNameEn}/">${cbook.authorName}</a></p>
                      </div>
                      <div class="book-cover">
                        <a class="cover-link" href="/book/${cbook.bookNameEn}/" target="_blank">
                          <img src="${request.contextPath }${cbook.imageUrl}" onerror="this.src='${request.contextPath }/images/nocover.jpg'" alt="${cbook.bookName}" /></a>
                        <span></span>
                      </div>
                    </div>
                  </li>
                      <#else>            
                  <li data-rid="${i}">
                      <div class="num-box">
                        <span class="num${i}">${i}</span></div>
                      <div class="name-box">
                        <a class="name" href="/book/${cbook.bookNameEn}/" target="_blank">${cbook.bookName}</a>
                        <i class="total"></i></div>
                    </li>
                    </#if>
                    <#assign i=i+1>
           </#list>
			</ul>
              </div>
            </div>
            </#if>
            </#list>
   			 </div>
      </div>
    </div><!--end main-content-wrap-->
  </div>
</div>
<#include "foots.ftl" >
</body>
</html>