#!/bin/bash

if type -p java; then
    echo "Using Java Executable found ins System PATH"
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo "Using Java Executable found in JAVA_HOME"
    _java="$JAVA_HOME/bin/java"
else
    echo "no java"
fi

if [[ "$_java" ]]; then
	version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
	echo "You are Using Java $version"
	if [[ "$version" > "1.7" ]]; then
		"$_java" -jar ccautotyper.jar gui 
	else
		echo "Java Version Must be 1.8 or Greater! You are using Java $version!"
	fi
fi