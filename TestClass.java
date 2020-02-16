import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDate;

public class TestClass{
    public static void main(String[] args){
        // Each entry is a unique game, identified by channel (key); value is array of sockets (players)
        HashMap<String, ArrayList<Player>> channels = new HashMap<>();
        HashMap<String, File> dictionaries = new HashMap<>();
        HashMap<String, File> stories = new HashMap<>();
        HashMap<String, String> reusable = new HashMap<>();
        
        try {
            // Load in dictionaries
            dictionaries.put("Nouns", new File("dictionaries/nouns.txt"));
            dictionaries.put("Nouns (Plural)", new File("dictionaries/nouns_plural.txt"));
            dictionaries.put("Places", new File("dictionaries/places.txt"));
            dictionaries.put("Verbs (Present)", new File("dictionaries/verbs_present.txt"));
            dictionaries.put("Verbs (Past)", new File("dictionaries/verbs_past.txt"));
            dictionaries.put("Verbs (Future)", new File("dictionaries/verbs_future.txt"));  
            dictionaries.put("Adjectives", new File("dictionaries/adjectives.txt"));
            dictionaries.put("Adverbs", new File("dictionaries/adverbs.txt"));

            // Load in story names and associated files
            stories.put("Three Little Pigs", new File("stories/3littlepigs.txt"));

            System.out.println("Everything is loaded... probably.");

            File x = stories.get("Three Little Pigs");
            BufferedReader fr = new BufferedReader(new FileReader(x));
            File story = createStory(stories.get("Three Little Pigs"));
            PrintWriter fw = new PrintWriter(story);

            String sentence;
            //System.out.println(sentence);

            while((sentence = fr.readLine()) != null){
                ArrayList<String> words = takeTurn(sentence, dictionaries, reusable);

                System.out.print("Words: ");
                for(int i = 0; i < words.size(); i++){
                    System.out.print(words.get(i) + " ");
                }

                writeToStory(sentence, words, fw, reusable);
            }
            fw.close();

        } catch(IOException ie){}
    }

    // ***************** HELPER METHODS *****************

    // Create copy of original story, which will be modified throughout the game
    private static File createStory(File original) { // WORKS
        // Filename: chosen story + channel + date
        String sub = original.getName().substring(0, original.getName().indexOf(".txt"));
        String filename =  sub + "_" + LocalDate.now() + ".txt";
        //String filePath = "../assets/" + filename;
        String filePath = filename;
        File newStory = new File(filePath);

        try {
            // Successfully created new file
            if(newStory.createNewFile()){
                System.out.println("Successfully created new story file.");
            } else
                System.out.println("File " + filename + " already exists in this directory.");
        } catch(IOException ie){}
        
        return newStory;
    }

    // Write new sentence into story
    private static void writeToStory(String sentence, ArrayList<String> words, PrintWriter fw,
                    HashMap<String, String> reusable){
        String[] breakdown = sentence.split("@");
        int count = 0; // Counter for current position in words

        // Replace all blanks with new words, in order
        for(int i = 0; i < breakdown.length; i++){
            String current = breakdown[i];

            // If prompt is found, replace with next substitute word
            if(current.contains("[")){
                // Check for reusable value
                if(reusable.containsKey(current)){
                    breakdown[i] = reusable.get(current);
                } else
                    breakdown[i] = words.get(count);
                count++;
            }
        }

        // Add sentence to story file
        String newSentence = String.join("", breakdown);
        fw.println(newSentence);
    }

    // Checks if a given string has any numbers
    private static boolean hasNum(String str){
        for(int i = 0; i < str.length(); i++){
            if(Character.isDigit(str.charAt(i))){
                return true;
            }
        }

        return false;
    }

    private static ArrayList<String> takeTurn(String sentence, 
        HashMap<String, File> dictionaries, HashMap<String, String> reusable){

        // Look for markers and extract them from sentence
        String[] markers = sentence.split("@");
        ArrayList<String> prompts = new ArrayList<>();

        for(int i = 0; i < markers.length; i++){
            String s = markers[i];
            //System.out.println("marker[" + i + "]: " + s);

            // Check if current marker is actually a prompt
            if(s.contains("[")){
                String currPrompt = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
                //System.out.println("1. Current prompt: " + currPrompt);

                // If a reusable variable
                // if(hasNum(currPrompt)){
                //     String p = "[" + currPrompt + "]"; 
                //     //System.out.println("p: " + p);
                //     // Check if p exists in hashmap; if it does, skip this prompt
                //     if(!reusable.containsKey(p)){
                //         // Create key for hashmap and extract pure prompt (no numbers)
                //         // String clippedPrompt = currPrompt.substring(0, prompts.get(i).length() - 1);
                //         // System.out.println("Clipped prompt: " + clippedPrompt);
                //         prompts.add(currPrompt);
                //         reusable.put(p, currPrompt);
                //     } else {
                //         System.out.println("Skipping...");
                //     }
                // } 

                prompts.add(currPrompt);
                //System.out.println("2. Current prompt: " + currPrompt);
            }
        }

        // Choose words
        ArrayList<String> words = new ArrayList<>();
        // File currDict;
        for(int i = 0; i < prompts.size(); i++){
            String word = "";
            if(hasNum(prompts.get(i))){
                // Create key for hashmap and extract pure prompt (no numbers)
                String p = "[" + prompts.get(i) + "]"; 
                String clippedPrompt = prompts.get(i).substring(0, prompts.get(i).length() - 1);
                System.out.println("Clipped prompt: " + clippedPrompt);

                word = chooseWord(clippedPrompt, dictionaries);
                words.add(word);
                reusable.put(p, word); // Update hashmap entry
            } else {
                word = chooseWord(prompts.get(i), dictionaries);
                words.add(word);
            }
            //System.out.println(word);
        }
        System.out.println(words);

        return words;
    }

    public static String chooseWord(String prompt, HashMap<String, File> dictionaries){ // WORKS
        String word = "";
        String wordType = "word";
        boolean valid = false;
        Scanner scan = new Scanner(System.in);
        System.out.println("Prompt: " + prompt);

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
                //System.out.println("Dictionary: " + currDict.getName());

        // Prompt for word
        while(!valid){
            //try {      
                System.out.println("Type in a " + wordType + ": ");
                word = scan.nextLine();

                // Check validity of word
                if(isValidWord(word, currDict))
                    valid = true;
                else
                    System.out.println("That word is not in the dictionary. Try again.");
            //} catch (IOException ie){}
        }
        return word;
    }

    // Check if given word is in the appropriate dictionary
    private static boolean isValidWord(String word, File dictionary){ // WORKS
        try{
            BufferedReader temp = new BufferedReader(new FileReader(dictionary));
            String currWord = "";
            //System.out.println(word);

            while((currWord = temp.readLine()) != null){
                //System.out.println(currWord);
                if(currWord.equalsIgnoreCase(word)) {
                    return true;
                }
            }
            temp.close();
        } catch(IOException ie){}
        return false;
    }
}