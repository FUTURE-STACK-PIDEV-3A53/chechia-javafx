#!/bin/bash
# Définir le chemin vers votre JDK si nécessaire
# export JAVA_HOME=/usr/lib/jvm/java-17-openjdk

# Rendre le script exécutable
chmod +x "$0"

# Exécuter l'application avec Maven JavaFX plugin et paramètres additionnels
mvn clean javafx:run -Djavafx.jlink.verbose=true 