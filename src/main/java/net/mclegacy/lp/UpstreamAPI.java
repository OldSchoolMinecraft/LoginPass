package net.mclegacy.lp;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.mclegacy.lp.ex.LinkException;
import okhttp3.*;

public class UpstreamAPI
{
    private static final Gson gson = new Gson();
    private static final OkHttpClient httpClient = LoginPass.getInstance().getHttpClient();
    private static final PluginConfig config = LoginPass.getInstance().getConfig();

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
}
