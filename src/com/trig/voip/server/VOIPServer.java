package com.trig.voip.server;

import com.trig.voip.server.commands.AbstractCommand;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/***
 * @author Steven
 *
 * Main VOIPServer class to handle all connections and commands
 */
public class VOIPServer {

    private int port; //The port number the server will run on
    private ServerSocket server; //The ServerSocket to accept connections with
    private boolean running = false; //Whether or not the server is currently running
    private static VOIPServer instance; //The Singleton instance

    private ArrayList<Client> clients = new ArrayList<Client>(); //List of all currently connected clients

    private VOIPServer() {
    }

    /***
     * Starts the VOIP Server with the specified port
     * @param port The port number to start the server on
     */
    public void start(int port) {
        if(running) { //If the cserver is already running, throw an exception
            throw new RuntimeException("Server is already running!");
        }
        this.port = port; //Store the port for later use
        System.out.println("Server is starting on port: " + port);

        //Start creating the server socket
        try {
            server = new ServerSocket(port);
            System.out.println("Listening on port: " + port);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        running = true; //Set the running flag to true
        //We will accept incoming connections on the main thread
        while(running) {
            try {
                Socket socket = server.accept(); //Accept all connections
                Client client = new Client(socket); //Create a Client with the socket and default name
                System.out.println("Connection started with " + client.getConnectionInfo());
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    /***
     * Interprets a command from a socket
     * @param cmd
     */
    public void acceptCommand(AbstractCommand cmd) {
        System.out.println("Interpreting command " + cmd.getClass().getName());
        cmd.interpret();
    }

    /***
     * Destroys the given client and frees up resources
     * @param client The client to destroy
     */
    public void dispose(Client client) {
        System.out.println("Closing connection with " + client);
        client.dispose(); //Call dispose on the client so it can handle its own garbage
        clients.remove(client); //Remove the client from the list
        client = null; //Help with GC
        System.out.println("Connection disposed");
    }

    /***
     * Returns the instance of VOIPServer, or creates one if none exists
     * @return An instance of VOIPServer
     */
    public static VOIPServer getInstance() {
        if(instance == null) {
            instance = new VOIPServer();
        }
        return instance;
    }
}
