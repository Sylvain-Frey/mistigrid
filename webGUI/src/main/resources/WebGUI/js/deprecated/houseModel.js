
/*  Script containing functions to communicate with the server.
*   author: Irvin Herrera Garza 
*/

var sylfrey = {
		
}

function sylfreySafeParse(json) {
  var safeJson = json.replace(/NaN/g,"0");
  try {
    return $.parseJSON(safeJson)
  } catch (e) { 
    return {parsingError : json}
  }
}

//Creates empty block of objects that will be later filled by generalUpdate()
var layoutObjectHandler = function(type, name, updateCallback){ 
  var address = "/layouts/" + type + "/" + name;
  var visualObjectHandler = null;
  //Get layout object info and display it. 
  $.ajax({
    type: 'GET',
    url: address,
    success: function(){},
    fail: function(){
      alert("Failed to retrieve contents from: "+address);
    },
    complete: function(data) {
      var layoutObject = sylfreySafeParse(data.responseText);
      if (layoutObject == null) return;
      //Apply data to visualObjectHandler
      var specs = {
          x: layoutObject.x,
          y: layoutObject.y,
          w: layoutObject.width,
          h: layoutObject.height,
          l: layoutObject.layer,
          iden: name,
          className: type,
          color: "#F42016"
      };
    
      // here are created the visual objects
      visualObjectHandler = visualObjectHandlerConstructor(specs);
      visualObjectHandler.drawObject();

    } //Close success function
  }); //Close AJAX call
  
  return {
    getAddress: function(){ return address; },
    getName: function(){ return name; },
    getType : function(){ return type; },
    getVisualObjectHandler : function(){ return visualObjectHandler; },
    update : function() {
      $.ajax({
        type: 'GET',
        url: address,
        success: function(){},
        fail: function(){
          alert("Failed to retrieve contents from: "+address);
        },
        complete: updateCallback // callback for updates
      }); //Close AJAX call
    }
  }; //Close return
}; //End of var


//atmosphereLayoutHandler inherits from layoutObjectHandler
var atmosphereLayoutHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);
      updateObjectView({iden: name, type: type, currentTemperature: layoutObject.currentTemperature});
    }
  );
};



//thermicObjectHandler inherits from layoutObjectHandler
var thermicObjectLayoutHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);
      updateObjectView({iden: name, type: type, currentTemperature: layoutObject.currentTemperature});
    }
  );
};


//heaterObjectHandler inherits from layoutObjectHandler
var heaterLayoutObjectHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);
      updateObjectView({type: type, iden: name, emissionPower: layoutObject.emissionPower});
    }
  );
};





//lampManagerLayoutObjectHandler inherits form layoutObjectHandler
var lampManagerLayoutObjectHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);  
      updateObjectView({type: type, iden: name, 
                isEconomising: layoutObject.isEconomising,
                status: layoutObject.status});
    }
  );  
};



//loadManagerLayoutObjectHandler inherits form layoutObjectHandler
var loadManagerLayoutObjectHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);  
      updateObjectView({type: type, iden: name, 
                maxConsumptionThreshold: layoutObject.maxConsumptionThreshold,
                prosumption: layoutObject.prosumption});
    }
  );  
};




//heaterManagerObjectHandler inherits form layoutObjectHandler
var heaterManagerLayoutObjectHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);  
      updateObjectView({type: type, iden: name,
                requiredTemperature: layoutObject.requiredTemperature, 
                isEconomizing: layoutObject.isEconomizing,
                status: layoutObject.status});
    }
  );  
};





//openingLayoutObjectHandler inherits form layoutObjectHandler
var openingLayoutObjectHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);         
      updateObjectView({type: type, iden: name, isClosed: layoutObject.isClosed, width: layoutObject.width, height: layoutObject.height});
    }
  );
};



//prosumerLayoutObjectHandler inherits form layoutObjectHandler
var prosumerLayoutHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);         
      updateObjectView({type: type, iden: name, prosumedPower: layoutObject.prosumedPower});
    }
  );
};


//lampLayoutObjectHandler inherits form layoutObjectHandler
var lampLayoutHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);         
      updateObjectView({type: type, iden: name, prosumedPower: layoutObject.prosumedPower, emissionPower: layoutObject.emissionPower});
    }
  );
};

//Function to get Index info
function loadIndexLayout(url){
  var layouts = [];
  $.ajax({
    url: url,
    type:'GET',
    success: function() {},
    complete: function(jqxhr){
      var indexInfo = sylfreySafeParse(jqxhr.responseText);
      $.each(indexInfo, function(key,value){
         for(i = 0; i<value.length; i++){
          switch(key){
            case "LampLayout":
              layouts.push(lampLayoutHandler(key,value[i]));
              break;              
            case "LampManagerLayout":
              layouts.push(lampManagerLayoutObjectHandler(key,value[i]));
              break;
            case "LoadManagerLayout":
              layouts.push(loadManagerLayoutObjectHandler(key,value[i]));
              break;
            case "ProsumerLayout":
              layouts.push(prosumerLayoutHandler(key,value[i]));
              break;
            case "AtmosphereLayout":
              layouts.push(atmosphereLayoutHandler(key,value[i]));
              break;
            case "ThermicObjectLayout":
              layouts.push(thermicObjectLayoutHandler(key,value[i]));
              break;
            case "HeaterLayout": 
              layouts.push(heaterLayoutObjectHandler(key,value[i]));
              break;
            case "HeaterManagerLayout": 
              layouts.push(heaterManagerLayoutObjectHandler(key,value[i]));
              break;
            case "OpeningLayout":
              layouts.push(openingLayoutObjectHandler(key,value[i]));
              break;
          }; 
        }
      }); //End each
      
    }, //End Complete callback
    fail: function(){
      alert("# error in loadIndexLayout: failed to load initial information, check your connection.");
    } //End Fail callback
  }); //End Ajax Call
}; //End of Index Handler

 

//Post data to server
function postData(address, data, metadata){
  $.ajax({
    type: 'POST',
    url: address,
    data: {data: data, metadata: metadata},
    dataType: "html",
    success:function() {} ,
    fail: function(){
      alert("Failed to retrieve contents from: " + address);
    },
    complete: function() { /* nothing should happen here */ }
  }); //Close AJAX call
};



//Create flag for timer. Used Closure to ensure the flag is not altered.
var flagConstructor = function(){
    var f = true;
    return {
      getFlag: function(){
        return f;
      },
      setFlag: function(){
        f = false;
      }
    };
};
  


//Function that gets the information that is to be updated.
function generalUpdate(){

  var address = "/layouts/AllLayouts";
  $.ajax({
    type: 'GET',
    url: address,
    success:function(){},
    fail: function(){
      alert("Failed to retrieve contents from: "+address);
    },
    complete: function(data) {
      var layoutObjects = sylfreySafeParse(data.responseText);
      $.each(layoutObjects, function(key,value){
        value.iden = key;
        updateObjectView(value);
      }); 
    } //Close success function
  }); //Close AJAX call
 
}


  



  
// Here is the main method
$(document).ready(function(){

  var flag = flagConstructor();
  //Activate console messages.
  systemMessage = false;

  var layouts = loadIndexLayout('/layoutsIndex');

  //Wait until all ajax requests are done
  $("body").ajaxStop( function(){
    if(flag.getFlag()){ //Check if it is already set.
      setInterval(generalUpdate,1000);
      flag.setFlag();
    }; 
  });

});