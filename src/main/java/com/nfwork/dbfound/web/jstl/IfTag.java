package com.nfwork.dbfound.web.jstl;

import jakarta.servlet.jsp.jstl.core.ConditionalTagSupport;

public class IfTag extends ConditionalTagSupport {
    private boolean test;

    public IfTag() {
        this.init();
    }

    public void release() {
        super.release();
        this.init();
    }

    protected boolean condition() {
        return this.test;
    }

    public void setTest(boolean test) {
        this.test = test;
    }

    private void init() {
        this.test = false;
    }
}
