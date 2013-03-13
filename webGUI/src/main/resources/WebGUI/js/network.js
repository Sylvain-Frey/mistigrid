if (sylfrey == undefined) var sylfrey = {};

sylfrey.network = (function() { // package definition

	function parse(json) {
		try {
			return $.parseJSON(json.replace(/NaN/g,"0"));
		} catch (e) { 
			return {parsingError : json};
		}
	};

	function get(address, callback) {
		$.ajax({
			type : "GET",
			url : address,
			success : function() {},
			fail : function() {},
			complete: function(resp) {
			    callback(parse(resp.responseText));
		    }
		});
	};

	function post(address, data, callback) {
		$.ajax({
			type : "POST",
			url : address,
			data : data,
			success : function() {},
			fail : function() {},
			complete: callback
		});		
	};
	
	// package contents
	return {
		get : get,
		post : post
	};

})();