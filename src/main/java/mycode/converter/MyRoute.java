package mycode.converter;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;
import org.apache.camel.Processor;
import org.apache.camel.dataformat.csv.CsvDataFormat;

@Component
public class MyRoute extends RouteBuilder {

    @Autowired
    BeanFactory factory;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm:ss.SSS");

    @Override
    public void configure() throws Exception {
        from("timer:foo?repeatCount=1").bean(this, "systemStartMessage()");
        from("file:work?delay=1s&noop=true&readLock=none&idempotent=true&idempotentKey=${file:name}-${file:modified}&exclude=【精査済み】.*")
                .unmarshal().string("Windows-31J")
                .unmarshal().csv()
                .bean(this, "readOrder")
                .bean(this, "rowsToListMap")
                .to("direct:order");

        from("direct:order")
                .choice().when().method(this, "nextOrder")
                .to("direct:output")
                .otherwise()
                .doTry()
                .to("direct:slip")
                .bean(this, "orderComplete")
                .to("direct:order")
                .doCatch(Throwable.class)
                .bean(this, "exceptionMessage");

        from("direct:slip").routingSlip(header("slip"), "#");
        from("direct:output")
                .bean(this, "rebuildMapList")
                .choice().when(simple("header.tsv"))
                .process(toCSVProcessor('\t'))
                .otherwise()
                .process(toCSVProcessor(','))
                .end()
                .setHeader(Exchange.FILE_NAME, simple("【精査済み】${header.CamelFileName}"))
                .to("file://work")
                .bean(this, "success")
                .filter()
                .simple("property.CamelBatchComplete")
                .bean(this, "allSuccess");
    }

    public boolean nextOrder(@Headers Map headers, @Body List body) {
        List<String> dsl = (List<String>) headers.get("dsl");
        if (dsl.isEmpty()) {
            return true;
        } else {
            String sentence = dsl.remove(0);
            headers.put("sentence", sentence);
            String order = sentence.split(" ")[0].replaceFirst("^!", "");
            headers.put("order", order);
            String param = sentence.replaceFirst("^!?" + order + " *", "");
            String[] orderSplit = order.split("\\.");
            headers.put("slip", "class:mycode.converter.bean." + StringUtils.capitalize(orderSplit[0].toLowerCase()));
            headers.put(Exchange.BEAN_METHOD_NAME, orderSplit[1]);
            headers.put("parameter", new Parameter(param));
            headers.put("itr", body.iterator());
            return false;
        }
    }

    public void readOrder(@Body List<List> rows, @Headers Map headers) {
        System.out.println(sdf.format(new Date()) + " [START]  ファイルの処理を開始しました。" + headers.get(Exchange.FILE_NAME));
        List<String> dsl = new ArrayList<>();
        while (true) {
            List<String> row = rows.get(0);
            String cell = row.get(0);
            if (row.size() == 1 && (cell.startsWith("!") || cell.startsWith("//"))) {
                if (!cell.startsWith("//")) {
                    dsl.add(cell);
                }
                rows.remove(0);
            } else if (cell.startsWith("@")) { // @tsvオプション, @makeFieldオプションをサポート
                headers.put(cell.replace("@", ""), true);
                rows.remove(0);
            } else {
                break;
            }
        }
        if (dsl.isEmpty()) {
            dsl.add("!preset.address");
        }
        headers.put("dsl", dsl);
        System.out.println(sdf.format(new Date()) + " [READ]   全ての命令は正しく読み込まれました。" + headers.get(Exchange.FILE_NAME));
    }

    public String toTSV(@Body String body) {
        return body.replaceAll("(?m),", "\t");
    }

    public ArrayList<LinkedHashMap<String, Object>> rebuildMapList(@Body List<Map<String, String>> body, @Header("field") Field field) {
        Iterator<Map<String, String>> itr = body.iterator();
        ArrayList<LinkedHashMap<String, Object>> rebuildMapList = new ArrayList<>();
        LinkedHashMap<String, Object> rebuildMap = new LinkedHashMap<>();
        for (String f : field) {
            rebuildMap.put(f, f);
        }
        rebuildMapList.add(rebuildMap);
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            rebuildMap = new LinkedHashMap<>();
            for (String f : field) {
                rebuildMap.put(f, map.get(f));
            }
            rebuildMapList.add(rebuildMap);
        }
        return rebuildMapList;

    }

    public List<Map<String, String>> rowsToListMap(@Headers Map headers, @Body List<List<String>> rows) {
        List<Map<String, String>> listMap = new LinkedList<>();
        ArrayList<String> field = new ArrayList<>();
        Boolean flag = (Boolean) headers.get("makeField");
        if (flag != null && flag) {
            for (int i = 1; i <= rows.get(0).size(); i++) {
                field.add("フィールド" + i);
            }
        } else {
            field = new ArrayList<>(rows.remove(0));
        }
        for (List<String> row : rows) {
            Map<String, String> map = new LinkedHashMap<>();
            int column = 0;
            for (String f : field) {
                try {
                    map.put(f, row.get(column).replace(",", "，"));
                } catch (ArrayIndexOutOfBoundsException a) {
                    map.put(f, "");
                }
                column++;
            }
            listMap.add(map);
        }
        headers.put("field", new Field(field));
        return listMap;
    }

    public Processor toCSVProcessor(final Character c) {
        return new Processor() {

            @Override
            public void process(Exchange exchange) throws Exception {
                CsvDataFormat csv = new CsvDataFormat();
                csv.setQuoteDisabled(true);
                csv.setUseMaps(true);
                csv.setDelimiter(c);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                csv.marshal(exchange, exchange.getIn().getBody(), baos);
                exchange.getIn().setBody(baos.toByteArray());
            }
        };
    }

    public void success(@Headers Map headers) {
        System.out.println(sdf.format(new Date()) + " [END]    全ての命令を完了しました。 " + headers.get(Exchange.FILE_NAME));
        System.out.println(sdf.format(new Date()));
    }

    public void orderComplete(@Headers Map headers) {
        System.out.println(sdf.format(new Date()) + " [DONE]   次の命令を完了しました。  " + headers.get("sentence"));
    }

    public void allSuccess() {
        System.out.println(sdf.format(new Date()) + " [ALL]    全てのファイルの処理が完了しました。");
        System.out.println(sdf.format(new Date()));
    }

    public void exceptionMessage(@Headers Map headers) {
        System.out.println(sdf.format(new Date()) + " [ERROR]  命令を実行中にエラーが発生しました。" + headers.get("sentence"));
    }

    public void systemStartMessage() {
        System.out.println(sdf.format(new Date()) + " [SYSTEM] システムの起動が完了しました。");
        System.out.println(sdf.format(new Date()) + " [SYSTEM] 終了する時は閉じるボタンを押してください。");
        System.out.println(sdf.format(new Date()));
    }
}
