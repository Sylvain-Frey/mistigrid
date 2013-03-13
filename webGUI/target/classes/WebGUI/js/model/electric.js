if (sylfrey == undefined) var sylfrey = {};
if (sylfrey.model == undefined) sylfrey.model = {};

sylfrey.model.electric = (function() { // package definition
	
    //package imports
	var post = sylfrey.network.post,
	    log = sylfrey.controls.log;
	
	function Prosumer(layout, prosumption) {
		
		layout.type = "Prosumer";
		layout.prosumption = prosumption;
		
		var superCreate = layout.create;
		layout.create = function() {
		    superCreate();
		    layout.view.addClass("Prosumer");
		    layout.view.proDiv = $("<div class='proDiv'></div>");
		    layout.view.proDiv.appendTo(layout.view);
		};
		
		layout.update = function(state) {
			layout.prosumption = state.prosumedPower;
			layout.view.proDiv.text("Prosumption: " + -layout.prosumption.toFixed(2) + " W");
		};
		
		return layout;
	};
	
	function Lamp(prosumer, maxProsumption) {
	
		prosumer.type = "Lamp";	
		
		var superCreate = prosumer.create;
		prosumer.create = function() {
		    superCreate();
		    prosumer.view.addClass("Lamp");
		    prosumer.view.maxProDiv = $("<div class='maxProDiv'></div>");
		    prosumer.view.maxProDiv.appendTo(prosumer.view);
		    prosumer.view.click( function(event) {
		        prosumer.isOn = !prosumer.isOn;
		        post("/layouts/LampLayout/" + prosumer.name, {data : prosumer.isOn});
		    });
		};
		
		prosumer.update = function(state) {
		    prosumer.prosumption = state.emissionPower;
		    prosumer.maxProsumption = state.maxEmissionPower;
		    prosumer.view.proDiv.text("P: " + prosumer.prosumption.toFixed(0) + " W");
			prosumer.view.maxProDiv.text("Max P: " + prosumer.maxProsumption.toFixed(0) + " W");
		    
		    prosumer.view.removeClass("full");
		    prosumer.view.removeClass("eco");		    
		    prosumer.view.removeClass("off");
		    
    		var isOff = (state.emissionPower === 0);
	    	var isFull = (state.emissionPower === state.maxEmissionPower);
	    	var isEco = !isOff && !isFull;
	    	
		    if (isFull) prosumer.view.addClass("full");
		    else if (isEco) prosumer.view.addClass("eco");
		    else prosumer.view.addClass("off");		    
		};
         			    
	    return prosumer;
	};

	function Heater(prosumer, maxProsumption) {
	
		prosumer.type = "Heater";
		prosumer.name = "Heater";
		prosumer.maxProsumption = maxProsumption;
		
		var superCreate = prosumer.create;
		prosumer.create = function() {
		    superCreate();
		    prosumer.view.addClass("Heater");
		    prosumer.view.maxProDiv = $("<div class='maxProDiv'></div>");
		    prosumer.view.maxProDiv.appendTo(prosumer.view);
		    
		};
		
		prosumer.update = function(state) {
		    prosumer.prosumption = state.emissionPower;
		    prosumer.maxProsumption = state.maxEmissionPower;
		    prosumer.view.proDiv.text("P: " + prosumer.prosumption.toFixed(0) + " W");
			prosumer.view.maxProDiv.text("Max P: " + prosumer.maxProsumption.toFixed(0) + " W");
		};
		
		return prosumer;
	};

	// package contents
	return {
		Prosumer : Prosumer,
		Lamp : Lamp,
		Heater : Heater
	};
	
})();
