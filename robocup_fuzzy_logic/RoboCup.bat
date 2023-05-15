cd bin
mklink /J res ..\res
set CLASSPATH=.;../lib/lwjgl.jar;../lib/lwjgl_util.jar;../lib/fuzzyJ110a.jar;../lib/jade.jar;../lib/commons-codec-1.3.jar
java -Djava.library.path=../native jade.Boot -agents "RC:robocup.RoboCup; A0:sma.PlayerAgent; A1:sma.PlayerAgent; A2:sma.PlayerAgent; A3:sma.PlayerAgent; A4:sma.PlayerAgent; A5:sma.PlayerAgent; A6:sma.PlayerAgent; A7:sma.PlayerAgent; A8:sma.PlayerAgent; A9:sma.PlayerAgent"
pause