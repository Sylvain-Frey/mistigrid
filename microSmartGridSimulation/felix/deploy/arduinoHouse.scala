import java.util.{ List => JList }
import scala.collection.JavaConversions._
import fr.tpt.s3.misTiGriD.conf._

object arduinoHouse {
    
  val atmosphere = "atmosphere"
  val atmosphereModel = Atmosphere( name=atmosphere, isManual=true, temperature=20, minTemperature=12, maxTemperature=22)

  val aggregator = "arduinoHouseAggregator"
  val aggregatorModel = Aggregator(aggregator, aggregator, false, 
  "akka://MisTiGriD@localhost:4005/user/remotedistrictAggregator")
  
  val aggregatorController = "arduinoAggregatorController"
  val loadTopic = "arduinoLoadTopic"
  val loadHierarch = "arduinoLoadHierarch"
             
  val kitchen = "kitchen"
  val bathroom = "bathroom"
  val parents_room = "parents_room"
  val children_room = "children_room"
  val visitors_room = "visitors_room"
  val livingroom = "livingroom"
  val entrance = "entrance"
  val wc = "wc"

  val lan = "wall_lan"
  val ean  = "wall_ean"
  val eae = "wall_eae"
  val law = "wall_law"
  val le = "wall_le"
  val ec = "wall_ec"
  val can = "wall_can"
  val cae = "wall_cae"
  val pc = "wall_pc"
  val ep = "wall_ep"
  val pae  = "wall_pae"
  val lwn  = "wall_lwn"
  val ews = "wall_ews"
  val lwe  = "wall_lwe"
  val eww  = "wall_eww"
  val eb  = "wall_eb"
  val wbe  = "wall_wbe"
  val bp  = "wall_bp"
  val lk  = "wall_lk"
  var lv = "wall_lv"
  val wv  = "wall_wv"
  val wbs  = "wall_wbs"
  val akw  = "wall_akw"
  val kv  = "wall_kv"
  val bv  = "wall_bv"
  val aks  = "wall_aks"
  val av  = "wall_av"
  val ba  = "wall_ba"
  val pas  = "wall_pas"

  val heater_K = "heater_" + kitchen
  val heater_V = "heater_" + visitors_room 
  val heater_C = "heater_" + children_room
  val heater_P = "heater_" + parents_room
  val heater_LR1 = "heater_" + livingroom + "_1"
  val heater_LR2 = "heater_" + livingroom + "_2"
  val heater_B = "heater_" + bathroom
  val heater_E = "heater_" + entrance

