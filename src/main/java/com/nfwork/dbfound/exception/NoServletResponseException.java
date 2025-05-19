package com.nfwork.dbfound.exception;

public class NoServletResponseException extends RuntimeException {

    private String content;

    public NoServletResponseException(String content) {
        super("Servlet Response is null");
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
