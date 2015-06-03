package mycode.converter.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Row {

    public void make(@Body List<Map<String, String>> listMap, @Header("parameter") Parameter param, @Header("field") Field field) {
        int count = Integer.parseInt(param.first());
        listMap.clear();
        for (int i = 0; i < count; i++) {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (String s : field) {
                map.put(s, "");
            }
            listMap.add(map);
        }
    }

    public void shuffle(@Body List<Map<String, String>> listMap) {
        Collections.shuffle(listMap);
    }

    public void top(@Body List<Map<String, String>> listMap, @Header("parameter") Parameter param) {
        int size = Integer.parseInt(param.first());
        while (listMap.size() > size) {
            listMap.remove(size);
        }
    }

    public void time(@Body List<Map<String, String>> listMap, @Header("parameter") Parameter param) {
        String timeField = param.first();
        List<Map<String, String>> newListMap = new ArrayList<>();
        for (Map<String, String> m : listMap) {
            String get = m.get(timeField);
            int time = Integer.parseInt(get);
            if (time < 1) {
                System.out.println(m);
            }
            for (int i = 0; i < time; i++) {
                newListMap.add(new LinkedHashMap<>(m));
            }
        }
        listMap.clear();
        for (Map<String, String> m : newListMap) {
            listMap.add(m);
        }
    }
}
