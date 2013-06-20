@echo off
echo Wscript.Sleep WScript.Arguments(0) >%tmp%\delay.vbs
cscript //b //nologo %tmp%\delay.vbs 5000
del %tmp%\delay.vbs > nul
 

