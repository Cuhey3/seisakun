package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Column {

    public void add(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String makeField = param.toString().split("=", -1)[0];
        String value = "";
        if (param.toString().contains("=")) {
            value = param.toString().split("=", -1)[1];
        }
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            if (!next.containsKey(makeField)) {
                next.put(makeField, value);
            }
        }
        field.addUnique(makeField);
    }

    public void addNext(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        
        String beforeField = param.first();
        String makeField = param.second().split("=", -1)[0];
        System.out.println(beforeField + " " + makeField);
        String value = "";
        if (param.second().contains("=")) {
            value = param.second().split("=", -1)[1];
        }
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            if (!next.containsKey(makeField)) {
                next.put(makeField, value);
            }
        }
        field.addNext(beforeField, makeField);
    }

    public void addTop(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String makeField = param.toString().split("=", -1)[0];
        String value = "";
        if (param.toString().contains("=")) {
            value = param.toString().split("=", -1)[1];
        }
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            if (!next.containsKey(makeField)) {
                next.put(makeField, value);
            }
        }
        field.addTop(makeField);
    }

    public void copy(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        String makeField = param.second();
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            map.put(makeField, map.get(targetField));
        }
        field.addUnique(makeField);
    }

    public void remove(@Header("parameter") Parameter param, @Header("field") Field field) {
        for (String p : param) {
            field.remove(p);
        }
    }

    public void rename(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String oldField = param.first();
        String newField = param.second();
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            next.put(newField, next.get(oldField));
        }
        field.set(field.indexOf(oldField), newField);
    }
}
