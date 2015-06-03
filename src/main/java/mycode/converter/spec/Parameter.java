package mycode.converter.spec;

import java.util.ArrayList;
import java.util.Arrays;

public class Parameter extends ArrayList<String> {

    private final String expression;

    public Parameter(String s) {
        super(Arrays.asList(s.split(" ")));
        this.expression = s;
    }

    @Override
    public String toString() {
        return this.expression;
    }

    public String first() {
        return this.get(0);
    }

    public String second() {
        return this.get(1);
    }

    public String third() {
        return this.get(2);
    }

    public String fourth() {
        return this.get(3);
    }

    public String fifth() {
        return this.get(4);
    }
}
