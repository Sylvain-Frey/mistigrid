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
if (sylfrey.model == undefined) sylfrey.model = {};

sylfrey.model.district = (function() { // package definition

  //package imports
  var post = sylfrey.network.post,
      log = sylfrey.controls.log;        

  function HouseManager(manager, prosumption, maxRequiredProsumption) {
                  
      manager.type = "LoadManager";
      manager.prosumption = prosumption;
      manager.maxRequiredProsumption = maxRequiredProsumption;
      
      var superCreate = manager.create;
      manager.create = function() {
        superCreate();
        manager.view.addClass("LoadManager");
        manager.view.addClass("flexible");
        manager.view.prosDiv = $("<div class='prosDiv'></div>");
        manager.view.prosDiv.appendTo(manager.view);
        manager.view.maxProsDiv = $("<div class='maxProsDiv'></div>");
        manager.view.maxProsDiv.appendTo(manager.view);
      };
      
      manager.update = function(state) {
        manager.prosumption = state.prosumption;
        manager.maxRequiredProsumption = state.maxConsumptionThreshold;
        manager.view.prosDiv.text("House P : " + -manager.prosumption.toFixed(0) + " W");
        manager.view.maxProsDiv.text("Goal : House P < " + -manager.maxRequiredProsumption + " W");   
        if (state.status == "flexible") {          
          manager.view.addClass("notEconomising");
          manager.view.removeClass("economising");
        } else {
          manager.view.addClass("economising");
          manager.view.removeClass("notEconomising");
        }
      };
      
      return manager;
    };
  
  // package contents
  return {
    HouseManager : HouseManager
  };
  
})();