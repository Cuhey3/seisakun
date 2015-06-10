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
            setSagawaResult(next, targetField);
        }
    }

    public void setSagawaResult(Map<String, String> map, String targetField) {
        String num;
        if (targetField == null) {
            num = mycode.converter.util.Utility.searchValue(map, Pattern.compile("^\\d+$"));
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
            get.select(".ichiran-table-header").remove();
            Elements outline = get.select(".ichiran-bg-toiawase_meisai tr .ichiran-fg-src-2");
            Elements detail = get.select(".ichiran-bg.syosai-bg-src");
            Element ol = outline.get(0);
            Element dt = detail.get(0);
            String detailText = dt.select(".syosai-dt1-src").eq(7).text();
            Matcher matcher = Pattern.compile("\\d{4}年\\d{1,2}月\\d{1,2}日").matcher(detailText);
            String day = "";
            if (matcher.find()) {
                day = matcher.group(0);
            }
            String status = ol.nextElementSibling().nextElementSibling().text();
            map.put("配送ステータス", status);
            map.put("配送ステータス更新日", day);
            map.put("配送ステータスURL", url);
        } else {
            map.put("配送ステータス", "取得失敗");
        }
    }
}
