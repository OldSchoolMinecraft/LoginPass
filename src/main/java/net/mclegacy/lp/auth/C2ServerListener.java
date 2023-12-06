package net.mclegacy.lp.auth;

import net.mclegacy.lp.LoginPass;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class C2ServerListener extends Thread
{
    private boolean running = true;
    private ServerSocket serverSocket;
    private final ArrayList<C2ServerHandler> c2connections;
    private static final boolean debugMode = (boolean) LoginPass.getInstance().getConfig().getConfigOption("debugMode");

    public C2ServerListener()
    {
        super("C2ServerListener");

        c2connections = new ArrayList<>();
    }

    public void start()
    {
        try
        {
            serverSocket = new ServerSocket();
            String host = String.valueOf(LoginPass.getInstance().getConfig().getConfigOption("c2host"));
            int port = (int) LoginPass.getInstance().getConfig().getConfigOption("c2port");
            serverSocket.bind(new InetSocketAddress(host, port));
            running = true;

            if (debugMode) System.out.println("[LoginPass] C2 listener started");
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void run()
    {
        while (running)
        {
            try
            {
                Socket c2socket = serverSocket.accept();
                if (c2socket == null) continue;
                System.out.println("[LoginPass] C2 connection: " + c2socket);
                C2ServerHandler handler = new C2ServerHandler(c2socket);
                handler.start();
                c2connections.add(handler);
            } catch (Exception ignored) {}
        }

        if (debugMode) System.out.println("[LoginPass] C2 server listener has stopped");
    }

    public void end()
    {
        running = false;
        c2connections.forEach(C2ServerHandler::end);
        c2connections.clear();
    }
}
