package com.nfwork.dbfound.model.bean;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.Entity;
import java.util.Map;

public class OtherwiseSql extends Sqls {

    @Override
    public void run() {
        Entity entity = getParent();
        if(entity instanceof CaseSql){
            super.run();
            return;
        }
        if (entity instanceof Sqls) {
            Sqls sqls = (Sqls) entity;
            int size = sqls.sqlList.size();
            if(size>0){
                SqlEntity sqlEntity = sqls.sqlList.get(size-1);
                if(sqlEntity instanceof WhenSql){
                    WhenSql whenSql = ((WhenSql) sqlEntity);
                    if(whenSql.getOtherwiseSql() != null){
                        throw new DBFoundRuntimeException("WhenSql can only have one OtherwiseSql");
                    }else{
                        whenSql.setOtherwiseSql(this);
                        return;
                    }
                }
            }
        }
        throw new DBFoundRuntimeException("OtherwiseSql must follow with WhenSql or in CaseSql");
    }

    @Override
    public void execute(Context context, Map<String, Param> params,String provideName) {
        // 执行相应操作
        for (SqlEntity sql : sqlList) {
            sql.execute(context, params, provideName);
        }
    }

}



