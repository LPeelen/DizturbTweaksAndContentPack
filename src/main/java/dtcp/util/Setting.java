package dtcp.util;

import com.fs.starfarer.api.Global;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a typed setting loaded from a JSON configuration file.
 * <p>
 * Supports primitive values and collections (e.g. List&lt;String&gt;) loaded from {@code data/config/config.json}.
 * If the setting cannot be found or parsed, the {@code defaultValue} is returned instead.
 *
 * @param <T> the type of the setting value
 */
public class Setting<T> {
    private static final String PATH_TO_CONFIG = "data/config/config.json";
    private static final Logger LOGGER = Global.getLogger(Setting.class);

    private final String key;
    private final T defaultValue;
    private final Class<?> memberType;

    /**
     * Constructs a setting for a single primitive or object value (e.g. String, Integer, Boolean).
     *
     * @param key          the key of the setting in the JSON file
     * @param defaultValue the default value to return if the setting is missing or invalid
     */
    public Setting(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.memberType = null;
    }

    /**
     * Constructs a setting for a collection of values (e.g. List&lt;String&gt;) stored as a JSON array.
     *
     * @param key          the key of the setting in the JSON file
     * @param defaultValue the default collection to return if the setting is missing or invalid
     * @param memberType   the expected type of elements in the JSON array (e.g. String.class)
     */
    public Setting(String key, T defaultValue, Class<?> memberType) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.memberType = memberType;
    }

    /**
     * Returns the value of this setting as loaded from the JSON configuration file.
     * <p>
     * If the setting is not found or cannot be parsed (e.g., type mismatch), the default value is returned.
     * For collection settings, each element is checked against {@code memberType}.
     *
     * @return the loaded setting value, or the default value on error
     */
    public T get() {
        try {
            Object jsonObject = JSONMapper.read(PATH_TO_CONFIG).opt(key);

            if (jsonObject == null) {
                LOGGER.warn("Could not find setting '" + key + "' falling back on default value '" +
                        defaultValue + "'");
                return defaultValue;
            }

            if (!(jsonObject instanceof JSONArray jsonArray)) {
                return (T) defaultValue.getClass().cast(jsonObject);
            }

            if (memberType == null) {
                throw new IllegalArgumentException("Argument memberType should not be null when getting a JSON array");
            }

            if (!(defaultValue instanceof Collection<?>)) {
                throw new IllegalArgumentException("Cannot cast '" + defaultValue.getClass().getTypeName() +
                        "' to " + Collection.class.getTypeName() + "' of '" + memberType.getTypeName() + "'");
            }

            Collection<Object> collection = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                if (!jsonArray.get(i).getClass().equals(memberType)) {
                    throw new ClassCastException("Cannot cast item of JSON array '" + key + "' with type '" +
                            jsonArray.get(i).getClass().getTypeName() + "' to '" + memberType.getTypeName() + "'");
                }
                collection.add(jsonArray.get(i));
            }

            return (T) collection;
        } catch (JSONException | IOException | IllegalArgumentException | ClassCastException e) {
            LOGGER.error("Error reading: '" + PATH_TO_CONFIG + "' - '" + e.getMessage() +
                    "' Falling back on default value '" + defaultValue + "'");
            return defaultValue;
        }
    }
}
