
window.onload =function(){
	
	$.ajax({
		type: 'GET',
		url: "/catagory/init",
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
			$("div.lf").show();
			$("div.wrapper").show();
			$("div.nav").show();
					//$("ul.nav_l").empty()
					var catagoryData=data.data.cgl;
					var modelData=data.data.mdl;
			$.each(catagoryData,function(index,n){
						//alert(catagoryData[index].cataNameEn)
			var rowLi=document.createElement("li");
			var child="<a href='javascript:void(0);'  onclick='openHtml(0,this.id,this.name)'  id='"+catagoryData[index].cataId+"'  name='"+catagoryData[index].cataName+"'>"+catagoryData[index].cataName+"</a>";
			rowLi.innerHTML=child;
			$("ul.nav_l").append(rowLi);
		});
			
	$.each(modelData,function(index,n){
				//alert(catagoryData[index].cataNameEn)
	var rowLi=document.createElement("li");
	var child="<a href='javascript:void(0);'  onclick='openHtml(1,this.id,this.name)'  id='"+modelData[index].modelId+"'  name='"+modelData[index].modelUrl+"'>"+modelData[index].modelName+"</a>";
	rowLi.innerHTML=child;
	$("ul.nav_r").append(rowLi);
});
		}
	});

}

function openHtml(type,id,name,bookId){
	$("div.wrapper").show();
	$("div.nav").show();
	var rightcontent=document.getElementById("rightcontent");
	if(type==0){
		if(id==0){
			window.location="/"
			}
		rightcontent.src=encodeURI(encodeURI("/html/catagory.html?catagoryId="+id+"&catagoryName="+name));
	}else if (type==1){
		rightcontent.src=encodeURI(encodeURI(name));
	}else if(type==2){
		rightcontent.src=encodeURI(encodeURI("/html/book.html?bookId="+id+"&bookName="+name));
	}else if(type==3){
		rightcontent.src=encodeURI(encodeURI("/html/production.html?authorId="+id+"&authorName="+name));
	}else if(type==4){
		$("div.wrapper").hide();
		$("div.nav").hide();
		rightcontent.src=encodeURI(encodeURI("/html/store.html?storeId="+id+"&storeName="+name+"&bookId="+bookId));
	}
	
	return true;

}