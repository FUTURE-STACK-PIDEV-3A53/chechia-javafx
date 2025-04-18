@echo off
rem Définir le chemin vers votre JDK si nécessaire
rem set JAVA_HOME=C:\Program Files\Java\jdk-17

rem Exécuter l'application avec Maven JavaFX plugin et paramètres additionnels
mvn clean javafx:run -Djavafx.jlink.verbose=true 