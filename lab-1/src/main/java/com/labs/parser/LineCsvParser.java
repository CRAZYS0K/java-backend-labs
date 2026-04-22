package com.labs.parser;

import com.labs.model.User;

public abstract class LineCsvParser {

    protected User parseLine(String line) {
        String[] parts = line.split(",", -1); // сохраняем пустые хвостовые поля
        if (parts.length < 9) {
            // пропуск некорректной строки
            return null;
        }

        Integer id = tryParseInt(parts[0]);
        if (id == null) return null;
        //id,name,status,species,type,gender,origin/name,location/name,created
        String name = normalize(parts[1]);
        String status = normalize(parts[2]);
        String species = normalize(parts[3]);
        String type = normalize(parts[4]);
        String gender = normalize(parts[5]);
        String origin = normalize(parts[6]);
        String location = normalize(parts[7]);
        String created = normalize(parts[8]);

        return new User(id, name, status, species, type, gender, origin, location, created);
    }

    private Integer tryParseInt(String s) {
        try {
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String normalize(String raw) {
        if (raw == null) return null;
        String v = raw.trim();
        if ("\"\"".equals(v)) {
            return ""; // явное пустое значение
        }
        if (v.isEmpty()) {
            return null; // отсутствует значение
        }
        return v;
    }
}
