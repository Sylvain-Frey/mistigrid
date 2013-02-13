
/*Script containing functions and objects for drawing the GUI.
*author: Irvin Herrera Garza 
*/

//Information about window
var documentW = $(document).width();
var documentH = $(document).height();
var viewSizeW = 1000; //Used to scale with the actual view layout
var viewSizeH = 1000; //If layout changes, update this value.


//Creates labels and adds space for text
function createLabel(iden,className){

  var _text = "";
  //Not write full name on object
  if(className === "HeaterManagerLayout"){
    _text = "Heater_Manager";
  }else if(className === "HeaterLayout"){
    _text = "Heater";
  }else{
    _text = iden;
  };
  var label = $('<div>',{
    width:20,
    'class':label,
    height:20,
    text: _text,
    id: iden+"label"
  });
  label.css({
    border:"none",
    "font-weight":"bold"
  });
  label.text = $('<p>',{});
  label.text.appendTo(label);

  return label;
}




//Updates labels on drawn objects
function updateLabels(layoutObject){

  var aux = "";

  switch(layoutObject.type){
    case "ThermicObjectLayout": aux = layoutObject.currentTemperature.toFixed(2) + "&ordmC"; break;
    case "AtmosphereLayout": aux = layoutObject.currentTemperature.toFixed(2) + "&ordmC"; break;
    case "ProsumerLayout": aux = layoutObject.prosumedPower.toFixed(2) + "W"; break;
    case "LampLayout": aux = layoutObject.prosumedPower.toFixed(2) + "W"; break;
    case "HeaterManagerLayout": aux = "req:"+ layoutObject.requiredTemperature.toFixed(2) + "&ordmC"; break;
    case "HeaterLayout": aux = layoutObject.emissionPower.toFixed(2) + "W"; break;
  };

  $('#'+layoutObject.iden+"label > p").replaceWith("<p>"+aux+"</p>");
  
};





//Creates a <Div> element and positions it
function createBlock(iden,className,x,y,w,h,l){
  
  var block = $('<div>',{
    id:iden,
    "class": className
  }); 
  
  
  if (iden === "atmosphere") {
    block.css({
      top: "0px",
      left: "0px",
      bottom: "0px",
      right: "0px",
      zIndex: l
    });      
  } else {
    block.css({
      top: y + "px",
      left: x + "px",
      width: w + "px",
      height: h + "px",
      zIndex: l
    });
  }
    
  var containerHeight = $("#houseContainer").height();
  var containerWidth = $("#houseContainer").width();
  var childHeight = y + h;
  var childWidth = x + w;

  if (childHeight > containerHeight) {
    $("#houseContainer").height(childHeight + 30);
  }
  if (childWidth > containerWidth) {
    $("#houseContainer").width(childWidth + 30);
  }
  
    
  return block;
}; //end createBlock()




//Updates Color of drawnobjects
function updateObjectView(layoutObject){

  if(layoutObject.type==="ThermicObjectLayout" || layoutObject.type==="AtmosphereLayout") { 

    updateLabels(layoutObject);

    var drawnObject = $('#'+layoutObject.iden);

   //Test if object is already drawn, if it is, modify it.
   if(drawnObject.height() > 0 && drawnObject.width()>0){

    var temp = layoutObject.currentTemperature;
    var color = '#'+((50-Math.floor(temp))*0x300+0xFF0000).toString(16); //Scales colors from yellow to red
   
    $('#'+layoutObject.iden).animate({backgroundColor:color},600);
   };
   
  } else if(layoutObject.type==="OpeningLayout"){

    //Choose to draw vertical opening layout or horizontal (comaring width and height)
    if(layoutObject.isClosed === false){
      if(layoutObject.width > layoutObject.height){ 
        $('#'+layoutObject.iden).removeClass("openingLayoutClosedVertical").addClass("openingLayoutOpen");
      }else{
        $('#'+layoutObject.iden).removeClass("openingLayoutClosedHorizontal").addClass("openingLayoutOpen");
      };
    }else{
      if(layoutObject.width > layoutObject.height){
        $('#'+layoutObject.iden).removeClass("openingLayoutOpen").addClass("openingLayoutClosedVertical");
      }else{
         $('#'+layoutObject.iden).removeClass("openingLayoutOpen").addClass("openingLayoutClosedHorizontal");
      };//Close if  
    }; //Close if

  } else if (layoutObject.type==="LampLayout") {
    updateLabels(layoutObject);
    if (layoutObject.emissionPower > 0) {    
      $('#'+layoutObject.iden).removeClass("off").addClass("on");
      $('#'+layoutObject.iden).css({"background-color": "yellow"});
    } else {
      $('#'+layoutObject.iden).removeClass("on").addClass("off");
      $('#'+layoutObject.iden).css({"background-color": "grey"});
    }
  } else if (layoutObject.type==="ProsumerLayout") {
    updateLabels(layoutObject);
  } else if (layoutObject.type==="HeaterManagerLayout") {
    if (layoutObject.isEconomizing === true) {
        $('#'+layoutObject.iden).css({"background-color": "green"});
    } else {
        $('#'+layoutObject.iden).css({"background-color": "grey"});
    }
    updateLabels(layoutObject);
  } else if (layoutObject.type==="LampManagerLayout") {
    if (layoutObject.isEconomising === true) {
        $('#'+layoutObject.iden).css({"background-color": "green"});
    } else {
        $('#'+layoutObject.iden).css({"background-color": "grey"});
    }
    updateLabels(layoutObject);
  } else {
    //Update labels, if its other type of layout
    updateLabels(layoutObject);
  };
};




