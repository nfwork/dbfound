package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
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
            condition = StringUtil.fullTrim(condition);
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
            if (type == SqlPartType.FOR && hasForChild()){
                throw new DBFoundRuntimeException("for loop nesting is not supported in SqlPart");
            }
            return getForPart(context, params, provideName);
        }else{
            if(DataUtil.isNull(this.getCondition())){
                throw new DBFoundRuntimeException("SqlPart the condition can not be null when the type is IF");
            }
            return getIfPart(context, params, provideName);
        }
    }

    private boolean hasForChild(){
        for (SqlPart sqlPart : sqlPartList){
            if(sqlPart.type == SqlPartType.FOR || sqlPart.hasForChild() ){
               return true;
            }
        }
        return false;
    }

    private String getForPart(Context context, Map<String, Param> params, String provideName) {
        String exeSourcePath = sourcePath;

        if(!ELEngine.isAbsolutePath(exeSourcePath)){
            exeSourcePath = context.getCurrentPath() +"." +exeSourcePath;
        }

        StringBuilder eSql = new StringBuilder();
        int dataSize = context.getDataLength(exeSourcePath);
        if(dataSize <= 0){
            return eSql.toString();
        }

        eSql.append(begin);
        Map<String, Object> elCache = new HashMap<>();

        for (String paramName : paramNameSet){
            Param param = params.get(paramName);
            if (DataUtil.isNotNull(param.getScope())){
                param.setBatchAssign(false);
            }else if (ELEngine.isAbsolutePath(param.getSourcePath())) {
                param.setBatchAssign(false);
            }
        }

        for (int i =0; i< dataSize; i++){
            context.setData("request._dbfoundForLoopIndex", i);
            for (String paramName : paramNameSet){
                Param param = params.get(paramName);
                if(param == null){
                    throw new ParamNotFoundException("param: "+ paramName +" not defined");
                }
                if (param.isBatchAssign()){
                    String newParamName = param.getName()+"_"+i;
                    String sp = DataUtil.isNull(param.getSourcePath())?param.getName():param.getSourcePath();
                    String sph;
                    if(item !=null && item.equals(sp)){
                        sph = exeSourcePath +"[" + i +"]";
                    }else{
                        sph = exeSourcePath +"[" + i +"]."+ sp;
                    }

                    Param existsParam = params.get(newParamName);
                    if(existsParam != null) {
                        if(sph.equals(existsParam.getSourcePathHistory())){
                            continue;
                        }else{
                            throw new DBFoundRuntimeException("SqlPart create param failed, the param '" + newParamName +"' already exists of sourcePath '" + existsParam.getSourcePathHistory() + "'");
                        }
                    }

                    Param newParam = (Param) param.cloneEntity();
                    newParam.setName(newParamName);
                    newParam.setSourcePathHistory(sph);

                    if(index !=null && index.equals(sp)){
                        newParam.setValue(i);
                        newParam.setSourcePathHistory("set_by_index");
                    }else{
                        Object value = context.getData(newParam.getSourcePathHistory(), elCache);
                        if("".equals(value) && param.isEmptyAsNull()){
                            value = null;
                        }
                        newParam.setValue(value);
                    }
                    params.put(newParam.getName(), newParam);
                }
            }

            String partSql = sql;
            if(!sqlPartList.isEmpty()){
                partSql = getSqlPartSql(params,context, provideName);
            }
            partSql = getIndexParamSql(partSql, params, i);

            if(!partSql.isEmpty()) {
                eSql.append(partSql).append(separator);
            }
        }
        context.setData("request._dbfoundForLoopIndex", null);
        eSql.delete(eSql.length()-separator.length(), eSql.length());
        eSql.append(end);

        return eSql.toString();
    }

    private String getIfPart(Context context, Map<String, Param> params, String provideName){
        String conditionSql = condition;
        Integer forLoopIndex = context.getInt("request._dbfoundForLoopIndex");
        if(forLoopIndex != null){
            conditionSql = getIndexParamSql(conditionSql,params, forLoopIndex);
        }
        if (checkCondition(conditionSql, params, context, provideName)) {
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
        while (m.find()) {
            String paramName = m.group();
            String pn = paramName.substring(2, paramName.length() - 1).trim();
            Param param = params.get(pn);
            if(param.isBatchAssign()){
                String value = "{@" + pn + "_" + index + "}";
                m.appendReplacement(buf, value);
            }
        }
        m.appendTail(buf);
        return buf.toString();
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
