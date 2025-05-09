package com.isidrosantiago.booknetwork.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomResponse<T> {
    private String status;
    private T data;
    private String message;
    private String internalErrorCode;
}
