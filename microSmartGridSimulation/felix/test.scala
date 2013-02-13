scala -cp "bin/felix.jar:bundles/*:bundles/scala/*:bundles/libs/*:bundles/felix/*"
val ctx = felix.getBundleContext
import fr.sylfrey.misTiGriD.alba.basic.roles.HouseLoadManager
ctx.getServiceReference(classOf[HouseLoadManager])