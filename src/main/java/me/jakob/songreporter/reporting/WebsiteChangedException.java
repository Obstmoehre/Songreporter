package me.jakob.songreporter.reporting;

public class WebsiteChangedException extends Exception {

    WebsiteElement changedElement;
    public WebsiteChangedException(WebsiteElement changedElement) {
        this.changedElement = changedElement;
    }

    public WebsiteElement getChangedElement() {
        return changedElement;
    }
}
