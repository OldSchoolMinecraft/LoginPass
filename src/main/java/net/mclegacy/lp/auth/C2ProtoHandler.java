package net.mclegacy.lp.auth;

import net.mclegacy.lp.LoginPass;
import net.mclegacy.lp.proto.PacketHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

public class C2ProtoHandler extends Thread
{
    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    public C2ProtoHandler(Socket socket)
    {
        this.socket = socket;
    }

    public void init()
    {
        try
        {
            this.dis = new DataInputStream(socket.getInputStream());
            this.dos = new DataOutputStream(socket.getOutputStream());

            start();
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public void run()
    {
        System.out.println("[LoginPass] C2 proto handler started: " + socket.getInetAddress());
        while (socket.isConnected() && !socket.isClosed())
        {
            try {
                String cmd = dis.readUTF();
                String[] parts = cmd.split(" ");
                String label = parts[0];
                if (label.equals("AUTH")) {
                    String username = parts[1];
                    String key = parts[2];

                    OfflinePlayer player = Bukkit.getOfflinePlayer(username);
                    if (player.isOnline() && key.equals(LoginPass.getInstance().getConfig().getConfigOption("c2key"))) {
                        AuthPluginHandler handler = selectAuthPlugin();
                        handler.authenticate(username, socket.getInetAddress().getHostAddress());
                        System.out.println("[LoginPass] " + username + " was authenticated via C2 (" + socket.getInetAddress() + ")");
                        dos.writeUTF("SUCCESS Authorized");
                        Bukkit.getPlayer(username).sendMessage(ChatColor.GREEN + "You have been authorized via " + ChatColor.BLUE + "MCLegacy");
                    } else {
                        dos.writeUTF("ERROR Invalid key or player is not online");
                    }
                }
            } catch (IndexOutOfBoundsException ex) {
                try {
                    dos.writeUTF("ERROR Missing data from request");
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            } catch (EOFException ignored) {}
            catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }
        System.out.println("[LoginPass] C2 proto handler stopped: " + socket.getInetAddress());
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
