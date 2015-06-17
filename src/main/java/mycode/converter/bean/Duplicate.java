package mycode.converter.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
        for (String p : params) {
            checkList.add(p.split("\\+"));
        }
        if (!field.contains("無効")) {
            while (itr.hasNext()) {
                itr.next().put("無効", "");
            }
            itr = listMap.iterator();
        }
        if (!field.contains("ファイル内重複")) {
            while (itr.hasNext()) {
                itr.next().put("ファイル内重複", "");
            }
            itr = listMap.iterator();
        }
        if (!field.contains("重複コード")) {
            while (itr.hasNext()) {
                itr.next().put("重複コード", "");
            }
            itr = listMap.iterator();
        }
        HashMap<Integer, Integer> count = new HashMap<>();
        HashSet<String> set = new HashSet<>();
        for (String[] keySet : checkList) {
            if (!set.isEmpty()) {
                HashSet<String> newSet = new HashSet<>();
                for (String s : set) {
                    newSet.add(s = s + "-");
                }
                set = newSet;
            }
            HashMap<String, Integer> map = new HashMap<>();
            String keySetName = "キー";
            for (String k : keySet) {
                keySetName += k;
            }

            while (itr.hasNext()) {
                Map<String, String> m = itr.next();
                if (m.get("無効") == null || !m.get("無効").isEmpty()) {
                    continue;
                }
                String key = "";
                for (String k : keySet) {
                    key += m.get(k) == null ? "" : m.get(k) + "-";
                }
                key = key.replaceFirst("-+$", "");
                m.put(keySetName, key);
                if (!key.isEmpty()) {
                    if (map.containsKey(key)) {
                        if (!set.contains(key)) {
                            set.add(key);
                            map.put(key, set.size());
                            count.put(set.size(), 0);
                        }
                    } else {
                        map.put(key, null);
                    }
                }
            }
            itr = listMap.iterator();
            while (itr.hasNext()) {
                Map<String, String> m = itr.next();
                if (m.get("無効") == null || !m.get("無効").isEmpty()) {
                    continue;
                }
                String key = "";
                for (String k : keySet) {
                    key += m.get(k) == null ? "" : m.get(k) + "-";
                }
                key = key.replaceFirst("-+$", "");
                Integer duplicateGroupNumber = map.get(key);
                if (duplicateGroupNumber != null) {
                    Integer c = count.get(duplicateGroupNumber);
                    String code = (duplicateGroupNumber * 1000 + (c + 1) + "").replaceFirst("(\\d{3})$", "-$1");
                    while (code.length() < 7) {
                        code = "0" + code;
                    }
                    if (c == 0) {
                        String get = m.get("重複コード");
                        if (get.isEmpty()) {
                            m.put("重複コード", code);
                        } else {
                            m.put("重複コード", get + "/" + code);
                        }
                    } else {
                        m.put("重複コード", code);
                        m.put("無効", "1");
                        m.put("ファイル内重複", keySetName);
                    }
                    count.put(duplicateGroupNumber, c + 1);
                }
            }
            field.addUnique(keySetName);
            itr = listMap.iterator();
        }
        field.addTop("無効");
        field.addUnique("ファイル内重複");
        field.addUnique("重複コード");
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
