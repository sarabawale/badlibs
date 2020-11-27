# Badlibs

*README currently under construction

This is a Madlibs-style game written entirely in Java for the command line. Players are given the option to create a new game or join an existing game. The person to initiate a game is deemed the "scribe" and determines how many players will be able to participate in that game (minimum of 2 including the scribe, maximum of 5). Once all spots have been filled the scribe will prompt the other player(s) for a noun, verb, adjective, or adverb depending on the word(s) to be substituted. The game is turn based; if there are more than two players then the non-scribe players are assigned a number and take turns suggesting words. The scribe does not get the entire story at once but rather a sentence at a time to prevent confusion. Once the story has been completed, the scribe sends it to all the other players and every player gets the option to export/save the story as a text file. 


## Getting Started

1. Compile all Java files

```
javac *.java
```

2. Start the Server program on a chosen port

```
java Server [port]
```

3. Open a new command line window and start the Client class on the same port

```
java Client localhost [port]
```

4. Enter information when prompted (there are checks for invalid input)

```
[need examples]
```

5. Repeat steps 3-4 depending on the number of players in the game (max 5)


## Playing the Game

*There are 2 versions of the same story - I recommend "3littlepigs-short.txt" for quick testing but not for a full game

Once all players are connected, the game setup will begin. The scribe will be asked to choose a story.

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```


## Authors

* **Sara Bawale**


## Acknowledgments

* Many thanks to professor Andy Chin for the support in developing this project
* Thank you to **Billie Thompson** - [PurpleBooth](https://github.com/PurpleBooth) for providing the the template for this README file
