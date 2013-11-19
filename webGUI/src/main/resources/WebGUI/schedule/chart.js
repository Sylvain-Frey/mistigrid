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
function chartFactory() {
  
    var greenFill = {
      linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
      stops: [[0, '#65CB38'], [1, '#76C056']]
    };
    var greenLine = '#227216';
    var redFill = {
      linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
      stops: [[0, '#B94E4E'], [1, '#DA2206']]
    };
    var redLine = '#7F0E19';
    
    function createChart(container, chartSeries) {
      return new Highcharts.Chart({
        
        chart: {
     type: 'area',
          renderTo: container
        },
        
        title: {
          text: 'Alba Packets Schedule : ' + container
        },
            
        legend: {
          enabled: (function(){return container != 'districtChart';}())
        },
   
        xAxis: {
          tickInterval: 1,
          min: 0,
          max: chartSeries.scheduleSize,
          labels: {
            formatter: function () {
         if ((this.value*4*24/chartSeries.scheduleSize % 24) == 0) return this.value*24/chartSeries.scheduleSize;
            }
         }
        },
        
        yAxis: {
          min: 0,
          max:3000
        },
                    
        plotOptions: {
            area: {
               stacking: 'normal',
               fillColor: {
                  linearGradient: { x1: 0, y1: 0, x2: 0, y2: 1},
                  stops: [
                      [0, '#A4A4A1'],
                      [1, '#F7F7F7']
                  ]
                },
                lineWidth: 1,
                marker: {
                  enabled: false
                },
                shadow: false,
                states: {
                    hover: {
                      lineWidth: 5
                    }
                }
            },
            series: {
                animation: false
            }
       },
      
       series: chartSeries
   
      });
      
    }
    
    function parseSeries(resp) {
     
     var data = $.parseJSON(resp.responseText)
     var series = [];
     var chartSeries = [];
      
     $.each(data.schedule, function(device, serie) {
         var chartPoints = [];
         $.each(serie.points, function (x, y) {
      chartPoints[x] = {y : y, radius : 0};
         });
         var chartSerie = {
      type : 'area',
      name : device,
      data : chartPoints,
      color : redLine,
      fillColor : redFill
         };
         if (serie.status == "flexible") {
      chartSerie.fillColor = greenFill;
      chartSerie.color = greenLine;
         }
         series.push(chartSerie);
     });
     
     var goalData = [];
     $.each(data.goal, function (x, y) {
         goalData[x] = {y : y, radius : 0};        
     });
     var goalSerie = {
       type : 'line',
       name : 'goal',
       data : goalData,
       color : 'red'
     };
     
     chartSeries[0] = goalSerie;   
     for (var i=0; i<series.length; i++) chartSeries[series.length - i] = series[i];
      
     return chartSeries;     
     
    }
    
    function getSeries(path, callback) { $.ajax({
   type : "GET",
   url : path,
   success : function() {},
   fail : function() {},
   complete: function (resp) { callback(parseSeries(resp)); }
    });}
    
    function update(chart, series) {
        
       $.each(series, function (index, serie) {
         $.each(chart.series, function (index, chartSerie) {
        if (chartSerie.name == serie.name) {
          chartSerie.setData(serie.data, false);
          var options = chartSerie.options;
          options.fillColor = serie.fillColor; 
          chartSerie.update(options, false);
        }
         });
       });   
            chart.redraw();
   
    }
        
    function chart(path, graphContainer, updateButton) {
   getSeries(path, function (chartSeries) {
     var chart = createChart(graphContainer, chartSeries);
     $('#' + updateButton).click(function(e) {
       $.ajax({
         type : "POST",
         url : path,
         complete: function (resp) { update(chart, parseSeries(resp)); }
       });
     });
     setInterval(function() { 
        getSeries(path, function(series) { update(chart, series); })
        }, 1000);
   });
    }
     
    return {
      chart: chart
    }
    
}