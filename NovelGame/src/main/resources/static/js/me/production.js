function GetQueryString(name)
    {
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = window.location.search.substr(1).match(reg);
         if(r!=null)return  unescape(r[2]); return null;
    }

window.onload =function(){
	$("div.wrapper",parent.document).show();
	$("div.nav",parent.document).show();
	var authorId=GetQueryString("authorId");
	var authorName=decodeURI(GetQueryString("authorName"));
		$.ajax({
		type: 'GET',
		url: "/book/initAuthorBookData?authorId="+authorId,
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					//$("ul.nav_l").empty()
					loadBookInfoData(data.data.abl);
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
		rowDiv2.innerHTML="<a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookInfoData[index].bookId+"'  name='"+bookInfoData[index].bookName+"' target=\"_blank\"><img src='"+bookInfoData[index].imageUrl+"' alt='"+bookInfoData[index].bookName+"' onerror=\"this.src='/images/nocover.jpg/'\" alt=''/></a>";
		rowDiv3.innerHTML="<h2><a href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+bookInfoData[index].bookId+"'  name='"+bookInfoData[index].bookName+"' target=\"_blank\">"+bookInfoData[index].bookName+"</a></h2><p></p><p>作者："+bookInfoData[index].authorName+"</p><p>类型：玄幻</p><p>简介："+bookInfoData[index].bookDesc+"</p>";
		rowDiv1.append(rowDiv2);
		rowDiv1.append(rowDiv3);
		$("div.ops_cover").append(rowDiv1);
});
}