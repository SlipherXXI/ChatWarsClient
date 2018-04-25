//
//Ryan Slipher
//


import java.io.*;
import java.net.*;

public class chatClient {
	public static void main(String[] args) throws IOException {
	        
		if (args.length != 2) {
			System.err.println(
				"Usage: java ChatWarsClient <host name> <port number>");
        	System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
        	Socket cwSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(cwSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
            	new InputStreamReader(cwSocket.getInputStream()));
        ) {
            BufferedReader stdIn =
                new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
	    	            
            System.out.println("before recieve");
            fromServer = in.readLine();
            System.out.println(fromServer);
            System.out.println("After recieve");
	            
            ListenRun listenThread = new ListenRun(in);
            listenThread.start();

            while (true) {
            	
                /*System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye."))
                    break;
                    */
            	
                System.out.println("enter data");
                fromUser = stdIn.readLine();
                if(fromUser.compareTo("exit")==0) {
                	cwSocket.close();
                	break;
                }	
                if (fromUser != null) {
                    //System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
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

class ListenRun extends Thread {
	BufferedReader reader;
	ListenRun(BufferedReader readerln){
		this.reader = readerln;
	}
	
	public void run() {
		// compare primes larger than midPrime
		String fromServer = null;
		try {
			while((fromServer = reader.readLine()) != null) {
				System.out.println(fromServer);
			}
		}catch(IOException e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}