package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;

@Component("split")
public class Split {

    private final Pattern fromAreaPattern = Pattern.compile("^([^\\d-]+)(\\d?[\\d条-]*[\\d-])([^\\d条-].*)$");

    public void building(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) {
        if (!field.contains("建物名")) {
            field.addNext("町域以降", "建物名");
        }
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String fromArea = map.get("町域以降");
            Matcher m = fromAreaPattern.matcher(fromArea);
            if (m.find()) {
                map.put("町域以降", m.group(1) + m.group(2).replaceFirst("-+$", ""));
                map.put("建物名", m.group(3));
            }
        }
    }

    public void name(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) {
        field.addUnique("姓");
        field.addUnique("名");
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String fullName = map.get("姓名");
            String[] split = fullName.split("(?<=^[^　 ]+)　");
            map.put("姓", split[0]);
            if (split.length == 2) {
                map.put("名", split[1]);
            }
        }
    }
}
