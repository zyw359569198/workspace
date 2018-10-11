<div class="top">
  <div class="main">
    <div class="lf">
      <div style="color: rgb(147, 0, 255);">请记住网址：<b style="color: rgb(255, 0, 186);">txt2.cc</b>，欢迎常来<b style="color: rgb(255, 0, 186);">txt2小说网</b>阅读小说</div>
    </div>
    <div class="rt">
      <a href="/model/search/?keyword=" target="_blank"><font color="#FF0000">站内搜索</font></a> |
      <!-- <a href="/index.php?m=Home&c=Feedback&a=add" target="_blank">反馈留言</a> | -->
      <a href="/mobile" target="_blank">手机版</a> | 
      <a href="javascript:void(0);" onclick="AddFavorite('txt2小说网',location.href)" target="_self" rel="nofollow">收藏本站</a>
    </div>
  </div>
</div>
<div class="wrapper">
  <div class="logo">
     <a href="/" style="background: url(${request.contextPath}/images/logo-txt2.png) no-repeat;">txt2小说网</a>
  </div>
  <div class="seach">
      <form action="/model/search/" accept-charset="utf-8" onsubmit="document.charset='utf-8';">
        <input id="search" type="text" autocomplete="off" name="keyword" class="searchinput" placeholder="请输入小说名和作者名来搜索，千万别输错字了！">
        <input type="submit" value="搜 索" class="searchgo">
      </form>
      <div class="hotkeys">
      <#list sif as searchInfo>
        <a href="${searchInfo.keyvalue}" target="_blank">${searchInfo.keyword}</a>
      </#list>
        </div>
  </div>
</div>
<div class="nav">
  <div class="main">
    <ul class="nav_l">
   <#list cgl as catagory> 
   <#if catagory.cataId=="0">
         <li><a href="/">${catagory.cataName}</a></li>
   <#else>
      <li><a href="/catagory/${catagory.cataNameEn}/">${catagory.cataName}</a></li>
      </#if>
      </#list>
  </ul>
    <ul class="nav_r">
       <#list mdl as model> 
       <li><a href="/model/${model.modelNameEn}/">${model.modelName}</a></li>
      </#list>
        </ul>
  </div>
</div>
