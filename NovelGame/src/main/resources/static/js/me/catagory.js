function GetQueryString(name)
    {
         var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
         var r = window.location.search.substr(1).match(reg);
         if(r!=null)return  unescape(r[2]); return null;
    }

window.onload =function(){
	var catagoryId=GetQueryString("catagoryId");
	var catagoryName=decodeURI(GetQueryString("catagoryName"));
	$("#placeId").text(catagoryName);
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