package mycode.converter.bean;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Normalize {

    public void address(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param) {
        normalize(itr, param, addressReplaceTable, addressReplaceAllTable);
    }

    public void zip(@Header("itr") Iterator<Map<String, String>> itr) {
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            String get = next.get("郵便番号");
            get = get.replace("-", "");
            while (get.length() < 7) {
                get = "0" + get;
            }
            next.put("郵便番号", get);
        }
    }

    public void phone(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) {
        ArrayList<String> normalizeField = new ArrayList<>();
        for (String f : field) {
            if (f.startsWith("電話番号") && !f.endsWith("確認")) {
                normalizeField.add(f);
            }
        }
        normalize(itr, normalizeField, phoneReplaceTable, phoneReplaceAllTable);
    }

    public void normalize(Iterator<Map<String, String>> itr, ArrayList<String> normalizeField, String[][] replaceTable, String[][] replaceAllTable) {
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            for (String n : normalizeField) {
                String target = map.get(n);
                String normalized = target;
                for (String[] tuple : replaceTable) {
                    normalized = normalized.replace(tuple[0], tuple[1]);
                }
                for (String[] tuple : replaceAllTable) {
                    normalized = normalized.replaceAll(tuple[0], tuple[1]);
                }
                normalized = Normalizer.normalize(normalized, Normalizer.Form.NFKC);
                if (!target.equals(normalized)) {
                    map.put(n, normalized);
                }
            }
        }
    }

    private final String[][] addressReplaceTable = {
        new String[]{"0", "０"}, new String[]{"1", "１"}, new String[]{"2", "２"}, new String[]{"3", "３"}, new String[]{"4", "４"},
        new String[]{"5", "５"}, new String[]{"6", "６"}, new String[]{"7", "７"}, new String[]{"8", "８"}, new String[]{"9", "９"},
        new String[]{"a", "ａ"}, new String[]{"b", "ｂ"}, new String[]{"c", "ｃ"}, new String[]{"d", "ｄ"}, new String[]{"e", "ｅ"},
        new String[]{"f", "ｆ"}, new String[]{"g", "ｇ"}, new String[]{"h", "ｈ"}, new String[]{"i", "ｉ"}, new String[]{"j", "ｊ"},
        new String[]{"k", "ｋ"}, new String[]{"l", "ｌ"}, new String[]{"m", "ｍ"}, new String[]{"n", "ｎ"}, new String[]{"o", "ｏ"},
        new String[]{"p", "ｐ"}, new String[]{"q", "ｑ"}, new String[]{"r", "ｒ"}, new String[]{"s", "ｓ"}, new String[]{"t", "ｔ"},
        new String[]{"u", "ｕ"}, new String[]{"v", "ｖ"}, new String[]{"w", "ｗ"}, new String[]{"x", "ｘ"}, new String[]{"y", "ｙ"},
        new String[]{"z", "ｚ"},
        new String[]{"A", "Ａ"}, new String[]{"B", "Ｂ"}, new String[]{"C", "Ｃ"}, new String[]{"D", "Ｄ"}, new String[]{"E", "Ｅ"},
        new String[]{"F", "Ｆ"}, new String[]{"G", "Ｇ"}, new String[]{"H", "Ｈ"}, new String[]{"I", "Ｉ"}, new String[]{"J", "Ｊ"},
        new String[]{"K", "Ｋ"}, new String[]{"L", "Ｌ"}, new String[]{"M", "Ｍ"}, new String[]{"N", "Ｎ"}, new String[]{"O", "Ｏ"},
        new String[]{"P", "Ｐ"}, new String[]{"Q", "Ｑ"}, new String[]{"R", "Ｒ"}, new String[]{"S", "Ｓ"}, new String[]{"T", "Ｔ"},
        new String[]{"U", "Ｕ"}, new String[]{"V", "Ｖ"}, new String[]{"W", "Ｗ"}, new String[]{"X", "Ｘ"}, new String[]{"Y", "Ｙ"},
        new String[]{"Z", "Ｚ"},
        new String[]{"'", "’"},
        new String[]{"ｰ", "ー"},
        new String[]{"―", "－"},
        new String[]{"-", "－"},
        new String[]{"‐", "－"},
        new String[]{" ", "　"}
    };

    private final String[][] addressReplaceAllTable = {
        new String[]{"　+", "　"},
        new String[]{"(?<![０-９])　", ""}, // 前に数字が来ていないスペースを取る
        new String[]{"　(?![０-９])", ""}, // 後ろに数字が来ていないスペースを取る
        new String[]{"(?<=[０-９])(　|丁目?|番地?の?|の)(?=[－０-９])", "－"}, // 数字と数字の間の　丁目・番地・のを取る
        new String[]{"(?<=[０-９])(番地?|号室?)$", ""}, // 末尾に番地・号室が数字とともにあれば番地・号室を取る
        new String[]{"(?<=[０-９ａ-ｚＡ-Ｚ])[－ー]+(?=[０-９ａ-ｚＡ-Ｚ])", "－"}, // 数字・アルファベットの間にある－・ーを一本にする
        new String[]{"^([^０-９]*)(?<![文万阿十所和分])(大字|小字)", "$1"}, // 数字が出現していない位置の大字・小字を取る（条件付き）
        new String[]{"^([^０-９]*)(?<![文万阿十所和分大小])字", "$1"}, // 数字が出現していない位置の字を取る（条件付き）
        new String[]{"(?<=[ァ-ヶ])－(?=[ァ-ヶ])", "ー"} // カタカナの間にある－をーにする
    };

    private final String[][] phoneReplaceTable = {
        new String[]{"-", ""}
    };
    private final String[][] phoneReplaceAllTable = {};
}
