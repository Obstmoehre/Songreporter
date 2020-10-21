package me.jakob.songreporter.reporting.exceptions;

public class CategoryNotReportableException extends Exception {

    private final int failedCategory;

    public CategoryNotReportableException(int failedCategory) {
        this.failedCategory = failedCategory;
    }

    public int getFailedCategory() {
        return failedCategory;
    }
}
