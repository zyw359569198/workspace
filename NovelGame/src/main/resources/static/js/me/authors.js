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
    		  pageSplit(20,num);
    	}
    	    $("div.pagelink:last").empty();
    	   var rowLi=document.createElement("li");
			rowLi.className="rows";
		    rowLi.innerHTML="共<b>"+parseInt($("div.pagelink:first").attr("value"))+"</b>条记录&nbsp;第<b>"+num+"</b>页/共<b>"+(Math.ceil(parseInt($("div.pagelink:first").attr("value"))/20))+"</b>页";
			$("div.pagelink:last").append(rowLi);
    }
});

}



function pageSplit(pageSize,pageNum){
		$.ajax({
		type: 'GET',
		url: "/pageApi/authors/"+pageSize+"/"+pageNum,
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					$("div.author-list").empty()
					loadBookData(data.data.bjl.list,pageSize,pageNum);
		}
	});
}

function loadBookData(bookUpdateInfoData,pageSize,pageNum){
		var rowUl=document.createElement("ul");
		var rowFixedUl=document.createElement("ul");
		var rowLi1=document.createElement("li");
		var rowLi2=document.createElement("li");
		var rowLi3=document.createElement("li");
		var rowLi4=document.createElement("li");
		rowFixedUl.className="title";
		rowLi1.className="num";rowLi1.innerHTML="序号";
		rowLi2.className="author";rowLi2.innerHTML="作者";
		rowLi3.className="bookname";rowLi3.innerHTML="代表作品";
		rowLi4.className="update";rowLi4.innerHTML="";
		rowFixedUl.appendChild(rowLi1);
		rowFixedUl.appendChild(rowLi2);
		rowFixedUl.appendChild(rowLi3);
		rowFixedUl.appendChild(rowLi4);
	$.each(bookUpdateInfoData,function(index,n){
		var rowLi1=document.createElement("li");
		var rowLi2=document.createElement("li");
		var rowLi3=document.createElement("li");
		var rowLi4=document.createElement("li");
		rowLi1.className="num";rowLi1.innerHTML=(pageSize*(pageNum-1)+index+1);
		rowLi2.className="author";rowLi2.innerHTML="<a href='/author/"+bookUpdateInfoData[index].authorNameEn+"/'  target='_blank'>"+bookUpdateInfoData[index].authorName+"</a>";
		rowLi3.className="bookname";rowLi3.innerHTML="<a class='bn vip' target='_blank' href='/book/"+bookUpdateInfoData[index].bookNameEn+"/'>"+bookUpdateInfoData[index].bookName+"</a>";
		rowLi4.className="update";rowLi4.innerHTML="";
		rowUl.appendChild(rowLi1);
		rowUl.appendChild(rowLi2);
		rowUl.appendChild(rowLi3);
		rowUl.appendChild(rowLi4);
});
		$("div.author-list").append(rowFixedUl);
		$("div.author-list").append(rowUl);
}
