package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.el.ELEngine;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.base.SqlPartType;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.StringUtil;
import org.dom4j.Element;

import java.util.*;
import java.util.regex.Matcher;

public class SqlPart extends Sql {

    String part;

    String condition;

    SqlPartType type = SqlPartType.IF;

    String sourcePath;

    String separator = ",";

    String begin = "";

    String end = "";

    String item;

    String index;

    boolean autoCompletion;

    boolean autoClearComma;

    private Set<String> paramNameSet;

    @Override
    public void doStartTag(Element element) {
        super.doStartTag(element);
        if(condition != null){
            condition = StringUtil.sqlFullTrim(condition);
        }
    }

    @Override
    public void doEndTag() {
        if (getParent() instanceof Sql) {
            Sql sql = (Sql) getParent();
            sql.getSqlPartList().add(this);
        }

        //生成part内容
        StringBuilder builder = new StringBuilder();
        builder.append(sql).append(",").append(getCondition());
        for (SqlPart sqlPart : sqlPartList) {
            builder.append(",").append(sqlPart.getPart());
        }
        part = builder.toString();

        //生成paramNameset集合
        paramNameSet = new LinkedHashSet<>();
        Matcher m = paramPattern.matcher(part);
        while (m.find()) {
            String param = m.group();
            String pn = param.substring(2, param.length() - 1).trim();
            paramNameSet.add(pn);
        }
    }

    protected String getPartSql(Context context,Map<String, Param> params,String provideName ){
        if(type == SqlPartType.FOR) {
            if(DataUtil.isNull(this.getSourcePath())){
                throw new DBFoundRuntimeException("SqlPart the sourcePath can not be null when the type is FOR");
            }
            if (hasForChild()){
                throw new DBFoundRuntimeException("for loop nesting is not supported in SqlPart");
            }
            return getForPart(context, params, provideName);
        }else{
            if(DataUtil.isNull(this.getCondition()) && DataUtil.isNull(this.getSourcePath())){
                throw new DBFoundRuntimeException("SqlPart the condition or sourcePath can not be null at the same time when the type is IF");
            }
            return getIfPart(context, params, provideName);
        }
    }

    private String getForPartItem(){
        Entity entity = this;
        while (true){
            entity = entity.getParent();
            if (entity instanceof SqlPart) {
                SqlPart sqlPart = (SqlPart) entity;
                if (sqlPart.type == SqlPartType.FOR) {
                    return sqlPart.getItem();
                }
            }else{
                break;
            }
        }
        return null;
    }

