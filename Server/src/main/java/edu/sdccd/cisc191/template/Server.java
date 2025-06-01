package edu.sdccd.cisc191.template;

import java.net.*;
import java.io.*;
import java.io.IOException;

/**
 * This program is a server that takes connection requests on
 * the port specified by the constant LISTENING_PORT.  When a
 * connection is opened, the program sends the current time to
 * the connected socket.  The program will continue to receive
 * and process connections until it is killed (by a CONTROL-C,
 * for example).  Note that this server processes each connection
 * as it is received, rather than creating a separate thread
 * to process the connection.
 */
public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Starts the server on the specified port.
     * Waits for a client connection and processes input lines, responding in JSON.
     * Handles all IO exceptions and logs errors. The server runs in a blocking
     * manner until the client disconnects or an error occurs.
     *
     * @param port The port number to listen on
     */
    public void start(int port) throws Exception {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started, listening on port " + port);
            clientSocket = serverSocket.accept();
            System.out.println("Client connected from " + clientSocket.getInetAddress());

            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                try {
                    CustomerRequest request = CustomerRequest.fromJSON(inputLine);
                    CustomerResponse response = new CustomerResponse(request.getId(), "Jane", "Doe");
                    out.println(CustomerResponse.toJSON(response));
                } catch (Exception reqEx) {
                    // Handles invalid JSON or processing errors per request
                    System.err.println("Failed to process request: " + reqEx.getMessage());
                    out.println("{\"error\": \"Invalid request format\"}");
                }
            }
        } catch (IOException e) {
            // Handles IO errors in server setup or client communication
            System.err.println("Server error: " + e.getMessage());
        } finally {
            // Ensures resources are closed even if an exception occurs
            try {
                stop();
            } catch (IOException stopEx) {
                System.err.println("Error closing server: " + stopEx.getMessage());
            }
        }
    }

    /**
     * Stops the server by closing all open resources.
     * Handles exceptions on close gracefully and logs any issues.
     *
     * @throws IOException if an error occurs during closing of sockets or streams
     */
    public void stop() throws IOException {
        if (in != null) {
            try { in.close(); } catch (IOException e) { System.err.println("Error closing input: " + e.getMessage()); }
        }
        if (out != null) {
            out.close(); // PrintWriter's close does not throw checked exceptions
        }
        if (clientSocket != null && !clientSocket.isClosed()) {
            try { clientSocket.close(); } catch (IOException e) { System.err.println("Error closing client socket: " + e.getMessage()); }
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            try { serverSocket.close(); } catch (IOException e) { System.err.println("Error closing server socket: " + e.getMessage()); }
        }
        System.out.println("Server stopped.");
    }

    /**
     * Main entry point. Starts the server on port 4444.
     * Handles and logs any exceptions encountered during server execution.
     *
     * @param args Command line arguments (not used)
     */

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.start(4444);
            server.stop();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
} //end class Server
