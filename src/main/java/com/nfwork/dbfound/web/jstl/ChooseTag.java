package com.nfwork.dbfound.web.jstl;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class ChooseTag extends TagSupport {
    private boolean subtagGateClosed;

    public ChooseTag() {
        this.init();
    }

    public void release() {
        super.release();
        this.init();
    }

    public synchronized boolean gainPermission() {
        return !this.subtagGateClosed;
    }

    public synchronized void subtagSucceeded() {
        if (this.subtagGateClosed) {
            throw new IllegalStateException("CHOOSE_EXCLUSIVITY");
        } else {
            this.subtagGateClosed = true;
        }
    }

    public int doStartTag() throws JspException {
        this.subtagGateClosed = false;
        return 1;
    }

    private void init() {
        this.subtagGateClosed = false;
    }
}
