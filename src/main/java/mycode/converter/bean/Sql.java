package mycode.converter.bean;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.springframework.stereotype.Component;

@Component
public class Sql {

    public void create(@Headers Map header, @Header("parameter") Parameter param, @Header("field") Field field) {
        header.put("sqlflag", true);
        LinkedHashMap<String, String> newRow = new LinkedHashMap<>();
        StringBuilder sb = new StringBuilder();
        sb.append("create table table_").append(System.currentTimeMillis() / 1000).append("([オートナンバー] AUTOINCREMENT");
        String idField = "オートナンバー";
        for (int i = 0; i < field.size(); i++) {
            if (i != field.size() - 1) {
                newRow.put(field.get(i), String.format("[%s] varchar(255)", field.get(i)));
                sb.append(String.format(",[%s] varchar(255)", field.get(i)));
            } else {
                newRow.put(field.get(i), String.format("[%s] varchar(255))", field.get(i)));
                sb.append(String.format(",[%s] varchar(255))", field.get(i)));
            }
        }
        field.addTop(idField);
        newRow.put(idField, "create table table_" + (System.currentTimeMillis() / 1000) + "([" + idField + "] AUTOINCREMENT");
        //body.add(0, newRow);
        header.put("sql", new String(sb));
    }
}
