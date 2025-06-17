package dtcp.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class SettingTest {

    private static JSONObject mockJson;

    @BeforeAll
    static void setup() throws JSONException {
        mockJson = new JSONObject("{\"test\": \"success\",\"testArray\": [1, 2, 3],\"mixedArray\": [1, true, 0.5]}");
    }

    @Test
    void withJsonString_whenGetSetting_thenGetValue() {
        try (MockedStatic<JSONMapper> mockedJSONUtils = Mockito.mockStatic(JSONMapper.class)) {
            mockedJSONUtils.when(() -> JSONMapper.read(Mockito.anyString())).thenReturn(mockJson);

            Setting<String> testSetting = new Setting<>("test", "default");
            Assertions.assertEquals("success", testSetting.get());
        }
    }

    @Test
    void withJsonArray_whenGetSetting_thenGetValue() {
        try (MockedStatic<JSONMapper> mockedJSONUtils = Mockito.mockStatic(JSONMapper.class)) {
            mockedJSONUtils.when(() -> JSONMapper.read(Mockito.anyString())).thenReturn(mockJson);

            Setting<List<Integer>> testSetting = new Setting<>("testArray", new ArrayList<>(), Integer.class);
            int[] resultArray = testSetting.get().stream().mapToInt(Integer::intValue).toArray();
            Assertions.assertArrayEquals(new int[]{1, 2, 3}, resultArray);
        }
    }

    @Test
    void withJsonArrayOfMultipleTypes_whenGetSetting_thenGetDefaultValue() {
        try (MockedStatic<JSONMapper> mockedJSONUtils = Mockito.mockStatic(JSONMapper.class)) {
            mockedJSONUtils.when(() -> JSONMapper.read(Mockito.anyString())).thenReturn(mockJson);

            int[] expectedArray = new int[]{4, 5, 6};
            List<Integer> defaultList = Arrays.stream(expectedArray).boxed().toList();

            Setting<List<Integer>> testSetting = new Setting<>("mixedArray", defaultList, Integer.class);
            int[] resultArray = testSetting.get().stream().mapToInt(Integer::intValue).toArray();

            Assertions.assertArrayEquals(expectedArray, resultArray);
        }
    }

    @Test
    void withIOException_whenGetSetting_thenGetDefaultValue() {
        try (MockedStatic<JSONMapper> mockedJSONUtils = Mockito.mockStatic(JSONMapper.class)) {
            mockedJSONUtils.when(() -> JSONMapper.read(Mockito.anyString())).thenThrow(new IOException("Test IO exception"));

            Setting<String> testSetting = new Setting<>("test", "default");
            Assertions.assertEquals("default", testSetting.get());
        }
    }

    @Test
    void withInvalidJson_whenGetSetting_thenGetDefaultValue() {
        try (MockedStatic<JSONMapper> mockedJSONUtils = Mockito.mockStatic(JSONMapper.class)) {
            mockedJSONUtils.when(() -> JSONMapper.read(Mockito.anyString())).thenThrow(new JSONException("Test JSON exception"));

            Setting<String> testSetting = new Setting<>("test", "default");
            Assertions.assertEquals("default", testSetting.get());
        }
    }

    @Test
    void withInvalidSetting_whenGetSetting_thenGetDefaultValue() {
        try (MockedStatic<JSONMapper> mockedJSONUtils = Mockito.mockStatic(JSONMapper.class)) {
            mockedJSONUtils.when(() -> JSONMapper.read(Mockito.anyString())).thenReturn(mockJson);

            Setting<String> testSetting = new Setting<>("invalid", "default");
            Assertions.assertEquals("default", testSetting.get());
        }
    }
}