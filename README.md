# Badlibs

This is a Madlibs-style game written entirely in Java for the command line. Players are given the option to create a new game or join an existing game. The person to initiate a game is deemed the "scribe" and determines how many players will be able to participate in that game (minimum of 2 including the scribe, maximum of 5). Once all spots have been filled the scribe will prompt the other player(s) for a noun, verb, adjective, or adverb depending on the word(s) to be substituted. The game is turn based; if there are more than two players then the non-scribe players are assigned a number and take turns suggesting words.  Once the story has been completed, the scribe sends it to all the other players and the silly story is saved as a text file. 


## Getting Started

1. Compile all Java files

```
javac *.java
```

2. Start the Server program on a chosen port

```
java Server <port>
```

3. Open a new command line window and start the Client class on the same port

```
java Client localhost <port>
```

4. Enter information when prompted (there are checks for invalid input)

```
############ BADLIBS ############

Welcome! Please enter your name:
Sara
```

```
Do you want to start or join a game? Enter S for start, J for join, or R for random:
S
```

```
Enter a channel name: 
badlibs
```
```
Enter the number of players for this game: 
4
```

```
Ready! Now waiting for other players to join...
Sara has joined the game.
```

5. Repeat steps 3-4 depending on the number of players in the game (max 5 including the scribe)


## Playing the Game

*There are 2 versions of the same story - I recommend "3littlepigs-short.txt" for quick testing but not for a full game

Once all players are connected, the game setup will begin. The scribe will be asked to choose a story, although at the moment there are only two: Three Little Pigs (short) and Three Little Pigs (full)

```
Which story would you like to use?
Three Little Pigs (short)
```

All players, including the scribe get an announcement of which player has the current turn.
```
It's <player>'s turn.
```

The player whose turn it is will get a series of prompts like the following:

```
Type in a(n) noun: 
book
Type in a(n) adverb: 
actually
Type in a(n) adjective: 
cold
```

This continues until the last sentence is filled in, then the modified story is revealed to all players.


## Known Issues

* Need to add list of story names in prompt for scribe to choose
* Nouns are currently all singular, need to distinguish between plurals and singulars in story text files
* Story is saved as a text file automatically, need to give players option to save it
* Scribe does not see the current sentence w/ prompts or the new sentence with substituted words


## Future Improvements

* Add more stories
* Add options to play again
* Expand all dictionaries to include more words


## Authors

* **Sara Bawale**


## Acknowledgments

* Many thanks to professor Andy Chin for the support in developing this project
* Thank you to **Billie Thompson** - [PurpleBooth](https://github.com/PurpleBooth) for providing the the template for this README file
