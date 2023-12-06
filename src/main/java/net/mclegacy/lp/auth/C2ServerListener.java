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
    private static final boolean debugMode = (boolean) LoginPass.getInstance().getConfig().getConfigOption("debugMode");

    public C2ServerListener()
    {
        super("C2ServerListener");
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
                C2ProtoHandler handler = new C2ProtoHandler(c2socket);
                handler.start();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }

        if (debugMode) System.out.println("[LoginPass] C2 server listener has stopped");
    }

    public void end()
    {
        running = false;
    }
}
