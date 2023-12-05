package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlTrim extends SqlPart{

    protected List<SqlPart> sqlPartList  = new ArrayList<>();

    @Override
    public String getPartSql(Context context, Map<String, Param> params, String provideName) {
        StringBuilder builder = new StringBuilder();
        for(SqlPart sqlPart : sqlPartList){
            builder.append(sqlPart.getPartSql(context,params,provideName));
        }
        int last = builder.length() -1;
        if(last > 0 && builder.charAt(last) == ','){
            builder.deleteCharAt(last);
        }
        if(builder.length() > 0 && builder.charAt(0) == ','){
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    @Override
    public String getPart() {
        return getSqlPartList().stream().map(v->v.getCondition()+","+v.getPart()).collect(Collectors.joining(","));
    }

    public List<SqlPart> getSqlPartList() {
        return sqlPartList;
    }
}
