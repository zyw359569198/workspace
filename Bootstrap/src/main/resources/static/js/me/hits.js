window.onload =function(){
			$.ajax({
		type: 'POST',
		url: "/pageApi/hits/"+$("#placeId").attr("value"),
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){					
		}
	});
}