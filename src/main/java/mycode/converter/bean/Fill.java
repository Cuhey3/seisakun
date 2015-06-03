/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Header;
import mycode.converter.spec.Parameter;

/**
 *
 * @author DOIF0568
 */
public class Fill {

    public void byPrevious(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param) {
        String targetField = param.first();
        String fill = "";
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            String get = next.get(targetField);
            if (get == null || get.isEmpty()) {
                next.put(targetField, fill);
            } else {
                fill = get;
            }
        }
    }
}
