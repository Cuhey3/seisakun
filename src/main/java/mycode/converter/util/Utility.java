package mycode.converter.util;

import java.util.Map;
import java.util.regex.Pattern;

public class Utility {

    public static String searchValue(Map<String, String> map, Pattern compiled) {
        for (String value : map.values()) {
            if (compiled.matcher(value).find()) {
                return value;
            }
        }
        return null;
    }
}
