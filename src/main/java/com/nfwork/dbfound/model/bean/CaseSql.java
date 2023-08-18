package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.Entity;

import java.util.Map;

public class CaseSql extends Sqls {

    @Override
    public void run() {
        Entity entity = getParent();
        if (entity instanceof Sqls) {
            WhenSql whenSql = null;
            WhenSql firstWhen = null;
            int otherwiseSize = 0;
            OtherwiseSql lastOtherwiseSql = null;
            for(SqlEntity sqlEntity: this.sqlList){
                if(sqlEntity instanceof WhenSql){
                    if(firstWhen == null){
                        firstWhen = (WhenSql) sqlEntity;
                    }
                    if(whenSql != null){
                       OtherwiseSql otherwiseSql = new OtherwiseSql();
                       otherwiseSql.sqlList.add(sqlEntity);
                       whenSql.setOtherwiseSql(otherwiseSql);
                    }
                    whenSql = (WhenSql) sqlEntity;
                }else{
                    otherwiseSize ++;
                    lastOtherwiseSql = (OtherwiseSql) sqlEntity;
                }
            }

            if(otherwiseSize > 1){
                throw new DBFoundRuntimeException("CaseSql can only have a OtherwiseSql");
            }
            if(whenSql==null){
                throw new DBFoundRuntimeException("CaseSql must have one whenSql");
            }
            if(lastOtherwiseSql != null) {
                whenSql.setOtherwiseSql(lastOtherwiseSql);
            }
            Sqls parent = (Sqls) entity;
            parent.sqlList.add(firstWhen);
        }
    }

    @Override
    public void execute(Context context, Map<String, Param> params,String provideName) {
    }

}



