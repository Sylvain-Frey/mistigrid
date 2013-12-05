#-------------------------------------------------------------------------------
# Copyright (c) 2013 EDF. This software was developed with the 
# collaboration of Télécom ParisTech (Sylvain Frey).
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the GNU Public License v3.0
# which accompanies this distribution, and is available at
# http://www.gnu.org/licenses/gpl.html
# 
# Contributors:
#     Sylvain Frey - initial API and implementation
#-------------------------------------------------------------------------------
Instructions:

- run OSGi with runFelix.sh
- kill it with ^C
- clean it (delete cache, starts with fresh bundle install) with purgeFelix.sh

- Felix console is available at http://localhost:8080/system/console/bundles
- Web GUI for the simulation is at http://localhost:8080/webgui/main.html
- the port for these two can be checked and changed editing conf/config.properties file
- cf. ../doc for additional information
