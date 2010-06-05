@ECHO OFF
SET dist=data\RSBot.jar

IF NOT EXIST "%dist%" CALL Compile.bat

START /BELOWNORMAL javaw -classpath "%dist%" org.rsbot.Application
