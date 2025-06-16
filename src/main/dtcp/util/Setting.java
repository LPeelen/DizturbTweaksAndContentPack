package dtcp.util;

import com.fs.starfarer.api.Global;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class Setting<T> {
    private static final String PATH_TO_CONFIG = "data/config/config.json";
    private static final Logger LOGGER = Global.getLogger(Setting.class);

    private final String key;
    private final T defaultValue;
    private final Class<?> memberType;

    public Setting(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.memberType = null;
    }

    public Setting(String key, T defaultValue, Class<?> memberType) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.memberType = memberType;
    }

    public T get() {
        try {
            Object jsonObject = JSONUtil.read(PATH_TO_CONFIG).opt(key);

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

            Object[] array = new Object[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                if (!jsonArray.get(i).getClass().equals(memberType)) {
                    throw new ClassCastException("Cannot cast item of JSON array '" + key + "' with type '" +
                            jsonArray.get(i).getClass().getTypeName() + "' to '" + memberType.getTypeName() + "'");
                }
                array[i] = jsonArray.get(i);
            }

            return (T) Arrays.asList(array);
        } catch (JSONException | IOException | IllegalArgumentException | ClassCastException e) {
            LOGGER.error("Error reading: '" + PATH_TO_CONFIG + "' - '" + e.getMessage() +
                    "' Falling back on default value '" + defaultValue + "'");
            return defaultValue;
        }
    }
}
