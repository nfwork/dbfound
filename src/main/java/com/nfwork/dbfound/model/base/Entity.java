package com.nfwork.dbfound.model.base;

import java.io.Serializable;
import java.util.List;

import com.nfwork.dbfound.el.PropertyTransfer;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.reflector.Reflector;
import com.nfwork.dbfound.util.LogUtil;
import org.dom4j.Attribute;
import org.dom4j.Element;

import com.nfwork.dbfound.exception.DBFoundPackageException;

public abstract class Entity extends PropertyTransfer implements Serializable, Cloneable {

	private static final long serialVersionUID = -4264599446199548963L;

	private Entity parent;

	public abstract void run();

	public void init(Element element) {
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

	public Object cloneEntity() {
		try {
			return clone();
		} catch (CloneNotSupportedException e) {
			throw new DBFoundPackageException("entity克隆异常:" + e.getMessage(), e);
		}
	}

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		this.parent = parent;
	}
}

