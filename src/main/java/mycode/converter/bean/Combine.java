package mycode.converter.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Combine {

    public void simple(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param) {
        combine(itr, param, "");
    }

    public void withSeparator(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param) {
        String separator = param.remove(1).replaceFirst("^/", "").replaceFirst("/$", "");
        combine(itr, param, separator);
    }

    public void combine(Iterator<Map<String, String>> itr, ArrayList<String> params, String separator) {
        String retainField = params.get(0);
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String combined = "";
            for (String field : params) {
                if (combined.isEmpty() || separator.isEmpty()) {
                    combined += map.get(field);
                } else {
                    combined += separator + map.get(field);
                }
                map.put(field, "");
            }
            map.put(retainField, combined);
        }
    }

    public void seimei(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) {
        field.addNext("名", "姓名");
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            map.put("姓名", map.get("姓") + "　" + map.get("名"));
        }
    }
}
