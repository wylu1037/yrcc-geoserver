package cn.gov.yrcc.internal.error;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * custom business exception.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2823905797357826099L;

    private int code = HttpStatus.BAD_REQUEST.value();

    private String message;

    public BusinessException() {

    }

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

