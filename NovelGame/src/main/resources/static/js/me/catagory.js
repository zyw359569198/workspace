window.onload =function(){
$.jqPaginator('#pagelink',{
    totalPages: Math.ceil(parseInt($("div.pagelink:first").attr("value"))/24),
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
    		  pageSplit($("div.pagelink:first").attr("name"),24,num);
    	}
    	    $("div.pagelink:last").empty();
    	   var rowLi=document.createElement("li");
			rowLi.className="rows";
		    rowLi.innerHTML="共<b>"+parseInt($("div.pagelink:first").attr("value"))+"</b>条记录&nbsp;第<b>"+num+"</b>页/共<b>"+(Math.ceil(parseInt($("div.pagelink:first").attr("value"))/24))+"</b>页";
			$("div.pagelink:last").append(rowLi);
    }
});

}



function pageSplit(cataNameEn,pageSize,pageNum){
		$.ajax({
		type: 'GET',
		url: "/pageApi/catagory/"+cataNameEn+"/"+pageSize+"/"+pageNum,
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					$("div.booklist").empty()
					loadBookUpdateInfoData(data.data.bul.list);
		}
	});
}

function loadBookUpdateInfoData(bookUpdateInfoData){
		var rowH1=document.createElement("h1");
		var rowUl=document.createElement("ul");
		var rowFixedLi=document.createElement("li");
		var rowSpan1=document.createElement("span");
		var rowSpan2=document.createElement("span");
		var rowSpan3=document.createElement("span");
		var rowSpan4=document.createElement("span");
		var rowSpan5=document.createElement("span");
		rowFixedLi.className="t";
		rowSpan1.className="sm";rowSpan2.innerHTML="小说名称";
		rowSpan2.className="zj";rowSpan3.innerHTML="最新章节";
		rowSpan3.className="zz";rowSpan4.innerHTML="作者";
		rowSpan4.className="sj";rowSpan5.innerHTML="更新";
		rowSpan5.className="zt";rowSpan1.innerHTML="状态";
		rowFixedLi.appendChild(rowSpan1);
		rowFixedLi.appendChild(rowSpan2);
		rowFixedLi.appendChild(rowSpan3);
		rowFixedLi.appendChild(rowSpan4);
		rowFixedLi.appendChild(rowSpan5);
		rowH1.innerHTML=bookUpdateInfoData[0].cataName;
		rowUl.appendChild(rowFixedLi);
	$.each(bookUpdateInfoData,function(index,n){
		var rowLi=document.createElement("li");
		var rowSpan1=document.createElement("span");
		var rowSpan2=document.createElement("span");
		var rowSpan3=document.createElement("span");
		var rowSpan4=document.createElement("span");
		var rowSpan5=document.createElement("span");
		var arrayTime=bookUpdateInfoData[index].createTime.split("-");
		rowSpan1.className="sm";rowSpan2.innerHTML="<a href='/book/"+bookUpdateInfoData[index].bookNameEn+"'  ><b>"+bookUpdateInfoData[index].bookName+"</b></a>";
		rowSpan2.className="zj";rowSpan3.innerHTML="&nbsp;<a href='/book/"+bookUpdateInfoData[index].bookNameEn+"/"+bookUpdateInfoData[index].storeId+"/'  >"+bookUpdateInfoData[index].storeName+"</a>";
		rowSpan3.className="zz";rowSpan4.innerHTML="<a href='/author/"+bookUpdateInfoData[index].authorNameEn+"/'  >"+bookUpdateInfoData[index].authorName+"</a>";
		rowSpan4.className="sj";rowSpan5.innerHTML=arrayTime[1]+"/"+arrayTime[2];
		rowSpan5.className="zt";rowSpan1.innerHTML=bookUpdateInfoData[index].isCompletion==0?"已完结":"连载中";
		rowLi.appendChild(rowSpan1);
		rowLi.appendChild(rowSpan2);
		rowLi.appendChild(rowSpan3);
		rowLi.appendChild(rowSpan4);
		rowLi.appendChild(rowSpan5);
		rowUl.appendChild(rowLi);
});
		$("div.booklist").append(rowH1);
		$("div.booklist").append(rowUl);
}
