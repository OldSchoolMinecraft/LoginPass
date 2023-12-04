package net.mclegacy.lp.proto;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AuthorizePacket extends DefaultPacket
{
    public String username;

    public AuthorizePacket(String username)
    {
        super(1);
        this.username = username;
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
