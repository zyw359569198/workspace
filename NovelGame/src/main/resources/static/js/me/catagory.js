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
}