package com.zoeziMitzanimedia.androidapp.retrofit_network;

public class ServerResponse {
    private Boolean status;
    private String errors;

    public ServerResponse(Boolean status, String errors) {
        this.status = status;
        this.errors = errors;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
