package com.arun.portfolio.dto;

/**
 * Request payload for saving or updating an application setting (e.g. resume URL).
 */
public class SettingRequest {

    private String value;

    public SettingRequest() {
    }

    public SettingRequest(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
