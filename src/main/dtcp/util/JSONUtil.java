package dtcp.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.lazylib.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JSONUtil {

    private static final Map<String, JSONObject> cache = new HashMap<>();

    public static JSONObject read(String path) throws IOException, JSONException {
        JSONObject cachedJson = cache.get(path);
        if (cachedJson != null) {
            return cachedJson;
        }

        byte[] jsonBytes = IOUtils.readAllBytes(path);
        String jsonString = new String(jsonBytes, StandardCharsets.UTF_8);
        JSONObject jsonObject = new JSONObject(jsonString);
        cache.put(path, jsonObject);
        return jsonObject;
    }
}
