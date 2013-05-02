if (sylfrey == undefined) var sylfrey = {};

sylfrey.graph = (function() { // package definition
        
	//package imports
	var get = sylfrey.network.get,
	    log = sylfrey.controls.log;
	
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
        
    function restRefresher(URL, chart) {
    
        get(URL, function(states) {
    	
		    $.each(states, function(index, data) {
    
                var visibility = 
                    data.type.indexOf("Goal") != -1 ||
                    data.type.indexOf("Aggregator") != -1 ;
                if (!chart.get(data.type)) {
                    chart.addSeries({
                        id: data.type,
                        name: data.type,
                        data: [[0,0]],
                        visible: visibility,
                        full: false
                    });                    
                }              
                var series = chart.get(data.type);
                           
                var point = data.content;
                
                // reverse prosumption sign convention...
                if (data.type.indexOf("sumption") != -1) point = -point; 
                series.addPoint(point, false, series.data.length >= chart.MAX_SIZE);
                
    		});

	    });
    }
    
    function displayGraph() {
    
        var host = window.document.location.hostname;
        var port = parseInt(window.document.location.port);// + 1; //!! convention here !!
        var URL = "/traces";
    
        var chart = Graph("graphContainer");
                    
        var worker = function() {
            restRefresher(URL, chart);
            chart.checkSeries();
            chart.redraw();
            setTimeout(worker,500);
        };
        
        worker();
    }
    
    $(displayGraph);
        
	// package contents
    return {
		Graph : Graph
 	};
    
})();
       
