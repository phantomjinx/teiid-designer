#!/bin/bash

#############
#
# Script to resolve the plugin's lib directory dependencies
#
# Note. This has to build both the target platform
#       in order to satisfy the dependencies of
#       this plugin.
#
#############

PLUGIN_HOME=$PWD
GIT_HOME=`cd "../.."; pwd`
SCRIPTS_HOME="$GIT_HOME/scripts"
MVN="$SCRIPTS_HOME/maven-wrapper.sh"

LIBS=(  modeshape-common.jar \
        modeshape-sequencer-ddl.jar)

PROCESS="no"

# Check to see if all the libraries have been downloaded
for i in ${!LIBS[*]}
do
  LIB=${LIBS[$i]}

	if [ ! -f lib/$LIB ]; then
		PROCESS="yes"
		break
	fi
done

if [ "x$PROCESS" == "xno" ]; then
	echo "Project source generation is up-to-date"
	exit 0
fi

# Build the target platform and install it
echo "=== Installing target platform ==="
cd "$GIT_HOME/target-platform"
$MVN install

echo "=== Generating external libraries and sources ==="
cd "$PLUGIN_HOME"
$MVN process-sources
