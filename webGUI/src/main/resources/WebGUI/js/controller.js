/*******************************************************************************
 * Copyright (c) 2013 EDF. This software was developed with the 
 * collaboration of Télécom ParisTech (Sylvain Frey).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Sylvain Frey - initial API and implementation
 ******************************************************************************/
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
			    var layout = Factory(specs);
				if (layout!=null) layouts[name] = layout;
			});			
		});

	};

	function update(layouts) {
	    
        get("/layouts/AllLayouts", function(allLayouts) {	    
	        $.each(allLayouts, function(id, state) {
			    layouts[id].update(state);
			});
		});		

	};

	// package contents
	return {
		create : create,
		update : update
	};

})();
