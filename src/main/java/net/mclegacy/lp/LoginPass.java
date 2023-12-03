package net.mclegacy.lp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mclegacy.lp.auth.C2ServerListener;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class LoginPass extends JavaPlugin
{
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

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, () ->
        {
            String holderName = String.valueOf(config.getConfigOption("holderName"));
            String holderUUID = String.valueOf(config.getConfigOption("holderUUID"));
            String c2host = String.valueOf(config.getConfigOption("c2host"));
            int c2port = (int) config.getConfigOption("c2port");

            JsonObject req = new JsonObject();
            req.addProperty("holderName", holderName);
            req.addProperty("holderUUID", holderUUID);
            req.addProperty("c2", c2host + ":" + c2port);
            req.addProperty("playerCount", getServer().getOnlinePlayers().length);
            JsonArray playersArray = new JsonArray();
            for (Player onlinePlayer : getServer().getOnlinePlayers())
                playersArray.add((onlinePlayer.getName()));
            req.add("onlinePlayers", playersArray);

            Request request = new Request.Builder()
                    .url("https://mclegacy.net/api/v1/servers")
                    .addHeader("X-API-KeyHolder", holderName)
                    .addHeader("X-API-Key", String.valueOf(config.getConfigOption("c2key")))
                    .put(RequestBody.create(gson.toJson(req), MediaType.parse("application/json")))
                    .build();

            try (Response response = getHttpClient().newCall(request).execute())
            {
                JsonObject res = gson.fromJson(response.body().string(), JsonObject.class);
                if (res == null)
                {
                    System.out.println("[LoginPass] An unknown error occurred while attempting to announce to MCLegacy server list.");
                    return;
                }
                if (!res.get("success").getAsBoolean()) System.out.println("[LoginPass] Failed to announce to MCLegacy server list: " + res.get("message").getAsString());
                else if (res.get("success").getAsBoolean() && res.has("notice")) System.out.println("[LoginPass] Notice from API: " + res.get("notice").getAsString());
            } catch (IOException e) {
                System.out.println("[LoginPass] Failed to announce to MCLegacy server list: " + e.getMessage());
            }
        }, 0L, 6000L);

        System.out.println("LoginPass enabled");
    }

    public void onDisable()
    {
        c2listener.end();

        System.out.println("LoginPass disabled");
    }
}
