package com.nfwork.dbfound.model;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.bean.Model;

public class ModelCache {

	private static final ConcurrentMap<String, Future<Model>> models = new ConcurrentHashMap<>();
	
	public static void remove(final String modelName) {
		models.remove(modelName);
	}
	
	public static Model get(final String modelName) {
	    Future<Model> future = models.get(modelName);
	    if (future == null) {
	        Callable<Model> callable = () -> ModelReader.readerModel(modelName);
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
	public static void clear() {
		models.clear();
	}
}
