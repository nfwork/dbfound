package com.nfwork.dbfound.web.jstl;

import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.rt.core.ForEachTag;

public class ForEach extends ForEachTag {
	private static final long serialVersionUID = 4338106166444815853L;
	private String sourcePath;

	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
}
