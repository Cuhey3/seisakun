package mycode.converter.bean;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Format {

    public void padding(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        int length = Integer.parseInt(param.second());
        String prefix = nullBlank(param, 2);
        String suffix = nullBlank(param, 3);
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            map.put(targetField, prefix + padding(map.get(targetField), length) + suffix);
        }
    }

    public String padding(String str, int length) {
        while (str.length() < length) {
            str = "0" + str;
        }
        return str;
    }

    public String nullBlank(ArrayList<String> al, int index) {
        if (al.size() > index) {
            return al.get(index);
        } else {
            return "";
        }
    }

    public void hyphenZip(@Header("itr") Iterator<Map<String, String>> itr) {
        Pattern p = Pattern.compile("(\\d{3})-?(\\d{4})");
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            String zip = next.get("郵便番号");
            Normalizer.normalize(zip, Normalizer.Form.NFKC);
            Matcher m = p.matcher(zip);
            if (m.find()) {
                next.put("郵便番号", m.group(1) + "-" + m.group(2));
            }
        }
    }
}
