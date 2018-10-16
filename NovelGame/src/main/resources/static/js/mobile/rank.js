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
	var hits=$("#div_test").attr("value");
	var pageSize=24;
	$.ajax({
	type: 'GET',
	url: "/mobile/mobileApi/rank/"+hits+"/"+pageSize+"/"+pageNum,
	contentType: "application/json;cherset=utf-8",
	dataType: "json",
	asynchronous: true,
	success: function(data){
				$("#div_test").empty()
				loadBookStoreInfoData(data.data.bkl.list);
	}
});
}

function loadBookStoreInfoData(storeList){
	var rowDivBox;
	var rowDiv;
	var rowDivBook;
	var rowP1;
	var rwoP2;
	var rowP3;
	$.each(storeList,function(index,n){
		rowDivBox=document.createElement("div");
		rowDivBox.className="bookbox";
		rowDiv=document.createElement("div");
		rowDiv.className="bookimg";
		rowDiv.innerHTML="<a href='/mobile/book/"+storeList[index].bookNameEn+"/'  title='"+storeList[index].bookName+"'><img src='"+storeList[index].imageUrl+"'  width='78'  onerror=\"this.src='/images/nocover.jpg'\" alt='"+storeList[index].bookName+"'></a>";
		rowDivBox.append(rowDiv);
		rowDivBook=document.createElement("div");
		rowDivBook.className="bookinfo";
		rowDivBook.innerHTML="<a href=\"/mobile/book/"+storeList[index].bookNameEn+"/\"  class=\"iTit\"><h2 class=\"bookname\">"+storeList[index].bookName+"</h2></a>";
		rowP1=document.createElement("p");
		rowP1.className="author";
		rowP1.innerHTML="作者：<a href=\"/mobile/author/"+storeList[index].authorNameEn+"/\" >"+storeList[index].authorName+"</a>";
		rowP2=document.createElement("p");
		rowP2.className="update";
		if(storeList[index].storeName==null||storeList[index].storeName=="undefined"){
		rowP2.innerHTML="<span>更新至：</span><a href=\"/mobile/book/"+storeList[index].bookNameEn+"/"+storeList[index].lastStoreId+"/\"></a>";
		}else{
		rowP2.innerHTML="<span>更新至：</span><a href=\"/mobile/book/"+storeList[index].bookNameEn+"/"+storeList[index].lastStoreId+"/\">"+storeList[index].storeName+"</a>";
		}
		rowP3=document.createElement("p");
		rowP3.className="intro_line";
		if(storeList[index].bookDesc.length > 50){
		rowP3.innerHTML="<span>简介：</span>"+storeList[index].bookDesc.substring(0,50)+"...</p>";
		}else{
		rowP3.innerHTML="<span>简介：</span>"+storeList[index].bookDesc+"</p>";
		}
		rowDivBook.append(rowP1);
		rowDivBook.append(rowP2);
		rowDivBook.append(rowP3);
		rowDivBox.append(rowDivBook);
	    $("#div_test").append(rowDivBox);

	});
	
	}