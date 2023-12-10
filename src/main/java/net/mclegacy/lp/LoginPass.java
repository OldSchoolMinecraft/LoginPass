package net.mclegacy.lp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mclegacy.lp.auth.*;
import net.mclegacy.lp.proto.AuthorizePacket;
import net.mclegacy.lp.proto.DisconnectPacket;
import net.mclegacy.lp.proto.ErrorPacket;
import net.mclegacy.lp.proto.PacketRegistry;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class LoginPass extends JavaPlugin
{
    public static final List<AuthPluginHandler> SUPPORTED_AUTH_HANDLERS = Arrays.asList(new OSASHandler(), new AuthMeHandler(), new xAuthHandler());
    private static final Gson gson = new Gson();
    private static OkHttpClient okHttpClient;
    private static LoginPass instance;
    public static LoginPass getInstance()
    {
        return instance;
    }

    private C2ServerListener c2listener;
    private PluginConfig config;

    public PluginConfig getConfig()
    {
        return config;
    }

    public OkHttpClient getHttpClient()
    {
        return okHttpClient;
    }

    public void onEnable()
    {
        instance = this;
        okHttpClient = new OkHttpClient();
        config = new PluginConfig(new File(getDataFolder(), "config.yml"));
        config.reload();

        PacketRegistry.registerPacket(1, AuthorizePacket.class);
        PacketRegistry.registerPacket(99, DisconnectPacket.class);
        PacketRegistry.registerPacket(111, ErrorPacket.class);

        c2listener = new C2ServerListener(config.getInt("c2port", 12992));
        c2listener.start();

        getCommand("mclink").setExecutor(new LinkCommand());

        UpstreamAPI.pingTracker(); // initial ping on startup
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, UpstreamAPI::pingTracker, 0L, 6000L); // 6K ticks = 5 minutes (i think lol)

        System.out.println("LoginPass enabled");
    }

    public void onDisable()
    {
        c2listener.stopServer();

        System.out.println("LoginPass disabled");
    }
}
