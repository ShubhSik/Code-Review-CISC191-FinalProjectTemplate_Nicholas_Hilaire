package edu.sdccd.cisc191.template;

import java.net.*;
import java.io.*;

/**
 * This program opens a connection to a computer specified
 * as the first command-line argument.  If no command-line
 * argument is given, it prompts the user for a computer
 * to connect to.  The connection is made to
 * the port specified by LISTENING_PORT.  The program reads one
 * line of text from the connection and then closes the
 * connection.  It displays the text that it read on
 * standard output.  This program is meant to be used with
 * the server program, DateServer, which sends the current
 * date and time on the computer where the server is running.
 */

public class Client {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Starts a connection to the specified server IP and port.
     * Handles connection errors and prints a message if the connection fails.
     *
     * @param ip   Server IP address
     * @param port Server port number
     * @throws IOException if the connection fails
     */

    public void startConnection(String ip, int port) throws IOException {
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            System.out.println("Connected to server at " + ip + ":" + port);
        } catch (IOException e) {
            // Close any partially opened resources if initialization failed
            safeClose(in);
            safeClose(out);
            safeClose(clientSocket);
            System.err.println("Could not connect to server: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Sends a request to the server and reads the response.
     * Handles IO errors and JSON parsing exceptions.
     *
     * @return The response from the server as a CustomerResponse object.
     * @throws IOException if there is a problem with communication
     * @throws Exception   if the response cannot be parsed
     */
    public CustomerResponse sendRequest() throws Exception {
        try {
            out.println(CustomerRequest.toJSON(new CustomerRequest(1)));
            String responseLine = in.readLine();
            if (responseLine == null) {
                throw new IOException("Server closed the connection unexpectedly.");
            }
            return CustomerResponse.fromJSON(responseLine);
        } catch (IOException e) {
            System.err.println("Error communicating with server: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Error parsing server response: " + e.getMessage());
            throw e;
        }
    }
    /**
     * Closes the client connection and associated resources.
     * Handles exceptions during close and logs any problems.
     */
    public void stopConnection() throws IOException {
        safeClose(in);
        safeClose(out);
        safeClose(clientSocket);
        System.out.println("Client connection closed.");
    }

    /**
     * Helper method to quietly close Closeable resources (streams, sockets, etc.)
     * @param resource The resource to close (can be null)
     */
    private void safeClose(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                System.err.println("Error closing resource: " + e.getMessage());
            }
        }
    }

    /**
     * Main entry point. Starts the client, sends a request, prints the response, and closes the connection.
     * Handles all exceptions and prints stack traces for debugging.
     *
     * @param args Command line arguments (not used)
     */

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        try {
            client.startConnection("127.0.0.1", 4444);
            System.out.println(client.sendRequest().toString());
        } catch (IOException e) {
            System.err.println("Network error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            client.stopConnection();
        }
    }
} //end class Client

