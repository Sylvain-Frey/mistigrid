if (sylfrey == undefined) var sylfrey = {};

sylfrey.graph = (function() { // package definition
        
	//package imports
	var get = sylfrey.network.get,
	    log = sylfrey.controls.log;
    
            
    // statistics modules
    var meanModule = function(window_size) {// number of samples
      return {
        meanData : new Array(window_size), // samples
        meanCounter : 0, // points on the oldest sample, to be replaced by a new one
        push : function(point) { // store new data point  
          this.meanData[this.meanCounter] = point;
          this.meanCounter = (this.meanCounter + 1) % window_size;
        },
        mean : function() { //compute average of mean district consumption
          var mean = 0;
          for (var i=0; i<window_size; i++) {
            if (this.meanData[i] != null) mean += this.meanData[i];
          }
          mean = mean / window_size;
          return mean;
        }
      };
    };      
	
    function Graph(graphContainer, controlContainer) {

        var chart = new Highcharts.Chart({
            chart: {
                renderTo: graphContainer,
                type: 'spline',
                animation: false,//Highcharts.svg,
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
        
        // sample size control by user
        chart.MAX_SIZE = 250;
        $("#sampleRange").val(chart.MAX_SIZE);
        $("#setSampleRange").click( function() {
            chart.MAX_SIZE = parseInt($("#sampleRange").val());
        });
        
        
        // statistics module 
        chart.stats = {
          mean_house : meanModule(50),
          mean_agg_heaters : {}, //mean modules for heaters
          agg_lamps : {}, //lamp prosumptions
          mean_agg_lamps : {} //mean modules for lamps
        }
        
        chart.addSeries({
          id: "average_house_prosumption",
          name: "average_house_prosumption",
          data: [[0,0]],
          visible: true,
          full: false
        }); 
        
        chart.addSeries({
          id: "average_heaters_total",
          name: "average_heaters_total",
          data: [[0,0]],
          visible: true,
          full: false
        }); 
        
        chart.addSeries({
          id: "average_",
          name: "average_",
          data: [[0,0]],
          visible: false,
          full: false
        });
                
        chart.addSeries({
          id: "lamps_total",
          name: "lamps_total",
          data: [[0,0]],
          visible: true,
          full: false
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
          // !! name-based series selection is not only ugly but dangerous !!
          var visibility = 
              data.type.indexOf("maxConsumptionGoal") != -1; //||
              //data.type.indexOf("Aggregator") != -1 ;
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
              
          //////////////
          // stats stuff
          var stats = chart.stats;
          
          // compute mean house prosumption
          if (data.type.indexOf("Aggregator") != -1) { 
            var mean_house = stats.mean_house;
            mean_house.push(point);                  
            var mean = mean_house.mean();
            var meanSeries = chart.get("average_house_prosumption");
            meanSeries.addPoint(mean, false, meanSeries.data.length >= chart.MAX_SIZE);
          }
          
          // store heater prosumption
          if (data.type.indexOf("prosumption_heater") != -1) { 
            var mean_agg_heaters = stats.mean_agg_heaters;
            if (mean_agg_heaters[data.type] === undefined) {
              mean_agg_heaters[data.type] = meanModule(50);
            }
            mean_agg_heaters[data.type].push(point);
          }
          
          // store lamp prosumption         
          if (data.type.indexOf("prosumption_lamp") != -1) { 
            var agg_lamps = stats.agg_lamps;
            agg_lamps[data.type] = point;
            var mean_agg_lamps = stats.mean_agg_lamps;
            if (mean_agg_lamps[data.type] === undefined) {
              mean_agg_lamps[data.type] = meanModule(50);
            }
            mean_agg_lamps[data.type].push(point);
          }
                
                
        });
        
        // compute aggregated heater prosumption
        var mean_heater_consumption = 0;
        $.each(chart.stats.mean_agg_heaters, function(heater_name, mean_module) {
          mean_heater_consumption += mean_module.mean();
        });
        var meanSeries = chart.get("average_heaters_total");
        meanSeries.addPoint(mean_heater_consumption, false, meanSeries.data.length >= chart.MAX_SIZE);
        
        // compute aggregated lamp prosumption 
        var lamp_prosumption = 0;
        $.each(chart.stats.agg_lamps, function(lamp_name, prosumption) {
          lamp_prosumption += prosumption;
        });
        var lampSeries = chart.get("lamps_total");
        lampSeries.addPoint(lamp_prosumption, false, lampSeries.data.length >= chart.MAX_SIZE);
        
        var mean_lamp_consumption = 0;
        $.each(chart.stats.mean_agg_lamps, function(lamp_name, mean_module) {
          mean_lamp_consumption += mean_module.mean();
        });
        meanSeries = chart.get("average_");
        meanSeries.addPoint(mean_lamp_consumption, false, meanSeries.data.length >= chart.MAX_SIZE);
        
        
      });
             
    }
    
    function displayGraph() {
    
        var URL = "/traces";    
        var chart = Graph("graphContainer");
                    
        var worker = function() {
            restRefresher(URL, chart);
            chart.checkSeries();
            chart.redraw();
            setTimeout(worker,200);
        };
        
        worker();
    }
    
    $(displayGraph);
        
	// package contents
    return {
      Graph : Graph
    };
    
})();
       
