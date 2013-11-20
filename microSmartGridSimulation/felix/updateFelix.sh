#!/bin/sh

cp -uv ../../wrappers/target/misTiGriD_wrappers-1.1.jar bundles/

cp -uv ../../environment/target/misTiGriD_environment-1.1.jar bundles/

cp -uv ../../temperature/target/misTiGriD_temperature-1.1.jar bundles/

cp -uv ../../electricity/target/misTiGriD_electricity-1.1.jar bundles/

cp -uv ../../appliances/target/misTiGriD_appliances-1.1.jar bundles/

cp -uv ../../alba/target/misTiGriD_alba-1.1.jar bundles/

cp -uv ../../deploy/target/misTiGriD_deploy-1.1.jar bundles/

cp -uv ../../layout/target/misTiGriD_layout-1.1.jar bundles/

cp -uv ../../trace/target/misTiGriD_trace-1.1.jar bundles/

cp -uv ../../webGUI/target/misTiGriD_webGUI-1.1.jar bundles/hot/

cp -uv ../../deploy/target/misTiGriD_deploy-1.1.jar bundles/

rm -rf felix-cache
echo '# cleared cache'
