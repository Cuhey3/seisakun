package mycode.converter.bean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import mycode.converter.spec.Field;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;

@Component
public class Groupby {

    public void zip(@Body List<Map<String, String>> body, @Header("field") Field field) {
        Iterator<Map<String, String>> iterator = body.iterator();
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        LinkedHashSet<String> set = new LinkedHashSet<>();
        while (iterator.hasNext()) {
            Map<String, String> next = iterator.next();
            if (next.containsKey("無効") && !next.get("無効").isEmpty()) {

            } else {
                String zip = next.get("郵便番号");
                if (zip != null && !zip.isEmpty()) {
                    if (map.containsKey(zip)) {
                        if (!set.contains(zip)) {
                            set.add(zip);
                            map.put(zip, set.size());
                        }
                    } else {
                        map.put(zip, null);
                    }
                }
            }
        }
        iterator = body.iterator();
        while (iterator.hasNext()) {
            Map<String, String> next = iterator.next();
            if (next.containsKey("無効") && !next.get("無効").isEmpty()) {
            } else {
                String zip = next.get("郵便番号");
                if (zip != null && !zip.isEmpty()) {
                    Integer get = map.get(zip);
                    if (get != null) {
                        next.put("郵番グループ", map.get(zip) + "");
                    }
                }
            }
        }
        field.addUnique("郵番グループ");
    }
}
