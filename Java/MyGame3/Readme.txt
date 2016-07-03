Jugglr – a juggling game

Use the mouse to move the paddle. See how long you can keep the balls in the air!

Build instructions

IDE - Intellij Idea Community Edition

I used Maven to import the following libraries:

org.jbox2d:jbox2d-library:2.2.1.12 - physics library, ported from C++ library Box2D
http://mvnrepository.com/artifact/org.jbox2d/jbox2d-library/2.2.1.1

org.slf4j:slf4j-jdk14:1.7.72 (JBox2D dependency)
http://mvnrepository.com/artifact/org.slf4j/slf4j-jdk14/1.7.7

I found it easiest to use Intellij's import library feature to do this (File->Project Structure-
>Libraries->New Project Library->From Maven)
