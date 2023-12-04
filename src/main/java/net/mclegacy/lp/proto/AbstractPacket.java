package net.mclegacy.lp.proto;

import net.mclegacy.lp.auth.C2ServerHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class AbstractPacket
{
    public int packetID;
    protected String c2key;
    protected boolean keyRequired;
    protected C2ServerHandler c2handler;

    public AbstractPacket(int packetID)
    {
        this(packetID, true);
    }

    public AbstractPacket(int packetID, boolean keyRequired)
    {
        this.packetID = packetID;
        this.keyRequired = keyRequired;
    }

    public void setC2Handler(C2ServerHandler c2handler)
    {
        this.c2handler = c2handler;
    }

    public abstract void readData(DataInputStream dis);

    public abstract void writeData(DataOutputStream dos);

    public abstract void handlePacket(PacketHandler handler);
}
