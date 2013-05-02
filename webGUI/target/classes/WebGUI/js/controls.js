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
