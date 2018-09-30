<div class="header">
        <div class="logo"><a href="/mobile" ><span classo="site_name">txt2小说网</span><span class="site_url" style="">&nbsp;txt2.cc</span></a></div>
    </div>
    <div class="nav">
        <ul>
        <#list mdl as model> 
       <li><a href="/${model.modelNameEn}/">${model.modelName}</a></li>
        </#list>
      </ul>
    </div>
    <div class="search">
        <form action="/mobile/search/" method="get">
        <table cellpadding="0" cellspacing="0" style="width:100%;">
        <tbody><tr>
        <td style="background-color:#fff; border:1px solid #CCC;"><input id="s_key" name="keyword" type="text" class="key" placeholder="输入书名/作者搜索，宁可少字不要错字" value=""></td>
        <td style="width:35px; background-color:#0080C0; background-image:url('${request.contextPath }/images/search.png'); background-repeat:no-repeat; background-position:center"><input type="submit" value="" class="go"></td>
        </tr>
        </tbody></table>
        </form>
    </div>