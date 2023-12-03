package net.mclegacy.lp;

import net.mclegacy.lp.auth.C2ServerListener;
import okhttp3.OkHttpClient;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LoginPass extends JavaPlugin
{
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
        System.out.println("LoginPass enabled");
    }

    public void onDisable()
    {
        c2listener.end();

        System.out.println("LoginPass disabled");
    }
}
