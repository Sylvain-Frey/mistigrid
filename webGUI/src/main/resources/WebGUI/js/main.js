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
$( function() {
  
  var get = sylfrey.network.get,
      create = sylfrey.controller.create,
      update = sylfrey.controller.update,
      controls = sylfrey.controls;
   
   controls.init();
   var index = {};
   var layouts = {};
   
   // load index of all layouts, and create them all
   get("/layoutsIndex", function(layoutList) {
     
     $.each(layoutList, function(type, names) {
       $.each(names, function(i, name) {
         index[name] = type;
       });
     });
     
     // create all layouts
     create(index, layouts);
          
   });


  // trigger updates periodically
  setInterval(function() {update(layouts)}, 500);	

});
