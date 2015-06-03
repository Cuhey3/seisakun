package mycode.converter.bean;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Similar {

    public void mask(@Body List<Map<String, String>> listMap, @Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        int pairCount = 1;
        int len = Integer.parseInt(param.remove(0));
        String resultField = resultField(param, len);
        field.addUnique(resultField);
        while (itr.hasNext()) {
            Map<String, String> row_a = itr.next();
            if (row_a.get("無効").equals("1")) {
                continue;
            }
            String a_key = "";
            for (String f : param) {
                a_key += row_a.get(f);
            }
            Map<String, String> a_keySubstrMap = keyToSubstrMap(a_key, len);
            Iterator<Map<String, String>> itr_b = listMap.iterator();
            boolean noSkip = false;
            while (itr_b.hasNext()) {
                Map<String, String> row_b = itr_b.next();
                if (row_b.get("無効").equals("1")) {
                    continue;
                }
                if (row_a == row_b) {
                    noSkip = true;
                } else if (noSkip) {
                    String b_key = "";
                    for (String f : param) {
                        b_key += row_b.get(f);
                    }
                    if (!b_key.equals(a_key) && substrCompare(b_key, a_keySubstrMap)) {
                        row_a.put(resultField, pairCount + "");
                        row_b.put(resultField, pairCount + "");
                        pairCount++;
                    }
                }
            }
        }
    }

    private Map<String, String> keyToSubstrMap(String key, int len) {
        Map<String, String> map = new java.util.HashMap<>();
        for (int i = 0; i < key.length() - len + 1; i++) {
            map.put(key.substring(0, i), key.substring(i + len));
        }
        return map;
    }

    public boolean substrCompare(String key, Map<String, String> map) {
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            if (key.startsWith(next.getKey()) && key.endsWith(next.getValue())) {
                return true;
            }
        }
        return false;
    }

    public String resultField(List<String> params, int len) {
        String resultField = "";
        for (String f : params) {
            resultField += f;
        }
        resultField += " " + len + "文字違い";
        return resultField;
    }
}
