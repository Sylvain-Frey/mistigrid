if (sylfrey == undefined) var sylfrey = {};

sylfrey.model = (function() { // package definition
	
	//package imports
	var log = sylfrey.controls.log;
	var ThermicObject = sylfrey.model.thermic.ThermicObject,
		Opening = sylfrey.model.thermic.Opening,
		Atmosphere = sylfrey.model.thermic.Atmosphere,
		Prosumer = sylfrey.model.electric.Prosumer,
		Lamp = sylfrey.model.electric.Lamp,
		Heater = sylfrey.model.electric.Heater,
		Manager = sylfrey.model.managers.Manager,
		ProsumerManager = sylfrey.model.managers.ProsumerManager,
		LampManager = sylfrey.model.managers.LampManager,
		HeaterManager = sylfrey.model.managers.HeaterManager,
		LoadManager = sylfrey.model.managers.LoadManager;
	 	
	
	function Layout(name, x, y, width, height, layer) { 
	
	    var scalingFactor = 1.4;
		var _x = scalingFactor*x;
		var _y = scalingFactor*y;
		var _width = scalingFactor*width;		
		var _height = scalingFactor*height;
				
	    var layout = {
			type : "Layout",
			name : name,
			x : _x,
			y : _y,
			width : _width,
			height : _height,
			layer : layer,
			view : null,
			create : function() {
				
				layout.view = $("<div>", {
					id : layout.name,
					"class" : layout.type
				});
				layout.view.appendTo("#houseContainer");
				
				// dynamic update of houseContainer size
				var maxH = $("#houseContainer").height();
				var maxW = $("#houseContainer").width();
				var newH = _y + _height;
				var newW = _x + _width;				
				if (newH > maxH) $("#houseContainer").height(newH);
				if (newW > maxW) $("#houseContainer").width(newW);
				
				layout.view.css({
					top : _y + "px",
					left : _x + "px",
					width : _width + "px",
					height : _height + "px",
					zIndex : layer					
				});
				layout.view.nameDiv = $("<div class='nameDiv'></div>");
        	    layout.view.nameDiv.appendTo(layout.view);
				layout.view.nameDiv.text(layout.name);
				
			},
			update : function(state) {
				
			},
			destroy : function() {
				layout.view.remove(); 
				layout.view = null;
			}
		};
	    
		return layout;
		
	};

	function Factory(specs) {
	
	    if (specs == null) return null;
	    
		var layout = {};
		var baseLayout = Layout(specs.name, specs.x, specs.y, specs.width, specs.height, specs.layer); 

		switch(specs.type){
		
		case "Layout":
			layout = baseLayout;
			break;            
			
		case "ThermicObjectLayout":
			layout = ThermicObject(
					baseLayout,
					specs.temperature);
			break;
		case "AtmosphereLayout":
			layout = Atmosphere(
			            ThermicObject(
        		        	baseLayout,
		        			specs.temperature));
			break;
		case "OpeningLayout":
			layout = Opening(
					ThermicObject(
							baseLayout,
							specs.temperature),
						specs.openned);
			break;
			
		case "LampLayout":
			layout = Lamp(
			            Prosumer(
			                baseLayout,
			                specs.prosumedPower), 
			            -specs.maxEmissionPower);
			break;              
		case "ProsumerLayout":
			layout = Prosumer(
					baseLayout, 
					specs.prosumption);
			break;
		case "HeaterLayout": 
			layout = Heater(
					Prosumer(
							baseLayout,
							specs.emissionPower),
						specs.maxEmissionPower);
			break;
			
			
		case "LampManagerLayout":
			layout = LampManager(
			            ProsumerManager(
    			            Manager(baseLayout),
	    		            specs.status));
			break;
		case "LoadManagerLayout":
			layout = LoadManager(
			            Manager(baseLayout),
			            specs.prosumption,
			            specs.maxConsumptionThreshold);
			break;
		case "ProsumerManagerLayout":
			layout = ProsumerManager(
					    Manager(baseLayout),
					    specs.status);
			break;
		case "HeaterManagerLayout": 
			layout = HeaterManager(
					ProsumerManager(
							Manager(baseLayout),
							specs.status),
						specs.requiredTemperature);
			break;
		default : 
		    log("# switch didn't match " + specs.type);
		    return null;
		}
		
		layout.create();
		return layout;
		
	};
	
	// package contents
	return {
		Layout : Layout,
		Factory : Factory
	};

})();
