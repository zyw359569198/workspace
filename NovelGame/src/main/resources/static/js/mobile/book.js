function changeClick(value){
	pageSplit(100,value);
}
function pageSplit(pageSize,pageNum){
	cataNameEn=$("div.block").attr("name");
	$.ajax({
	type: 'GET',
	url: "/mobile/mobileApi/book/"+cataNameEn+"/"+pageSize+"/"+pageNum,
	contentType: "application/json;cherset=utf-8",
	dataType: "json",
	asynchronous: true,
	success: function(data){
				$("ul.chapter").empty()
				loadBookStoreInfoData(data.data.bul.list);
	}
});
}

function loadBookStoreInfoData(){
	
}