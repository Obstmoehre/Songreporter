package me.jakob.songreporter.config;

public class Config {
    private String browser;
    private String songsDirectory;
    private String scriptsDirectory;
    private String eMail;
    private String password;
    private boolean[] categories;

    public String getBrowser() {
        return this.browser;
    }

    void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getSongsDirectory() {
        return this.songsDirectory;
    }

    void setSongsDirectory(String songsDirectory) {
        this.songsDirectory = songsDirectory;
    }

    public String getEMail() {
        return eMail;
    }

    void setEMail(String eMail) {
        this.eMail = eMail;
    }

    public String getPassword() {
        return password;
    }

    void setPassword(String password) {
        this.password = password;
    }

    public String getScriptsDirectory() {
        return scriptsDirectory;
    }

    void setScriptsDirectory(String scriptsDirectory) {
        this.scriptsDirectory = scriptsDirectory;
    }

    public boolean[] getCategories() {
        return categories;
    }

    void setCategories(boolean[] categories) {
        this.categories = categories;
    }
}
