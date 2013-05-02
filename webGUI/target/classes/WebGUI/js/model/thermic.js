if (sylfrey == undefined) var sylfrey = {};
if (sylfrey.model == undefined) sylfrey.model = {};

sylfrey.model.thermic = (function() { // package definition
	
	//package imports
	var post = sylfrey.network.post,
	    log = sylfrey.controls.log;
	
	function ThermicObject(layout, temperature) {
		
		layout.type = "ThermicObject";
		layout.temperature = temperature;
		
		var superCreate = layout.create;
		layout.create = function() {
		    superCreate();
		    layout.view.addClass("ThermicObject");
		    layout.view.tempDiv = $("<div class='tempDiv'></div>");
		    layout.view.tempDiv.appendTo(layout.view);
		};
		
		layout.update = function(state) {
			layout.temperature = state.currentTemperature;
			layout.view.tempDiv.text(layout.temperature.toFixed(1) + " \u00B0C");
		};
		
		return layout;
	};

    function Atmosphere(thermicObject) {
        
        thermicObject.type = "Atmosphere";
        
        var superCreate = thermicObject.create;
		thermicObject.create = function() {
		
		    superCreate();
		    thermicObject.view.addClass("Atmosphere");
		    
		    thermicObject.view.tempCtrl = $("<div class='tempCtrl'></div>");
		    thermicObject.view.tempCtrl.plus = $("<button class='tempCtrlPlus'>+</div>");
   		    thermicObject.view.tempCtrl.minus = $("<button class='tempCtrlMinus'>-</div>");
		    thermicObject.view.tempCtrl.appendTo(thermicObject.view);
   		    thermicObject.view.tempCtrl.plus.appendTo(thermicObject.view.tempCtrl);
   		    thermicObject.view.tempCtrl.minus.appendTo(thermicObject.view.tempCtrl);

		    thermicObject.view.tempCtrl.plus.click( function() {
                var address = "/layouts/AtmosphereLayout/" + thermicObject.name;
                post(address, {data : true});
            });
            
		    thermicObject.view.tempCtrl.minus.click( function() {
                var address = "/layouts/AtmosphereLayout/" + thermicObject.name;
                post(address, {data : false});
            });
            
		};
		
		return thermicObject;
        
    };

	function Opening(thermicObject, openned) {
	    thermicObject.type = "Opening";
		thermicObject.openned = openned;
		
        var superCreate = thermicObject.create;
		thermicObject.create = function() {		
		    superCreate();
		    thermicObject.view.nameDiv.text("");
		    thermicObject.view.addClass("Opening");
		    
		    thermicObject.view.click( function(e) {
                var address = "/layouts/OpeningLayout/" + thermicObject.name;
                post(address, {data : !thermicObject.isOpen});		        
		    });
		}
		
		thermicObject.update = function(state) {
			thermicObject.isOpen = state.isOpen;
            if (thermicObject.isOpen) {
                thermicObject.view.addClass("open");
                thermicObject.view.removeClass("closed");                
            } else {
                thermicObject.view.addClass("closed");
                thermicObject.view.removeClass("open");                                    
            }
		};
		
		return thermicObject;
		
	};

	// package contents
	return {
		ThermicObject : ThermicObject,
		Atmosphere : Atmosphere,
		Opening : Opening
	};
	
})();
