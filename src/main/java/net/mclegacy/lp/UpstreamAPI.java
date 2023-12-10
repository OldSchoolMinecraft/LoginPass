package net.mclegacy.lp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.mclegacy.lp.ex.LinkException;
import okhttp3.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class UpstreamAPI
{
    private static final Gson gson = new Gson();
    private static final OkHttpClient httpClient = LoginPass.getInstance().getHttpClient();
    private static final PluginConfig config = LoginPass.getInstance().getConfig();
    private static boolean iconAlreadyCached = false;
    private static String serverIconSHA1 = "N/A";
    private static boolean shouldUpdateIconInNextPing = true;
    private static final boolean debugMode = (boolean) LoginPass.getInstance().getConfig().getConfigOption("debugMode");

    public static void validateLinkCode(String username, int code) throws LinkException
    {
        JsonObject req = new JsonObject();
        req.addProperty("username", username);
        req.addProperty("code", code);

        Request request = new Request.Builder()
                .url("https://mclegacy.net/api/v1/linking")
                .addHeader("X-API-KeyHolder", String.valueOf(config.getConfigOption("holderName")))
                .addHeader("X-API-Key", String.valueOf(config.getConfigOption("c2key")))
                .post(RequestBody.create(gson.toJson(req), MediaType.parse("application/json")))
                .build();

        try (Response response = httpClient.newCall(request).execute())
        {
            JsonObject res = gson.fromJson(response.body().string(), JsonObject.class);
            if (res == null)
                throw new LinkException("API gave an unreadable response. Try again later.");
            if (!res.get("success").getAsBoolean()) throw new LinkException("API refused to link: " + res.get("message").getAsString());
        } catch (Exception ex) {
            throw new LinkException("Error while linking: " + ex.getMessage());
        }
    }

    public static void pingTracker()
    {
        String holderName = String.valueOf(config.getConfigOption("holderName"));
        String holderUUID = String.valueOf(config.getConfigOption("holderUUID"));
        String c2host = String.valueOf(config.getConfigOption("c2host"));
        int c2port = (int) config.getConfigOption("c2port");
        File iconFile = new File(LoginPass.getInstance().getDataFolder(), "server-icon.png");

        JsonObject req = new JsonObject();
        req.addProperty("holderName", holderName);
        req.addProperty("holderUUID", holderUUID);
        req.addProperty("c2", c2host + ":" + c2port);
        req.addProperty("maxPlayerCount", Bukkit.getServer().getMaxPlayers());
        if (iconFile.exists() && shouldUpdateIconInNextPing)
            req.addProperty("serverIcon", Util.encodeFileToBase64(iconFile.getAbsolutePath()));
        ArrayList<String> playersArray = new ArrayList<>();
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers())
            playersArray.add((onlinePlayer.getName()));
        req.add("players", gson.toJsonTree(playersArray));

        String rawreq = gson.toJson(req);
        if (debugMode) System.out.println("Raw request: " + rawreq);

        Request request = new Request.Builder()
                .url("https://mclegacy.net/api/v1/ping")
                .addHeader("X-API-KeyHolder", holderName)
                .addHeader("X-API-Key", String.valueOf(config.getConfigOption("c2key")))
                .post(RequestBody.Companion.create(rawreq, MediaType.parse("application/json")))
                .build();

        try (Response response = LoginPass.getInstance().getHttpClient().newCall(request).execute())
        {
            String rawres = response.body().string();
            if (debugMode) System.out.println("Raw response from LoginPass API: " + rawres);
            PingResponse res = gson.fromJson(rawres, PingResponse.class);
            if (res == null)
            {
                System.out.println("[LoginPass] An unknown error occurred while attempting to announce to MCLegacy server list, status: " + response.code());
                return;
            }
            if (!res.success) System.out.println("[LoginPass] Failed to announce to MCLegacy server list, status: " + response.code());
            else {
                iconAlreadyCached = res.iconAlreadyCached;
                serverIconSHA1 = res.iconHash;
                String localSHA1 = Util.calculateFileSHA1(iconFile.getAbsolutePath());
                shouldUpdateIconInNextPing = serverIconSHA1.length() < 20 || !serverIconSHA1.equals(localSHA1);
            }
            if (res.notice != null) System.out.println("[LoginPass] Notice from API: " + res.notice);
        } catch (IOException e) {
            System.out.println("[LoginPass] Failed to announce to MCLegacy server list: " + e.getMessage());
        }
    }
}
