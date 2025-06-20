package com.nfwork.dbfound.model.dsql;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.DSqlNotSupportException;
import com.nfwork.dbfound.model.dsql.function.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FunctionResolver extends DSqlValueResolver {

    protected static final Map<String, DSqlFunction> functionMap = new ConcurrentHashMap<>();

    static {
        register("if",new If());
        register("ifnull",new IfNull());
        register("isnull", new IsNull());
        register("nvl",new Nvl());
        register("trim",new Trim());
        register("length", new Length());
        register("lengthb",new LengthB());
        register("char_length", new CharLength());
        register("substring_index",new SubstringIndex());
        register("substring",new Substring());
        register("substr",functionMap.get("substring"));
        register("concat",new Concat());
        register("find_in_set",new FindInSet());
        register("instr",new Instr());
        register("locate",new Locate());
        register("upper",new Upper());
        register("lower",new Lower());
    }
    static void register(String functionName, DSqlFunction function) {
        DSqlFunction dSqlFunction = functionMap.get(functionName);
        if (dSqlFunction != null) {
            if(!dSqlFunction.getClass().isAssignableFrom(function.getClass())) {
                throw new DBFoundRuntimeException("Function '" + functionName + "' is already registered by " + functionMap.get(functionName).getClass().getName());
            }
        }
        functionMap.put(functionName, function);
    }

    @Override
    public Object getValue(Expression expression, List<Object> param, String provideName, Context context) {
        ConnectionProvide provide = ConnectionProvideManager.getConnectionProvide(provideName);
        Function function = (Function)expression;
        if(function.getMultipartName().size()!=1){
            throw new DSqlNotSupportException();
        }

        String functionName = function.getMultipartName().get(0).toLowerCase();
        DSqlFunction dSqlFunction = functionMap.get(functionName);
        if(dSqlFunction == null){
            throw new DSqlNotSupportException();
        }
        if (!dSqlFunction.isSupported(provide.getSqlDialect())){
            throw new DSqlNotSupportException();
        }

        List<Object> fpList = new ArrayList<>();
        if(function.getParameters()!=null) {
            List<Expression> list = function.getParameters().getExpressions();
            if (list != null) {
                for (Expression exp : list) {
                    Object value = DSqlEngine.getExpressionValue(exp, param, provideName, context);
                    fpList.add(value);
                }
            }
        }
        return dSqlFunction.apply(fpList,provide.getSqlDialect());
    }

}