  val walls = Map[String, Wall](
    lan -> Wall(0.01f, false, 0.05f, 3, List(livingroom, atmosphere)),
    ean -> Wall(0.01f, false, 0.05f, 3, List(entrance, atmosphere)),
    eae -> Wall(0.01f, false, 0.05f, 3, List(entrance, atmosphere)),
    law -> Wall(0.01f, false, 0.05f, 3, List(livingroom, atmosphere)),
    le  -> Wall(0.01f, false, 0.05f, 3, List(livingroom, entrance)),
    ec  -> Wall(0.01f, false, 0.05f, 3, List(entrance, children_room)),
    can -> Wall(0.01f, false, 0.05f, 3, List(children_room, atmosphere)),
    cae -> Wall(0.01f, false, 0.05f, 3, List(children_room, atmosphere)),
    pc  -> Wall(0.01f, false, 0.05f, 3, List(parents_room, children_room)),
    ep  -> Wall(0.01f, false, 0.05f, 3, List(entrance, parents_room)),
    pae -> Wall(0.01f, false, 0.05f, 3, List(parents_room, atmosphere)),
    lwn -> Wall(0.01f, false, 0.05f, 3, List(livingroom, wc)),
    ews -> Wall(0.01f, false, 0.05f, 3, List(entrance, wc)),
    lwe -> Wall(0.01f, false, 0.05f, 3, List(livingroom, wc)),
    eww -> Wall(0.01f, false, 0.05f, 3, List(entrance, wc)),
    eb  -> Wall(0.01f, false, 0.05f, 3, List(entrance, bathroom)),
    wbe -> Wall(0.01f, false, 0.05f, 3, List(wc, bathroom)),
    bp  -> Wall(0.01f, false, 0.05f, 3, List(bathroom, parents_room)),
    lk  -> Wall(0.01f, false, 0.05f, 3, List(livingroom, kitchen)),
    lv  -> Wall(0.01f, false, 0.05f, 3, List(livingroom, visitors_room)),
    wv  -> Wall(0.01f, false, 0.05f, 3, List(wc, visitors_room)),
    wbs -> Wall(0.01f, false, 0.05f, 3, List(wc, bathroom)),
    akw -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, kitchen)),
    kv  -> Wall(0.01f, false, 0.05f, 3, List(kitchen, visitors_room)),
    bv  -> Wall(0.01f, false, 0.05f, 3, List(bathroom, visitors_room)),
    aks -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, kitchen)),
    av  -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, visitors_room)),
    ba  -> Wall(0.01f, false, 0.05f, 3, List(bathroom, atmosphere)),
    pas -> Wall(0.01f, false, 0.05f, 3, List(parents_room, atmosphere)))

  val rooms = Map[String, TH](
      kitchen    	-> TH( temperature=24, heatCapacity=14, walls=List(lk, akw, kv, aks, heater_K)),
      bathroom   	-> TH( temperature=24, heatCapacity=17, walls=List(eb, wbs, wbe, bp, bv, ba, heater_B)),
      parents_room	-> TH( temperature=24, heatCapacity=42, walls=List(pc, ep, pae, bp, pas, heater_P)),
      children_room	-> TH( temperature=24, heatCapacity=18, walls=List(ec, can, cae, pc, heater_C)),
      visitors_room	-> TH( temperature=24, heatCapacity=16, walls=List(lv, wv, kv, bv, av, heater_V)),
      livingroom 	-> TH( temperature=24, heatCapacity=90, walls=List(lan, law, le, lwn, lwe, lk, lv, heater_LR1, heater_LR2)),
      entrance   	-> TH( temperature=24, heatCapacity=23, walls=List(ean, eae, le, ec, ep, ews, eww, eb, heater_E)),
      wc         	-> TH( temperature=24, heatCapacity=8,  walls=List(lwn, ews, lwe, eww, wbe, wv, wbs)))
      
  val heaters = Map[String, Tuple2[Heater, HeaterManager]](
      heater_K	   -> (Heater(0, 0.05f, 0.1f, 400f, aggregator, kitchen),	HeaterManager(heater_K   + "_manager", 50, 22, true, loadTopic, aggregatorController, loadHierarch, heater_K,   kitchen)),
      heater_V     -> (Heater(0, 0.05f, 0.1f, 400f, aggregator, visitors_room),	HeaterManager(heater_V   + "_manager", 50, 22, true, loadTopic, aggregatorController, loadHierarch, heater_V,   visitors_room)),
      heater_C     -> (Heater(0, 0.05f, 0.1f, 400f, aggregator, children_room),	HeaterManager(heater_C   + "_manager", 50, 22, true, loadTopic, aggregatorController, loadHierarch, heater_C,   children_room)),
      heater_P     -> (Heater(0, 0.05f, 0.1f, 400f, aggregator, parents_room),	HeaterManager(heater_P   + "_manager", 50, 22, true, loadTopic, aggregatorController, loadHierarch, heater_P,   parents_room)),
      heater_LR1   -> (Heater(0, 0.05f, 0.1f, 400f, aggregator, livingroom),	HeaterManager(heater_LR1 + "_manager", 50, 22, true, loadTopic, aggregatorController, loadHierarch, heater_LR1, livingroom)),
      heater_LR2   -> (Heater(0, 0.05f, 0.1f, 400f, aggregator, livingroom),	HeaterManager(heater_LR2 + "_manager", 50, 22, true, loadTopic, aggregatorController, loadHierarch, heater_LR2, livingroom)),
      heater_B     -> (Heater(0, 0.05f, 0.1f, 400f, aggregator, bathroom),	HeaterManager(heater_B   + "_manager", 50, 22, true, loadTopic, aggregatorController, loadHierarch, heater_B,   bathroom)),
      heater_E     -> (Heater(0, 0.05f, 0.1f, 400f, aggregator, entrance),	HeaterManager(heater_E   + "_manager", 50, 22, true, loadTopic, aggregatorController, loadHierarch, heater_E,   entrance)))
   

  ////////////
  // LAYOUTS //
  ////////////

  val s = 40 // scale 
  val o = 80 // offset

  val roomLayouts = Map[String, Dim](
    kitchen    		-> Dim( x=0*s+o,  y=10*s+o,  width=6*s,  height=4*s,   layer=5),
    bathroom   		-> Dim( x=11*s+o, y=9*s+o,   width=5*s,  height=4*s,   layer=5),
    parents_room	-> Dim( x=16*s+o, y=8*s+o,   width=6*s,  height=5*s,   layer=5),
    children_room	-> Dim( x=16*s+o, y=4*s+o,   width=6*s,  height=4*s,   layer=5),
    visitors_room	-> Dim( x=6*s+o,  y=10*s+o,  width=5*s,  height=4*s,   layer=5),
    livingroom 		-> Dim( x=0*s+o,  y=0*s+o,   width=11*s, height=10*s,  layer=5),
    entrance   		-> Dim( x=11*s+o, y=4*s+o,   width=5*s,  height=5*s,   layer=5),
    wc         		-> Dim( x=10*s+o, y=7*s+o,   width=3*s,  height=3*s,   layer=7))  

  val heaterLayouts = Map[String, Tuple2[String,Pos]](
    heater_K 	-> (kitchen, 	   Pos(  1*s+o, 10*s+o, 10)),
    heater_V 	-> (visitors_room, Pos(  9*s+o, 10*s+o, 10)),
    heater_C 	-> (children_room, Pos( 19*s+o,  4*s+o, 10)),
    heater_P	-> (parents_room,  Pos( 19*s+o,  8*s+o, 10)),
    heater_LR1	-> (livingroom,    Pos(  3*s+o,  2*s+o, 10)),
    heater_LR2 	-> (livingroom,    Pos(  7*s+o,  5*s+o, 10)),
    heater_B 	-> (bathroom,      Pos( 11*s+o, 10*s+o, 10)),
    heater_E 	-> (entrance,      Pos( 13*s+o,  4*s+o, 10)))

  val wallLayouts = Map[String, Dim]()/*
    br 	-> Dim(540, 250, 10, 80, 6),
    er 	-> Dim(550, 460, 10, 80, 6),
    els -> Dim(250, 640, 80, 10, 6),
    ae 	-> Dim( 50, 550, 10, 80, 6),
    ke 	-> Dim(160, 440, 80, 10, 6),
    we 	-> Dim(360, 440, 80, 10, 6))
*/    
  
  
  $[HouseFactory].make(
      atmosphereModel,
      aggregatorModel,
      walls, wallLayouts,
      rooms, roomLayouts,
      heaters, heaterLayouts)
      
   val spawn = $[HouseFactory].spawn _
   val & = $[HouseFactory].& _
   
   spawn("ProsumptionController",
    "instance.name" -> aggregatorController,
    "actorPath" -> aggregatorController,
    "period" -> "500",
    "maxConsumption" -> "1800",
    //"managed.service.pid" -> "managedAggregatorController",
    "requires.from" -> &("prosumer" -> aggregator)
  )

  spawn("Topic",
    "instance.name" -> loadTopic,
    "topicPath" -> loadTopic
  )

  spawn("LoadHierarch",
    "instance.name" -> loadHierarch,
    "actorPath" -> loadHierarch,
    "period" -> "500",
    "highThreshold" -> "-2600",
    "requires.from" -> &("aggregator" -> aggregator)
  )
  
  
  
  
  spawn("Lamp",
    "instance.name" -> "lamp_kitchen",
    "prosumedPower" -> "0",
    "maxEmissionPower" -> "400",
    "requires.from" -> &("aggregator" -> aggregator)
  )

  spawn("LampLayout",
    "instance.name" -> "lamp_kitchen_layout",
    "x" -> "200",
    "y" -> "650",
    "width" -> "60",
    "height" -> "40",
    "layer" -> "10",
    "requires.from" -> &("lamp" -> "lamp_kitchen")    
  )

  spawn("Lamp",
    "instance.name" -> "lamp_visitors_room",
    "prosumedPower" -> "0",
    "maxEmissionPower" -> "400",
    "requires.from" -> &("aggregator" -> aggregator)
  )

  spawn("LampLayout",
    "instance.name" -> "lamp_visitors_room_layout",
    "x" -> "550",
    "y" -> "650",
    "width" -> "60",
    "height" -> "40",
    "layer" -> "10",
    "requires.from" -> &("lamp" -> "lamp_visitors_room")    
  )

  spawn("Lamp",
    "instance.name" -> "lamp_living_1",
    "prosumedPower" -> "0",
    "maxEmissionPower" -> "400",
    "requires.from" -> &("aggregator" -> aggregator)
  )

  spawn("LampLayout",
    "instance.name" -> "lamp_living_1_layout",
    "x" -> "200",
    "y" -> "400",
    "width" -> "60",
    "height" -> "40",
    "layer" -> "10",
    "requires.from" -> &("lamp" -> "lamp_living_1")    
  )


  spawn("Lamp",
    "instance.name" -> "lamp_living_2",
    "prosumedPower" -> "0",
    "maxEmissionPower" -> "400",
    "requires.from" -> &("aggregator" -> aggregator)
  )

  spawn("LampLayout",
    "instance.name" -> "lamp_living_2_layout",
    "x" -> "400",
    "y" -> "400",
    "width" -> "60",
    "height" -> "40",
    "layer" -> "10",
    "requires.from" -> &("lamp" -> "lamp_living_2")    
  )
   
}
