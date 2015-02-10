# Radius
A Scala-based arcade game in the similar to the 1980's era Breakout games.

### Build Requirements

* [Java SDK 1.7] (http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* [Scala 2.11.4] (http://scala-lang.org/download/)
* [SBT 0.13+] (http://www.scala-sbt.org/download.html)

### Configuring the project for your IDE

#### Generating an Eclipse project

    $ sbt eclipse

#### Generating an Intellij Idea project

    $ sbt gen-idea

### Building the code

    $ sbt clean assembly

### Run the game

To start the Ricochet:

	$ java -jar ricochet.jar