package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;
import org.apache.camel.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
        field.addUnique("配送ステータス");
        field.addUnique("配送ステータス詳細");
        field.addUnique("配送ステータス更新日");
        field.addUnique("配送ステータスURL");
    }

    public boolean setSagawaResult(Map<String, String> map, String targetField) {
        String num;
        if (targetField == null || targetField.isEmpty()) {
            num = mycode.converter.util.Utility.searchValue(map, Pattern.compile("^\\d{12}$"));
        } else {
            num = map.get(targetField);
        }
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
        if (get != null) {
            try {
                String detailText = get.select(".table_basic tr").last().select("td").html().replace("&nbsp;", "").split("<")[0];
                Matcher matcher = Pattern.compile("(\\d{4})年(\\d{1,2})月(\\d{1,2})日").matcher(detailText);
                String day = "";
                if (matcher.find()) {
                    day = matcher.group(1) + "/" + matcher.group(2) + "/" + matcher.group(3);
                }
                map.put("配送ステータス", get.select("table").first().select("tr").last().select("td").last().text());
                map.put("配送ステータス詳細", detailText);
                map.put("配送ステータス更新日", day);
                map.put("配送ステータスURL", url);
            } catch (Throwable t) {
                t.printStackTrace();
                map.put("配送ステータス", "取得失敗（ページ解釈エラー）");
                return false;
            }
            return true;
        } else {
            map.put("配送ステータス", "取得失敗（通信エラー）");
            return false;
        }
    }
}
