package dev.bloodcore.disguise.util;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class HTTPUtility {

    private final JavaPlugin plugin;
    private final Cache<String, String[]> cachedSkinResponses = CacheBuilder.newBuilder()
            .expireAfterWrite(5L, TimeUnit.MINUTES)
            .build();

    private interface JSONResponseCallback {
        void handle(JsonObject response);
    }

    private interface UUIDResponseCallback {
        void handle(String uuid);
    }

    public interface GetTextureResponse {
        void handle(String texture, String signature);
    }

    public void getTextureAndSignature(String playerName, GetTextureResponse response) {
        String[] previousResponse = cachedSkinResponses.getIfPresent(playerName);
        if (previousResponse != null) {
            response.handle(previousResponse[0], previousResponse[1]);
            return;
        }

        getUUIDForPlayerName(playerName, (uuid -> {
            if (uuid == null) {
                response.handle(null, null);
                return;
            }

            getTextureAndSignatureFromUUID(uuid, ((texture, signature) -> {
                cachedSkinResponses.put(playerName, new String[]{texture, signature});
                response.handle(texture, signature);
            }));
        }));
    }

    public void getUUIDForPlayerName(String playerName, UUIDResponseCallback response) {
        get("https://api.mojang.com/users/profiles/minecraft/" + playerName, (uuidReply) -> {
            if (uuidReply == null) {
                response.handle(null);
                return;
            }

            String uuidString = uuidReply.get("id").getAsString();
            if (uuidString == null) {
                response.handle(null);
                return;
            }

            response.handle(uuidString);
        });
    }

    public void getTextureAndSignatureFromUUID(String uuidString, GetTextureResponse response) {

            String texture = null;
            String signature = null;
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://api.mineskin.org/generate/user/" + uuidString).openConnection();
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();

                    texture= reply.split("\"value\":\"")[1].split("\"")[0];
                    signature = reply.split("\"signature\":\"")[1].split("\"")[0];

                }
            }catch (Exception ignored){}


            response.handle(texture, signature);

    }

    private void get(String url, JSONResponseCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL rawURL = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) rawURL.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                connection.disconnect();

                if (content.toString().isEmpty()) { // Mojang API 204 fix
                    Bukkit.getScheduler().runTask(plugin, () -> callback.handle(null));
                    return;
                }

                JsonObject jsonObject = new JsonParser().parse(content.toString()).getAsJsonObject();
                Bukkit.getScheduler().runTask(plugin, () -> callback.handle(jsonObject));
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getScheduler().runTask(plugin, () -> callback.handle(null));
            }
        });
    }
}
