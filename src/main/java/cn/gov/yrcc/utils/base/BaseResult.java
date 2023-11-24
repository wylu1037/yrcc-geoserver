package cn.gov.yrcc.utils.base;

import java.io.Serial;
import java.io.Serializable;

@SuppressWarnings("all")
public class BaseResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = -8328363186964987657L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 信息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    @SuppressWarnings("unused")
    public static <T> BaseResult<T> instance() {
        return new BaseResult<>();
    }

    public static <T> BaseResult<T> success() {
        return new BaseResult<T>().code(200).message("成功");
    }

    public static <T> BaseResult<T> success(T data) {
        return new BaseResult<T>().data(data).code(200).message("请求成功");
    }

    public static <T> BaseResult<T> error(int code, String message) {
        return new BaseResult<T>().code(code)
                .message(message);
    }

    private BaseResult() {
    }

    public BaseResult(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BaseResult<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public BaseResult<T> message(String message) {
        this.message = message;
        return this;
    }

    public BaseResult<T> data(T data) {
        this.data = data;
        return this;
    }
}

