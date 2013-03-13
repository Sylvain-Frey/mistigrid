if (sylfrey == undefined) var sylfrey = {};

sylfrey.graph = (function() { // package definition
        
	//package imports
	var log = sylfrey.controls.log;
	
    function Graph(graphContainer, controlContainer) {

        var chart = new Highcharts.Chart({
            chart: {
                renderTo: graphContainer
            },
            title: {
                text: "Temperature & Power Prosumption"
            },
            xAxis: {
                title: {
                    text: "Time in seconds"
                },
            },
            yAxis: {
                title: {
                    text: "Temperature in °C, Prosumption in W"
                }
            },
            series: [],
        });
        
        chart.MAX_SIZE = 50;
        $("#sampleRange").val(chart.MAX_SIZE);
        $("#setSampleRange").click( function() {
            chart.MAX_SIZE = parseInt($("#sampleRange").val());
        });
        
        chart.checkSeries = function() {
            $.each(chart.series, function(index, serie) {
                while (serie.xData.length > chart.MAX_SIZE || serie.yData.length > chart.MAX_SIZE) {
                    serie.xData.shift(); 
                    serie.yData.shift(); 
                }
            });
        };
        return chart;
        
    }
    
    function GraphWebSocket(webSocketURL, chart) {
                
        var socket = new WebSocket(webSocketURL);
            
        socket.onopen = function() {
            socket.send("subscribe");    
        }
                   
        socket.onmessage = function(msg) {
            
            if (msg.data.substring(0,1) == "#") { // non-data message: ignore
                return;
            }
            var data = $.parseJSON(msg.data);
            if (!chart.get(data.type)) {
                chart.addSeries({
                    id: data.type,
                    name: data.type,
                    data: [[0,0]],
                    visible: false,
                    full: false
                });                    
            }              
            var series = chart.get(data.type);
                       
            var point = data.content;
            // reverse prosumption sign convention...
            if (data.type.indexOf("sumption") != -1) point = -point; 
            series.addPoint(point, false, series.data.length >= chart.MAX_SIZE);
                     
        }
                    
        socket.onclose = function(){
            socket.send("unsubscribe")
        }   
        
        return socket;
    }
    
    function displayGraph() {
    
        var host = window.document.location.hostname;
        var port = parseInt(window.document.location.port) + 1; //!! convention here !!
        var webSocketURL = "ws://" + host + ":" + port + "/traces";
    
        if(!("WebSocket" in window)){
            alert("WebSockets are not supported by your browser: you cannot visualise the graphs =(.");
            return;     
        }
            
        var chart = Graph("graphContainer");
            
        try{
            GraphWebSocket(webSocketURL, chart);                        
        } catch (e) {
            log(e);
            return;
        }
        
        var worker = function() {
            chart.checkSeries();
            chart.redraw();
            setTimeout(worker,1000);
        };
        
        worker();
    }
    
    $(displayGraph);
        
	// package contents
    return {
		Graph : Graph
 	};
    
})();
       
