package io.njiwa.common.rest.types;

import java.util.ArrayList;
import java.util.List;

public class RestResponse {
    public Status status;
    public Object response;
    public List<String> errors = new ArrayList<>();

    public enum Status {
        Success, Failed, Undefined
    }

    public RestResponse() {
    }

    public RestResponse(Status status, Object resp) {
        this.response = resp;
        this.status = status;
    }
}