    private String getForPart(Context context, Map<String, Param> params, String provideName) {
        String exeSourcePath = sourcePath;

        if(!ELEngine.isAbsolutePath(exeSourcePath)){
            exeSourcePath = context.getCurrentPath() +"." +exeSourcePath;
        }

        StringBuilder eSql = new StringBuilder();

        Object rootData = context.getData(exeSourcePath);
        int dataSize = DataUtil.getDataLength(rootData);
        if(dataSize <= 0){
            return "";
        }
        if(rootData instanceof Collection && !(rootData instanceof ArrayList)){
            rootData = ((Collection<?>)rootData).toArray();
        }

        eSql.append(begin);

        for (String paramName : paramNameSet){
            Param param = params.get(paramName);
            if(param == null){
                throw new ParamNotFoundException("param: "+ paramName +" not defined");
            }
            if (DataUtil.isNotNull(param.getScope())){
                param.setBatchAssign(false);
            }else if (ELEngine.isAbsolutePath(param.getSourcePath())) {
                param.setBatchAssign(false);
            }
        }

        int appendCount = 0;
        String currentPath = context.getCurrentPath();
        try {
            for (int i = 0; i < dataSize; i++) {
                context.getRequestDatas().put("_dbfoundForLoopIndex", i);
                String forPath = exeSourcePath + "[" + i + "]";
                context.setCurrentPath(forPath);

                Object currentData = DBFoundEL.getDataByIndex(i,rootData);

                for (String paramName : paramNameSet) {
                    Param param = params.get(paramName);
                    if (param.isBatchAssign()) {
                        String newParamName = param.getName() + "_" + i;
                        String sp = DataUtil.isNull(param.getSourcePath()) ? param.getName() : param.getSourcePath();

                        Param newParam = (Param) param.cloneEntity();
                        newParam.setName(newParamName);

                        if (index != null && index.equals(sp)) {
                            newParam.setValue(i);
                            newParam.setSourcePathHistory("set_by_index");
                        }else {
                            String sph;
                            Object value;
                            if (item != null && item.equals(sp)) {
                                sph = forPath;
                                value = currentData;
                            } else {
                                sph = forPath + "." + sp;
                                value = DBFoundEL.getData(sp, currentData);
                            }
                            if ("".equals(value) && param.isEmptyAsNull()) {
                                value = null;
                            }
                            Param existsParam = params.get(newParamName);
                            if (existsParam != null) {
                                if (sph.equals(existsParam.getSourcePathHistory())) {
                                    continue;
                                } else {
                                    throw new DBFoundRuntimeException("SqlPart create param failed, the param '" + newParamName + "' already exists of sourcePath '" + existsParam.getSourcePathHistory() + "'");
                                }
                            }
                            newParam.setValue(value);
                            newParam.setSourcePathHistory(sph);
                        }
                        params.put(newParam.getName(), newParam);
                    }
                }

                String partSql = sql;
                if (!sqlPartList.isEmpty()) {
                    partSql = getSqlPartSql(params, context, provideName);
                }
                partSql = getIndexParamSql(partSql, params, i);

                if (!partSql.isEmpty()) {
                    eSql.append(partSql).append(separator);
                    appendCount++;
                }
            }
        }finally {
            context.getRequestDatas().remove("_dbfoundForLoopIndex");
            context.setCurrentPath(currentPath);
        }

        if(appendCount>0) {
            eSql.delete(eSql.length() - separator.length(), eSql.length());
            eSql.append(end);
            return eSql.toString();
        }else{
            return "";
        }
    }

    private String getIfPart(Context context, Map<String, Param> params, String provideName){
        boolean isOK = false;
        Integer forLoopIndex = DataUtil.intValue(context.getRequestDatas().get("_dbfoundForLoopIndex"));

        if(DataUtil.isNotNull(condition)){
            String conditionSql = condition;
            if(forLoopIndex != null){
                conditionSql = getIndexParamSql(conditionSql,params, forLoopIndex);
            }
            if (checkCondition(conditionSql, params, context, provideName)) {
                isOK = true;
            }
        }else if(DataUtil.isNotNull(sourcePath)) {
            Object data;
            if(ELEngine.isAbsolutePath(sourcePath)){
                data = context.getData(sourcePath);
            }else{
                String item = null;
                if(forLoopIndex != null){
                    item = getForPartItem();
                }
                Object currentData = context.getData(context.getCurrentPath());
                if(sourcePath.equals(item)) {
                    data = currentData;
                }else{
                    data = DBFoundEL.getData(sourcePath, currentData);
                }
            }

            if (DataUtil.isNotNull(data) && DataUtil.getDataLength(data) != 0) {
               isOK = true;
            }
        }

        if (isOK) {
            if(sqlPartList.isEmpty()){
                return sql;
            }else {
                return getSqlPartSql(params, context, provideName);
            }
        } else {
            return "";
        }
    }

    private String getIndexParamSql(String sql, Map<String, Param> params, int index) {
        Matcher m = paramPattern.matcher(sql);
        StringBuffer buf = new StringBuffer();
        int size = 0;
        while (m.find()) {
            String paramName = m.group();
            String pn = paramName.substring(2, paramName.length() - 1).trim();
            Param param = params.get(pn);
            if(param.isBatchAssign()){
                size ++;
                String value = "{@" + pn + "_" + index + "}";
                m.appendReplacement(buf, value);
            }
        }
        if(size == 0){
            return sql;
        }else {
            m.appendTail(buf);
            return buf.toString();
        }
    }

    public String getPart() {
        return part;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public SqlPartType getType() {
        return type;
    }

    public void setType(SqlPartType type) {
        this.type = type;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isAutoCompletion() {
        return autoCompletion;
    }

    public void setAutoCompletion(boolean autoCompletion) {
        this.autoCompletion = autoCompletion;
    }

    public boolean isAutoClearComma() {
        return autoClearComma;
    }

    public void setAutoClearComma(boolean autoClearComma) {
        this.autoClearComma = autoClearComma;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
}
