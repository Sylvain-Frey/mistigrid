$(document).ready(function() {
//    var jsonService = new JsonRpc.ServiceProxy("/jsonrpc/traces", {
//        asynchronous: true,
//        methods: ["traces.listTraces", "traces.data"]
//    }
    if(!("WebSocket" in window)){
        alert("WebSockets not supported");
        return;     
    }
    
    var socket;
    var host = window.document.location.hostname;
    var port = parseInt(window.document.location.port) + 1; //!! convention here !!
    var webSocketURL = "ws://" + host + ":" + port + "/traces";
    var MAX_SIZE = 50;

    var chart = new Highcharts.Chart({
         chart: {
            renderTo: 'graphContainer'
         },
         title: {
            text: 'Temperature & Power Prosumption'
         },
         xAxis: {
            title: {
                text: 'Time in seconds'
            },
        },
        yAxis: {
            title: {
                text: 'Temperature in °C, Prosumption in W'
            }
        },
         series: []
      });
            
    try{
                var socket = new WebSocket(webSocketURL);
                
                socket.onopen = function(){
                    socket.send("subscribe");    
                }
                
                socket.onmessage = function(msg){
                
                    if (msg.data.substring(0,1) == "#") {
                        //alert(msg.data);
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
                    if (!series.full && series.data.length >= MAX_SIZE) {
                        series.full = true;
                    }
                    
                    series.addPoint(data.content, false, series.full);
                    
                }
                
                socket.onclose = function(){
                    socket.send("unsubscribe")
                }           
                    
    } catch(exception){
         alert('<p>Error'+exception);
    }
    
    var worker = function() {
        chart.redraw();
        setTimeout(worker,1000);
    };
    
    worker();
    
});
            
//JsonRpc.setAsynchronous(jsonService, true);

//var traces = {};



//var getData = function(trace) {
//        jsonService.traces.data({
//            params: [trace],
//            onSuccess: function(result) {
//                if (!chart.get(trace)) {
//                    chart.addSeries({
//                        id: trace,
//                        name: trace,
//                        data: [[1,1]]
//                    });
//                }
//                var series = new Array();
//                for (point in result) {
//                    var zePoint = result[point];
//                    var x = parseFloat(zePoint[0]);
//                    var y = parseFloat(zePoint[1]); 
//                    series.push([x,y]);
//                }
//                chart.get(trace).setData(series,false);
//            },
//            onException: function(e) {
//                alert(e);
//            }
//        });
//};

//var worker = function() {
//    jsonService.traces.listTraces({
//        params:[],
//        onSuccess: function(result) {
//            for (index in result) {
//                getData(result[index]);
//            }
//        },
//        onException: function(e) {
//            alert(e);
//        }
//    });
//    chart.redraw();
//    setTimeout(worker, 10000);    
//};

//worker();

//});