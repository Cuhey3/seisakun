package mycode.converter.bean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Validate {

    private final Pattern phonePattern = Pattern.compile("^0([57-9]0\\d{8}|([57-9][^0]|[1-46]\\d)\\d{7})$");

    public void phone(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) {
        ArrayList<String> checkField = new ArrayList<>();
        for (String f : field) {
            if (f.startsWith("電話番号") && !f.endsWith("確認")) {
                checkField.add(f);
            }
        }
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            boolean flag = false;
            for (String f : checkField) {
                String phone = map.get(f);
                if (phonePattern.matcher(phone.replace("-", "")).find()) {
                    flag = true;
                    break;
                }
            }
            if (flag) {
                map.put("電話番号確認", "○");
            } else {
                map.put("電話番号確認", "×");
                map.put("無効", "1");
            }
        }
        field.addUnique("電話番号確認");
    }

    public void value(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        Pattern p = Pattern.compile("[\\*＊■@]");
        field.addTop("文字無効");
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            for (String targetField : param) {
                String targetValue = next.get(targetField);
                if (targetValue != null) {
                    if (p.matcher(targetValue).find()) {
                        next.put("文字無効", "1");
                    }
                }
            }
        }
    }
}
