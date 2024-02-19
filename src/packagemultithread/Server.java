package packagemultithread;

import java.io.*;
import java.net.*;

// Server class
public class Server {
    private static int clientCount = 1;

    public static void main(String[] args) {
        ServerSocket server = null;

        try {
            // server is listening on port 1234
            server = new ServerSocket(1234);
            server.setReuseAddress(true);
            System.out.println("Server started");

    		System.out.println("Waiting for a client ...");
    		
            // running infinite loop for getting client request
            while (true) {
            	
                // socket object to receive incoming client requests
                Socket client = server.accept();
                System.out.println("Client accepted");
                
                // Displaying that a new client is connected to the server
                int clientId = clientCount++;
                System.out.println("Client" + clientId + " connected from "
                        + client.getInetAddress().getHostAddress());

                // create a new thread object
                ClientHandler clientSock = new ClientHandler(client, clientId);

                // This thread will handle the client separately
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final int clientId;

        // Constructor
        public ClientHandler(Socket socket, int clientId) {
            this.clientSocket = socket;
            this.clientId = clientId;
        }

        public void run() {
            PrintWriter out = null;
            BufferedReader in = null;
            try {
                // get the output stream of the client
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // get the input stream of the client
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String line;
                while ((line = in.readLine()) != null) {
                    // writing the received message from the client with client identifier
                    System.out.printf("Client%d said: %s\n", clientId, line);
                    out.println("Server received your message: " + line);
                }
                //System.out.println(line);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
