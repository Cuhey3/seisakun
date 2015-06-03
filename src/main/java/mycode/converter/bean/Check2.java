package mycode.converter.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mycode.converter.spec.Field;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import org.apache.commons.dbutils.handlers.MapListHandler;

@Component
public class Check2 {

    @Autowired
    DriverManagerDataSource dataSource;
    private boolean ready = false;
    private final LinkedHashMap<String, Integer> stableKenCityNameMap = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> incompleteKenCityNameMap = new LinkedHashMap<>();
    private final LinkedHashMap<Pattern, Integer> fuzzyKenCityNameMap = new LinkedHashMap<>();
    private final LinkedHashMap<Integer, String> cityIdToKenCityMap = new LinkedHashMap<>();
    private final Pattern[] equalityPatterns = new Pattern[]{
        Pattern.compile("[ヶケがガ]"),
        Pattern.compile("[っつッツ]"),
        Pattern.compile("[のノ之]"),
        Pattern.compile("[沢澤]"),
        Pattern.compile("[桧檜]"),
        Pattern.compile("[粕糟]"),
        Pattern.compile("[諫諌]"),
        Pattern.compile("[竈釜]"),
        Pattern.compile("[渕淵]"),
        Pattern.compile("[芦葦]"),
        Pattern.compile("[高髙]"),
        Pattern.compile("[槙槇]"),
        Pattern.compile("[齋斉斎齊]"),
        Pattern.compile("[舘館]"),
        Pattern.compile("[濱浜]"),
        Pattern.compile("[餅餠]")
    };
    private final char[] fuzzyChar = "ヶケがガっつッツのノ之沢澤桧檜粕糟諫諌竈釜渕淵芦葦高髙槙槇齋斉斎齊舘館濱浜餅餠".toCharArray();
    private final Pattern[] numberPatterns = new Pattern[]{
        Pattern.compile("[2２二]十?[1１一]"),
        Pattern.compile("[2２二]十?[2２二]"),
        Pattern.compile("[2２二]十?[3３三]"),
        Pattern.compile("[2２二]十?[4４四]"),
        Pattern.compile("[2２二]十?[5５五]"),
        Pattern.compile("[2２二]十?[6６六]"),
        Pattern.compile("[2２二]十?[7７七]"),
        Pattern.compile("[2２二]十?[8８八]"),
        Pattern.compile("[2２二]十?[9９九]"),
        Pattern.compile("[3３三]十?[1１一]"),
        Pattern.compile("[3３三]十?[2２二]"),
        Pattern.compile("[3３三]十?[3３三]"),
        Pattern.compile("[3３三]十?[4４四]"),
        Pattern.compile("[3３三]十?[5５五]"),
        Pattern.compile("[3３三]十?[6６六]"),
        Pattern.compile("[3３三]十?[7７七]"),
        Pattern.compile("[3３三]十?[8８八]"),
        Pattern.compile("[3３三]十?[9９九]"),
        Pattern.compile("[4４四]十?[1１一]"),
        Pattern.compile("[4４四]十?[2２二]"),
        Pattern.compile("[4４四]十?[3３三]"),
        Pattern.compile("[4４四]十?[4４四]"),
        Pattern.compile("[4４四]十?[5５五]"),
        Pattern.compile("[4４四]十?[6６六]"),
        Pattern.compile("[4４四]十?[7７七]"),
        Pattern.compile("[4４四]十?[8８八]"),
        Pattern.compile("[4４四]十?[9９九]"),
        Pattern.compile("[5５五]十?[1１一]"),
        Pattern.compile("[5５五]十?[2２二]"),
        Pattern.compile("[5５五]十?[3３三]"),
        Pattern.compile("[5５五]十?[4４四]"),
        Pattern.compile("[5５五]十?[5５五]"),
        Pattern.compile("[5５五]十?[6６六]"),
        Pattern.compile("[5５五]十?[7７七]"),
        Pattern.compile("[5５五]十?[8８八]"),
        Pattern.compile("[5５五]十?[9９九]"),
        Pattern.compile("[6６六]十?[1１一]"),
        Pattern.compile("[6６六]十?[2２二]"),
        Pattern.compile("[6６六]十?[3３三]"),
        Pattern.compile("[6６六]十?[4４四]"),
        Pattern.compile("[6６六]十?[5５五]"),
        Pattern.compile("[6６六]十?[6６六]"),
        Pattern.compile("[6６六]十?[7７七]"),
        Pattern.compile("[6６六]十?[8８八]"),
        Pattern.compile("[6６六]十?[9９九]"),
        Pattern.compile("[1１一十][1１一]"),
        Pattern.compile("[1１一十][2２二]"),
        Pattern.compile("[1１一十][3３三]"),
        Pattern.compile("[1１一十][4４四]"),
        Pattern.compile("[1１一十][5５五]"),
        Pattern.compile("[1１一十][6６六]"),
        Pattern.compile("[1１一十][7７七]"),
        Pattern.compile("[1１一十][8８八]"),
        Pattern.compile("[1１一十][9９九]"),
        Pattern.compile("[2２二][0０十]"),
        Pattern.compile("[3３三][0０十]"),
        Pattern.compile("[4４四][0０十]"),
        Pattern.compile("[5５五][0０十]"),
        Pattern.compile("[6６六][0０十]"),
        Pattern.compile("[2２二]"),
        Pattern.compile("[3３三]"),
        Pattern.compile("[4４四]"),
        Pattern.compile("[5５五]"),
        Pattern.compile("[6６六]"),
        Pattern.compile("[7７七]"),
        Pattern.compile("[8８八]"),
        Pattern.compile("[9９九]"),
        Pattern.compile("([1１][0０]|十)"),
        Pattern.compile("[1１一]")
    };
    private final char[] numberChar = "0123456789０１２３４５６７８９一二三四五六七八九十".toCharArray();
    private final Pattern[] cityNamePatterns = new Pattern[]{
        Pattern.compile("^([^市区]+市)([^市区]+区)$"), //0
        Pattern.compile("^([^郡]+郡)(.+町)$"), //1
        Pattern.compile("^([^郡]+郡)(.+村)$"),//2
        Pattern.compile("^.+市$"),//3
        Pattern.compile("^[^市区]+区$"),//4
        Pattern.compile("^[^郡]+町$"),//5
        Pattern.compile("^[^郡]+村$")//6
    };

