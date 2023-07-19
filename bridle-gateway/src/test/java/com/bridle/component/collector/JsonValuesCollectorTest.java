package com.bridle.component.collector;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.bridle.component.collector.CollectorTestUtils.MSG_ID_KEY;
import static com.bridle.component.collector.CollectorTestUtils.RQUID_VALUE;
import static com.bridle.component.collector.CollectorTestUtils.RQ_UID_KEY;
import static com.bridle.component.collector.CollectorTestUtils.SYS_ID_KEY;
import static com.bridle.component.collector.CollectorTestUtils.SYS_ID_VALUE;
import static com.bridle.component.collector.CollectorTestUtils.createCorrectJsonExpressionsByName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class JsonValuesCollectorTest {

    private static final String BAD_JSON =
            "{" + "\"" + RQ_UID_KEY + "\": \"" + RQUID_VALUE + "\"," + "\"" + SYS_ID_KEY + "\": " + "\"" +
                    SYS_ID_VALUE + "\"";

    private static final String CORRECT_JSON =
            "{" + "\"" + RQ_UID_KEY + "\": \"" + RQUID_VALUE + "\"," + "\"" + SYS_ID_KEY + "\": " + "\"" +
                    SYS_ID_VALUE + "\"" + "}";

    @Test
    void collectValuesThrowsExceptionWhenBodyIsNull() {
        JsonValuesCollector collector = new JsonValuesCollector(new HashMap<>());
        assertThrows(JsonCollectorException.class, () -> collector.collectValues(null));
    }

    @Test
    void collectValuesThrowsExceptionWhenBodyIsNotCorrectJson() {
        JsonValuesCollector collector = new JsonValuesCollector(createCorrectJsonExpressionsByName());
        assertThrows(JsonCollectorException.class, () -> collector.collectValues(BAD_JSON));
    }

    @Test
    void collectValuesReturnsEmptyOptionalWhenExpressionMapIsNull() {
        JsonValuesCollector collector = new JsonValuesCollector(null);
        Optional<Map<String, Object>> result = collector.collectValues(CORRECT_JSON);
        assertTrue(result.isEmpty());
    }

    @Test
    void collectValuesReturnsEmptyOptionalWhenExpressionMapIsEmpty() {
        JsonValuesCollector collector = new JsonValuesCollector(new HashMap<>());
        Optional<Map<String, Object>> result = collector.collectValues(CORRECT_JSON);
        assertTrue(result.isEmpty());
    }

    @Test
    void collectValuesThrowsExceptionWhenAnyExpressionIsNotCorrect() {
        JsonValuesCollector collector = new JsonValuesCollector(createIncorrectExpressionsByName());
        assertThrows(JsonCollectorException.class, () -> collector.collectValues(CORRECT_JSON));
    }

    @Test
    void collectValuesReturnsMapOfValues() {
        JsonValuesCollector collector = new JsonValuesCollector(createCorrectJsonExpressionsByName());
        Optional<Map<String, Object>> result = collector.collectValues(CORRECT_JSON);
        assertTrue(result.isPresent());
        assertEquals(RQUID_VALUE, result.get().get(RQ_UID_KEY));
        assertEquals(SYS_ID_VALUE, result.get().get(SYS_ID_KEY));
        assertNull(result.get().get(MSG_ID_KEY));
    }

    @Test
    void collectValuesReturnsNullValueIfThereIsNoValueInJson() {
        Map<String, String> expressionsByName = new HashMap<>();
        String unknownKey = "unknownKey";
        expressionsByName.put(unknownKey, "/unknownKey");
        JsonValuesCollector collector = new JsonValuesCollector(expressionsByName);
        Optional<Map<String, Object>> result = collector.collectValues(CORRECT_JSON);
        assertTrue(result.isPresent());
        assertNull(result.get().get(unknownKey));
        assertEquals(1, result.get().size());
    }

    @Test
    void collectValuesReturnsEmptyStringIfEmtpyStringInJsonValue() {
        Map<String, String> expressionsByName = new HashMap<>();
        String emptyKey = "empty";
        expressionsByName.put(emptyKey, "/empty");
        JsonValuesCollector collector = new JsonValuesCollector(expressionsByName);
        String jsonWithEmptyValue = "{" + "\"" + emptyKey + "\": \"\"}";
        Optional<Map<String, Object>> result = collector.collectValues(jsonWithEmptyValue);
        assertTrue(result.isPresent());
        assertNull(result.get().get(""));
        assertEquals(1, result.get().size());
    }

    private Map<String, String> createIncorrectExpressionsByName() {
        Map<String, String> expressionsByName = new HashMap<>();
        expressionsByName.put(SYS_ID_KEY, SYS_ID_KEY + "Expression");
        expressionsByName.put(RQ_UID_KEY, "/" + RQ_UID_KEY);
        return expressionsByName;
    }
}
