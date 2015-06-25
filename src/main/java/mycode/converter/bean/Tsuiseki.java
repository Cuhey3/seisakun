package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;
import org.apache.camel.Header;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class Tsuiseki {

    public void sagawa(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field, @Header("parameter") Parameter param) {
        String targetField = null;
        if (!param.isEmpty()) {
            targetField = param.first();
        }
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            if (!setSagawaResult(next, targetField)) {
                break;
            }
        }
        field.addUnique("佐川ステータス");
        field.addUnique("佐川ステータス詳細");
        field.addUnique("佐川ステータス更新日");
        field.addUnique("佐川ステータスURL");
    }

    public boolean setSagawaResult(Map<String, String> map, String targetField) {
        String num;
        Pattern p = Pattern.compile("^\\d{12}$");
        if (targetField == null || targetField.isEmpty()) {
            num = mycode.converter.util.Utility.searchValue(map, p);
        } else {
            num = map.get(targetField);
        }
        if (num != null && (p.matcher(num).find() || (targetField != null && !targetField.isEmpty()))) {
            String url = "http://k2k.sagawa-exp.co.jp/p/web/okurijosearch.do?okurijoNo=" + num;
            Document get = null;
            int tryCount = 0;
            while (get == null && tryCount < 10) {
                try {
                    get = Jsoup.connect(url).get();
                } catch (Throwable t) {
                    tryCount++;
                }
            }
            if (get != null && tryCount < 10) {
                try {
                    String detailText = get.select(".table_basic tr").last().select("td").html().replace("&nbsp;", "").split("<")[0];
                    Matcher matcher = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日").matcher(detailText);
                    String day = "";
                    if (matcher.find()) {
                        day = matcher.group(1) + "/" + matcher.group(2) + "/" + matcher.group(3);
                    }
                    map.put("佐川ステータス", get.select("table").first().select("tr").last().select("td").last().text());
                    map.put("佐川ステータス詳細", detailText);
                    map.put("佐川ステータス更新日", day);
                    map.put("佐川ステータスURL", url);
                    return true;
                } catch (Throwable t) {
                    map.put("佐川ステータス", "取得失敗（ページ解釈エラー）");
                    return false;
                }

            } else {
                map.put("佐川ステータス", "取得失敗（通信エラー）");
                return false;
            }
        } else {
            map.put("佐川ステータス", "取得失敗（伝票番号不備）");
            return true;
        }
    }

    public void yamato(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field, @Header("parameter") Parameter param) {
        String targetField = null;
        if (!param.isEmpty()) {
            targetField = param.first();
        }
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            if (!setYamatoResult(next, targetField)) {
                break;
            }
        }
        field.addUnique("ヤマトステータス");
        field.addUnique("ヤマトステータス更新日");
        field.addUnique("ヤマトステータスURL");
    }

    public void yubin(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field, @Header("parameter") Parameter param) {
        String targetField = null;
        if (!param.isEmpty()) {
            targetField = param.first();
        }
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            if (!setYubinResult(next, targetField)) {
                break;
            }
        }
        field.addUnique("郵便ステータス");
        field.addUnique("郵便ステータス更新日");
        field.addUnique("郵便ステータスURL");
    }

    public boolean setYamatoResult(Map<String, String> map, String targetField) {
        String num;
        Pattern p = Pattern.compile("^\\d{12}$");
        if (targetField == null || targetField.isEmpty()) {
            num = mycode.converter.util.Utility.searchValue(map, p);
        } else {
            num = map.get(targetField);
        }
        if (num != null && (p.matcher(num).find() || (targetField != null && !targetField.isEmpty()))) {
            Connection connect = Jsoup.connect("http://toi.kuronekoyamato.co.jp/cgi-bin/tneko");
            connect.data("number00", "1");
            connect.data("number01", num);
            Document get = null;
            int tryCount = 0;
            while (get == null && tryCount < 10) {
                try {
                    get = connect.post();
                } catch (Throwable t) {
                    tryCount++;
                }
            }
            if (get != null && tryCount < 10) {
                Elements select = get.select(".ichiran").select("tr").eq(2).select("td");
                try {
                    map.put("ヤマトステータス", select.eq(4).text());
                    map.put("ヤマトステータス更新日", select.eq(3).text());
                    map.put("ヤマトステータスURL", "http://jizen.kuronekoyamato.co.jp/jizen/servlet/crjz.b.NQ0010?id=" + num);
                    return true;
                } catch (Throwable t) {
                    map.put("ヤマトステータス", "取得失敗（ページ解釈エラー）");
                    return false;
                }
            } else {
                map.put("ヤマトステータス", "取得失敗（通信エラー）");
                return false;
            }
        } else {
            map.put("ヤマトステータス", "取得失敗（伝票番号不備）");
            return true;
        }
    }

    public boolean setYubinResult(Map<String, String> map, String targetField) {
        Pattern p = Pattern.compile("(?<!0[57-9]0)\\d{11}$");
        String num;
        if (targetField == null || targetField.isEmpty()) {
            num = mycode.converter.util.Utility.searchValue(map, p);
        } else {
            num = map.get(targetField);
        }
        if (num != null && (p.matcher(num).find() || (targetField != null && !targetField.isEmpty()))) {
            String url = "https://trackings.post.japanpost.jp/services/srv/search/direct?reqCodeNo1=" + num + "&searchKind=S002&locale=ja";
            Document get = null;
            int tryCount = 0;
            while (get == null && tryCount < 10) {
                try {
                    get = Jsoup.connect(url).get();
                } catch (Throwable t) {
                    tryCount++;
                }
            }
            if (get != null && tryCount < 10) {
                Elements table = get.select("table").eq(1);
                try {
                    map.put("郵便ステータス", table.select(".w_150").last().text());
                    map.put("郵便ステータス更新日", table.select(".w_120").last().text());
                    map.put("郵便ステータスURL", "https://trackings.post.japanpost.jp/services/srv/search/direct?reqCodeNo1=" + num);
                    return true;
                } catch (Throwable t) {
                    map.put("郵便ステータス", "取得失敗（ページ解釈エラー）");
                    return false;
                }
            } else {
                map.put("郵便ステータス", "取得失敗（通信エラー）");
                return false;
            }
        } else {
            map.put("郵便ステータス", "取得失敗（伝票番号不備）");
            return true;
        }
    }
}
