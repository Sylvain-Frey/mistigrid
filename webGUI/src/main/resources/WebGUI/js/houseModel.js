
/*  Script containing functions to communicate with the server.
*   author: Irvin Herrera Garza 
*/

var sylfreySafeParse = function(json) {
  var safeJson = json.replace(/NaN/g,"0");
  try {
    return $.parseJSON(safeJson)
  } catch (e) { 
    alert("# parsing error " + safeJson)
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
    success: function(){} ,
    fail: function(){
      alert("Failed to retrieve contents from: "+address);
    },
    complete: function(data) {
      var layoutObject = sylfreySafeParse(data.responseText.replace("NaN","null"));
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
      updateObjectView({type: type, iden: name, isEconomising: layoutObject.isEconomising});
    }
  );  
};





//heaterManagerObjectHandler inherits form layoutObjectHandler
var heaterManagerLayoutObjectHandler = function(type,name){
  return layoutObjectHandler(type,name,
    function(data) {
      layoutObject = sylfreySafeParse(data.responseText);  
      updateObjectView({type: type, iden: name, requiredTemperature: layoutObject.requiredTemperature, isEconomizing: layoutObject.isEconomizing});
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
  var atmosphereLayoutHandlersIndex = [];
  var thermicObjectHandlersIndex = [];
  var prosumerLayoutHandlersIndex = [];
  var lampLayoutHandlersIndex = [];
  var lampManagerLayoutHandlersIndex = [];
  var heaterLayoutHandlersIndex = [];
  var heaterManagerLayoutHandlersIndex = [];
  var openingLayoutHandlersIndex = [];
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
              var temp = lampLayoutHandler(key,value[i]);
              lampLayoutHandlersIndex.push(temp);
              break;              
            case "LampManagerLayout":
              var temp = lampManagerLayoutObjectHandler(key,value[i]);
              lampManagerLayoutHandlersIndex.push(temp);
              break;
            case "ProsumerLayout":
              var temp = prosumerLayoutHandler(key,value[i]);
              prosumerLayoutHandlersIndex.push(temp);
              break;
            case "AtmosphereLayout":
              var temp = atmosphereLayoutHandler(key,value[i]);
              atmosphereLayoutHandlersIndex.push(temp);
              break;
            case "ThermicObjectLayout":
              var temp = thermicObjectLayoutHandler(key,value[i]);
              thermicObjectHandlersIndex.push(temp);
              break;
            case "HeaterLayout": 
              var temp = heaterLayoutObjectHandler(key,value[i]);
              heaterLayoutHandlersIndex.push(temp);
              break;
            case "HeaterManagerLayout": 
              var temp = heaterManagerLayoutObjectHandler(key,value[i]);
              heaterManagerLayoutHandlersIndex.push(temp);
              break;
            case "OpeningLayout":
              var temp = openingLayoutObjectHandler(key,value[i]);
              openingLayoutHandlersIndex.push(temp);
              break;
          }; 
        }
      }); //End each
      
    }, //End Complete callback
    fail: function(){
      alert("# error in loadIndexLayout: failed to load initial information, check your connection.");
    } //End Fail callback
  }); //End Ajax Call
  return {
    getThermicObjectHandlers: function(){ 
      return thermicObjectHandlersIndex;
    },
    getHeaterLayoutHandlers: function(){
      return heaterLayoutHandlersIndex;
    },
    getHeaterManagerLayoutHandlers: function(){
      return heaterManagerLayoutHandlersIndex;
    }
  };
}; //End of Index Handler

 

//Post data to server
function postData(address, data){
  $.ajax({
    type: 'POST',
    url: address,
    data: {info: data},
    dataType: "html",
    success:function() {} ,
    fail: function(){
      alert("Failed to retrieve contents from: "+address);
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

  var indexes = loadIndexLayout('/layoutsIndex');

  //Wait until all ajax requests are done
  $("body").ajaxStop( function(){
    if(flag.getFlag()){ //Check if it is already set.
      setInterval(generalUpdate,1000);
      flag.setFlag();
    }; 
  });

});