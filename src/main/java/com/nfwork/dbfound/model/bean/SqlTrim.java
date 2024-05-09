package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;

import java.util.Map;

public class SqlTrim extends SqlPart{

    @Override
    public String getPartSql(Context context, Map<String, Param> params, String provideName) {
        String result;
        if(!sqlPartList.isEmpty()) {
            result = getSqlPartSql(params, context, provideName);
        }else{
            result = sql;
        }
        boolean sub = false;
        int begin =0;
        int end = result.length();

        while (begin < end &&  result.charAt(begin)==','){
            begin ++;
            sub =true;
        }
        while(end > begin && result.charAt(end-1) ==','){
           end--;
           sub = true;
        }
        if(sub){
            result = result.substring(begin, end);
        }
        return result;
    }

}
