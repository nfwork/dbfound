package com.nfwork.dbfound.web.jstl;

public class OtherwiseTag extends WhenTagSupport {
    public OtherwiseTag() {
    }

    protected boolean condition() {
        return true;
    }
}