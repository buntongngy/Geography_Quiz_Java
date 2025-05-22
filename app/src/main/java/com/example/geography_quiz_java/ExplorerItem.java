package com.example.geography_quiz_java;

public class ExplorerItem {
    private final String title;
    private final int iconRes;

    public ExplorerItem(String title, int iconRes) {
        this.title = title;
        this.iconRes = iconRes;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExplorerItem that = (ExplorerItem) o;

        if (iconRes != that.iconRes) return false;
        return title != null ? title.equals(that.title) : that.title == null;
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + iconRes;
        return result;
    }

    @Override
    public String toString() {
        return "ExplorerItem{" +
                "title='" + title + '\'' +
                ", iconRes=" + iconRes +
                '}';
    }
}