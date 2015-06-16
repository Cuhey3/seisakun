package mycode.converter.bean;

import java.util.Iterator;
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
        Iterator<Map<String, String>> itr1 = body.iterator();
        LinkedHashSet<String> zipSet = new LinkedHashSet<>();
        while (itr1.hasNext()) {
            Map<String, String> next1 = itr1.next();
            Iterator<Map<String, String>> itr2 = body.iterator();
            boolean flag = false;
            Integer sign = null;
            String ng1 = next1.get("無効");
            if (ng1 != null && !ng1.isEmpty()) {
                continue;
            }
            String zip1 = next1.get("郵便番号");
            if (zip1 == null) {
                continue;
            }
            while (itr2.hasNext()) {
                Map<String, String> next2 = itr2.next();
                String ng2 = next2.get("無効");
                if (ng2 != null && !ng2.isEmpty()) {
                    continue;
                }
                if (flag) {
                    String zip2 = next2.get("郵便番号");
                    if (zip2 != null) {
                        if (zip1.equals(zip2)) {
                            if (sign == null) {
                                zipSet.add(zip1);
                                sign = zipSet.size();
                                next1.put("郵番グループ", sign + "");
                            }
                            next2.put("郵番グループ", sign + "");
                        }
                    }
                } else {
                    if (next1.equals(next2)) {
                        flag = true;
                    }
                }
            }
        }
        
        field.addUnique("郵番グループ");
    }
}
