package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.util.DataUtil;
import java.util.Map;

public class ExcelColumn {

    private String name;

    private String title;

    private Integer width = 100;

    private Map<String,Object> mapper;

    private String format;

    public ExcelColumn(){
    }

    public ExcelColumn(Map<String,Object> data){
        Object name = data.get("name");
        assert name != null;
        this.name = name.toString();

        Object width = data.get("width");
        if(DataUtil.isNotNull(width)){
            this.width = DataUtil.intValue(width);
        }

        Object title = data.get("title");
        if(DataUtil.isNull(title)){
            title = data.get("content"); //兼容老版本协议，3.4后改为title；
        }
        assert title != null;
        this.title = title.toString();

        Map<String,Object> mapper = (Map)data.get("mapper");
        if (mapper != null){
            this.mapper = mapper;
        }

        Object format = data.get("format");
        if(DataUtil.isNotNull(format)){
            this.format = format.toString();
        }
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public Integer getWidth() {
        return width;
    }

    public Map<String, Object> getMapper() {
        return mapper;
    }

    public String getFormat() {
        return format;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public void setMapper(Map<String, Object> mapper) {
        this.mapper = mapper;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
