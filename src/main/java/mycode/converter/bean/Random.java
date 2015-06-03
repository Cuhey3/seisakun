package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;
import mycode.converter.util.MTRandom;

@Component
public class Random {

    public void single(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        if (targetField.isEmpty()) {
            targetField = "乱数";
        }
        MTRandom random = new MTRandom();
        while (itr.hasNext()) {
            itr.next().put(targetField, random.nextDouble() + "");
        }
        field.addUnique(targetField);
    }

    public void multi(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField, multiField;
        if (param.size() == 1) {
            targetField = "乱数";
            multiField = param.first();
        } else {
            multiField = param.first();
            targetField = param.second();
        }

        MTRandom random = new MTRandom();
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            map.put(targetField, multi(random, map.get(multiField)) + "");
        }
        field.addUnique(targetField);
    }

    private Double multi(MTRandom random, String m) {
        int multi = Integer.parseInt(m);
        Double max = 0.0;
        for (int i = 0; i < multi; i++) {
            max = Math.max(max, random.nextDouble());
        }
        return max;
    }
}
