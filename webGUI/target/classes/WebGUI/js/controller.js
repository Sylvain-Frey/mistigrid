if (sylfrey == undefined) var sylfrey = {};

sylfrey.controller = (function() { // package definition

	// package imports
	var Factory = sylfrey.model.Factory,
		get = sylfrey.network.get,
		post = sylfrey.network.post;

	function create(index, layouts) {

		$.each(index, function(name, type) {
			get("/layouts/" + type + "/" + name, function(specs) {
			    if (specs != null) specs.name = name; //dirty
			    var layout = Factory(specs)
				if (layout!=null) layouts[name] = layout;
			});			
		});

	};

	function update(layouts) {
	    
        get("/layouts/AllLayouts", function(allLayouts) {	    
	        $.each(allLayouts, function(id, state) {
			    layout = layouts[id].update(state);			    
			});
		});		

	};

	// package contents
	return {
		create : create,
		update : update
	};

})();
