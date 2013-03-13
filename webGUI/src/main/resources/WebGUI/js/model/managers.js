if (sylfrey == undefined) var sylfrey = {};
if (sylfrey.model == undefined) sylfrey.model = {};

sylfrey.model.managers = (function() { // package definition
	
    //package imports
	var post = sylfrey.network.post,
	    log = sylfrey.controls.log;
	
	function Manager(layout) {
	    
		layout.type = "Manager";
		
        var superCreate = layout.create;
		layout.create = function() {
		    superCreate();
		    layout.view.addClass("Manager");
		}
		
		return layout;
	    
	}
	
	function ProsumerManager(manager, status) {
		
		manager.type = "ProsumerManager";
		manager.status = status;
	
		var superCreate = manager.create;
		manager.create = function() {
		
		    superCreate();
		    manager.view.nameDiv.text("ProsumerManager");
		    manager.view.addClass("ProsumerManager");
		    manager.view.mode = $("<div class='mode'>Mode: </div>");
		    manager.view.mode.appendTo(manager.view);		    
		    manager.view.mode.toggle = $("<button class='modeButton'></button>");
		    manager.view.mode.toggle.appendTo(manager.view.mode);		    
		    manager.view.status = $("<div class='status'></div>");
		    manager.view.status.appendTo(manager.view);		    
		    manager.view.isEco = $("<div class='isEco'></div>");
		    manager.view.isEco.appendTo(manager.view);
		    
		    manager.view.mode.click( function(event) {
		        post("/layouts/" + manager.type + "Layout/" + manager.name, {data : "toggle", metadata : "status"});
		    });
		    
		};
	
		manager.update = function(state) {
			manager.status = state.status;
								
			if (state.status === "nonFlexible") {
			    manager.view.mode.toggle.text("comfort");
			    manager.view.mode.toggle.addClass("comfort");
			    manager.view.mode.toggle.removeClass("saving");
	    		manager.view.status.text("Status: non flexible");	
			    manager.view.addClass("nonFlexible");
			    manager.view.removeClass("flexible");
			} else if (state.status === "flexible") {
			    manager.view.mode.toggle.text("saving");
			    manager.view.mode.toggle.addClass("saving");
			    manager.view.mode.toggle.removeClass("comfort");
    			manager.view.status.text("Status: flexible");	
			    manager.view.addClass("flexible");
			    manager.view.removeClass("nonFlexible");
    	    }
    	        	    
			if (state.isEconomizing || state.isEconomising) {   
			    manager.view.isEco.text("Economising: yes");
		        manager.view.addClass("economising");
		        manager.view.removeClass("notEconomising");
			} else {
			    manager.view.isEco.text("Economising: no");
		        manager.view.addClass("notEconomising");
		        manager.view.removeClass("economising");
			}
		};
	
		return manager;
	};
	
	function LampManager(manager) {
	
	    manager.type = "LampManager";
	    
	    var superCreate = manager.create;
		manager.create = function() {		
		    superCreate();
		    manager.view.nameDiv.text("LampManager");
		}
		return manager;
	
	}

	function HeaterManager(manager, requiredTemperature) {
	
		manager.type = "HeaterManager";
		manager.requiredTemperature = requiredTemperature;
		
		var superCreate = manager.create;
		manager.create = function() {
		
		    superCreate();
		    manager.view.addClass("HeaterManager");
		    manager.view.nameDiv.text("HeaterManager");
		    manager.view.reqTempDiv = $("<div class='reqTempDiv'></div>");
		    manager.view.reqTempDiv.appendTo(manager.view);
		    		    
		};
		
		var superUpdate = manager.update;
		manager.update = function(state) {		
		    superUpdate(state);
			manager.requiredTemperature = state.requiredTemperature;
			if (state.isEconomizing || state.isEconomising) {   
    			manager.view.reqTempDiv.text("Goal: " + (manager.requiredTemperature.toFixed(1)-2) + " \u00B0C");
    	    } else {
    	        manager.view.reqTempDiv.text("Goal: " + manager.requiredTemperature.toFixed(1) + " \u00B0C");
    	    }
		};
		return manager;
	};
	
	function LoadManager(manager, prosumption, maxRequiredProsumption) {
	
		manager.type = "LoadManager";
		manager.prosumption = prosumption;
		manager.maxRequiredProsumption = maxRequiredProsumption;
	
		var superCreate = manager.create;
		manager.create = function() {
		    superCreate();
		    manager.view.addClass("LoadManager");
		    manager.view.prosDiv = $("<div class='prosDiv'></div>");
		    manager.view.prosDiv.appendTo(manager.view);
		    manager.view.maxProsDiv = $("<div class='maxProsDiv'></div>");
		    manager.view.maxProsDiv.appendTo(manager.view);
		    
		    
		    manager.view.threshCtrl = $("<div class='threshCtrl'>Change goal: </div>");
		    manager.view.threshCtrl.plus = $("<button class='threshCtrlPlus'>+</div>");
   		    manager.view.threshCtrl.minus = $("<button class='threshCtrlMinus'>-</div>");
		    manager.view.threshCtrl.appendTo(manager.view);
   		    manager.view.threshCtrl.plus.appendTo(manager.view.threshCtrl);
   		    manager.view.threshCtrl.minus.appendTo(manager.view.threshCtrl);

		    manager.view.threshCtrl.plus.click( function() {
                var address = "/layouts/LoadManagerLayout/" + manager.name;
                post(address, {data : false});
            });
            
		    manager.view.threshCtrl.minus.click( function() {
                var address = "/layouts/LoadManagerLayout/" + manager.name;
                post(address, {data : true});
            });
            
		};
		
		var superUpdate = manager.update;
		manager.update = function(state) {
		    superUpdate(state);
			manager.prosumption = state.prosumption;
			manager.maxRequiredProsumption = state.maxConsumptionThreshold;
			manager.view.prosDiv.text("House P : " + -manager.prosumption.toFixed(0) + " W");
			manager.view.maxProsDiv.text("Goal : House P < " + -manager.maxRequiredProsumption + " W");
		};
	
		return manager;
	};

	// package contents
	return {
	    Manager : Manager,
		ProsumerManager : ProsumerManager,
		LampManager : LampManager,
		HeaterManager : HeaterManager,
		LoadManager : LoadManager
	};
	
})();
