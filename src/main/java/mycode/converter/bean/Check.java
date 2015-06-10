package mycode.converter.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import mycode.converter.spec.Field;

@Component
public class Check {

    private final String zipField = "郵便番号";
    private final String stateField = "都道府県";
    private final String cityField = "市区町村";
    private final String areaField = "町域以降";
    private final String number = "[0-9０-９]";
    private final String notNumber = "[^0-9０-９]";
    private final String start = "^";
    private final Pattern p_existNumber = Pattern.compile(start + notNumber + "+" + number + "|" + start + number + "+[条]" + notNumber + "*" + number);
    private final Pattern oneCharDifferentPattern = Pattern.compile(start + "(" + notNumber + "+)([^0-9０-９東西南北中]?)(.+)\\1([^0-9０-９東西南北中]?)\\3(.*)$");
    @Autowired
    private DriverManagerDataSource datasource;
    private final Pattern ritouPattern = Pattern.compile("^(0(431(40[123456]|52[12345])|783(871|95[12345])|97(0(10|21|31|40)1|1(111|20[12])))|100(0(10[1234]|21[12]|301|40[12]|511|601)|1(10[123]|21[123]|301|401|511|62[123]|701)|2(10|21)1)|4(130004|440416|70350[45])|5(170(00[12345]|205|703)|230801)|6(560961|72010[123]|8(40(10[1234567]|211|30[123]|4(0[1234]|1[123]))|50(0(0[1234567]|1[1234567]|2[1234567])|10[123456]|3(0[123456]|1[1234])|4(1[123]|3[12345]))))|7(0(13203|48153|60306)|140(03[5678]|30[12])|2(00204|20061|3002[12]|50(231|30[123]|40[123]))|3(40(017|10[123]|30[1234])|90(5(0[1234567]|1[12345678]|2[12345]|3[123456]|4[123]|5[0123456789]|88)|607))|4(00051|2(0041|1(114|40[14]|516)|2(108|801))|30003|50057|70832)|5(00095|80(00[13]|701)|96542)|6(0009[12]|1(3110|4(1(0[123456]|1[12345]|2[12]|3[1234]|4[123456]|5[12345])|30[12345678]|4(0[12345]|1[12]|2[123456]|3[1234])|66[1234]))|2007[123]|30(1(0[12345678]|11)|2(2[12345678]|31))|400[45]0|80071|9110[89])|7(41760|5001[03])|88067[789]|9(1(2(14[345]|20[1234567])|4(32[1234]|43[12]|5(0[123456789]|1[01]))|809[123])|20(080|891)|4(1(10[123]|30[123456789]|40[1234])|2(1(0[1234]|1[0123456789])|20[1234]|30[12345]|410|5(0[123456789]|1[0123]|20|30|4[012]|50)))|68060|8(0(09[6789]|212)|3358)|9(2(12[123]|433)|3470)))|8(1(15(1(0[1234567]|1[1234567]|2[12345]|3[123456]|4[1234]|5[12345]|6[123])|2(0[1234]|1[12345]|2[123456])|3(01|1[123456]|2[123456])|46[12345678]|5(01|1[123]|2[123]|3[123]|4[123456]|5[123456])|7(3[1234]|4[1234]|5[1234567]))|7(0(0(0[12356]|1[123456]|2[1234]|3[12345])|15[1234567]|24[12345678]|32[123456]|43[12345]|51[1234])|1(10[1234567]|2(0[12]|1[234]|23|3[1234]|4[156]|5[1234567])|30[1234567]|41[123]|5(1[123]|2[12345]|3[123])|60[123]|7(0[1234]|1[12345]|2[12345]))|2(24[123]|33[123])))|47(0(131|3(06|17)|40[56])|1524)|5(11(201|315)|3(0(0(0[1234567]|1[12345678]|2[1234567]|3[123]|4[1234]|5[1234]|6[123456])|2(0[12345678]|1[12345])|31[123]|41[123]|50[12345678]|6(0[123456789]|1[123])|70[123456])|2(17[1234]|20[1234]|3(0[12345]|1[12345])|48[12])|3(10[12]|32[123]))|7(0071|2532|3271|4(10[123]|21[1234]|4(0[1245]|1[123456])|51[1234]|60[1234]|7(0[123456789]|1[12])|81[123456]|90[1234]))|9(4(30[123456789]|529|745)|5(101|80[123456])))|6(603(03|13|34)|93711)|7(21501|6(000[12345678]|1313)|92501)|820096|9(0090[123]|1(3(1(0[1234]|1[12345678])|2(02|2[12])|43[12]|60[1234567]|70[123456])|4(20[12345678]|31[12]|40[123456789])|5(101|20[12345]|301)|6(1(4[1234]|5[1234]|6[1234])|2(0[123]|1[1234567]|2[123456789]|3[123]))|7(1(0[12345]|1[123456])|42[123456]|6(0[12345]|1[12]|2[1234])|731)|8(11[1234567]|2(01|16|21)|32[123456789])|9(1(0[1234]|1[123456]|2[12345]|3[123456])|2(0[1234567]|1[12345]|2[1234]|3[1234])|30[123456789]))|4(0(0(0[12345678]|1[1234567]|2[1234567]|3[123456]|4[12345678]|6[1238])|10[12345678]|3(2[1234]|3[123]|5[12])|41[12]|5(0[12345678]|1[123])|62[1234567]|77[123456])|1(11[123456]|20[12345]|3(04|21)|5(0[12345678]|1[12345]|2[1234]|31)|74[123456]|85[12345])|2(14[1234]|23[1234567]|32[123]|4(0[1234]|1[1234567])|50[12]|601)|3(10[1234567]|21[1234]|30[12345]|41[123]|52[123]|63[123]))|61(101|2(0[123456]|81)|301|41[12]|5(12|21)|60[12])|91501))|9(0(13(1(0[12345678]|1[12345]|2[12345]|3[12345678])|31[12]|40[123]|50[12]|601|70[123]|80[123456]|90[123])|42317|50(50[12345]|60[12345]|70[12345])|60(0(0[12345678]|1[12345])|10[123456789]|20[1234]|30[123456]|42[12]|50[1234567]|60[123])|7(0(0(0[1234]|1[1234]|2[1234])|24[123]|33[123]|45[123])|1(101|221|311|43[12345]|54[1234]|751|801)))|280072|5(2(0(0(0[1234567]|1[123456]|2[12345678])|1(0[123456789]|1[123456])|2(0[1234567]|1[123456])|3(0[1234567]|1[12345678]|2[12])|4(2[12]|3[12345])|5(0[12345]|1[12345])|6(0[123456]|1[12345]|2[123456])|7(0[123456]|1[123456])|8(2[1234]|5[1234567]))|1(2(0[123456789]|1[123])|3(0[12345678]|1[12345]|2[123456])|43[12345]|5(0[12345678]|1[12345678]|2[12345678]|3[123456789]|4[12345678]|5[12345678]|6[12345678]|7[12345]|8[1234])|64[123456])|2(13[123456]|2(0[123456]|1[12345]|2[123456]))|3(11[123456789]|20[1234567]|4(2[12345]|3[12345])|54[123456]))|80061)|8(5019[12345]|6(0023|2(116|21[12]|52[56]))|806(0[1234567]|1[123]|2[123]|3[1234]))|980281))$");

