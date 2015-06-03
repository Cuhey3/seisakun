package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Match {

    public void regex(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        String okField = targetField + "確認";
        String regexString = param.second().replaceFirst("^/", "").replaceFirst("/$", "");
        Pattern regex = Pattern.compile(regexString);
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String target = map.get(targetField);
            if (regex.matcher(target).find()) {
                map.put(okField, "○");
            } else {
                map.put("無効", "1");
                map.put(okField, "×");
            }
        }
        field.addTop("無効");
        field.addUnique(okField);
    }

    public void notRegex(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        String okField = targetField + "確認";
        String regexString = param.second().replaceFirst("^/", "").replaceFirst("/$", "");
        Pattern regex = Pattern.compile(regexString);
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String target = map.get(targetField);
            if (regex.matcher(target).find()) {
                map.put("無効", "1");
                map.put(okField, "×");
            } else {
                map.put(okField, "○");
            }
        }
        field.addTop("無効");
        field.addUnique(okField);
    }

    public void value(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first().split("=")[0];
        String value = "";
        try {
            value = param.first().split("=")[1];
        } catch (Throwable t) {
        }
        String okField = targetField + "確認";
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String target = map.get(targetField);
            if (target.equals(value)) {
                map.put(okField, "○");
            } else {
                map.put("無効", "1");
                map.put(okField, "×");
            }
        }
        field.addTop("無効");
        field.addUnique(okField);
    }

    public void notValue(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first().split("=")[0];
        String value = "";
        try {
            value = param.first().split("=")[1];
        } catch (Throwable t) {
        }
        String okField = targetField + "確認";
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String target = map.get(targetField);
            if (target.equals(value)) {
                map.put("無効", "1");
                map.put(okField, "×");
            } else {
                map.put(okField, "○");
            }
        }
        field.addTop("無効");
        field.addUnique(okField);
    }
}
