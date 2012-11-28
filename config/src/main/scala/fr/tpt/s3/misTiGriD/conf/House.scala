package fr.tpt.s3.misTiGriD.conf

import scala.collection.JavaConversions._

object SampleHouse {
    
  val atmosphere = "atmosphere"
  val atmosphereModel = Atmosphere(atmosphere, 20, 12, 22, false)
  
  val aggregator = Aggregator("houseAggregator", "houseAggregator", false, "!!_TO_BE_FIXED_!!akka://grid@127.0.0.1:2563/user/districtAggregator")
      
  val kitchen = "kitchen"
  val bathroom = "bathroom"
  val room = "room"
  val livingroom = "livingroom"
  val entrance = "entrance"
  val wc = "wc"

  val akn = "wall_akn"
  val ab  = "wall_ab"
  val arn = "wall_arn"
  val rae = "wall_rae"
  val lae = "wall_lae"
  val las = "wall_las"
  val alw = "wall_alw"
  val akw = "wall_akw"
  val kb  = "wall_kb"
  val rl  = "wall_rl"
  val ele = "wall_ele"
  val kw  = "wall_kw"
  val bw  = "wall_bw"
  val wr  = "wall_wr"
  val br  = "wall_br"
  val er  = "wall_er"
  var els = "wall_els"
  val ae  = "wall_ae"
  val ke  = "wall_ke"
  val we  = "wall_we"
    
  val heaterKitchen = "heater_" + kitchen
  val heaterRoom = "heater_" + room
  val heaterLR1 = "heater_" + livingroom + "_1"
  val heaterLR2 = "heater_" + livingroom + "_2"
  val heaterBathroom = "heater_" + bathroom
  val heaterEntrance = "heater_" + entrance

  val walls = Map[String, Wall](
    akn -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, kitchen)),
    ab  -> Wall(0.01f, false, 0.05f, 2, List(atmosphere, bathroom)),
    arn -> Wall(0.01f, false, 0.05f, 4, List(atmosphere, room)),
    rae -> Wall(0.01f, false, 0.05f, 5, List(room, atmosphere)),
    lae -> Wall(0.01f, false, 0.05f, 4, List(livingroom, atmosphere)),
    las -> Wall(0.01f, false, 0.05f, 9, List(livingroom, atmosphere)),
    alw -> Wall(0.01f, false, 0.05f, 3, List(atmosphere, livingroom)),
    akw -> Wall(0.01f, false, 0.05f, 4, List(atmosphere, kitchen)),
    kb  -> Wall(0.01f, false, 0.05f, 3, List(kitchen, bathroom)),
    rl  -> Wall(0.01f, false, 0.05f, 4, List(room, livingroom)),
    ele -> Wall(0.01f, false, 0.05f, 1, List(entrance, livingroom)),
    kw  -> Wall(0.01f, false, 0.05f, 1, List(kitchen, wc)),
    bw  -> Wall(0.01f, false, 0.05f, 2, List(bathroom, wc)),
    wr  -> Wall(0.01f, false, 0.05f, 1, List(wc, room)),
    br  -> Wall(0.01f, false, 0.05f, 3, List(bathroom, room)),
    er  -> Wall(0.01f, false, 0.05f, 1, List(entrance, room)),
    els -> Wall(0.01f, false, 0.05f, 5, List(entrance, livingroom)),
    ae  -> Wall(0.01f, false, 0.05f, 2, List(atmosphere, entrance)),
    ke  -> Wall(0.01f, false, 0.05f, 3, List(kitchen, entrance)),
    we  -> Wall(0.01f, false, 0.05f, 2, List(wc, entrance)))
    
  val rooms = Map[String, TH](
      kitchen    -> TH(24, 12, List(akw, akn, kb, kw, ke, heaterKitchen)),
      bathroom   -> TH(24, 6,  List(ab, br, bw, kb, heaterBathroom)),
      room       -> TH(24, 20, List(arn, rae, rl, er, wr, br, heaterRoom)),
      livingroom -> TH(24, 31, List(els, ele, rl, lae, las, alw, heaterLR1, heaterLR2)),
      entrance   -> TH(24, 10, List(ke, we, er, ele, els, ae, heaterEntrance)),
      wc         -> TH(24, 2,  List(bw, wr, we, kw)))
      
  val heaters = Map[String, Tuple2[Heater, HeaterManager]](
      heaterKitchen  -> (Heater(0, 0.05f, 0.1f, 400f, "houseAggregator", kitchen),	HeaterManager(heaterKitchen  + "_manager", 50, 22, true, "loadTopic", "aggregatorController", "loadHierarch", heaterKitchen,  kitchen)),
      heaterRoom     -> (Heater(0, 0.05f, 0.1f, 400f, "houseAggregator", room),		HeaterManager(heaterRoom     + "_manager", 50, 22, true, "loadTopic", "aggregatorController", "loadHierarch", heaterRoom,     room)),
      heaterLR1      -> (Heater(0, 0.05f, 0.1f, 400f, "houseAggregator", livingroom),	HeaterManager(heaterLR1      + "_manager", 50, 22, true, "loadTopic", "aggregatorController", "loadHierarch", heaterLR1,      livingroom)),
      heaterLR2      -> (Heater(0, 0.05f, 0.1f, 400f, "houseAggregator", livingroom),	HeaterManager(heaterLR2      + "_manager", 50, 22, true, "loadTopic", "aggregatorController", "loadHierarch", heaterLR2,      livingroom)),
      heaterBathroom -> (Heater(0, 0.05f, 0.1f, 400f, "houseAggregator", bathroom),	HeaterManager(heaterBathroom + "_manager", 50, 22, true, "loadTopic", "aggregatorController", "loadHierarch", heaterBathroom, bathroom)),
      heaterEntrance -> (Heater(0, 0.05f, 0.1f, 400f, "houseAggregator", entrance),	HeaterManager(heaterEntrance + "_manager", 50, 22, true, "loadTopic", "aggregatorController", "loadHierarch", heaterEntrance, entrance)))
      

  ////////////
  // LAYOUTS //
  ////////////

  val roomLayouts = Map[String, Dim](
    kitchen    -> Dim(  50,  50, 300, 400, 5),
    bathroom   -> Dim( 350,  50, 200, 300, 5),
    room       -> Dim( 550,  50, 400, 500, 5),
    livingroom -> Dim(  50, 550, 900, 400, 3),
    entrance   -> Dim(  50, 450, 500, 200, 5),
    wc         -> Dim( 350, 350, 200, 100, 5))  

  val heaterLayouts = Map[String, Tuple2[String,Pos]](
    heaterKitchen 	-> (kitchen, 	Pos(150, 250, 10)),
    heaterBathroom 	-> (bathroom, 	Pos(350, 250, 10)),
    heaterRoom 		-> (room, 	Pos(750, 450, 10)),
    heaterLR1	 	-> (livingroom, Pos(850, 650, 10)),
    heaterLR2	 	-> (livingroom, Pos(250, 750, 10)),
    heaterEntrance 	-> (entrance, 	Pos(450, 550, 10)))

  val wallLayouts = Map[String, Dim](
    br 	-> Dim(540, 250, 20, 80, 6),
    er 	-> Dim(540, 460, 20, 80, 6),
    els -> Dim(250, 640, 80, 20, 6),
    ae 	-> Dim( 40, 550, 20, 80, 6),
    ke 	-> Dim(160, 440, 80, 20, 6),
    we 	-> Dim(360, 440, 80, 20, 6))
    
}