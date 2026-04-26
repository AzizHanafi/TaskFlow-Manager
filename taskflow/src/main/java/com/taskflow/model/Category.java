package com.taskflow.model;

public enum Category {
    KULIAH("Kuliah"),
    KERJA("Kerja"),
    PRIBADI("Pribadi"),
    LAINNYA("Lainnya");

    private final String label;

    Category(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
