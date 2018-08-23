window.onload =function(){
	$("div.wrapper",parent.document).show();
	$("div.nav",parent.document).show();
	$.ajax({
		type: 'GET',
		url: "/book/init",
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					//$("ul.nav_l").empty()
					loadBookHitsData(data.data.bkl);
					loadBookCataData(data.data.tjl);
					loadBookCreateData(data.data.bcl);
					loadBookUpdateInfoData(data.data.bul);
		}
	});

}

function loadBookUpdateInfoData(bookUpdateInfoData){
		var rowH2=document.createElement("h2");
		var rowUl=document.createElement("ul");
		var rowFixedLi=document.createElement("li");
		var rowSpan1=document.createElement("span");
		var rowSpan2=document.createElement("span");
		var rowSpan3=document.createElement("span");
		var rowSpan4=document.createElement("span");
		var rowSpan5=document.createElement("span");
		rowFixedLi.className="t";
		rowSpan1.className="lx";rowSpan1.innerHTML="类型";
		rowSpan2.className="sm";rowSpan2.innerHTML="书名";
		rowSpan3.className="zj";rowSpan3.innerHTML="最新章节";
		rowSpan4.className="zz";rowSpan4.innerHTML="作者";
		rowSpan5.className="sj";rowSpan5.innerHTML="时间";
		rowFixedLi.appendChild(rowSpan1);
		rowFixedLi.appendChild(rowSpan2);
		rowFixedLi.appendChild(rowSpan3);
		rowFixedLi.appendChild(rowSpan4);
		rowFixedLi.appendChild(rowSpan5);
		rowH2.innerHTML="最新更新";
		rowUl.appendChild(rowFixedLi);
	$.each(bookUpdateInfoData,function(index,n){
		var rowLi=document.createElement("li");
		var rowSpan1=document.createElement("span");
		var rowSpan2=document.createElement("span");
		var rowSpan3=document.createElement("span");
		var rowSpan4=document.createElement("span");
		var rowSpan5=document.createElement("span");
		var arrayTime=bookUpdateInfoData[index].createTime.split("-");
		rowSpan1.className="lx";rowSpan1.innerHTML="["+bookUpdateInfoData[index].cataName+"]";
		rowSpan2.className="sm";rowSpan2.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookUpdateInfoData[index].bookId+"'  name='"+bookUpdateInfoData[index].bookName+"'>"+bookUpdateInfoData[index].bookName+"</a>";
		rowSpan3.className="zj";rowSpan3.innerHTML="&nbsp;<a href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name)'  id='"+bookUpdateInfoData[index].id+"'  name='"+bookUpdateInfoData[index].storeName+"'>"+bookUpdateInfoData[index].storeName+"</a>";
		rowSpan4.className="zz";rowSpan4.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(3,this.id,this.name)'  id='"+bookUpdateInfoData[index].authorId+"'  name='"+bookUpdateInfoData[index].authorName+"'>"+bookUpdateInfoData[index].authorName+"</a>";
		rowSpan5.className="sj";rowSpan5.innerHTML=arrayTime[1]+"/"+arrayTime[2];
		rowLi.appendChild(rowSpan1);
		rowLi.appendChild(rowSpan2);
		rowLi.appendChild(rowSpan3);
		rowLi.appendChild(rowSpan4);
		rowLi.appendChild(rowSpan5);
		rowUl.appendChild(rowLi);
});
		$("div.lastupdate").append(rowH2);
		$("div.lastupdate").append(rowUl);
}

function loadBookCreateData(bookCreateData){
		var rowH2=document.createElement("h2");
		var rowUl=document.createElement("ul");
		rowH2.innerHTML="最新小说";
	$.each(bookCreateData,function(index,n){
		var rowLi=document.createElement("li");
		var rowSpanLx=document.createElement("span");
		var rowSpanRx=document.createElement("span");
			rowSpanLx.className="lx";
			rowSpanRx.className="rx";
		rowSpanLx.innerHTML="["+bookCreateData[index].cataName+"]";
		rowSpanRx.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookCreateData[index].bookId+"'  name='"+bookCreateData[index].bookName+"'>"+bookCreateData[index].bookName+"</a>";
		rowLi.appendChild(rowSpanLx);
		rowLi.appendChild(rowSpanRx);
		rowUl.appendChild(rowLi);
});
		$("div.postdate").append(rowH2);
		$("div.postdate").append(rowUl);
}


function loadBookCataData(bookCataData){
	var count=0;
	var rowDiv;
	$.each(bookCataData,function(index,n){
			var rowUl=document.createElement("ul");
			if(count>-1&&count<4){
				rowDiv=$("div.tuijian:first")
				if(count==0){
					rowUl.className="l";
				}
			}else if(count>3&&count<8){
		rowDiv=$("div.tuijian:last")
		if(count==3){
			rowUl.className="l";
		}
	}else{
		return false;
	}
var bookItemData=bookCataData[index]
if(bookItemData.length==0){
	return true;
}
$.each(bookItemData,function(index,n){
	var rowLi=document.createElement("li");
	if(index==0){
		var rowCataLi=document.createElement("li");
		rowCataLi.className="t";
		rowCataLi.innerHTML="<h2><a href='javascript:void(0);'  onclick='parent.openHtml(0,this.id,this.name)'  id='"+bookItemData[index].cataId+"'  name='"+bookItemData[index].cataName+"'>"+bookItemData[index].cataName+"</a></h2>";
		rowUl.appendChild(rowCataLi);
	}
		rowLi.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookItemData[index].bookId+"'  name='"+bookItemData[index].bookName+"'>"+bookItemData[index].bookName+"</a>/<a target='_blank' href='javascript:void(0);'  onclick='parent.openHtml(3,this.id,this.name)'  id='"+bookItemData[index].authorId+"'  name='"+bookItemData[index].authorName+"'>"+bookItemData[index].authorName+"</a>";
		rowUl.appendChild(rowLi);
});
		rowDiv.append(rowUl);
		count=count+1;
});
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