package net.mclegacy.lp.proto;

import java.util.HashMap;

public class PacketRegistry
{
    public static HashMap<Integer, Class<? extends AbstractPacket>> idToClass = new HashMap<>();

    public static void registerPacket(int packetID, Class<? extends AbstractPacket> packetClass)
    {
        idToClass.put(packetID, packetClass);
    }
    public static Class<? extends AbstractPacket> getPacketClass(int packetID)
    {
        return idToClass.get(packetID);
    }
}
