Instructions:

- run OSGi with runFelix.sh
- clean it (deletes bundle cache, starts with fresh bundle install) with purgeFelix.sh
- once OSGi runs, (Scala console should show a "zz>" prompt) load the simulation configuration with ":load config.scala"
- the simulation configuration in deploy/house.scala can be edited manually: it is interpreted at runtime.
- cf. "config" project for source code of the Scala DSL used for deployment.

- Felix console is available at http://localhost:8080/system/console/bundles
- Web GUI for the simulation is at http://localhost:8080/webgui/mainpage.html
- the port for these two can be checked and changed editing conf/config.properties file.