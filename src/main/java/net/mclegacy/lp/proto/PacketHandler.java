package net.mclegacy.lp.proto;

import net.mclegacy.lp.LoginPass;
import net.mclegacy.lp.auth.AuthHandlerException;
import net.mclegacy.lp.auth.AuthPluginHandler;

public class PacketHandler
{
    public void handlePacket(AbstractPacket packet)
    {
        if (packet != null)
        {
            System.err.println("[LoginPass] Received invalid packet from C2 client: " + packet.packetID + ": " + packet.c2handler.c2socket);
        } else System.err.println("[LoginPass] Received malformed packet");
    }

    public void handleDisconnectPacket(DisconnectPacket packet)
    {
        if (packet != null)
        {
            try
            {
                System.out.println("[LoginPass] C2 client disconnected: " + packet.message);
                packet.c2handler.end();
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    public void handleAuthorizePacket(AuthorizePacket packet)
    {
        if (packet != null)
        {
            try
            {
                AuthPluginHandler handler = selectAuthPlugin();
                handler.authenticate(packet.username, packet.c2handler.c2socket.getInetAddress().getHostAddress());
                System.out.println("[LoginPass] " + packet.username + " was authenticated via C2 (" + packet.c2handler.c2socket + ")");
            } catch (AuthHandlerException e) {
                throw new RuntimeException(e);
            }
        } else System.err.println("[LoginPass] Received malformed auth packet");
    }

    private AuthPluginHandler selectAuthPlugin()
    {
        return LoginPass.SUPPORTED_AUTH_HANDLERS.stream()
                .filter(AuthPluginHandler::isInstalled)
                .reduce((first, second) -> {
                    throw new RuntimeException("Multiple auth plugins are installed. Only one can be used at a time.");
                })
                .orElseThrow(() -> new RuntimeException("No supported auth plugins detected"));
    }
}
