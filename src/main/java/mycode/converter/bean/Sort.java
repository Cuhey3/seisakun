package mycode.converter.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Parameter;

@Component
public class Sort {

    public void dict(@Body List<Map<String, String>> listMap, @Header("parameter") Parameter param) {
        final String targetField = param.first();
        final Integer sign = getSign(nullBlank(param, 1));
        Collections.sort(listMap, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return sign * ((Map<String, String>) o1).get(targetField).compareTo(((Map<String, String>) o2).get(targetField));
            }
        });

    }

    public void asLong(@Body List<Map<String, String>> listMap, @Header("parameter") Parameter param) {
        final String targetField = param.first();
        final Integer sign = getSign(nullBlank(param, 1));
        Collections.sort(listMap, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Long l1 = Long.parseLong(((Map<String, String>) o1).get(targetField));
                Long l2 = Long.parseLong(((Map<String, String>) o2).get(targetField));
                if (l1 > l2) {
                    return sign;
                } else if (l1 < l2) {
                    return -sign;
                } else {
                    return 0;
                }
            }
        });
    }

    public void asDouble(@Body List<Map<String, String>> listMap, @Header("parameter") Parameter param) {
        final String targetField = param.first();
        final Integer sign = getSign(nullBlank(param, 1));
        Collections.sort(listMap, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                double d1 = Double.parseDouble(((Map<String, String>) o1).get(targetField));
                double d2 = Double.parseDouble(((Map<String, String>) o2).get(targetField));
                if (d1 > d2) {
                    return sign;
                } else if (d1 < d2) {
                    return -sign;
                } else {
                    return 0;
                }
            }
        });
    }

    private int getSign(String signOrder) {
        if (signOrder.isEmpty() || signOrder.equals("asc") || signOrder.equals("ASC")) {
            return 1;
        } else if (signOrder.equals("desc") || signOrder.equals("DESC")) {
            return -1;
        } else {
            return 0;
        }
    }

    public String nullBlank(ArrayList<String> al, int index) {
        if (al.size() > index) {
            return al.get(index);
        } else {
            return "";
        }
    }
}
