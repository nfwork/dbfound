package com.nfwork.dbfound.web.jstl;

import jakarta.servlet.jsp.tagext.TagData;
import jakarta.servlet.jsp.tagext.TagExtraInfo;

public class ForEachTEI extends TagExtraInfo {
    private static final String ITEMS = "items";
    private static final String BEGIN = "begin";
    private static final String END = "end";

    public ForEachTEI() {
    }

    public boolean isValid(TagData us) {
        return isSpecified(us, "items") || isSpecified(us, "begin") && isSpecified(us, "end");
    }

    public static boolean isSpecified(TagData data, String attributeName) {
        return data.getAttribute(attributeName) != null;
    }
}