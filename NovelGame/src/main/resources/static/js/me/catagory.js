function iframeHeight(){
             // obj 这里是要获取父页面的iframe对象
                 var obj = parent.document.getElementById('rightcontent');
             // 调整父页面的高度为此页面的高度
                 obj.height = this.document.body.scrollHeight+50;
             }
             
function GetQueryString(name)
    {
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = window.location.search.substr(1).match(reg);
         if(r!=null)return  unescape(r[2]); return null;
    }

window.onload =function(){
	$("div.wrapper",parent.document).show();
	$("div.nav",parent.document).show();
	var catagoryId=GetQueryString("catagoryId");
	var catagoryName=decodeURI(GetQueryString("catagoryName"));
	$("#placeId").text(catagoryName);
		$.ajax({
		type: 'GET',
		url: "/book/queryBookByHits?cataId="+catagoryId,
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					//$("ul.nav_l").empty()
					loadBookHitsData(data.data.bcl);
					loadBookUpdateInfoData(data.data.bul,catagoryName);
					iframeHeight();
		}
	});
}
function loadBookUpdateInfoData(bookUpdateInfoData,catagoryName){
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
		rowH1.innerHTML=catagoryName;
		rowUl.appendChild(rowFixedLi);
	$.each(bookUpdateInfoData,function(index,n){
		var rowLi=document.createElement("li");
		var rowSpan1=document.createElement("span");
		var rowSpan2=document.createElement("span");
		var rowSpan3=document.createElement("span");
		var rowSpan4=document.createElement("span");
		var rowSpan5=document.createElement("span");
		var arrayTime=bookUpdateInfoData[index].createTime.split("-");
		rowSpan1.className="sm";rowSpan2.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookUpdateInfoData[index].bookId+"'  name='"+bookUpdateInfoData[index].bookName+"'><b>"+bookUpdateInfoData[index].bookName+"</b></a>";
		rowSpan2.className="zj";rowSpan3.innerHTML="&nbsp;<a href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name)'  id='"+bookUpdateInfoData[index].id+"'  name='"+bookUpdateInfoData[index].storeName+"'>"+bookUpdateInfoData[index].storeName+"</a>";
		rowSpan3.className="zz";rowSpan4.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(3,this.id,this.name)'  id='"+bookUpdateInfoData[index].authorId+"'  name='"+bookUpdateInfoData[index].authorName+"'>"+bookUpdateInfoData[index].authorName+"</a>";
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
function loadBookHitsData(bookData){
	$.each(bookData,function(index,n){
		//alert(catagoryData[index].cataNameEn)
		var rowDl=document.createElement("dl");
		var rowDt=document.createElement("dt");
		var rowDd=document.createElement("dd");
		var rowH3=document.createElement("h3");
		var rowP=document.createElement("p");
		var rowSpan=document.createElement("span");
		rowDt.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookData[index].bookId+"'  name='"+bookData[index].bookName+"'><img src='"+bookData[index].imageUrl+"' alt='"+bookData[index].bookName+"' onerror=\"this.src='/images/nocover.jpg/'\" alt=''/></a>";
		rowH3.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookData[index].bookId+"'  name='"+bookData[index].bookName+"'>"+bookData[index].bookName+"</a>";
		rowSpan.innerHTML="<a target='_blank' href='javascript:void(0);'  onclick='parent.openHtml(3,this.id,this.name)'  id='"+bookData[index].authorId+"'  name='"+bookData[index].authorName+"'>"+bookData[index].authorName+"</a>";
		rowP.innerHTML="&emsp;&emsp;"+bookData[index].bookDesc;
		rowDd.appendChild(rowH3);
		rowDd.appendChild(rowSpan);
		rowDd.appendChild(rowP);
		rowDl.appendChild(rowDt);
		rowDl.appendChild(rowDd);
		$("div.fengtui").append(rowDl);
});
}