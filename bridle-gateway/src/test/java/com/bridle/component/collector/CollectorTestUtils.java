package com.bridle.component.collector;

import java.util.HashMap;
import java.util.Map;

public class CollectorTestUtils {
    public static final String RQ_UID_KEY = "RqUID";
    public static final String RQUID_VALUE = "123456";
    public static final String SYS_ID_KEY = "SysId";
    public static final String SYS_ID_VALUE = "KALITA";
    public static final String MSG_ID_KEY = "MsgId";

    static Map<String, String> createCorrectJsonExpressionsByName() {
        Map<String, String> expressionsByName = new HashMap<>();
        expressionsByName.put(RQ_UID_KEY, "/" + RQ_UID_KEY);
        expressionsByName.put(SYS_ID_KEY, "/" + SYS_ID_KEY);
        expressionsByName.put(MSG_ID_KEY, "/" + MSG_ID_KEY);
        return expressionsByName;
    }
}
