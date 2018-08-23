function GetQueryString(name)
    {
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = window.location.search.substr(1).match(reg);
         if(r!=null)return  unescape(r[2]); return null;
    }

window.onload =function(){
	$("div.wrapper",parent.document).show();
	$("div.nav",parent.document).show();
	var bookId=GetQueryString("bookId");
	var bookName=decodeURI(GetQueryString("bookName"));
		$.ajax({
		type: 'GET',
		url: "/book/initBookData?bookId="+bookId,
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					//$("ul.nav_l").empty()
					loadBookInfoData(data.data.bil[0]);
					loadStoreData(data.data.sil)
					loadBookHitsData(data.data.bkl);
		}
	});
}

function loadBookInfoData(bookInfoData){
	$("div.lf").append("<img src='"+bookInfoData.imageUrl+"' onerror=\"this.src='/images/nocover.jpg/'\" alt='"+bookInfoData.bookName+"'>");
	var rowH1=document.createElement("h1");
	rowH1.innerHTML=bookInfoData.bookName;
	var rowDiv=document.createElement("div");
	rowDiv.className="msg";
	var rowEm1=document.createElement("em");
	var rowEm2=document.createElement("em");
	var rowEm3=document.createElement("em");
	rowEm1.innerHTML="作者：<a target='_blank' a href='javascript:void(0);'  onclick='parent.openHtml(3,this.id,this.name)'  id='"+bookInfoData.authorId+"'  name='"+bookInfoData.authorName+"'>"+bookInfoData.authorName+"</a> ";
	rowEm2.innerHTML="状态："+bookInfoData.isCompletion==0?"已完结":"连载中";
	rowEm3.innerHTML="更新时间："+bookInfoData.updateTime;
	rowDiv.appendChild(rowEm1);
	rowDiv.appendChild(rowEm2);
	rowDiv.appendChild(rowEm3);
	var rowDiv2=document.createElement("div");
	rowDiv2.className="info";
	rowDiv2.innerHTML="<a href='#footer' rel='nofollow'>直达底部</a>";
	var rowDiv3=document.createElement("div");
	rowDiv3.className="intro";
	rowDiv3.innerHTML=bookInfoData.bookDesc;
	$("div.rt").append(rowH1);
	$("div.rt").append(rowDiv);
	$("div.rt").append(rowDiv2);
	$("div.rt").append(rowDiv3);
}
function loadStoreData(storeData){
	var rowUl=document.createElement("ul");
	$.each(storeData,function(index,n){
		//alert(catagoryData[index].cataNameEn)
		var rowLi=document.createElement("li");
		rowLi.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData[index].bookId+"\")'  id='"+storeData[index].storeId+"'  name='"+storeData[index].storeName+"'>"+storeData[index].storeName+"</a>";
		rowUl.appendChild(rowLi);
});
		$("div.mulu").append(rowUl);
}
function loadBookHitsData(bookHitsData){
		var rowH3=document.createElement("h3");
		rowH3.innerHTML="猜你喜欢";
		$("div.guess").append(rowH3);
	$.each(bookHitsData,function(index,n){
		var rowDiv=document.createElement("div");
		rowDiv.className="image";
		rowDiv.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookHitsData[index].bookId+"'  name='"+bookHitsData[index].bookName+"'  title='"+bookHitsData[index].bookName+"' /><img src='"+bookHitsData.imageUrl+"' onerror=\"this.src='/images/nocover.jpg/'\" alt='"+bookHitsData.bookName+"'><br>";
		var rowSpan=document.createElement("span");
		rowSpan.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookHitsData[index].bookId+"'  name='"+bookHitsData[index].bookName+"'>"+bookHitsData[index].bookName+"</a>";
		rowDiv.append(rowSpan);
				$("div.guess").append(rowDiv);

});
}