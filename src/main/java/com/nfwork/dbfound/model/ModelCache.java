package com.nfwork.dbfound.model;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.bean.Model;

public class ModelCache {

	private final ConcurrentMap<String, Future<Model>> models = new ConcurrentHashMap<>();

	private final ModelReader modelReader = new ModelReader();
	
	protected void remove(final String modelName) {
		models.remove(modelName);
	}

	/**
	 * 获取model
	 *
	 * @param modelName model name
	 * @return model
	 */
	protected Model getModel(String modelName) {
		Model model = get(modelName);

		if(!model.isPkgModel() && DBFoundConfig.isModelModifyCheck()) {
			File file = new File(model.getFileLocation());
			long newFileLastModify = file.lastModified();
			if (newFileLastModify > model.getFileLastModify()) {
				remove(modelName);
				model = get(modelName);
			}
		}
		return model;
	}
	
	protected Model get(final String modelName) {
	    Future<Model> future = models.get(modelName);
	    if (future == null) {
	        Callable<Model> callable = () -> modelReader.readerModel(modelName);
	        FutureTask<Model> task = new FutureTask<>(callable);
	 
	        future = models.putIfAbsent(modelName, task);
	        if (future == null) {
	            future = task;
	            task.run();
	        }
	    }
	 
	    try {
	        return future.get();
	    } catch (Exception e) {
	    	models.remove(modelName);
			if(e instanceof DBFoundRuntimeException){
				throw (DBFoundRuntimeException)e;
			}else if(e.getCause() instanceof DBFoundRuntimeException){
				throw (DBFoundRuntimeException)e.getCause();
			}else{
				throw new DBFoundPackageException(e);
			}
	    }
	}

	/**
	 * 清空缓存
	 */
	protected void clear() {
		models.clear();
	}
}
