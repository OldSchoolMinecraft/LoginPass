package net.mclegacy.lp.proto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AuthorizePacket extends DefaultPacket
{
    public String username;
    public String originalIP;

    public AuthorizePacket(String username, String originalIP)
    {
        super(1);
        this.username = username;
        this.originalIP = originalIP;
    }

    public AuthorizePacket()
    {
        super(1);
    }

    @Override
    public void readData(DataInputStream dis)
    {
        try
        {
            super.readData(dis);
            this.username = dis.readUTF();
            this.originalIP = dis.readUTF();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    @Override
    public void writeData(DataOutputStream dos)
    {
        try
        {
            super.writeData(dos);
            dos.writeUTF(username);
            dos.writeUTF(originalIP);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void handlePacket(PacketHandler handler)
    {
        handler.handleAuthorizePacket(this);
    }
}
