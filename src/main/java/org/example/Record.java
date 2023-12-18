package org.example;

import java.util.HashMap;
import java.util.Map;

public class Record {
    private Map<String, String> fields = new HashMap<>();

    public void addField(String name, String value) {
        fields.put(name, value);
    }

    @Override
    public String toString() {
        return "Record{" + "fields=" + fields + '}';
    }
}
