@echo off

set "classpath=libs\FileFinder.jar"
java -cp "%classpath%" componenten.Main

start javaw -jar StringDeposit.jar
exit
