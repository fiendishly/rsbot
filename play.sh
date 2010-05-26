#!/bin/sh

res=resources
name=RSBot
dist=data/RSBot.jar

if [ ! -e "$dist" ]; then make; fi

macargs=
if [ "$(uname)" = "Darwin" ]; then
	macargs="-Xdock:icon=$resources/images/icon.png -Dcom.apple.mrj.application.apple.menu.about.name=$name -Dapple.laf.useScreenMenuBar=true"
	growl=$(which growlnotify)
	if [ -n "$growl" ]; then macargs="-Dgrowl.enabled=true -Dgrowl.path=\"$growl\" $macargs"; fi
fi

java -jar $macargs -Xmx512m "$dist" > /dev/null &
