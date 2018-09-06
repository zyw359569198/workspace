window.onload =function(){
$.jqPaginator('#pagelink',{
    totalPages: Math.ceil(parseInt($("div.pagelink:first").attr("value"))/20),
    visiblePages: 11,
    currentPage: 1,
    first: '<a class="" href="javascript:;">首页</a>',
    prev: '<a class="" href="javascript:;">上一页</a>',
    next: '<a class="" href="javascript:;">下一页</a>',
    last: '<a class="" href="javascript:;">末页</a>',
    page: '<a class="" href="javascript:;">{{page}}</a>',
    onPageChange: function (num, type) {
    	if(type=='init'){
    	}else{
    		  pageSplit($("div.pagelink:first").attr("name"),20,num);
    	}
    	    $("div.pagelink:last").empty();
    	   var rowLi=document.createElement("li");
			rowLi.className="rows";
		    rowLi.innerHTML="共<b>"+parseInt($("div.pagelink:first").attr("value"))+"</b>条记录&nbsp;第<b>"+num+"</b>页/共<b>"+(Math.ceil(parseInt($("div.pagelink:first").attr("value"))/20))+"</b>页";
			$("div.pagelink:last").append(rowLi);
    }
});

}



function pageSplit(authorNameEn,pageSize,pageNum){
		$.ajax({
		type: 'GET',
		url: "/pageApi/author/"+authorNameEn+"/"+pageSize+"/"+pageNum,
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					$("div.ops_cover").empty()
					loadBookInfoData(data.data.abl.list);
		}
	});
}

function loadBookInfoData(bookInfoData){
	var rowDiv=document.createElement("div");
	var flag=true;
	rowDiv.className="ops_lf";
		$.each(bookInfoData,function(index,n){
			if(flag){
				rowDiv.innerHTML="特约作者 <em>"+bookInfoData[index].authorName+"</em> 相关作品 <em>"+bookInfoData.length+"</em> 部</div><div class=\"rt\">";
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
		rowDiv3.innerHTML="<h2><a href='/book/"+bookInfoData[index].bookNameEn+"/'   target=\"_blank\">"+bookInfoData[index].bookName+"</a></h2><p></p><p>作者："+bookInfoData[index].authorName+"</p><p>类型：玄幻</p><p>简介："+bookInfoData[index].bookDesc+"</p>";
		rowDiv1.append(rowDiv2);
		rowDiv1.append(rowDiv3);
		$("div.ops_cover").append(rowDiv1);
});
}