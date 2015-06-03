package mycode.converter.bean;

import java.util.Iterator;
import java.util.Map;
import org.apache.camel.Header;
import org.springframework.stereotype.Component;
import mycode.converter.spec.Field;
import mycode.converter.spec.Parameter;

@Component
public class Number {

    public void is(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) throws Exception {
        String targetField = param.first();
        String sign = param.second();
        int signNumber = 0;
        switch (sign) {
            case ">=":
                signNumber = 1;
                break;
            case ">":
                signNumber = 2;
                break;
            case "<=":
                signNumber = 3;
                break;
            case "<":
                signNumber = 4;
                break;
            default:
                throw new Exception();
        }
        String okField = targetField + "確認";
        int number = Integer.parseInt(param.third());
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            if (check(map.get(targetField), signNumber, number)) {
                map.put(okField, "○");
            } else {
                map.put(okField, "×");
                map.put("無効", "1");
            }
        }
        field.addUnique(okField);
    }

    public void between(@Header("itr") Iterator<Map<String, String>> itr, @Header("parameter") Parameter param, @Header("field") Field field) throws Exception {
        int number1 = Integer.parseInt(param.first());
        String sign1 = param.second();
        String targetField = param.third();
        String sign2 = param.fourth();
        int number2 = Integer.parseInt(param.fifth());
        int signNumber = 0;
        switch (sign1) {
            case "<=":
                switch (sign2) {
                    case "<=":
                        signNumber = 1;
                        break;
                    case "<":
                        signNumber = 2;
                        break;
                    default:
                        throw new Exception();
                }
                break;
            case "<":
                switch (sign2) {
                    case "<=":
                        signNumber = 3;
                        break;
                    case "<":
                        signNumber = 4;
                        break;
                    default:
                        throw new Exception();
                }
                break;
            case ">=":
                switch (sign2) {
                    case ">=":
                        signNumber = 5;
                        break;
                    case ">":
                        signNumber = 6;
                        break;
                    default:
                        throw new Exception();
                }
                break;
            case ">":
                switch (sign2) {
                    case ">=":
                        signNumber = 7;
                        break;
                    case ">":
                        signNumber = 8;
                        break;
                    default:
                        throw new Exception();
                }
                break;
            default:
                throw new Exception();
        }

        String okField = targetField + "確認";
        while (itr.hasNext()) {
            Map<String, String> map = itr.next();
            if (checkBetween(map.get(targetField), signNumber, number1, number2)) {
                map.put(okField, "○");
            } else {
                map.put(okField, "×");
                map.put("無効", "1");
            }
        }
        field.addUnique(okField);
    }

    public boolean check(String str, int signNumber, int number) {
        try {
            int targetNumber = Integer.parseInt(str);
            if (signNumber == 1 && targetNumber >= number) {
                return true;
            } else if (signNumber == 2 && targetNumber > number) {
                return true;
            } else if (signNumber == 3 && targetNumber <= number) {
                return true;
            } else {
                return signNumber == 4 && targetNumber < number;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public boolean checkBetween(String str, int signNumber, int number1, int number2) {
        try {
            int targetNumber = Integer.parseInt(str);
            if (signNumber == 1 && number1 <= targetNumber && targetNumber <= number2) {
                return true;
            } else if (signNumber == 2 && number1 <= targetNumber && targetNumber < number2) {
                return true;
            } else if (signNumber == 3 && number1 < targetNumber && targetNumber <= number2) {
                return true;
            } else if (signNumber == 4 && number1 < targetNumber && targetNumber < number2) {
                return true;
            } else if (signNumber == 5 && number1 >= targetNumber && targetNumber >= number2) {
                return true;
            } else if (signNumber == 6 && number1 >= targetNumber && targetNumber > number2) {
                return true;
            } else if (signNumber == 7 && number1 > targetNumber && targetNumber >= number2) {
                return true;
            } else {
                return signNumber == 8 && number1 > targetNumber && targetNumber > number2;
            }
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
