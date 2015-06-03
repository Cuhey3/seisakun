package mycode.converter.bean;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;

@Component
public class Create {

    public void regex(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) {
        String targetField = param.first();
        if (field.size() == 1) {
            targetField = field.get(0);
        }
        StringBuilder sbb = new StringBuilder("(");
        while (itr.hasNext()) {
            Map<String, String> next = itr.next();
            sbb.append(next.get(targetField)).append("|");
        }
        String str = new String(sbb).replaceFirst("\\|$", ")");
        Pattern p = Pattern.compile("\\([^\\[\\]\\(\\)]+\\|[^\\[\\]\\(\\)]+(?<!\\|)\\)");
        while (true) {
            int gap = 0;
            Matcher m = p.matcher(str);
            StringBuilder sb = new StringBuilder(str);
            while (m.find()) {
                String replace = getReplace(m.group(0));
                sb.replace(m.start() + gap, m.end() + gap, replace);
                gap += replace.length() - m.group().length();
            }
            str = new String(sb);
            if (!p.matcher(str).find()) {
                break;
            }
        }
        Pattern p0 = Pattern.compile("\\(([^\\[\\]\\(\\)\\|]*)\\)");
        str = p0.matcher(str).replaceAll("$1");
        Pattern p1 = Pattern.compile("\\([^\\(\\)\\|](\\|[^\\(\\)\\|])+\\)");
        while (true) {
            int gap = 0;
            Matcher m = p1.matcher(str);
            StringBuilder sb = new StringBuilder(str);
            while (m.find()) {
                String replace = "[" + m.group(0).replaceFirst("^\\(", "").replaceFirst("\\)$", "").replace("|", "") + "]";
                sb.replace(m.start() + gap, m.end() + gap, replace);
                gap += replace.length() - m.group().length();
            }
            str = new String(sb);
            if (!p1.matcher(str).find()) {
                break;
            }
        }
        System.out.println(str);
    }

    static String getReplace(String match) {
        match = match.replaceFirst("^\\(", "");
        match = match.replaceFirst("\\)$", "");
        TreeSet<String> ts = new TreeSet<>(new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                int l1 = ((String) o1).length();
                int l2 = ((String) o2).length();
                if (l1 == l2) {
                    return ((String) o1).compareTo((String) o2);
                } else {
                    return l2 - l1;
                }
            }
        });
        ts.addAll(Arrays.asList(match.split("\\|")));
        TreeSet<Character> top = new TreeSet<>();
        for (String s : ts) {
            top.add(s.charAt(0));
        }
        if (top.size() == 1) {
            StringBuilder sb = new StringBuilder(top.first() + "(");
            for (String s : ts) {
                sb.append(s.substring(1)).append("|");
            }
//            System.out.println(" 2: => " + new String(sb).replaceFirst("\\|$", ")"));
            return new String(sb).replaceFirst("\\|$", ")");
        }
        TreeSet<Character> bottom = new TreeSet<>();
        for (String s : ts) {
            bottom.add(s.charAt(s.length() - 1));
        }
        if (bottom.size() == 1) {
            StringBuilder sb = new StringBuilder("(");
            for (String s : ts) {
                sb.append(s.substring(0, s.length() - 1)).append("|");
            }
            return new String(sb).replaceFirst("\\|$", ")" + bottom.first());
        }
        if (true) {
            StringBuilder sb = new StringBuilder("(");
            for (Character c : top) {
                sb.append(c).append("(");
                for (String s : ts) {
                    if (s.charAt(0) == c) {
                        sb.append(s.substring(1)).append("|");
                    }
                }
                sb = new StringBuilder(new String(sb).replaceFirst("\\|$", ")|"));
            }
            return new String(sb).replaceFirst("\\|$", ")");

        } else {
            StringBuilder sb = new StringBuilder("(");
            for (Character c : bottom) {
                sb.append("(");
                for (String s : ts) {
                    if (s.endsWith(c + "")) {
                        sb.append(s.substring(0, s.length() - 1)).append("|");
                    }
                }
                sb = new StringBuilder(new String(sb).replaceFirst("\\|$", ")")).append(c).append("|");
            }
//            System.out.println(" 5: => " + new String(sb).replaceFirst("\\|$", ")"));
            return new String(sb).replaceFirst("\\|$", ")");
        }
    }

}
