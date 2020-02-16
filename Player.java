import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.Math;
import java.time.LocalDate;

public class Player extends Thread {
    // I/O variables
    Socket socket;
    BufferedReader in;
    PrintWriter out;

    // Game variables
    private String name;
    private String channel;
    private boolean isScribe;
    private boolean ready;
    File story;
    File story_org;
    int numPlayers;
    int turn;

    // Shared variables
    public HashMap<String, File> dictionaries;
    public HashMap<String, File> stories;
    public ArrayList<Player> players;


    public Player(Socket s, String n, ArrayList<Player> p, HashMap<String, File> d, 
                        HashMap<String, File> st, String c, int num, int count){
        this.socket = s;
        this.name = n;
        this.players = p;
        this.dictionaries = d;
        this.stories = st;
        this.channel = c;
        this.numPlayers = num;
        this.turn = count;
    }

    public void run(){
        try{
            printToAll(name + " has joined the game.");

            // Initialize variables
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.ready = true;

            while(!allReady()){
                // *whistling while twiddling thumbs*
            }

            // ************* SET UP GAME *************
            
            printToAll("\nLet the game begin!");
            
            boolean gameOver = false;

            // Initialize game with scribe
            Player scribe = players.get(0);
            scribe.setScribeStatus(true);

            // Load chosen story and file reader/writer for scribe
            BufferedReader fr = scribe.chooseStory();

            String filename =  scribe.channel + "_" + LocalDate.now() + ".txt";
            File newStory = new File(filename);
            
            // Successfully created new file
            if(newStory.createNewFile()){
                System.out.println("Successfully created new story file.");
            } else {
                System.out.println("File " + filename + " already exists in this directory.");
            }

            this.story = newStory;
            PrintWriter fw = new PrintWriter(this.story);

            // Assign players and roles
            Player p1 = players.get(1);
            Player p2 = null;
            Player p3 = null;
            Player p4 = null;
            
            if(numPlayers == 3)
                p2 = players.get(2);
            if(numPlayers == 4)
                p3 = players.get(3);

            ArrayList<String> toScribe = new ArrayList<>();
            turn = 1;
            int max = numPlayers-1;

            // ************* PLAY GAME *************

            String currSentence;
            while(!gameOver && (currSentence = fr.readLine()) != null){
                if(currSentence.length() == 0){ // Add white space to new document
                    fw.println("");
                    currSentence = fr.readLine();
                } else if(currSentence == null) // Reached end of story
                    gameOver = true;

                if(turn == max)
                    turn = 1;
                else{
                    turn++;
                }

                toScribe = players.get(turn).takeTurn(currSentence);  
                scribe.writeToStory(currSentence, toScribe, fw);
            }

            fw.close();

            // Scribe reveals the nonsense to all
            BufferedReader storyTime = new BufferedReader(new FileReader(story));
            String x = "";

            while((x = storyTime.readLine()) != null){
                printToAll(x);
            }
            printToAll("\nThanks for playing!");

        } catch (IOException e) { }
    }

    // ***************** HELPER METHODS *****************

    // ********** Set methods **********
        public void setPlayerName(String pname){ this.name = pname; }
        public void setScribeStatus(boolean isScribe){ this.isScribe = isScribe; }
        public void setPlayers(ArrayList<Player> players){ this.players = players; }
        public void setReadyState(boolean ready){ this.ready = ready; }
        public void setStory(String storyName){ this.story_org = stories.get(storyName); }
        public void setChannelName(String channel) { this.channel = channel; }
        public void setNumPlayers(int num) { this.numPlayers = num; }

    // ********** Get methods **********
        public boolean isScribe(){ return this.isScribe; }
        public BufferedReader getInput(){ return this.in; }
        public PrintWriter getOutput(){ return this.out; }
        public ArrayList<Player> getPlayers(){ return this.players; }
        public boolean isReady(){ return this.ready; }
        public int getTurn(){ return this.turn; }

    public void printToAll(String message){
        try{
            for(int i = 0; i < players.size(); i++){
                Socket curSocket = players.get(i).socket;
                PrintWriter otherOut = new PrintWriter(curSocket.getOutputStream(), true);
                otherOut.println(message);
            }
        } catch(IOException ie){}

    }

