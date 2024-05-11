package com.nfwork.dbfound.model.bean;

import java.util.List;

import com.nfwork.dbfound.el.PropertyTransfer;
import com.nfwork.dbfound.model.reflector.Reflector;
import com.nfwork.dbfound.util.LogUtil;
import org.dom4j.Attribute;
import org.dom4j.Element;

public abstract class Entity extends PropertyTransfer {

	private Entity parent;

	public void doStartTag(Element element) {
		List<Attribute> list = element.attributes();
		for (Attribute attribute : list) {
			String name =  attribute.getName();
			Reflector reflector = Reflector.forClass(getClass());

			if(reflector.hasSetter(name)) {
				Object value = attribute.getValue();
				try {
					reflector.setProperty(this, name, value);
				} catch (Exception e) {
					LogUtil.error(e.getMessage(), e);
				}
			}
		}
	}

	public abstract void doEndTag();

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}
}

