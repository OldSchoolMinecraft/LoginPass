package net.mclegacy.lp.auth;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class C2ServerListener extends Thread
{
    private boolean running = false;
    private ServerSocket serverSocket;
    private final ArrayList<C2ServerHandler> c2connections;

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
            running = true;
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void run()
    {
        System.out.println("C2 server listener has started");

        while (running)
        {
            try
            {
                Socket c2socket = serverSocket.accept();
                c2connections.add(new C2ServerHandler(c2socket).begin());
            } catch (Exception ignored) {}
        }

        System.out.println("C2 server listener has stopped");
    }

    public void end()
    {
        running = false;
        c2connections.forEach(C2ServerHandler::end);
        c2connections.clear();
    }
}
