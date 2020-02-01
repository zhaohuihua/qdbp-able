package com.gitee.qdbp.able.exception;

/**
 * 可追加消息的Exception
 *
 * @author zhaohuihua
 * @version 170624
 */
public abstract class EditableException extends RuntimeException {

    /** 版本序列号 **/
    private static final long serialVersionUID = 1L;

    /** 异常消息 **/
    private String message;

    /** 构造函数 **/
    public EditableException() {
        super();
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     */
    public EditableException(String message) {
        super(message);
        this.message = super.getMessage();
    }

    /**
     * 构造函数
     * 
     * @param cause 异常原因
     */
    public EditableException(Throwable cause) {
        super(cause);
        this.message = super.getMessage();
    }

    /**
     * 构造函数
     * 
     * @param message 异常消息
     * @param cause 异常原因
     */
    public EditableException(String message, Throwable cause) {
        super(message, cause);
        this.message = super.getMessage();
    }

    /**
     * 获取异常消息
     * 
     * @return 异常消息
     */
    @Override
    public String getMessage() {
        return this.message;
    }

    /**
     * 设置消息
     * 
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 在消息前面追加异常消息
     * 
     * @param message 异常消息
     */
    public void prependMessage(String message) {
        if (message == null || message.length() == 0) {
            return;
        }

        if (this.message == null) {
            this.message = message;
        } else {
            char last = message.charAt(message.length() - 1);
            if (last == ' ' || last == '\t' || last == '\r' || last == '\n') {
                this.message = message + this.message;
            } else {
                this.message = message + " " + this.message;
            }
        }
    }

    /**
     * 在消息后面追加异常消息
     * 
     * @param message 异常消息
     */
    public void appendMessage(String message) {
        if (message == null || message.length() == 0) {
            return;
        }

        if (this.message == null) {
            this.message = message;
        } else {
            char last = message.charAt(message.length() - 1);
            if (last == ' ' || last == '\t' || last == '\r' || last == '\n') {
                this.message = this.message + message;
            } else {
                this.message = this.message + " " + message;
            }
        }
    }

}