    public void jusho1(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) throws SQLException {
        if (!ready) {
            init();
        }
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            String address = next.get("元住所");
            boolean find = false;
            for (Map.Entry<String, Integer> entry : stableKenCityNameMap.entrySet()) {
                if (address.startsWith(entry.getKey())) {
                    Integer id = entry.getValue();
                    next.put("市区町村ID", id + "");
                    next.put("住所1メモ", "");
                    next.put("住所1", cityIdToKenCityMap.get(id));
                    next.put("作業中住所", address.replaceFirst("^" + entry.getKey(), ""));
                    find = true;
                    break;
                }
            }
            if (!find) {
                for (Map.Entry<String, Integer> entry : incompleteKenCityNameMap.entrySet()) {
                    if (address.startsWith(entry.getKey())) {
                        Integer id = entry.getValue();
                        next.put("市区町村ID", id + "");
                        next.put("住所1メモ", "一部補記");
                        next.put("住所1", cityIdToKenCityMap.get(id));
                        next.put("作業中住所", address.replaceFirst("^" + entry.getKey(), ""));
                        find = true;
                        break;
                    }
                }
            }
            if (!find) {
                for (Map.Entry<Pattern, Integer> entry : fuzzyKenCityNameMap.entrySet()) {
                    Matcher matcher = entry.getKey().matcher(address);
                    if (matcher.find()) {
                        Integer id = entry.getValue();
                        next.put("市区町村ID", id + "");
                        next.put("住所1メモ", "文字置換");
                        next.put("住所1", cityIdToKenCityMap.get(id));
                        next.put("作業中住所", matcher.replaceFirst(""));
                        find = true;
                        break;
                    }
                }
            }
            if (!find) {
                next.put("市区町村ID", "");
                next.put("住所1メモ", "該当なし");
                next.put("住所1", "");
                next.put("作業中住所", "");
            }
        }
        field.addUnique("市区町村ID");
        field.addUnique("住所1メモ");
        field.addNext("元住所", "住所1");
        field.add("作業中住所");
    }

    public void jusho2B(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) throws SQLException {
        MapListHandler handler = new MapListHandler();
        Pattern chome = Pattern.compile("^([^- 　－ー0-9０-９]+)([1-9１-９][0-9０-９]?)([- 　－ー]+)(.+)$");
        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("select * from ad_address where city_id = ? and town_name <> 'NULL'");) {
            int j = 0;
            while (itr.hasNext()) {
                System.out.println(j++);
                Map<String, String> next = itr.next();
                if (!next.get("住所1メモ").equals("該当なし")) {
                    String originalZip = next.get("郵便番号");
                    String id = next.get("市区町村ID");
                    String partAddress = next.get("作業中住所");
                    statement.setInt(1, Integer.parseInt(id));
                    ResultSet rs = statement.executeQuery();
                    List<Map<String, Object>> matchZipInfo = (List<Map<String, Object>>) handler.handle(rs);
                    makeTownBlockAndSort(matchZipInfo);
                    char[] ca = partAddress.toCharArray();
                    List<Map<String, Object>> unmatchZipInfo = createUnmatchZipInfo(matchZipInfo, originalZip);
                    boolean find = false;
                    for (Map<String, Object> row : matchZipInfo) {
                        String townBlock = (String) row.get("townBlock");
                        if (townBlock.isEmpty()) {
                            continue;
                        }
                        if (partAddress.startsWith(townBlock) || partAddress.startsWith("大字" + townBlock) || partAddress.startsWith("字" + townBlock)) {
                            next.put("住所2", townBlock);
                            next.put("住所2メモ", "完全");
                            next.put("作業中住所", partAddress.replaceFirst("(大?字)?" + townBlock, ""));
                            find = true;
                            break;
                        }
                    }
                    Matcher m = chome.matcher(partAddress);
                    if (!find && m.find()) {
                        for (Map<String, Object> row : matchZipInfo) {
                            String townBlock = (String) row.get("townBlock");
                            if (townBlock.isEmpty()) {
                                continue;
                            }
                            String chomeKouho = m.group(1) + m.group(2) + "丁目" + m.group(4);
                            if (chomeKouho.startsWith(townBlock) || chomeKouho.startsWith("大字" + townBlock) || chomeKouho.startsWith("字" + townBlock)) {
                                next.put("住所2", townBlock);
                                next.put("住所2メモ", "丁目");
                                next.put("作業中住所", chomeKouho.replaceFirst("(大?字)?" + townBlock, ""));
                                find = true;
                                break;
                            }
                        }
                    }
                    if (!find) {
                        for (Map<String, Object> row : unmatchZipInfo) {
                            String townBlock = (String) row.get("townBlock");
                            if (townBlock.isEmpty()) {
                                continue;
                            }
                            if (partAddress.startsWith(townBlock)) {
                                String zip = (String) row.get("zip");
                                next.put("住所2", townBlock);
                                next.put("住所2メモ", "郵便番号変更 " + originalZip + "→" + zip);
                                next.put("郵便番号", zip);
                                next.put("作業中住所", partAddress.replaceFirst(townBlock, ""));
                                find = true;
                                break;
                            }
                        }
                    }
                    int[] bestOfBest = new int[]{};
                    if (!find) {
                        int[] best = new int[]{};
                        for (Map<String, Object> row : matchZipInfo) {
                            String townBlock = (String) row.get("townBlock");
                            char[] cb = townBlock.toCharArray();
                            char blast = cb[cb.length - 1];
                            try {
                                for (int i = ca.length - 1; i >= 0; i--) {
                                    if (ca[i] == blast) {
                                        best = best(best, getLine(ca, cb, i, cb.length - 1, new int[]{}));
                                    }
                                }
                            } catch (Throwable t) {
                                System.out.println(partAddress + "\t" + townBlock);
                            }
                            if (best.length > 0 && update(bestOfBest, best)) {
                                bestOfBest = best;
                                next.put("候補", townBlock);
                                next.put("作業中住所候補", partAddress.substring(bestOfBest[0] + 1));
                                next.put("作業中修正候補", partAddress.substring(0, bestOfBest[0] + 1));
                            }
                        }
                        if (bestOfBest.length > 0) {
                            next.put("住所2", next.get("候補"));
                            next.put("住所2メモ", "類似 " + next.get("作業中修正候補") + "→" + next.get("候補"));
                            next.put("作業中住所", next.get("作業中住所候補"));
                        }
                        find = true;
                    }
                    if (!find && !matchZipInfo.isEmpty()) {
                        next.put("住所2メモ ", "不明 " + matchZipInfo.get(0).get("townBlock") + "？");
                    }
                }
            }
        }
        field.addNext("住所1", "住所2");
        field.addNext("住所1メモ", "住所2メモ");
    }

    public void jusho2(@Header("itr") Iterator<Map<String, String>> itr, @Header("field") Field field) throws SQLException {
        MapListHandler handler = new MapListHandler();

        try (Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement("select * from ad_address where city_id = ? and town_name <> 'NULL'");) {
            int i = 0;
            while (itr.hasNext()) {
                System.out.println(i++);
                Map<String, String> next = itr.next();
                if (!next.get("住所1メモ").equals("該当なし")) {
                    String originalZip = next.get("郵便番号");
                    String id = next.get("市区町村ID");
                    String partAddress = next.get("作業中住所").replaceFirst("^大?字", "");
                    statement.setInt(1, Integer.parseInt(id));
                    ResultSet rs = statement.executeQuery();
                    List<Map<String, Object>> matchZipInfo = (List<Map<String, Object>>) handler.handle(rs);
                    makeTownBlockAndSort(matchZipInfo);
                    List<Map<String, Object>> unmatchZipInfo = createUnmatchZipInfo(matchZipInfo, originalZip);
                    boolean find = false;
                    for (Map<String, Object> row : matchZipInfo) {
                        String townBlock = (String) row.get("townBlock");
                        if (partAddress.startsWith(townBlock)) {
                            next.put("住所2", townBlock);
                            next.put("住所2メモ", "完全");
                            next.put("作業中住所", partAddress.replaceFirst(townBlock, ""));
                            find = true;
                            break;
                        } else {
                            if (partAddress.contains("字")) {
                                String partAddress2 = partAddress.replace("大字", "");
                                Matcher m = Pattern.compile((String) row.get("townBlockPattern")).matcher(partAddress2);
                                if (m.find()) {
                                    next.put("住所2", townBlock);
                                    next.put("住所2メモ", "字・大字消去");
                                    next.put("作業中住所", m.replaceFirst(""));
                                    find = true;
                                    break;
                                }
                            }
                            if (hasNumber(townBlock)) {
                                Pattern p = createNumberConvertPattern(townBlock);
                                Matcher m = p.matcher(partAddress);
                                if (m.find()) {
                                    next.put("住所2", townBlock);
                                    next.put("住所2メモ", "漢数字置換");
                                    next.put("作業中住所", m.replaceFirst(""));
                                    find = true;
                                    break;
                                }
                            }
                            if (isFuzzy(townBlock)) {
                                Pattern p = createTownBlockFuzzyPattern(townBlock);
                                Matcher m = p.matcher(partAddress);
                                if (m.find()) {
                                    next.put("住所2", townBlock);
                                    next.put("住所2メモ", "文字置換");
                                    next.put("作業中住所", m.replaceFirst(""));
                                    find = true;
                                    break;
                                }
                            }
                        }
                    }
                    if (!find) {
                        for (Map<String, Object> row : unmatchZipInfo) {
                            String zip = (String) row.get("zip");
                            String townBlock = (String) row.get("townBlock");
                            if (partAddress.startsWith(townBlock)) {
                                next.put("住所2", townBlock);
                                next.put("住所2メモ", "郵便番号変更 " + originalZip + "→" + zip);
                                next.put("郵便番号", zip);
                                next.put("作業中住所", partAddress.replaceFirst(townBlock, ""));
                                break;
                            } else {
                                if (hasNumber(townBlock)) {
                                    Pattern p = createNumberConvertPattern(townBlock);
                                    Matcher m = p.matcher(partAddress);
                                    if (m.find()) {
                                        next.put("住所2", townBlock);
                                        next.put("住所2メモ", "漢数字置換/郵便番号変更 " + originalZip + "→" + zip);
                                        next.put("作業中住所", m.replaceFirst(""));
                                        break;
                                    }
                                }
                                if (isFuzzy(townBlock)) {
                                    Pattern p = createTownBlockFuzzyPattern(townBlock);
                                    //System.out.println(townBlock + "\t" + p.pattern());
                                    Matcher m = p.matcher(partAddress);
                                    if (m.find()) {
                                        next.put("住所2", townBlock);
                                        next.put("住所2メモ", "文字置換/郵便番号変更 " + originalZip + "→" + zip);
                                        next.put("作業中住所", m.replaceFirst(""));
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        field.addNext("住所1", "住所2");
        field.addNext("住所1メモ", "住所2メモ");
    }

    public void init() throws SQLException {
        MapListHandler handler = new MapListHandler();
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select city_id,ken_name,city_name from ad_address group by city_id");
            List<Map<String, Object>> body = (List<Map<String, Object>>) handler.handle(rs);
            for (Map<String, Object> map : body) {
                String cityName = (String) map.get("city_name");
                String kenName = (String) map.get("ken_name");
                Integer cityId = (Integer) map.get("city_id");
                stableKenCityNameMap.put(kenName + cityName, cityId);
                cityIdToKenCityMap.put(cityId, kenName + cityName);
                int i;
                Matcher m = null;
                for (i = 0; i < 7; i++) {
                    m = cityNamePatterns[i].matcher(cityName);
                    if (m.find()) {
                        break;
                    }
                }
                String shi, ku, gun, chou;
                String[] keys, keys2;
                switch (i) {
                    case 0:
                        shi = m.group(1);
                        ku = m.group(2);
                        keys = new String[]{
                            shi + ku,
                            kenName + ku,};
                        putAll(incompleteKenCityNameMap, keys, cityId);
                        if (isFuzzy(kenName + shi + ku)) {
                            keys2 = new String[]{
                                kenName + shi + ku,
                                shi + ku,
                                kenName + ku
                            };
                            patternPutAll(fuzzyKenCityNameMap, keys2, cityId);
                        }
                        break;
                    case 1:
                    case 2:
                        gun = m.group(1);
                        chou = m.group(2);
                        keys = new String[]{
                            kenName + chou,
                            gun + chou,};
                        putAll(incompleteKenCityNameMap, keys, cityId);
                        if (isFuzzy(kenName + gun + chou)) {
                            keys2 = new String[]{
                                kenName + gun + chou,
                                kenName + chou,
                                gun + chou
                            };
                            patternPutAll(fuzzyKenCityNameMap, keys2, cityId);
                        }
                        break;
                    default:
                        keys = new String[]{
                            cityName
                        };
                        putAll(incompleteKenCityNameMap, keys, cityId);
                        if (isFuzzy(kenName + cityName)) {
                            keys2 = new String[]{
                                kenName + cityName,
                                cityName
                            };
                            patternPutAll(fuzzyKenCityNameMap, keys2, cityId);
                        }
                        break;
                }
            }
            Iterator<Map.Entry<String, Integer>> iterator = incompleteKenCityNameMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                if (next.getValue() == null) {
                    iterator.remove();
                }
            }
            try {
                statement.execute("create index city_id_idx on ad_address(city_id)");
                System.out.println("city_id index created.");
            } catch (Throwable t) {
            }
            ready = true;
        }
    }

    public void putAll(LinkedHashMap<String, Integer> incompleteMap, String[] keys, Integer cityId) {
        HashSet<String> set = new HashSet<>();
        for (String key : keys) {
            if (set.add(key)) {
                if (incompleteMap.containsKey(key)) {
                    incompleteMap.put(key, null);
                } else {
                    incompleteMap.put(key, cityId);
                }
            }
        }
    }

    public void patternPutAll(LinkedHashMap<Pattern, Integer> fuzzyMap, String[] keys, Integer cityId) {
        HashSet<Pattern> set = new HashSet<>();
        for (String key : keys) {
            for (Pattern p : equalityPatterns) {
                Matcher m = p.matcher(key);
                if (m.find()) {
                    key = m.replaceAll(p.pattern());
                }
            }
            Pattern newPattern = Pattern.compile("^" + key);
            if (set.add(newPattern)) {
                if (fuzzyMap.containsKey(newPattern)) {
                    fuzzyMap.put(newPattern, null);
                } else {
                    fuzzyMap.put(newPattern, cityId);
                }
            }
        }
    }

    public boolean isFuzzy(String key) {
        for (char c : fuzzyChar) {
            for (char b : key.toCharArray()) {
                if (c == b) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasNumber(String key) {
        for (char c : numberChar) {
            for (char b : key.toCharArray()) {
                if (c == b) {
                    return true;
                }
            }
        }
        return false;
    }

    public Pattern createTownBlockFuzzyPattern(String townBlock) {
        for (Pattern p : equalityPatterns) {
            Matcher m = p.matcher(townBlock);
            if (m.find()) {
                townBlock = m.replaceAll(p.pattern());
            }
        }
        if (townBlock.contains("塚台")) {
            System.out.println(townBlock);
        }
        return Pattern.compile("^" + townBlock);
    }

    public Pattern createNumberConvertPattern(String townBlock) {
        for (Pattern p : numberPatterns) {
            Matcher m = p.matcher(townBlock);
            if (m.find()) {
                townBlock = m.replaceAll(p.pattern());
                break;
            }
        }
        return Pattern.compile("^" + townBlock);
    }

    public List<Map<String, Object>> createUnmatchZipInfo(List<Map<String, Object>> result, String zip) {
        List<Map<String, Object>> unmatch = new ArrayList<>();
        Iterator<Map<String, Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> next = iterator.next();
            if (!next.get("zip").equals(zip)) {
                iterator.remove();
                unmatch.add(next);
            }
        }
        return unmatch;
    }

    public void makeTownBlockAndSort(List<Map<String, Object>> result) {
        Iterator<Map<String, Object>> iterator = result.iterator();
        while (iterator.hasNext()) {
            Map<String, Object> row = iterator.next();
            String town = (String) row.get("town_name");
            String block = (String) row.get("block_name");
            String kyoto_street = (String) row.get("kyoto_street");
            if (block.equals("NULL")) {
                block = "";
            }
            if (kyoto_street.equals("NULL")) {
                kyoto_street = "";
            }
            row.put("townBlock", kyoto_street + town + block);
            row.put("len", (kyoto_street + town + block).length());
        }
        Collections.sort(result, new Comparator<Map<String, Object>>() {

            @Override
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                return ((Integer) (m2.get("len"))) - ((Integer) (m1.get("len")));
            }
        });
    }

    public int[] best(int[] old, int[] next) {
        int oldLen = old.length;
        int nextLen = next.length;
        if (oldLen == nextLen) {
            if (old[0] > next[0]) {
                return next;
            } else {
                return old;
            }
        } else if (oldLen > nextLen) {
            return old;
        } else {
            return next;
        }
    }

    public int[] add(int[] old, int a, int b) {

        int[] next = new int[old.length + 2];
        System.arraycopy(old, 0, next, 0, old.length);
        next[old.length] = a;
        next[old.length + 1] = b;
        return next;
    }

    public int[] getLine(char[] ca, char[] cb, int a, int b, int[] found) {

        int[] old = add(found, a, b);
        int[] best = found;

        for (int i = a - 1; i >= 0; i--) {
            for (int j = b - 1; j >= 0; j--) {
                if (ca[i] == cb[j]) {
                    best = best(best, getLine(ca, cb, i, j, old));
                }
            }
        }
        return best;
    }

    public boolean update(int[] old, int[] next) {
        int oldLen = old.length;
        int nextLen = next.length;
        if (oldLen == nextLen) {
            return old[0] > next[0];
        } else {
            return oldLen <= nextLen;
        }
    }
}
