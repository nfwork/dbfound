package com.nfwork.dbfound.util;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.core.Transaction;

public class TransactionUtil {

    public static <T> T execute(Context context, CallBack<T> callBack) throws Exception {
        Transaction transaction = context.getTransaction();
        try {
            transaction.begin();
            T result = callBack.call();
            transaction.commit();
            return result;
        }catch (Throwable throwable){
            transaction.rollback();
            throw throwable;
        }finally {
            transaction.end();
        }
    }

    public static void executeWithoutResult(Context context, CallBackWithoutResult callBack) throws Exception {
        Transaction transaction = context.getTransaction();
        try {
            transaction.begin();
            callBack.call();
            transaction.commit();
        }catch (Throwable throwable){
            transaction.rollback();
            throw throwable;
        }finally {
            transaction.end();
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
