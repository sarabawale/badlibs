import java.io.*;
import java.net.*;

public class Client{
    public static void main(String[] args) throws IOException {
        
        if (args.length != 2) {
            System.err.println(
                "Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try {
            // Connect socket to given host name and port number
            Socket socket = new Socket(hostName, portNumber); 
            PrintWriter out =
                new PrintWriter(socket.getOutputStream(), true);
            BufferedReader stdIn = // Get input stream from standard in (keyboard input from console)
                new BufferedReader(
                    new InputStreamReader(System.in)) ;

            String userInput;
            String serverInput;

            // Initialize a thread to listen for input from server
            ServerListener serverListener = new ServerListener(socket);
            serverListener.start();
            while ((userInput = stdIn.readLine()) != null) { // Wait for input from standard in
                out.println(userInput);
            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        } 
    }
}

class ServerListener extends Thread {
    Socket socket;
    ServerListener(Socket socket) {
        this.socket = socket;
    }
    public void run() {
        try {
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(socket.getInputStream())); // Input stream from server
            String serverInput;
            while((serverInput = in.readLine()) != null) {
                System.out.println(serverInput); // Print input from server into console
            }
        }catch(IOException ie) {

        }
    }
}