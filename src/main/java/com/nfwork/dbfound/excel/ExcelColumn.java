package com.nfwork.dbfound.excel;

import com.nfwork.dbfound.util.DataUtil;
import java.util.Map;

public class ExcelColumn {

    private final String name;

    private final String title;

    private final Integer width;

    private Map<String,Object> mapper;

    private String format;

    public ExcelColumn(Map<String,Object> data){
        Object name = data.get("name");
        assert name != null;
        this.name = name.toString();

        Object width = data.get("width");
        if(DataUtil.isNotNull(width)){
            this.width = DataUtil.intValue(width);
        }else{
            this.width = 100;
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
}
