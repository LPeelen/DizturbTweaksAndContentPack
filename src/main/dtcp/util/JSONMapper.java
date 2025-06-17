package dtcp.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.lazywizard.lazylib.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple JSON loader with in-memory caching for parsed {@link JSONObject} instances.
 * <p>
 * Designed for reading configuration files with reduced overhead by avoiding repeated disk reads.
 * Subsequent calls to {@code read(path)} return the cached object if available.
 */
public class JSONMapper {

    private static final Map<String, JSONObject> cache = new HashMap<>();

    /**
     * Reads a JSON file from the given path and parses it into a {@link JSONObject}.
     * <p>
     * Parsed objects are cached while Starsector is running to avoid repeated I/O.
     *
     * @param path the path to the JSON file (relative to the Starsector data directory)
     * @return the parsed {@link JSONObject}
     * @throws IOException   if the file cannot be read
     * @throws JSONException if the file is not valid JSON
     */
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
