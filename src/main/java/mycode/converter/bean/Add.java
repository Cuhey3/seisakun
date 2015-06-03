package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Add {

    public void autoNumber(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        int count = 0;
        if (param.size() > 1) {
            count = Integer.parseInt(param.second());
        }
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            map.put(targetField, ++count + "");
        }
        field.addUnique(targetField);
    }
}
