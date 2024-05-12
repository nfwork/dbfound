package com.nfwork.dbfound.web.ui;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

public class EventTag extends TagSupport implements Cloneable {

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
