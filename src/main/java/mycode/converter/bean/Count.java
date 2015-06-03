package mycode.converter.bean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Count {

    public void limit(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        LinkedHashMap<String, Integer> countMap = new LinkedHashMap<>();
        for (String p : param.second().split(":")) {
            String[] split = p.split("=");
            countMap.put(split[0], Integer.parseInt(split[1]));
        }
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String key = map.get(targetField);
            Integer count = countMap.get(key);
            if (count == null || count == 0 || field.contains("無効") && map.get("無効").equals("1")) {
                map.put("当選F", "");
            } else {
                map.put("当選F", "1");
                countMap.put(key, --count);
            }
        }
        field.addUnique("当選F");
    }

    public void sequence(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        String outputField = param.second();
        LinkedHashMap<String, Integer> countMap = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> sequenceMap = new LinkedHashMap<>();
        for (String p : param.third().split(";")) {
            String[] split = p.split("=");
            countMap.put(split[0], Integer.parseInt(split[1]));
            sequenceMap.put(split[0], 1);
        }
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String key = map.get(targetField);
            Integer count = countMap.get(key);
            if (count == null || count == 0 || field.contains("無効") && map.get("無効").equals("1")) {
                map.put(outputField, "");
            } else {
                Integer sequence = sequenceMap.get(key);
                map.put(outputField, sequence + "");
                countMap.put(key, --count);
                sequenceMap.put(key, ++sequence);
            }
        }
        field.addUnique(outputField);
    }
}
