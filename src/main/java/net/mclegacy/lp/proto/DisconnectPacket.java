package net.mclegacy.lp.proto;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class DisconnectPacket extends DefaultPacket {
    private String message;

    public DisconnectPacket()
    {
        super(99);
    }

    public DisconnectPacket(String message)
    {
        super(99);
        this.message = message;
    }

    public void readData(DataInputStream dis)
    {
        try
        {
            super.readData(dis);
            this.message = dis.readUTF();
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void writeData(DataOutputStream dos)
    {
        try
        {
            super.writeData(dos);
            dos.writeUTF(message);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}
