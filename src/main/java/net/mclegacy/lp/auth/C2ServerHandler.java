package net.mclegacy.lp.auth;

import net.mclegacy.lp.LoginPass;
import net.mclegacy.lp.proto.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class C2ServerHandler extends Thread
{
    public Socket c2socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean running = true;
    private final PacketHandler packetHandler = new PacketHandler();
    private static final boolean debugMode = (boolean) LoginPass.getInstance().getConfig().getConfigOption("debugMode");

    public C2ServerHandler(Socket c2socket)
    {
        super("C2ServerHandler");

        this.c2socket = c2socket;
    }

    public C2ServerHandler begin()
    {
        start();
        return this;
    }

    public void start()
    {
        try
        {
            running = true;
            dis = new DataInputStream(c2socket.getInputStream());
            dos = new DataOutputStream(c2socket.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void run()
    {
        while (running)
        {
            try
            {
                int packetID = dis.readInt();
                if (debugMode) System.out.println("[LoginPass] C2 received packet: " + packetID);
                Class<? extends AbstractPacket> packetClass = PacketRegistry.getPacketClass(packetID);
                AbstractPacket packet = packetClass.getConstructor().newInstance(packetID);
                packet.setC2Handler(this);
                packet.readData(dis);
                packet.handlePacket(packetHandler);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                sendPacket(new ErrorPacket(ex.getMessage()));
                end();
                return;
            }
        }

        System.out.println("[LoginPass] C2 connection closed: " + c2socket);
    }

    public void sendPacket(AbstractPacket packet) {
        if (!running || c2socket.isClosed() || !c2socket.isConnected()) return;
        packet.writeData(dos);
        try
        {
            dos.flush();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void end()
    {
        try
        {
            running = false;
            dis.close();
            dos.close();
            c2socket.close();
        } catch (Exception ignored) {}
    }
}
