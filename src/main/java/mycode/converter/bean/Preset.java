package mycode.converter.bean;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Preset {

    @Autowired
    BeanFactory factory;
    @Autowired
    Column column;
    @Autowired
    Replace replace;
    @Autowired
    Combine combine;
    @Autowired
    Normalize normalize;
    @Autowired
    Check check;
    @Autowired
    Split split;
    @Autowired
    Similar similar;
    @Autowired
    Duplicate duplicate;
    @Autowired
    Add add;
    @Autowired
    Find find;

    public void address(@Body List<Map<String, String>> listMap, @Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) throws SQLException {
        column.addTop(itr, new Parameter("無効"), field);
        if (!field.contains("市区町村")) {
            column.addNext(itr, new Parameter("都道府県 市区町村"), field);
        }
        itr = listMap.iterator();
        if (!field.contains("町域以降")) {
            column.addNext(itr, new Parameter("市区町村 町域以降"), field);
        }
        itr = listMap.iterator();
        if (!field.contains("市区町村")) {
            replace.character(itr, new Parameter("市区町村 ヶ→ケ"));
        }
        itr = listMap.iterator();
        combine.withSeparator(itr, new Parameter("都道府県 /　/ 市区町村 町域以降"));
        if (field.contains("番地")) {
            itr = listMap.iterator();
            combine.withSeparator(itr, new Parameter("都道府県 /　/ 番地"));
            column.remove(new Parameter("番地"), field);
        }
        itr = listMap.iterator();

        normalize.address(itr, new Parameter("都道府県"));
        itr = listMap.iterator();
        check.state(itr, field);
        itr = listMap.iterator();
        check.city(itr, field);
        itr = listMap.iterator();
        check.houseNumber(itr, field);
    }

    public void zipAndAddress(@Body List<Map<String, String>> listMap, @Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) throws SQLException {
        param = new Parameter("無効");
        column.addTop(itr, param, field);
        param = new Parameter("都道府県 市区町村");
        itr = listMap.iterator();
        column.addNext(itr, param, field);
        param = new Parameter("市区町村 町域以降");
        itr = listMap.iterator();
        column.addNext(itr, param, field);
        param = new Parameter("町域以降 建物名");
        itr = listMap.iterator();
        column.addNext(itr, param, field);
        param = new Parameter("都道府県 /　/ 市区町村 町域以降");
        itr = listMap.iterator();
        combine.withSeparator(itr, param);
        param = new Parameter("都道府県");
        itr = listMap.iterator();
        normalize.address(itr, param);
        itr = listMap.iterator();
        check.state(itr, field);
        itr = listMap.iterator();
        check.city(itr, field);
        itr = listMap.iterator();
        check.houseNumber(itr, field);
        itr = listMap.iterator();
        split.building(itr, field);

    }

    public void similar(@Body List<Map<String, String>> listMap, @Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        param = new Parameter("0 郵便番号 姓");
        similar.mask(listMap, itr, param, field);
        /*itr = listMap.iterator();
         param = new Parameter("2 電話番号");
         similar.mask(listMap, itr, param, field);
         itr = listMap.iterator();
         if (field.contains("姓名")) {
         similar.mask(listMap, itr, new Parameter("3 郵便番号 姓名"), field);
         } else if (field.contains("氏名")) {
         similar.mask(listMap, itr, new Parameter("3 郵便番号 氏名"), field);
         }*/
    }

    public void duplicate(@Body List<Map<String, String>> listMap, @Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) throws Throwable {
        String paramString;
        if (field.contains("ナンバリング")) {
            paramString = "ナンバリング";
        } else if (field.contains("SEQ")) {
            paramString = "SEQ";
        } else {
            add.autoNumber(listMap.iterator(), new Parameter("SEQ 0"), field);
            paramString = "SEQ";
        }

        if (field.contains("電話番号")) {
            paramString += " 電話番号";
        }
        paramString += " 市区町村+町域以降";

        duplicate.single(listMap, new Parameter(paramString), field);
    }

    public void find(@Body List<Map<String, String>> listMap, @Header("field") Field field) throws Throwable {
        find.name(listMap, field);
        find.phone(listMap, field);
        find.zip(listMap, field);
        find.address(listMap, field);
        find.building(listMap, field);
    }
}
