package me.jakob.songreporter.config;

class Config {
    private String browser;
    private String songsDirectory;
    private String scriptsDirectory;
    private String eMail;
    private boolean[] categories;

    String getBrowser() {
        return this.browser;
    }

    void setBrowser(String browser) {
        this.browser = browser;
    }

    String getSongsDirectory() {
        return this.songsDirectory;
    }

    void setSongsDirectory(String songsDirectory) {
        this.songsDirectory = songsDirectory;
    }

    String getEMail() {
        return eMail;
    }

    void setEMail(String eMail) {
        this.eMail = eMail;
    }

    String getScriptsDirectory() {
        return scriptsDirectory;
    }

    void setScriptsDirectory(String scriptsDirectory) {
        this.scriptsDirectory = scriptsDirectory;
    }

    boolean[] getCategories() {
        return categories;
    }

    void setCategories(boolean[] categories) {
        this.categories = categories;
    }
}
