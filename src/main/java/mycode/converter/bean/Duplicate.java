package mycode.converter.bean;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Duplicate {

    public void single(@Body List<Map<String, String>> listMap, @Header("parameter") Parameter params, @Header("field") Field field) {
        List<String[]> checkList = new LinkedList<>();
        Iterator<Map<String, String>> itr = listMap.iterator();
        String idField = params.remove(0);
        for (String p : params) {
            checkList.add(p.split("\\+"));
        }
        if (!field.contains("無効")) {
            while (itr.hasNext()) {
                itr.next().put("無効", "");
            }
            itr = listMap.iterator();
        }
        while (itr.hasNext()) {
            itr.next().put("ファイル内重複", "");
        }
        itr = listMap.iterator();
        for (String[] keySet : checkList) {
            HashMap<String, String> keys = new HashMap<>();
            String keySetName = "キー";
            for (String k : keySet) {
                keySetName += k;
            }

            while (itr.hasNext()) {
                Map<String, String> m = itr.next();
                if (!m.get("無効").isEmpty()) {
                    continue;
                }
                String seq = m.get(idField);
                String key = "";
                for (String k : keySet) {
                    key += m.get(k) + "-";
                }
                key = key.replaceFirst("-+$", "");
                m.put(keySetName, key);
                String getSeq = keys.get(key);
                if (getSeq != null && !getSeq.equals(seq)) {
                    if (m.get("ファイル内重複").isEmpty()) {
                        m.put("ファイル内重複", keySetName + ":" + getSeq);
                        m.put("無効", "1");
                    }
                } else if (!key.equals("")) {
                    keys.put(key, seq);
                }
            }
            field.addUnique(keySetName);
            itr = listMap.iterator();
        }
        field.addTop("無効");
        field.addUnique("ファイル内重複");
    }

    public void multi(@Body List<Map<String, String>> listMap, @Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        int pairCount = 1;
        String resultField = resultField(param);
        field.addUnique(resultField);
        while (itr.hasNext()) {
            Map<String, String> row_a = itr.next();
            Iterator<Map<String, String>> itr_b = listMap.iterator();
            boolean noSkip = false;
            while (itr_b.hasNext()) {
                Map<String, String> row_b = itr_b.next();
                if (row_a == row_b) {
                    noSkip = true;
                } else if (noSkip) {
                    Set<String> keys = new LinkedHashSet<>();
                    for (String f : param) {
                        keys.add(row_a.get(f));
                    }
                    boolean ok = true;
                    for (String f : param) {
                        String key = row_b.get(f);
                        if (!key.isEmpty() && keys.contains(key)) {
                            ok = false;
                            break;
                        }
                    }
                    if (!ok) {
                        row_a.put(resultField, pairCount + "");
                        row_b.put(resultField, pairCount + "");
                        row_b.put("無効", "1");
                        pairCount++;
                    }
                }
            }
        }
    }

    public String resultField(List<String> params) {
        String resultField = "";
        for (String f : params) {
            resultField += f;
        }
        resultField += "重複";
        return resultField;
    }
}
