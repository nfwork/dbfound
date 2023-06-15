package com.nfwork.dbfound.web.jstl;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.jstl.core.ConditionalTagSupport;
import jakarta.servlet.jsp.tagext.Tag;

public abstract class WhenTagSupport extends ConditionalTagSupport {
    public WhenTagSupport() {
    }

    public int doStartTag() throws JspException {
        Tag parent;
        if (!((parent = this.getParent()) instanceof ChooseTag)) {
            throw new JspTagException("WHEN_OUTSIDE_CHOOSE");
        } else if (!((ChooseTag)parent).gainPermission()) {
            return 0;
        } else if (this.condition()) {
            ((ChooseTag)parent).subtagSucceeded();
            return 1;
        } else {
            return 0;
        }
    }
}
