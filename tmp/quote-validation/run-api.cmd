@echo off
cd /d "%~dp0..\.."
java "-Dserver.port=18080" "-Dapp.dgii.rnc.auto-sync-enabled=false" "-Dspring.main.banner-mode=off" -jar "services\api\target\peralta-pos-api-0.1.0.jar" > "tmp\quote-validation\api.out.log" 2> "tmp\quote-validation\api.err.log"
