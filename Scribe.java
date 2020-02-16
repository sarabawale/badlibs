// import java.io.*;
// import java.net.*;
// import java.util.*;
// import java.lang.Math;
// import java.util.concurrent.Semaphore;

// public class Scribe extends Thread {
//     Socket socket;
//     String name;
//     BufferedReader in;
//     PrintWriter out;
//     Semaphore turn;
//     ArrayList<Socket> players;
    
//     Scribe(Socket socket, String name, BufferedReader in, PrintWriter out, Semaphore turn, ArrayList<Socket> players) {
//         this.socket = socket;
//         this.name = name;      
//         this.in = in;
//         this.out = out;
//         this.turn = turn;
//         this.players = players;
//     }

//     public void run(){
//         // choose story name
//         out.println("Choose a story from the list.");
//         String story = in.readLine();

//         String filename = stories.get(story);

//         // parse story by line
//         File file = new File(filename);
//         FileReader reader = new FileReader(file);

//         while(reader.hasNextLine()){
//             String sentence = reader.nextLine();
//             // check validity of words against dictionary

//             // copying the story + new words to a separate document
//         }

        

//         // once end of story is reached, pull up the whole silly mess for everyone

//         // option to play again
//     }

//     // ***************** HELPER METHODS *****************


//     private void getWord(){
        
//     }

//     private void writeStory(){

//     }

//     private void shareStory(){

//     }

//     private void saveStory(FileWriter fw){
        
//     }

//     private void printToAll(String message){
//         try {
//             for(int i = 0; i < players.size(); i++){
//                 PrintWriter out = new PrintWriter(players.get(i).getOutputStream(), true);
//                 out.println(message);
//             }
//         } catch(IOException ie){}
//     }
// }