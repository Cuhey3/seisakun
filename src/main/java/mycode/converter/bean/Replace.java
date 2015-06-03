package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Replace {

    private final String[][] romeTable = {
        new String[]{"Ⅰ", "Ｉ"},
        new String[]{"Ⅱ", "ＩＩ"},
        new String[]{"Ⅲ", "ＩＩＩ"},
        new String[]{"Ⅳ", "ＩＶ"},
        new String[]{"Ⅴ", "Ｖ"},
        new String[]{"Ⅵ", "ＶＩ"},
        new String[]{"Ⅶ", "ＶＩＩ"},
        new String[]{"Ⅷ", "ＶＩＩＩ"},
        new String[]{"Ⅸ", "ＩＸ"},
        new String[]{"Ⅹ", "Ｘ"}
    };

    private final String[][] docomoTable = {
        new String[]{"", "・"},
        new String[]{"", "Ｉ"},
        new String[]{"", "ＩＩ"},
        new String[]{"", "ＩＩＩ"},
        new String[]{"", "柳"},
        new String[]{"", "高"},
        new String[]{"", "住"},
        new String[]{"", "崎"},
        new String[]{"", "Ｖ"},
        new String[]{"", "吉"},
        new String[]{"", "翔"},
        new String[]{"", "館"},
        new String[]{"", "濱"},
        new String[]{"", "瀬"},
        new String[]{"", "琢"},
        new String[]{"", "朗"},
        new String[]{"", "尭"},
        new String[]{"", "塚"},
        new String[]{"", "博"},
        new String[]{"", "渉"},
        new String[]{"", "周"},
        new String[]{"", "（株）"},
        new String[]{"", "緑"},
        new String[]{"", "徳"},
        new String[]{"", "溝"},
        new String[]{"", "溝"},//二個目
        new String[]{"", "棚"},
        new String[]{"", "桑"},
        new String[]{"", "進"},
        new String[]{"", "進"},
        new String[]{"", "遥"},
        new String[]{"", "慧"},
        new String[]{"", "恵"},
        //new String[]{"", "?"}, うまくいかないのでやらない
        new String[]{"", "祐"},
        new String[]{"", "蓮"},
        new String[]{"", "肇"},
        new String[]{"", "郎"},
        new String[]{"", "教"},
        new String[]{"", "瀧"},
        new String[]{"", "緒"},
        new String[]{"", "起"},
        new String[]{"", "梢"},
        new String[]{"", "菜"},
        new String[]{"", "都"},
        new String[]{"", "蔵"},
        new String[]{"", "龍"},
        new String[]{"", "塚"},
        new String[]{"", "史"}
    };

    public void regex(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param) {
        String targetField = param.first();
        String toReplace = param.second().split("→", 2)[0];
        String replacement = param.second().split("→", 2)[1];
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String target = map.get(targetField);
            String replaced = target.replaceAll(toReplace, replacement);
            if (!target.equals(replaced)) {
                map.put(targetField, replaced);
            }
        }
    }

    public void character(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param) {
        String targetField = param.first();
        String toReplace = param.second().split("→", 2)[0];
        String replacement = param.second().split("→", 2)[1];
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String target = map.get(targetField);
            String replaced = target.replace(toReplace, replacement);
            if (!target.equals(replaced)) {
                map.put(targetField, replaced);
            }
        }
    }

    public void docomo(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String toReplace = map.get(targetField);
            String replaced = toReplace;
            for (String[] tuple : docomoTable) {
                replaced = replaced.replace(tuple[0], tuple[1]);
            }
            if (!replaced.equals(toReplace)) {
                map.put(targetField + "外字修正", toReplace);
                map.put(targetField, replaced);
            }
        }
        field.addUnique(targetField + "外字修正");
    }

    public void rome(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            String toReplace = map.get(targetField);
            String replaced = toReplace;
            for (String[] tuple : romeTable) {
                replaced = replaced.replace(tuple[0], tuple[1]);
            }
            if (!replaced.equals(toReplace)) {
                map.put(targetField + "ローマ数字修正", toReplace);
                map.put(targetField, replaced);
            }
        }
        field.addUnique(targetField + "ローマ数字修正");
    }
}
