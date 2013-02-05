$(document).ready( function() {    

    // displaying available factories    
    
    var factoriesBox = document.getElementById("factories");
    
    $.ajax({
        type: "GET",
        url: "/webdeploy/servlet", 
        success: function(data) {
            fillFactoryList(data);
        },
        dataType: "json"
    });    
    
    function fillFactoryList(factories) {
        alert(factories);
        factories.forEach( function fillBox(element, index, array) {
            var factory = document.createElement("option");
            factory.text = element;
            factory.value = element;
            factoriesBox.options.add(factory);
        });
    }
    

    // deployment query
    
    $("#submitButton").click( function() {
        $.ajax({
            type: "POST",
            url: "/webdeploy/servlet/" + factoriesBox.options[factoriesBox.selectedIndex].text, 
            data: $('#content').value, 
            success: function(data) {
                alert(data);
            },
            dataType: "json"
        });
    });
    
    
 });