    public void state(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            Statement statement = connection.createStatement();
            while (itr.hasNext()) {
                Map<String, String> m = itr.next();
                m.put("都道府県確認", "");
                if (m.containsKey("無効") && !m.get("無効").isEmpty()) {
                    continue;
                }
                String targetZip = simpleZip(m.get(zipField));
                String targetState = m.get(stateField);
                String targetCity = m.get(cityField);
                ResultSet rs = statement.executeQuery("select * from 郵便番号_全国 where 郵便番号 = '" + targetZip + "'");
                boolean flag = false;
                int i = 0;
                String zipToAddress = "";
                while (rs.next() && !flag) {
                    i++;
                    String cursorState = rs.getString("都道府県");
                    String cursorCity = rs.getString("市区町村");
                    String cursorArea = rs.getString("町域2");
                    zipToAddress += cursorState;
                    zipToAddress += cursorCity;
                    zipToAddress += cursorArea;

                    if (cursorState.equals(targetState)) {
                        flag = true;
                    } else if (targetState.startsWith(cursorState)) {
                        m.put(stateField, cursorState);
                        m.put(cityField, targetState.replaceFirst("^(" + cursorState + ")+[ 　]*", "") + targetCity);
                        flag = true;
                    } else if (targetState.startsWith(cursorCity)) {
                        m.put(stateField, cursorState);
                        m.put(cityField, targetState + targetCity);
                        m.put("都道府県確認", "○都道府県挿入");
                        flag = true;
                    }
                }
                rs.close();
                if (flag) {
                    if (m.get("都道府県確認").isEmpty()) {
                        m.put("都道府県確認", "○");
                    }
                } else {
                    String targetArea = m.get(areaField);
                    rs = statement.executeQuery("select * from 郵便番号_全国 where 市区町村町域2 like '" + targetCity.replaceAll("[ケヶがガ]", "_") + "%'");
                    HashSet<String> stateSet = new HashSet<>();
                    HashSet<String> citySet = new HashSet<>();
                    HashSet<String> zipSet = new HashSet<>();
                    while (rs.next()) {
                        zipSet.add(rs.getString("郵便番号"));
                        stateSet.add(rs.getString("都道府県"));
                        citySet.add(rs.getString("市区町村"));
                    }
                    if (zipSet.size() == 1) {
                        m.put("郵便番号", zipSet.iterator().next());
                        m.put(stateField, stateSet.iterator().next());
                        m.put(cityField, citySet.iterator().next());
                        m.put(areaField, targetCity.replaceFirst("^" + m.get(cityField).replaceAll("[ケヶがガ]", "."), "") + targetArea);
                        m.put("都道府県確認", "○市区町村町域→郵便番号→都道府県修正");
                    } else if (citySet.size() == 1) {
                        m.put(stateField, stateSet.iterator().next());
                        m.put(cityField, citySet.iterator().next());
                        m.put(areaField, targetCity.replaceFirst("^" + m.get(cityField).replaceAll("[ケヶがガ]", "."), "") + targetArea);
                        m.put("都道府県確認", "○市区町村→都道府県修正");
                    } else if (stateSet.size() == 1) {
                        m.put(stateField, stateSet.iterator().next());
                        m.put("都道府県確認", "○市区町村→都道府県修正");
                    } else {
                        System.out.println(targetZip + "\t" + targetState + "\t" + targetCity + "\t" + targetArea);
                        m.put("都道府県確認", "×");
                        m.put("無効", "1");
                        if (i == 0) {
                            m.put("郵便番号に対応する住所", "該当がありません");
                        } else if (i == 1) {
                            m.put("郵便番号に対応する住所", zipToAddress);
                        } else if (i > 1) {
                            m.put("郵便番号に対応する住所", "候補が複数あります");
                        }
                    }
                }

            }
        }
        if (!field.contains("郵便番号に対応する住所")) {
            if (field.contains("建物名")) {
                field.addNext("建物名", "郵便番号に対応する住所");
            } else {
                field.addNext("町域以降", "郵便番号に対応する住所");
            }
        }
        field.addUnique("都道府県確認");
        field.addTop("無効");

    }

    public void city(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            Statement statement = connection.createStatement();
            while (itr.hasNext()) {
                Map<String, String> map = itr.next();
                map.put("市区町村確認", "");
                if (map.containsKey("無効") && !map.get("無効").isEmpty()) {
                    continue;
                }
                String targetZip = simpleZip(map.get(zipField));
                String targetCity = map.get(cityField);
                String targetArea = map.get(areaField);

                ResultSet rs = statement.executeQuery("select * from 郵便番号_全国 where 郵便番号 = '" + targetZip + "'");
                boolean flag = false;
                int i = 0;
                String zipToAddress = "";
                while (rs.next()) {
                    i++;
                    zipToAddress += rs.getString("都道府県");
                    String cursorCity = rs.getString("市区町村");
                    String cursorArea = rs.getString("町域2");
                    zipToAddress += cursorCity;
                    zipToAddress += cursorArea;

                    if (cursorCity.equals(targetCity)) {
                        map.put("市区町村確認", "○");
                    } else if (targetCity.startsWith(cursorCity)) {
                        map.put(areaField, targetCity.replaceFirst("^" + cursorCity + "[ 　]*", "") + targetArea);
                        map.put("市区町村確認", "○");
                    } else if ((cursorCity.startsWith(targetCity) || cursorCity.endsWith(targetCity)) && targetArea.startsWith(cursorArea)) {
                        map.put("市区町村確認", "○市区町村一部補記");
                    } else if ((targetCity + targetArea).startsWith(cursorCity)) {
                        map.put(areaField, (targetCity + targetArea).replaceFirst("^" + cursorCity + "[ 　]*", ""));
                        map.put("市区町村確認", "○分割位置変更");
                    } else if (!targetCity.startsWith(cursorArea) && targetCity.contains(cursorArea) && cursorCity.startsWith(targetCity.replaceFirst(cursorArea + ".*$", ""))) {
                        map.put(areaField, cursorArea + targetCity.replaceFirst(".*?" + cursorArea, ""));
                        map.put("市区町村確認", "○区名補記" + targetCity.replaceFirst(cursorArea + ".*$", "") + "→" + cursorCity);
                    } else if (!targetCity.startsWith(cursorArea) && targetCity.contains(cursorArea) && cursorCity.endsWith(targetCity.replaceFirst(cursorArea + ".*$", ""))) {
                        map.put(areaField, cursorArea + targetCity.replaceFirst(".*?" + cursorArea, ""));
                        map.put("市区町村確認", "○郡名補記" + targetCity.replaceFirst(cursorArea + ".*$", "") + "→" + cursorCity);
                    } else {
                        continue;
                    }
                    map.put(cityField, cursorCity);
                    flag = true;
                    break;
                }

                if (!flag) {
                    rs = statement.executeQuery("select * from 郵便番号_全国 where 郵便番号 = '" + targetZip + "'");
                    while (rs.next()) {
                        String cursorCity = rs.getString("市区町村");
                        Matcher match;
                        if ((match = oneCharDifferentPattern.matcher(cursorCity + targetCity + targetArea)).find()) {
                            map.put(cityField, cursorCity);
                            map.put(areaField, match.group(5));
                            map.put("市区町村確認", "○一文字変更" + match.group(4) + "→" + match.group(2));
                            flag = true;
                            break;
                        }
                    }

                }

                if (!flag) {
                    map.put("市区町村確認", "×");
                    if (i == 0) {
                        map.put("郵便番号に対応する住所", "該当がありません");
                    } else if (i == 1) {
                        map.put("郵便番号に対応する住所", zipToAddress);
                    } else if (i > 1) {
                        map.put("郵便番号に対応する住所", "候補が複数あります");
                    }
                    map.put("無効", "1");
                }
            }
            field.addUnique("市区町村確認");
            if (!field.contains("郵便番号に対応する住所")) {
                if (field.contains("建物名")) {
                    field.addNext("建物名", "郵便番号に対応する住所");
                } else {
                    field.addNext("町域以降", "郵便番号に対応する住所");
                }
            }
            field.addTop("無効");
        }
    }

    public void houseNumber(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            Statement statement = connection.createStatement();
            while (itr.hasNext()) {
                Map<String, String> map = itr.next();
                map.put("番地確認", "");
                map.put("特殊町域", "");
                if (map.containsKey("無効") && !map.get("無効").isEmpty()) {
                    continue;
                }
                boolean flag = false;
                String targetZip = simpleZip(map.get(zipField));
                String targetArea = map.get(areaField);
                if (targetArea.isEmpty()) {
                    flag = false;
                } else if (targetArea.contains("無番地")) {
                    flag = true;
                } else {
                    ResultSet rs = statement.executeQuery("select * from 郵便番号_全国 where 郵便番号='" + targetZip + "' order by 町域2 DESC");
                    int i = 0;
                    String zipToAddress = "";
                    while (rs.next()) {
                        i++;
                        zipToAddress += rs.getString("都道府県");
                        zipToAddress += rs.getString("市区町村");
                        String area = rs.getString("町域2");
                        zipToAddress += area;
                        if (area.isEmpty()) {
                            flag = true;
                            map.put("特殊町域", rs.getString("町域"));
                            break;
                        } else if (p_existNumber.matcher(targetArea).find()) {
                            if (normalizeHouseNumber(targetArea).startsWith(normalizeHouseNumber(area))) {
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (i == 0) {
                        map.put("郵便番号に対応する住所", "該当がありません");
                    } else if (i == 1) {
                        map.put("郵便番号に対応する住所", zipToAddress);
                    } else if (i > 1) {
                        map.put("郵便番号に対応する住所", "候補が複数あります");
                    }
                }
                if (flag) {
                    map.put("番地確認", "○");
                } else {
                    map.put("番地確認", "×");
                }
                if (!(map.get("番地確認")).startsWith("○")) {
                    map.put("番地確認", "×");
                    map.put("無効", "2");
                    System.out.println(map.get(areaField));
                }
            }
            field.addUnique("番地確認");
            if (!field.contains("郵便番号に対応する住所")) {
                if (field.contains("建物名")) {
                    field.addNext("建物名", "郵便番号に対応する住所");
                } else {
                    field.addNext("町域以降", "郵便番号に対応する住所");
                }
            }

            field.addUnique("特殊町域");
            field.addTop("無効");
        }
    }

    public void ritou(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) {
        int count = 0;
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            String zip = next.get("郵便番号").replace("-", "");
            if (ritouPattern.matcher(zip).find()) {
                next.put("離島F", "1");
                count++;
            }
        }
        field.addNext("無効", "離島F");
        field.addTop("離島F");
        System.out.println("離島は" + count + "件ありました。");
    }

    public String normalizeHouseNumber(String str) {
        str = str.replaceAll("[零０]", "0")
                .replaceAll("[一壱１]", "1")
                .replaceAll("[二２弐]", "2")
                .replaceAll("[三３]", "3")
                .replaceAll("[四４]", "4")
                .replaceAll("[五５]", "5")
                .replaceAll("[六６]", "6")
                .replaceAll("[七７]", "7")
                .replaceAll("[八８]", "8")
                .replaceAll("[九９]", "9")
                .replaceAll("[がガヶ]", "ケ")
                .replaceAll("[之の]", "ノ")
                .replaceAll("淵", "渕")
                .replaceAll("薮", "藪")
                .replaceAll("[つッ]", "ツ")
                .replaceAll("惠", "恵")
                .replaceAll("篭", "籠")
                .replaceAll("桧", "檜")
                .replaceAll("桧", "檜")
                .replaceAll("竜", "龍")
                .replaceAll("餅", "餠")
                .replaceAll("祗", "祇")
                .replaceAll("曾", "曽")
                .replaceAll("滝", "瀧")
                .replaceAll("広", "廣")
                .replaceAll("粕", "糟")
                .replaceAll("(\\d)十(\\d)", "$1$2")
                .replaceAll("十(\\d)", "1$1")
                .replaceAll("十", "");
        return str;
    }

    public String simpleZip(String zip) {
        return zip.replace("-", "");
    }
}
