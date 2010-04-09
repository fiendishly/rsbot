@ECHO OFF
SET dist=data\RSBot.jar

IF NOT EXIST "%dist%" CALL Compile.bat

START /BELOWNORMAL javaw -jar -Xmx512m "%dist%"
