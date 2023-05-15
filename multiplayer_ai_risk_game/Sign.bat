SET PATH=C:\Program Files\Java\jdk1.6.0_16\bin
move build\LoadScene.jar HTML\LoadScene.jar
jarsigner -keystore warkeystore -storepass qwertyuiop -keypass qwerty -signedjar HTML/sLoadScene.jar HTML/LoadScene.jar Rondao
pause