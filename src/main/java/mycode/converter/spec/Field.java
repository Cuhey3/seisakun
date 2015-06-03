package mycode.converter.spec;

import java.util.ArrayList;

public class Field extends ArrayList<String> {

    public Field(ArrayList<String> field) {
        super(field);
    }

    public void addUnique(String newField) {
        if (!this.contains(newField)) {
            this.add(newField);
        }
    }

    public void addTop(String newField) {
        if (!this.contains(newField)) {
            this.add(0, newField);
        }
    }

    public void addNext(String before, String next) {
        this.add(this.indexOf(before) + 1, next);
    }
}
