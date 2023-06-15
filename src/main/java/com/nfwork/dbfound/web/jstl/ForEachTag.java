package com.nfwork.dbfound.web.jstl;

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.jstl.core.LoopTag;
import jakarta.servlet.jsp.tagext.IterationTag;

import java.util.ArrayList;

public class ForEachTag extends ForEachSupport implements LoopTag, IterationTag {
    public ForEachTag() {
    }

    public void setBegin(int begin) throws JspTagException {
        this.beginSpecified = true;
        this.begin = begin;
        this.validateBegin();
    }

    public void setEnd(int end) throws JspTagException {
        this.endSpecified = true;
        this.end = end;
        this.validateEnd();
    }

    public void setStep(int step) throws JspTagException {
        this.stepSpecified = true;
        this.step = step;
        this.validateStep();
    }

    public void setItems(Object o) throws JspTagException {
        if (o == null) {
            this.rawItems = new ArrayList();
        } else {
            this.rawItems = o;
        }

    }
}
