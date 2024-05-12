package com.nfwork.dbfound.util;

import com.nfwork.dbfound.core.Context;

public class TransactionUtil {

    public static <T> T execute(Context context, CallBack<T> callBack) throws Exception {
        try {
            context.getTransaction().begin();
            return callBack.call();
        }catch (Throwable throwable){
            context.getTransaction().rollback();
            throw throwable;
        }finally {
            context.getTransaction().end();
        }
    }

    public static void executeWithoutResult(Context context, CallBackWithoutResult callBack) throws Exception {
        try {
            context.getTransaction().begin();
            callBack.call();
        }catch (Throwable throwable){
            context.getTransaction().rollback();
            throw throwable;
        }finally {
            context.getTransaction().end();
        }
    }

    @FunctionalInterface
    public interface CallBack<T>{
        T call() throws Exception;
    }

    @FunctionalInterface
    public interface CallBackWithoutResult{
        void call() throws Exception;
    }

}
