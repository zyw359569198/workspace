window.onload =function(){
	
	$.ajax({
		type: 'GET',
		url: "/catagory/init",
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					//$("ul.nav_l").empty()
					var catagoryData=data.data.cgl;
					var modelData=data.data.mdl;
			$.each(catagoryData,function(index,n){
						//alert(catagoryData[index].cataNameEn)
			var rowLi=document.createElement("li");
			var child="<a href='javascript:void(0);'  onclick='openHtml(0,this.id,this.name)'  id='"+catagoryData[index].id+"'  name='"+catagoryData[index].cataName+"'>"+catagoryData[index].cataName+"</a>";
			rowLi.innerHTML=child;
			$("ul.nav_l").append(rowLi);
		});
			
	$.each(modelData,function(index,n){
				//alert(catagoryData[index].cataNameEn)
	var rowLi=document.createElement("li");
	var child="<a href='javascript:void(0);'  onclick='openHtml(1,this.id,this.name)'  id='"+modelData[index].id+"'  name='"+modelData[index].modelUrl+"'>"+modelData[index].modelName+"</a>";
	rowLi.innerHTML=child;
	$("ul.nav_r").append(rowLi);
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