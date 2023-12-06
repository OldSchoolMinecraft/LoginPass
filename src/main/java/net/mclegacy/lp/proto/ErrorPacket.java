package net.mclegacy.lp.proto;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ErrorPacket extends DefaultPacket
{
    public String error;

    public ErrorPacket()
    {
        super(111);
    }

    public ErrorPacket(String error)
    {
        super(111);
        this.error = error;
    }

    @Override
    public void readData(DataInputStream dis)
    {
        try
        {
            super.readData(dis);
            this.error = dis.readUTF();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void writeData(DataOutputStream dos)
    {
        try
        {
            super.writeData(dos);
            dos.writeUTF(error);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void handlePacket(PacketHandler handler) {
        try {
            handler.handlePacket(this);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
