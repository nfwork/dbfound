package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.model.base.Entity;
import org.dom4j.Element;

public class SqlPart extends Entity {

    String part;

    String condition;

    @Override
    public void init(Element element) {
        super.init(element);
        part = element.getTextTrim();
    }

    @Override
    public void run() {
        if (getParent() instanceof Sql) {
            Sql sql = (Sql) getParent();
            sql.getSqlPartList().add(this);
        }
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}