    public boolean allReady(){
        if(players.size() < numPlayers)
            return false;
        else {
            for(int i = 0; i < players.size(); i++){
                if(!players.get(i).isReady())
                    return false;
            }
        }
        return true;
    }

    private BufferedReader chooseStory(){
        BufferedReader fr = null;
        boolean valid = false;
        try {
            String title = "";

            // Input check
            while(!valid){
                out.println("Which story would you like to use?");
                title = in.readLine();

                if(!stories.containsKey(title)){
                    out.println("Sorry, that story doesn't exist.");
                } else {
                    valid = true;
                }
            }

            File story = stories.get(title);
            this.story_org = story;
            fr = new BufferedReader(new FileReader(story));
            return fr;
        } catch(IOException ie){
            out.println("File not found.");
        } finally {
            this.story_org = story;
            return fr;
        }
        
    }

    // Write new sentence into story
    private void writeToStory(String sentence, ArrayList<String> words, PrintWriter fw){
        String[] breakdown = sentence.split("@");
        int count = 0; // Counter for current position in words

        // Replace all blanks with new words, in order
        for(int i = 0; i < breakdown.length; i++){
            String current = breakdown[i];

            // If prompt is found, replace with next substitute word
            if(current.contains("[")){
                breakdown[i] = words.get(count);
                count++;
            }
        }

        // Add sentence to story file
        String newSentence = String.join("", breakdown);
        fw.println(newSentence);
    }

    private ArrayList<String> takeTurn(String sentence){
        printToAll("\nIt's " + name + "'s turn.\n");
        /* Look for markers (identified by @[type]@) and extract them from sentence:
            @[vp]@ = verb present tense
            @[vpp]@ = verb past tense
            @[vf]@ = verb future tense
            @[n]@ = noun
            @[np]@ = noun plural
            @[p]@ = place
            @[a]@ = adjective
            @[av]@ = adverb
        */

        String[] markers = sentence.split("@");
        ArrayList<String> prompts = new ArrayList<>();

        for(int i = 0; i < markers.length; i++){
            String s = markers[i];

            // Check if current marker is actually a prompt
            if(s.contains("[")){
                String currPrompt = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                prompts.add(currPrompt);
            }
        }

        // Choose words
        ArrayList<String> words = new ArrayList<>();
        for(int i = 0; i < prompts.size(); i++){
            String word = "";
            word = chooseWord(prompts.get(i));
            words.add(word);
        }

        return words;
    }

   public String chooseWord(String prompt){
        String word = "";
        String wordType = "word";
        boolean valid = false;

        // Get appropriate dictionary
        File currDict = null;
        switch(prompt){
            case "vp":
                currDict = dictionaries.get("Verbs (Present)");
                wordType = "verb (present tense)";
                break;
            case "vpp":
                currDict = dictionaries.get("Verbs (Past)");
                wordType = "verb (past tense)";
                break;
            case "vf":
                currDict = dictionaries.get("Verbs (Future)");
                wordType = "verb (future tense)";
                break;
            case "n":
                currDict = dictionaries.get("Nouns");
                wordType = "noun";
                break;
            case "np":
                currDict = dictionaries.get("Nouns (Plural)");
                wordType = "noun (plural)";
                break;
            case "p":
                currDict = dictionaries.get("Places");
                wordType = "place";
                break;
            case "a":
                currDict = dictionaries.get("Adjectives");
                wordType = "adjective";
                break;
            case "av":
                currDict = dictionaries.get("Adverbs");
                wordType = "adverb";
                break;
        }

        // Prompt for word
        while(!valid){
            try {      
                out.println("Type in a(n) " + wordType + ": ");

                // Check validity of word
                if(isValidWord((word = in.readLine()), currDict))
                    valid = true;
                else
                    out.println("That word is not in the dictionary. Try again.");
            } catch (IOException ie){}
        }
        return word;
    }

    // Check if given word is in the appropriate dictionary
    private static boolean isValidWord(String word, File dictionary){ // WORKS
        try{
            BufferedReader temp = new BufferedReader(new FileReader(dictionary));
            String currWord;

            while((currWord = temp.readLine()) != null){
                if(currWord.equalsIgnoreCase(word)) {
                    return true;
                }
            }
            temp.close();
        } catch(IOException ie){}
        return false;
    }
}