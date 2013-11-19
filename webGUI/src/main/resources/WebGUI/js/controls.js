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

sylfrey.controls = (function() { // package definition

    // font control buttons
    function init() {
     
        $("#increaseFontSize").click(function() {
    	    var fontSize = parseInt($("#fontSize").text()) + 1;
        	$("#fontSize").text(fontSize);
        	$("#houseContainer").css("font-size", fontSize + "px")
        });
    
        $("#decreaseFontSize").click(function() {
        	var fontSize = parseInt($("#fontSize").text()) - 1;
        	$("#fontSize").text(fontSize);
        	$("#houseContainer").css("font-size", fontSize + "px")
        });
        
    };
    
    function log(msg) {
        var newLine = $("<div><div>")
        newLine.text(msg);
        newLine.appendTo($("#console"));
    }
    
    //package contents
    return {
        init : init,
        log : log
    };

})();
