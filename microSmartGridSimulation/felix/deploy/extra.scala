val spawn = $[HouseFactory].spawn _
val & = $[HouseFactory].& _

spawn("ProsumptionController",
    "instance.name" -> "aggregatorController",
    "actorPath" -> "aggregatorController",
    "period" -> "500",
    "maxConsumption" -> "1800",
    //"managed.service.pid" -> "managedAggregatorController",
    "requires.from" -> &("prosumer" -> "houseAggregator")
)

spawn("Topic",
    "instance.name" -> "loadTopic",
    "topicPath" -> "loadTopic"
)

spawn("LoadHierarch",
    "instance.name" -> "loadHierarch",
    "actorPath" -> "loadHierarch",
    "period" -> "500",
    "highThreshold" -> "-2600",
    "requires.from" -> &("aggregator" -> "houseAggregator")
)
