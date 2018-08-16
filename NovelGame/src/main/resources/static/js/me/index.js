window.onload =function(){
	
	$.ajax({
		type: 'GET',
		url: "/catagory/init",
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){
					//$("ul.nav_l").empty()
					var cataGoryData=data.data;
					$.each(cataGoryData,function(index,n){
						//alert(cataGoryData[index].cataNameEn)
			var rowLi=document.createElement("li");
			var child="<a href='javascript:void(0);'  onclick='openCatagory(this.id,this.name)'  id='"+cataGoryData[index].id+"'  name='"+cataGoryData[index].cataName+"'>"+cataGoryData[index].cataName+"</a>";
			rowLi.innerHTML=child;
			$("ul.nav_l").append(rowLi);
		});
		}
	});

}

function openCatagory(id,name){
	if(id==0){
		window.location="/"
		}
	var rightcontent=document.getElementById("rightcontent");
	rightcontent.src=encodeURI(encodeURI("/html/catagory.html?catagoryId="+id+"&catagoryName="+name));
	return true;
}