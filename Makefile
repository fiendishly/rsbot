CC=javac
CFLAGS=
SRC=src
LIB=lib
RES=resources
OUT=bin
LSTF=temp.txt
IMGDIR=$(RES)/images
MANIFEST=$(RES)/Manifest.txt
VERSIONFILE=.version
VERSION=`cat $(VERSIONFILE)`
SCRIPTS=scripts
DIST=data/RSBot.jar
XCD=$(shell pwd)
CLASSFILES=`ls $(SCRIPTS)/*.class 2> /dev/null | wc -l`

.PHONY: all Bot Scripts mostlyclean clean

all: Bot Scripts
	if [ -e "$(DIST)" ]; then rm "$(DIST)"; fi
	if [ -e "$(LSTF)" ]; then rm "$(LSTF)"; fi
	cp "$(MANIFEST)" "$(LSTF)"
	echo "Specification-Version: \"$(VERSION)\"" >> "$(LSTF)"
	echo "Implementation-Version: \"$(VERSION)\"" >> "$(LSTF)"
	jar cfm "$(DIST)" "$(LSTF)" -C "$(OUT)" . $(SCRIPTS)/*.class $(IMGDIR)/*.png $(RES)/*.bat $(RES)/*.sh $(RES)/version.dat
	rm "$(LSTF)"

Bot:
	if [ ! -d "$(OUT)" ]; then mkdir "$(OUT)"; fi
	"$(CC)" $(CFLAGS) -d "$(OUT)" `find "$(SRC)" -name "*.java"`

Scripts: mostlyclean
	"$(CC)" $(CFLAGS) -cp "$(OUT)" $(SCRIPTS)/*.java

mostlyclean:
	if [ "$(CLASSFILES)" != "0" ]; then rm $(SCRIPTS)/*.class; fi

clean: mostlyclean
	if [ -e "$(DIST)" ]; then rm "$(DIST)"; fi
	if [ -d "$(OUT)" ]; then rm -R "$(OUT)"; fi



