package mycode.converter.bean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Convert {

    @Autowired
    private DriverManagerDataSource datasource;

    private final Pattern statePattern = Pattern.compile("^(東京都|北海道|大阪府|京都府|[^都道府県]{2,3}県)");

    public void addressToZip(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) throws SQLException {
        try (Connection connection = datasource.getConnection()) {
            String targetField = param.toString();
            while (itr.hasNext()) {
                boolean flag = true;
                Map<String, String> map = itr.next();
                String address = normalize(map.get(targetField));
                Matcher match = statePattern.matcher(address);
                if (match.find()) {
                    String state = match.group(1);
                    ResultSet rs = connection.createStatement().executeQuery("select * from 郵便番号_全国 where 都道府県 = '" + state + "'");
                    String city = "";
                    while (rs.next()) {
                        city = rs.getString("市区町村");
                        if (!city.isEmpty() && address.startsWith(state + normalize(city))) {
                            break;
                        } else {
                            city = "";
                        }
                    }
                    if (!city.isEmpty()) {
                        String area;
                        rs = connection.createStatement().executeQuery("select * from 郵便番号_全国 where 都道府県 = '" + state + "' and 市区町村 = '" + city + "'");
                        String stateAndCity = state + normalize(city);
                        TreeMap<String, String> areaMap = new TreeMap<>();
                        while (rs.next()) {
                            area = rs.getString("町域2");
                            if (!area.isEmpty()) {
                                areaMap.put((99 - area.length()) + "\t" + normalize(area), rs.getString("郵便番号"));
                            }
                        }
                        Iterator<String> iterator = areaMap.keySet().iterator();
                        while (iterator.hasNext()) {
                            String areaKey = iterator.next();
                            area = areaKey.split("\t")[1];
                            if (address.startsWith(stateAndCity + area)) {
                                map.put("郵便番号", areaMap.get(areaKey));
                                flag = false;
                                break;
                            }
                        }
                    }
                }
                if (flag) {
                    System.out.println(address);
                    map.put("無効", "1");
                }
                field.addUnique("郵便番号");
            }
        }
    }

    String normalize(String str) {
        return str.replaceAll("[ヶがガ]", "ケ")
                .replaceAll("[之の]", "ノ")
                .replaceAll("薮", "藪")
                .replaceAll("[ 　]", "")
                .replaceAll("(?<![文万阿十所和分])(大字|小字)", "")
                .replaceAll("(?<![文万阿十所和分大小])字", "");
    }
}