//Return string with correct size ratio
function getSizeRatio(layoutObjectSize,documentSize,viewSize){
  /*var aux = (layoutObjectSize * documentSize)/viewSize;
  return aux+"px";*/
  return (layoutObjectSize * documentSize)/viewSize;
};

//Elements for dialog boxes
function buttonGen(label,funct){
  return $('<button></button>').html(label).click(funct);
};
function formGen(name,text){
  return $('<form></form>',{id:name}).html(text+'<input type="text" name="input" /> <br><br><br>');
};

//Create Dialogboxes:
function createDialogBox(objectType,id,text,title){

  var address = "/layouts/"+objectType+"/"+id;

  var buttonCancel = buttonGen("Cancel");
  var buttonOk = buttonGen("Send");
  var form = formGen(id+"form",text); //Add text to dialogbox

  form.append(buttonCancel).append(buttonOk);

  function validate(event) {
    event.preventDefault();
    var value = $('#'+id+'form > input').val();
    postData(address,value); //Post data to server.
    $dialogBox.dialog("close");
    $dialogBox.empty().remove();
  }

  var $dialogBox = $('<div></div>').append(form).dialog({
    autoOpen: false,
    title : title //Title of dialog box
  }).keypress(function(e) {
    if (e.keyCode == $.ui.keyCode.ENTER) {
       validate(e);
    }
  });

  //Cancel button
  buttonCancel.click(function(event){
    event.preventDefault(); //Prevent from refreshing webpage when canceling.
    $dialogBox.dialog("close");
  });
  //Ok button
  buttonOk.click(function(event){
    validate(event);
  });

  return $dialogBox;
}//End createDialogBox()

//Creates visualObjectHandler creates visual object and takes care of on-click callbacks
var visualObjectHandlerConstructor = function(specs){

  //Adjust elements depending on window size
  var x = getSizeRatio(specs.x,documentW,viewSizeW); 
  var y = getSizeRatio(specs.y,documentH,viewSizeH);
  var w = getSizeRatio(specs.w,documentW,viewSizeW);
  var h = getSizeRatio(specs.h,documentH,viewSizeH);
  var l = specs.l;
  var iden = specs.iden;
  var className = specs.className;

  //Tweak to remove bug caused by atmosphere having a higher layer.
  //if(iden==="atmosphere"){
  //  l = -1;
  //};

  //Create elements
  var block= createBlock(iden,className,x,y,w,h,l);

  block.label = createLabel(iden,className);


  //Open-close openingLayoutObject
  if (className === "OpeningLayout"){
    //Address
    var address = "/layouts/"+className+"/"+iden;

    //Adds highlight
    $(block).mouseover(function(){
      $(this).addClass("highlight");
    }).mouseout(function(){
      $(this).removeClass("highlight");
    });

    block.click(function(){
      var currentStatus = $('#'+iden).attr('class');
      var value = '';
      if(currentStatus == "OpeningLayout openingLayoutClosedHorizontal highlight" || 
        currentStatus == "OpeningLayout openingLayoutClosedVertical highlight"){
        value = "true";
      }else{
        value = "false";
      };

      //Adds "clicking" animation
      $(this).addClass("insetshadow");
      var that = this;
      setTimeout(function(){
        $(that).removeClass("insetshadow");
      },200);
      postData(address,value);
    });

  };//End if


  //---- Adding dialog boxes:
  if (className==="HeaterManagerLayout"){
    //Adds highlight
    $(block).mouseover(function(){
      $(this).addClass("highlight");
    }).mouseout(function(){
      $(this).removeClass("highlight");
    });
    
    block.click(function(){
    
      var dialogHeaterManager = createDialogBox(className,iden,"Introduce new value(C):",iden);
      dialogHeaterManager.dialog('open');
      return false;
    });
    
  } 
  
  
  if(className === "HeaterLayout"){
    //Adds highlight
    $(block).mouseover(function(){
      $(this).addClass("highlight");
    }).mouseout(function(){
      $(this).removeClass("highlight");
    });

    block.click(function(){
      var dialogHeater = createDialogBox(className,iden,"Introduce new value(W):",iden);
      dialogHeater.dialog('open');
      return false;
    });
  };
  
  
  
  if(className === "LampLayout"){
    //Adds highlight
    $(block).mouseover(function(){
      $(this).addClass("highlight");
    }).mouseout(function(){
      $(this).removeClass("highlight");
    });

    block.click(function(){
      var address = "/layouts/"+className+"/"+iden;
      var currentState = $(this).hasClass("on");
      postData(address,!currentState);
      return false;
    });
  };
  
  
  if(className === "AtmosphereLayout"){
    block.mousedown(function(event){
      var address = "/layouts/"+className+"/"+iden;
      var increase = (event.which == 1);
      postData(address, increase);
      return false;
    });
  };
  
  
  return{
    drawObject: function(){ //Add element to '#houseContainer' object
      block.appendTo('#houseContainer');
      if(className !== "OpeningLayout"){block.label.appendTo("#"+iden);};
    },
  };
}; //End of visual object handler constructor