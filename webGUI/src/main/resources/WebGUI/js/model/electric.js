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
        
        function Storage(prosumer, load, loadCapacity, state) {
          
          prosumer.type = "Storage";
          prosumer.load = load;
          prosumer.loadCapacity = loadCapacity;
          prosumer.state = state;
          
          var superCreate = prosumer.create;
          prosumer.create = function() {
            superCreate();
            prosumer.view.addClass("Storage");
            prosumer.view.loadDiv = $("<div class='loadDiv'></div>");
            prosumer.view.loadDiv.appendTo(prosumer.view);
            prosumer.view.stateDiv = $("<div class='stateDiv'></div>");
            prosumer.view.stateDiv.appendTo(prosumer.view);
            
          };
          
          prosumer.update = function(state) {
            prosumer.prosumption = state.prosumedPower;
            prosumer.state = state.state;
            prosumer.load = state.load;
            prosumer.loadCapacity = state.loadCapacity;
            prosumer.view.proDiv.text("P: " + -prosumer.prosumption.toFixed(0) + " W");
            prosumer.view.loadDiv.text("load: " + -prosumer.load.toFixed(0) + "/" + -prosumer.loadCapacity.toFixed(0) + " W");           
            prosumer.view.stateDiv.text("State: " + state.state);
            
            
            prosumer.view.removeClass("load0 load1 load2 load3 load4 load5");
            var loadRatio = prosumer.load / prosumer.loadCapacity;
            if (loadRatio<0.05) prosumer.view.addClass("load0");
            else if (loadRatio<0.25) prosumer.view.addClass("load1");
            else if (loadRatio<0.45) prosumer.view.addClass("load2");
            else if (loadRatio<0.65) prosumer.view.addClass("load3");
            else if (loadRatio<0.85) prosumer.view.addClass("load4");
            else prosumer.view.addClass("load5");
            
          };
          
          return prosumer;
        };

	// package contents
	return {
		Prosumer : Prosumer,
		Lamp : Lamp,
		Heater : Heater,
                Storage : Storage
	};
	
})();
