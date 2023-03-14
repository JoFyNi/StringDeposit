@echo off
title Your Program Name

set "APP_DIR=%~dp0"
cd /d "%APP_DIR%"

echo Launching Your Program Name...
start javaw -jar StringDeposit.jar

exit
