package net.mclegacy.lp.auth;

import net.mclegacy.lp.LoginPass;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class C2ServerListener extends Thread
{
    private ServerSocket serverSocket;
    private static final boolean debugMode = (boolean) LoginPass.getInstance().getConfig().getConfigOption("debugMode");

    private boolean isRunning;

    public C2ServerListener(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isRunning = true;
            System.out.println("Server started on port " + port);
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                Socket socket = serverSocket.accept(); // Wait for a client connection
                System.out.println("New connection from: " + socket.getInetAddress());

                // Handle the connection in a separate thread
//                C2ProtoHandler handler = new C2ProtoHandler(socket);
//                handler.init();
                C2ServerHandler handler = new C2ServerHandler(socket);
                handler.init();
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }
}
