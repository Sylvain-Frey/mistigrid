if (sylfrey == undefined) var sylfrey = {};

sylfrey.network = (function() { // package definition

    function trim(url) {        
	    // trim leading slash
	    if (url.indexOf('/')==0) return url.substring(1);
	    else return url;
    };

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
			url : trim(address),
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
			url : trim(address),
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
