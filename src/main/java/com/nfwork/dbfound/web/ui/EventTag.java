package com.nfwork.dbfound.web.ui;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.jsp.JspTagException;
import jakarta.servlet.jsp.tagext.TagSupport;

public class EventTag extends TagSupport implements Cloneable {
 
	private static final long serialVersionUID = 6811748681670079843L;

	String id;

	List<Event> events;

	public int doStartTag() throws JspTagException {
		events = new ArrayList<Event>();
		return EVAL_BODY_INCLUDE;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void setEvents(List<Event> events) {
		this.events = events;
	}

}
