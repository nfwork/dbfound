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

public class SqlPart extends SqlEntity {

    String part;

    String partTmp;

    String condition;

    SqlPartType type = SqlPartType.IF;

    String sourcePath;

    String separator = ",";

    String begin = "";

    String end = "";

    boolean autoCompletion;

    boolean autoClearComma;

    private Set<String> paramNameSet;

    @Override
    public void init(Element element) {
        super.init(element);
        part = StringUtil.fullTrim(element.getTextTrim());
        if(condition != null){
            condition = StringUtil.fullTrim(condition);
        }
        if(type == SqlPartType.FOR){
            paramNameSet = new LinkedHashSet<>();
            partTmp = initPartSql(part, paramNameSet);
        }
    }

    @Override
    public void run() {
        if (getParent() instanceof Sql) {
            Sql sql = (Sql) getParent();
            sql.getSqlPartList().add(this);
        }else if(getParent() instanceof SqlTrim){
            SqlTrim trim = (SqlTrim) getParent();
            trim.getSqlPartList().add(this);
        }
    }

    @Override
    public void execute(Context context, Map<String, Param> params, String provideName) {

    }

    protected String getPartSql(Context context,Map<String, Param> params,String provideName ){
        if(this.type == SqlPartType.FOR) {
            if(DataUtil.isNull(this.getSourcePath())){
                throw new DBFoundRuntimeException("SqlPart the sourcePath can not be null when the type is FOR");
            }
            return this.getPart(context, params);
        }else{
            if(DataUtil.isNull(this.getCondition())){
                throw new DBFoundRuntimeException("SqlPart the condition can not be null when the type is IF");
            }
            if (checkCondition(this.getCondition(), params, context, provideName)) {
                return this.getPart();
            } else {
                return "";
            }
        }
    }

    private String getPart(Context context, Map<String, Param> params) {
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

        String partSql = partTmp;
        for (String paramName : paramNameSet){
            Param param = params.get(paramName);
            if (DataUtil.isNotNull(param.getScope())){
                param.setBatchAssign(false);
            }else if (ELEngine.isAbsolutePath(param.getSourcePath())) {
                param.setBatchAssign(false);
            }
            if(!param.isBatchAssign()){
                partSql = partSql.replace(param.getName()+"_##",param.getName());
            }
        }

        Map<String, Object> elCache = new HashMap<>();

        for (int i =0; i< dataSize; i++){
            for (String paramName : paramNameSet){
                Param param = params.get(paramName);
                if(param == null){
                    throw new ParamNotFoundException("param: "+ paramName +" not defined");
                }
                if (param.isBatchAssign()){
                    String newParamName = param.getName()+"_"+i;
                    String sp = DataUtil.isNull(param.getSourcePath())?param.getName():param.getSourcePath();
                    String sph = exeSourcePath +"[" + i +"]."+ sp;

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
                    Object value = context.getData(newParam.getSourcePathHistory(), elCache);
                    if("".equals(value) && param.isEmptyAsNull()){
                        value = null;
                    }
                    newParam.setValue(value);
                    params.put(newParam.getName(), newParam);
                }
            }
            eSql.append(partSql.replace("##", i + "")).append(separator);
        }
        eSql.delete(eSql.length()-separator.length(), eSql.length());
        eSql.append(end);

        return eSql.toString();
    }

    private String initPartSql(String sql, Set<String> batchParamNameSet ) {
        Matcher m = paramPattern.matcher(sql);
        StringBuffer buf = new StringBuffer();
        while (m.find()) {
            String param = m.group();
            String pn = param.substring(2, param.length() - 1).trim();
            batchParamNameSet.add(pn);
            String value = "{@" + pn +"_##}";
            m.appendReplacement(buf, value);
        }
        m.appendTail(buf);
        return buf.toString();
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
}
