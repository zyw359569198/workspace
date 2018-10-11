function GetQueryString(name)
    {
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = window.location.search.substr(1).match(reg);
         if(r!=null)return  r[2]; return null;
    }


window.onload =function(){
	var keyword=decodeURI(GetQueryString("keyword"));
	document.getElementById('search').value =keyword;
	if(keyword==null||keyword==''||keyword=="null"){
		$("div.ops_two").empty()
		var rowDiv=document.createElement("div");
	     rowDiv.className="ops_lf";
		rowDiv.innerHTML="与 <em></em> 相关作品 <em>0</em> 部</div><div class=\"rt\">";
		$("div.ops_two").append(rowDiv);
	return;
	}
$.jqPaginator('#pagelink',{
    totalPages: Math.ceil(parseInt($("div.pagelink:first").attr("value"))/10),
    visiblePages: 11,
    currentPage: 1,
    first: '<a class="" href="javascript:;">首页</a>',
    prev: '<a class="" href="javascript:;">上一页</a>',
    next: '<a class="" href="javascript:;">下一页</a>',
    last: '<a class="" href="javascript:;">末页</a>',
    page: '<a class="" href="javascript:;">{{page}}</a>',
    onPageChange: function (num, type) {
    		  pageSplit(keyword,10,num);
    	    $("div.pagelink:last").empty();
    	   var rowLi=document.createElement("li");
			rowLi.className="rows";
		    rowLi.innerHTML="共<b>"+parseInt($("div.pagelink:first").attr("value"))+"</b>条记录&nbsp;第<b>"+num+"</b>页/共<b>"+(Math.ceil(parseInt($("div.pagelink:first").attr("value"))/10))+"</b>页";
			$("div.pagelink:last").append(rowLi);
    }
});

}



function pageSplit(keyword,pageSize,pageNum){
		$.ajax({
		type: 'GET',
		url: "/pageApi/search/"+keyword+"/"+pageSize+"/"+pageNum,
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					$("div.ops_cover").empty();
					$("div.ops_two").empty();
					loadBookInfoData(data.data.abl.list,keyword);
		}
	});
}

function loadBookInfoData(bookInfoData,keyword){
	var rowDiv=document.createElement("div");
	var flag=true;
	rowDiv.className="ops_lf";
	   if(bookInfoData.length<1){
	   	var rowDivNo=document.createElement("div");
		     rowDivNo.className="ops_no";
			rowDiv.innerHTML="与 <em>"+keyword+"</em> 相关作品 <em>"+bookInfoData.length+"</em> 部</div><div class=\"rt\">";
			$("div.ops_two").append(rowDiv);
			rowDivNo.innerHTML="对不起本站还没有这本书，搜索时宁可少字也不要错字。";
			$("div.ops_cover").append(rowDivNo);
			}
		$.each(bookInfoData,function(index,n){
			if(flag){
				rowDiv.innerHTML="与 <em>"+keyword+"</em> 相关作品 <em>"+bookInfoData.length+"</em> 部</div><div class=\"rt\">";
				$("div.ops_two").append(rowDiv);
				flag=false;
			}
		var rowDiv1=document.createElement("div");
		rowDiv1.className="block";
		var rowDiv2=document.createElement("div");
		rowDiv2.className="block_img";
		var rowDiv3=document.createElement("div");
		rowDiv3.className="block_txt";
		rowDiv2.innerHTML="<a href='/book/"+bookInfoData[index].bookNameEn+"/'   target=\"_blank\"><img src='"+bookInfoData[index].imageUrl+"' alt='"+bookInfoData[index].bookName+"' onerror=\"this.src='/images/nocover.jpg/'\" alt=''/></a>";
		rowDiv3.innerHTML="<h2><a href='/book/"+bookInfoData[index].bookNameEn+"/'    target=\"_blank\">"+bookInfoData[index].bookName+"</a></h2><p></p><p>作者：<a href='/author/"+bookInfoData[index].authorNameEn+"/'    target=\"_blank\">"+bookInfoData[index].authorName+"</a></p><p>类型：玄幻</p><p>简介："+bookInfoData[index].bookDesc+"</p>";
		rowDiv1.append(rowDiv2);
		rowDiv1.append(rowDiv3);
		$("div.ops_cover").append(rowDiv1);
});
}