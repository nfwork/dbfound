package com.nfwork.dbfound.model.reflector;

import java.lang.reflect.InvocationTargetException;

public interface Invoker {
  Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

  Class<?> getType();
}
