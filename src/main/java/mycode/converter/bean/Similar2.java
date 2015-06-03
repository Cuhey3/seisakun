/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mycode.converter.bean;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;

@Component
public class Similar2 {

    public void mask(@Body List<Map<String, String>> listMap, @Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) {
        LinkedHashMap<String, Integer> lhm = new LinkedHashMap<>();
        field.addUnique("郵便番号姓");
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            if (next.get("無効").isEmpty()) {
                String zipFirstName = next.get("郵便番号") + next.get("姓");
                Integer get = lhm.get(zipFirstName);
                if (get == null) {
                    lhm.put(zipFirstName, 1);
                } else {
                    lhm.put(zipFirstName, get + 1);
                }
            }
        }
        itr = listMap.iterator();
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            if (next.get("無効").isEmpty()) {
                String zipFirstName = next.get("郵便番号") + next.get("姓");
                if (lhm.get(zipFirstName) > 1) {
                    next.put("郵便番号姓", "1");
                }
            }
        }
    }
}
