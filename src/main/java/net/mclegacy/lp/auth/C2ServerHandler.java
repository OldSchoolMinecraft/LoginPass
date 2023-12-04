package net.mclegacy.lp.auth;

import net.mclegacy.lp.proto.AbstractPacket;
import net.mclegacy.lp.proto.AuthorizePacket;
import net.mclegacy.lp.proto.PacketHandler;
import net.mclegacy.lp.proto.PacketRegistry;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class C2ServerHandler extends Thread
{
    public Socket c2socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private boolean running = false;
    private final PacketHandler packetHandler = new PacketHandler();

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
                Class<? extends AbstractPacket> packetClass = PacketRegistry.getPacketClass(packetID);
                AbstractPacket packet = packetClass.getConstructor().newInstance(packetID);
                packet.setC2Handler(this);
                packet.readData(dis);
                packet.handlePacket(packetHandler);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
                end();
                return;
            }
        }
    }

    public void sendPacket(AbstractPacket packet)
    {
        if (!running || c2socket.isClosed() || !c2socket.isConnected()) return;
        packet.writeData(dos);
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
