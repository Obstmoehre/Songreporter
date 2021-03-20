package me.jakob.songreporter.reporting.objects;

public class Categories {
    private String print;
    private String digital;
    private String record;
    private String translate;

    public Categories(String print, String digital, String record, String translate) {
        this.print = print;
        this.digital = digital;
        this.record = record;
        this.translate = translate;
    }

    public Categories() {
        this.print = "0";
        this.digital = "0";
        this.record = "0";
        this.translate = "0";
    }

    public String getPrint() {
        return print;
    }

    public void setPrint(String print) {
        this.print = print;
    }

    public String getDigital() {
        return digital;
    }

    public void setDigital(String digital) {
        this.digital = digital;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
