function changeClick(object,type){
	var thisObj;
    var value;  
    if(type==-999){
    	value=parseInt(object);
    	$("span.left a").attr("value",value);
    	$("span.right a").attr("value",value);
    	pageSplit(value);
    	if(value<=1){
    		$("span.left a").attr("class","before");
    		$("span.right a").attr("class","onclick");
    	}else if(value==parseInt($("div.listpage").attr("value"))){
    		$("span.right a").attr("class","before");
    		$("span.left a").attr("class","onclick");
    	}else{
    		$("span.left a").attr("class","onclick");
    		$("span.right a").attr("class","onclick");
    	}
    }else{
    thisObj=$(object);//js对象转jquery对象  
    value=parseInt(thisObj.attr("value"))+type;
    if(type==-1 && value> 0){
    	    $("span.left a").attr("value",value);
    	     $("#pageselect").val(value);
    	    $("span.right a").attr("value",value);
    	    $("span.right a").attr("class","onclick");
    	    pageSplit(parseInt(thisObj.attr("value")));
    }
    if(type==-1 && value<= 1){
    	$("span.left a").attr("class","before");
    }
    
    if(type==1 && value!=(parseInt($("div.listpage").attr("value"))+1)){
    	    $("span.left a").attr("value",value);
    	     $("#pageselect").val(value);
    	    $("span.right a").attr("value",value);
    	    $("span.left a").attr("class","onclick");
    	    pageSplit(parseInt(thisObj.attr("value")));

    }
    
    if(type==1 && value==(parseInt($("div.listpage").attr("value")))){
    	$("span.right a").attr("class","before");
    }
    }
}
function pageSplit(pageNum){
	var cataNameEn=$("div.block").attr("name");
	var pageSize=100;
	$.ajax({
	type: 'GET',
	url: "/mobile/mobileApi/book/"+cataNameEn+"/"+pageSize+"/"+pageNum,
	contentType: "application/json;cherset=utf-8",
	dataType: "json",
	asynchronous: true,
	success: function(data){
				$("ul.chapter").empty()
				loadBookStoreInfoData(data.data.bul.list);
	}
});
}

function loadBookStoreInfoData(storeList){
	var rowLi;
	$.each(storeList,function(index,n){
		rowLi=document.createElement("li");
		rowLi.innerHTML="<a href=\"/mobile/book/"+storeList[index].bookNameEn+"/"+storeList[index].storeId+"/\" >"+storeList[index].storeName+"</a>";
		$("ul.chapter").append(rowLi);
	});
	
}