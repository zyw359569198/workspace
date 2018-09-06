window.onload =function(){
			$.ajax({
		type: 'POST',
		url: "/pageApi/hits/"+$("div.place").attr("value"),
		contentType: "application/json;cherset=utf-8",
		dataType: "json",
		asynchronous: true,
		success: function(data){					
		}
	});
}