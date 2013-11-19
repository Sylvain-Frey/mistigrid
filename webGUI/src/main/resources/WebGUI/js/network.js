/*******************************************************************************
 * Copyright (c) 2013 Sylvain Frey.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
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