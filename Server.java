import java.io.*;
import java.net.*;
import java.util.*;
import java.util.HashMap;
import java.lang.Math;

public class Server{
    private static String DICT_PATH = "dictionaries/";
    private static String STORY_PATH = "stories/";
    private static int MAX_PLAYERS = 3;

    public static void main(String[] args){
        if (args.length != 1) {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        HashMap<String, File> dictionaries = new HashMap<>();
        HashMap<String, File> stories = new HashMap<>();
        HashMap<String, ArrayList<Player>> channels = new HashMap<>();
        ArrayList<Player> players = new ArrayList<Player>();

        int numPlayers = 2;

        // Load in dictionaries
        dictionaries.put("Nouns", new File(DICT_PATH + "nouns.txt"));
        dictionaries.put("Nouns (Plural)", new File(DICT_PATH + "nouns_plural.txt"));
        dictionaries.put("Places", new File(DICT_PATH + "places.txt"));
        dictionaries.put("Verbs (Present)", new File(DICT_PATH + "verbs_present.txt"));
        dictionaries.put("Verbs (Past)", new File(DICT_PATH + "verbs_past.txt"));
        dictionaries.put("Verbs (Future)", new File(DICT_PATH + "verbs_future.txt"));  
        dictionaries.put("Adjectives", new File(DICT_PATH + "adjectives.txt"));
        dictionaries.put("Adverbs", new File(DICT_PATH + "adverbs.txt"));

        // Load in story names and associated files
        stories.put("Three Little Pigs (short)", new File(STORY_PATH + "3littlepigs-short.txt"));
        stories.put("Three Little Pigs (full)", new File(STORY_PATH + "3littlepigs-full.txt"));

        try (
            ServerSocket serverSocket =
                new ServerSocket(Integer.parseInt(args[0])); //Listen to port on Server for incoming connections
        ) {
            System.out.println("Server has started.");
            while(true) { // Loop for continuously listening for new incoming connections
                Socket playerSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader( // Grab input stream from socket
                    new InputStreamReader(playerSocket.getInputStream()));
                PrintWriter out = new PrintWriter(playerSocket.getOutputStream(), true);
                System.out.println("Client connected successfully.");

                // Player information
                String name = "";
                String channel = "";
                String playerType = "";
                ArrayList<Player> channelList = new ArrayList<>();
                int turn = 1;

                // ************* CONNECT TO GAME *************
                
                // Get player input
                out.println("\n############ BADLIBS ############\n");
                out.println("Welcome! Please enter your name:");
                name = in.readLine();

                out.println("Do you want to start or join a game? Enter S for start, J for join, or R for random:");
                boolean valid = false;
                boolean validChannel = false;
                boolean validNum = false;

                // Validate user input
                while(!valid){
                    playerType = in.readLine();

                    // SCRIBE (start game)
                    if(playerType.equalsIgnoreCase("S")){ 
                        // Get channel name
                        while(!validChannel){
                            out.println("Enter a channel name: ");
                            channel = in.readLine();
                            if (channels.containsKey(channel)) {
                                out.println("Sorry, that channel already exists.");
                            } else if(channel.length() == 0){
                                out.println("Please enter a channel name.");
                            } else {
                                channels.put(channel, channelList);
                                validChannel = true;
                            }
                        }

                        // Get number of players
                        while(!validNum){
                            out.println("Enter the number of players for this game: ");
                            String input = in.readLine();
                            int temp = 0;
                            if(input.length() == 0) {
                                out.println("Please enter the number of players.");
                            } else if((temp = Integer.parseInt(input)) < 2 || temp > 6){
                                out.println("You need between 2-5 players for a game.");
                            } else {
                                numPlayers = temp;
                                validNum = true;
                            }
                        }

                        out.println("Ready! Now waiting for other players to join...");
                        valid = true;
                    }

                    // PLAYER (join specific game)  
                    else if(playerType.equalsIgnoreCase("J")) { 
                        while(!validChannel){
                            out.println("Enter the channel you wish to join: ");
                            channel = in.readLine();
                            if (!channels.containsKey(channel)) {
                                out.println("Sorry, that channel doesn't exist.");
                            } else if(channels.get(channel).size() == numPlayers){
                                out.println("Sorry, that channel is full.");
                            } else {
                                channelList = channels.get(channel);
                                turn = channelList.get(0).getTurn();
                                validChannel = true;
                            }
                        }
                        out.println("All set!");
                        valid = true;
                    } 

                    // PLAYER (join random game) 
                    else if(playerType.equalsIgnoreCase("R")){ 
                        out.println("Please hold...");
                        for(String key : channels.keySet()){
                            if(channels.get(key).size() != numPlayers){
                                channelList = channels.get(key);
                                turn = channelList.get(0).getTurn();
                                break;
                            }
                        }
                        out.println("All set!");
                        valid = true;
                    } else {
                        out.println("Invalid input. Enter S for start, J for join, or R for random: ");
                    }
                }

                Player player = new Player(playerSocket, name, channelList, dictionaries, 
                                            stories, channel, numPlayers, turn);
                channelList.add(player);
                player.start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}