package net.mclegacy.lp.proto;

import net.mclegacy.lp.LoginPass;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class DefaultPacket extends AbstractPacket
{
    public DefaultPacket(int packetID)
    {
        super(packetID);
    }

    public void readData(DataInputStream dis)
    {
        try
        {
            if (keyRequired)
            {
                c2key = dis.readUTF();
                if (!c2key.equals(LoginPass.getInstance().getConfig().getConfigOption("c2key")))
                {
                    c2handler.sendPacket(new ErrorPacket("Invalid C2 key"));
                    c2handler.sendPacket(new DisconnectPacket("Bye"));
                    c2handler.end();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void writeData(DataOutputStream dos)
    {
        try
        {
            dos.writeInt(packetID);
            dos.writeUTF("C2");
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void handlePacket(PacketHandler handler)
    {
        handler.handlePacket(this);
    }
}
