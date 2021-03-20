package me.jakob.songreporter.reporting.exceptions;

import me.jakob.songreporter.reporting.enums.WebsiteElement;

public class WebsiteChangedException extends CCLILoginException {

    WebsiteElement changedElement;
    public WebsiteChangedException(WebsiteElement changedElement) {
        super("The code of at least one element of the website has changed");
        this.changedElement = changedElement;
    }

    public WebsiteElement getChangedElement() {
        return changedElement;
    }
}
