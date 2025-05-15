package com.nfwork.dbfound.model;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.el.DBFoundEL;
import com.nfwork.dbfound.exception.ExecuteNotFoundException;
import com.nfwork.dbfound.exception.QueryNotFoundException;
import com.nfwork.dbfound.model.bean.Execute;
import com.nfwork.dbfound.model.bean.Model;
import com.nfwork.dbfound.model.bean.Query;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;

import java.util.HashMap;
import java.util.Map;

public class ModelOperator {

    private static final ModelCache modelCache = new ModelCache();

    public static final String defaultBatchPath = "param.GridData";

    public static final String defaultPath = "param";

    /**
     * 查询 可以指定当前路径、是否自动分页、返回对象的查询
     *
     * @param context context
     * @param modelName model name
     * @param queryName query name
     * @param sourcePath source path
     * @param autoPaging auto paging
     * @param clazz clazz
     * @return T
     */
    public <T> QueryResponseObject<T> query(Context context, String modelName, String queryName, String sourcePath, boolean autoPaging, Class<T> clazz) {
        try {
            if(context.onTopModelDeep()) {
                LogUtil.info("-----------------------query begin--------------------------------------");
            }
            context.modelDeepIncrease();

            if (DataUtil.isNull(queryName)) {
                queryName = "_default";
            }
            if(DataUtil.isNull(sourcePath)){
                sourcePath = defaultPath;
            }
            Model model = modelCache.getModel(modelName);

            // 把model、currentPath对象放入到 当前线程里
            context.setCurrentModel(modelName);
            context.setCurrentPath(sourcePath);

            Query query = model.getQuery(queryName);
            if (query == null) {
                throw new QueryNotFoundException("can not found Query:" + queryName + ", on Model:" + modelName);
            }
            return query.doQuery(context, sourcePath, autoPaging, clazz);
        } finally {
            context.modelDeepReduce();
            if(context.onTopModelDeep()) {
                context.closeConns();
                LogUtil.info("-----------------------query end----------------------------------------");
            }
        }
    }

    /**
     * 批量执行操作
     *
     * @param context context
     * @param modelName model name
     * @param executeName execute name
     * @param sourcePath sourcePath;
     * @return ResponseObject
     */
    public ResponseObject batchExecute(Context context, String modelName, String executeName, String sourcePath) {
        try {
            if(context.onTopModelDeep()) {
                LogUtil.info("-----------------------batch execute begin------------------------------");
            }
            context.modelDeepIncrease();

            if (DataUtil.isNull(executeName)) {
                executeName = "addOrUpdate";
            }

            // 批量执行查找客户端数据的路径
            String batchExecutePath;
            if (DataUtil.isNull(sourcePath)) {
                batchExecutePath = defaultBatchPath;
            }else{
                batchExecutePath = sourcePath;
            }

            ResponseObject ro = null;

            Object rootData = context.getData(batchExecutePath);
            int size =  DataUtil.getDataLength(rootData);

            if (size > 0) {
                Model model = modelCache.getModel(modelName);

                // 把modelName对象放入到 当前线程里
                context.setCurrentModel(modelName);
                Map<String, Object> elCache = new HashMap<>();

                for (int j = 0; j < size; j++) {
                    String en ;
                    String currentPath = batchExecutePath + "[" + j + "]";
                    context.setCurrentPath(currentPath);
                    Object currentData = DBFoundEL.getDataByIndex(j, rootData);

                    if ("addOrUpdate".equals(executeName)) {
                        String status = context.getString(currentPath + "._status");
                        if (status != null)
                            status = status.toUpperCase();
                        if ("NEW".equals(status)) {
                            en = "add";
                        } else if ("OLD".equals(status)) {
                            en = "update";
                        } else {
                            throw new ExecuteNotFoundException("cant not found (_status) field, can not found Execute");
                        }
                    } else {
                        en = executeName;
                    }
                    Execute execute = model.getExecute(en);
                    if (execute == null) {
                        throw new ExecuteNotFoundException("can not found Execute:" + executeName + ", on Model:" + modelName);
                    }
                    ro = execute.doExecute(context, currentPath, currentData, elCache);
                }
            }
            if(ro == null){
                ro = new ResponseObject();
                ro.setSuccess(true);
                ro.setMessage("success");
            }
            return ro;
        } finally {
            context.modelDeepReduce();
            if(context.onTopModelDeep()) {
                context.closeConns();
                LogUtil.info("-----------------------batch execute end--------------------------------");
            }
        }
    }

    /**
     * 执行操作
     *
     * @param context context
     * @param modelName model name
     * @param executeName execute name
     * @param sourcePath source path
     * @return ResponseObject
     */
    public ResponseObject execute(Context context, String modelName, String executeName, String sourcePath) {
        try {
            if(context.onTopModelDeep()) {
                LogUtil.info("-----------------------execute begin------------------------------------");
            }
            context.modelDeepIncrease();

            if (DataUtil.isNull(executeName)) {
                executeName = "_default";
            }
            if(DataUtil.isNull(sourcePath)){
                sourcePath = defaultPath;
            }

            Model model = modelCache.getModel(modelName);
            Execute execute = model.getExecute(executeName);
            if (execute == null) {
                throw new ExecuteNotFoundException("can not found Execute:" + executeName + ", on Model:" + modelName);
            }
            // 把model、currentPath对象放入到 当前线程里
            context.setCurrentPath(sourcePath);
            context.setCurrentModel(modelName);

            Map<String, Object> elCache = new HashMap<>();
            Object currentData = context.getData(sourcePath);
            return execute.doExecute(context, sourcePath, currentData, elCache);
        } finally {
            context.modelDeepReduce();
            if(context.onTopModelDeep()) {
                context.closeConns();
                LogUtil.info("-----------------------execute end--------------------------------------");
            }
        }
    }
}
