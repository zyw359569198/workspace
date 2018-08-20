window.onload =function(){
	
	$.ajax({
		type: 'GET',
		url: "/book/init",
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					//$("ul.nav_l").empty()
					var bookData=data.data.bkl;	
					var bookCataData=data.data.tjl;
$.each(bookData,function(index,n){
		//alert(catagoryData[index].cataNameEn)
var rowDl=document.createElement("dl");
var rowDt=document.createElement("dt");
var rowDd=document.createElement("dd");
var rowH3=document.createElement("h3");
var rowP=document.createElement("p");
var rowSpan=document.createElement("span");
rowDt.innerHTML="<a href='javascript:void(0);'  onclick='openHtml(1,this.id,this.name)'  id='"+bookData[index].id+"'  name='"+bookData[index].bookName+"'><img src='"+bookData[index].imageUrl+"' alt='"+bookData[index].bookName+"' onerror=\"this.src='/images/nocover.jpg/'\" alt=''/></a>";
rowH3.innerHTML="<a href='javascript:void(0);'  onclick='openHtml(1,this.id,this.name)'  id='"+bookData[index].id+"'  name='"+bookData[index].bookName+"'>"+bookData[index].bookName+"</a>";
rowSpan.innerHTML="<a target='_blank' href='javascript:void(0);'  onclick='openHtml(2,this.id,this.name)'  id='"+bookData[index].authorId+"'  name='"+bookData[index].authorName+"'>"+bookData[index].authorName+"</a>";
rowP.innerHTML="&emsp;&emsp;"+bookData[index].bookDesc;
rowDd.appendChild(rowH3);
rowDd.appendChild(rowSpan);
rowDd.appendChild(rowP);
rowDl.appendChild(rowDt);
rowDl.appendChild(rowDd);
$("div.fengtui").append(rowDl);
});

$.each(bookCataData,function(index,n){
var rowUl=document.createElement("ul");
if(index==0){
	rowUl.className="l";
	$("div.tuijian").append(rowUl);
}else if (index==4){
}
var rowDl=document.createElement("dl");
var rowDt=document.createElement("dt");
var rowDd=document.createElement("dd");
var rowH3=document.createElement("h3");
var rowP=document.createElement("p");
var rowSpan=document.createElement("span");
rowDt.innerHTML="<a href='javascript:void(0);'  onclick='openHtml(1,this.id,this.name)'  id='"+bookData[index].id+"'  name='"+bookData[index].bookName+"'><img src='"+bookData[index].imageUrl+"' alt='"+bookData[index].bookName+"' onerror=\"this.src='/images/nocover.jpg/'\" alt=''/></a>";
rowH3.innerHTML="<a href='javascript:void(0);'  onclick='openHtml(1,this.id,this.name)'  id='"+bookData[index].id+"'  name='"+bookData[index].bookName+"'>"+bookData[index].bookName+"</a>";
rowSpan.innerHTML="<a target='_blank' href='javascript:void(0);'  onclick='openHtml(2,this.id,this.name)'  id='"+bookData[index].authorId+"'  name='"+bookData[index].authorName+"'>"+bookData[index].authorName+"</a>";
rowP.innerHTML="&emsp;&emsp;"+bookData[index].bookDesc;
rowDd.appendChild(rowH3);
rowDd.appendChild(rowSpan);
rowDd.appendChild(rowP);
rowDl.appendChild(rowDt);
rowDl.appendChild(rowDd);
$("div.fengtui").append(rowDl);
});
		}
	});

}

function openHtml(type,id,name){
	var rightcontent=document.getElementById("rightcontent");
	if(type==0){
		if(id==0){
			window.location="/"
			}
		rightcontent.src=encodeURI(encodeURI("/html/catagory.html?catagoryId="+id+"&catagoryName="+name));
	}else if (type==1){
		rightcontent.src=encodeURI(encodeURI(name));
	}
	
	return true;

}