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
	$("div.wrapper",parent.document).hide();
	$("div.nav",parent.document).hide();
	var storeId=GetQueryString("storeId");
	var bookId=GetQueryString("bookId");
	var storeName=decodeURI(GetQueryString("storeName"));
		$.ajax({
		type: 'GET',
		url: "/store/init?storeId="+storeId+"&bookId="+bookId,
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					//$("ul.nav_l").empty()
					loadStoreData(data.data.sdl[0]);
					iframeHeight();
		}
	});
}

function loadStoreData(storeData){
	var rowH1=document.createElement("h1");
	rowH1.innerHTML=storeData.storeName;
	var rowDiv=document.createElement("div");
	rowDiv.className="pereview";
	if(storeData.preStoreId==0&&storeData.nextStoreId!=0){
			rowDiv.innerHTML="   <a class='back' href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+storeData.bookId+"'  name='' target='_top'>返回目录</a><a  href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData.bookId+"\")'  id='"+storeData.nextStoreId+"'  name='"+storeData.storeName+"' target='_top' >下一章</a> ";
	}else if(storeData.nextStoreId==0&&storeData.preStoreId!=0){
			rowDiv.innerHTML="<a  href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData.bookId+"\")'  id='"+storeData.preStoreId+"'  name='"+storeData.storeName+"' target='_top' >上一章</a>    <a class='back' href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+storeData.bookId+"'  name='' target='_top'>返回目录</a>";
	}else if(storeData.preStoreId==0&&storeData.nextStoreId!==0){
			rowDiv.innerHTML="   <a class='back' href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+storeData.bookId+"'  name='' target='_top'>返回目录</a>";
	}else{
			rowDiv.innerHTML="<a  href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData.bookId+"\")'  id='"+storeData.preStoreId+"'  name='"+storeData.storeName+"' target='_top' >上一章</a>    <a class='back' href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+storeData.bookId+"'  name='' target='_top'>返回目录</a><a  href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData.bookId+"\")'  id='"+storeData.nextStoreId+"'  name='"+storeData.storeName+"' target='_top' >下一章</a> ";
	}
	var rowDiv2=document.createElement("div");
	rowDiv2.className="yd_text2";
	rowDiv2.innerHTML=storeData.storeContent;
	var rowDiv3=document.createElement("div");
	rowDiv3.className="pereview";
	if(storeData.preStoreId==0&&storeData.nextStoreId!=0){
		rowDiv3.innerHTML="   <a class='back' href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+storeData.bookId+"'  name='' target='_top'>返回目录</a><a  href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData.bookId+"\")'  id='"+storeData.nextStoreId+"'  name='"+storeData.storeName+"' target='_top' >下一章</a> ";
}else if(storeData.nextStoreId==0&&storeData.preStoreId!=0){
	rowDiv3.innerHTML="<a  href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData.bookId+"\")'  id='"+storeData.preStoreId+"'  name='"+storeData.storeName+"' target='_top' >上一章</a>    <a class='back' href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+storeData.bookId+"'  name='' target='_top'>返回目录</a>";
}else if(storeData.preStoreId==0&&storeData.nextStoreId!==0){
	rowDiv3.innerHTML="   <a class='back' href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+storeData.bookId+"'  name='' target='_top'>返回目录</a>";
}else{
	rowDiv3.innerHTML="<a  href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData.bookId+"\")'  id='"+storeData.preStoreId+"'  name='"+storeData.storeName+"' target='_top' >上一章</a>    <a class='back' href='javascript:void(0);'  onclick='parent.openHtml(2,this.id,this.name)'  id='"+storeData.bookId+"'  name='' target='_top'>返回目录</a><a  href='javascript:void(0);'  onclick='parent.openHtml(4,this.id,this.name,\""+storeData.bookId+"\")'  id='"+storeData.nextStoreId+"'  name='"+storeData.storeName+"' target='_top' >下一章</a> ";
}
	$("div.novel").append(rowH1);
	$("div.novel").append(rowDiv);
	$("div.novel").append(rowDiv2);
	$("div.novel").append(rowDiv3);
}