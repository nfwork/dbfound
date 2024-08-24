package dbfound.test.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.model.adapter.ObjectQueryAdapter;
import com.nfwork.dbfound.model.base.Count;
import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public class QueryTestAdapter implements ObjectQueryAdapter {

    @Override
    public void beforeQuery(Context context, Map<String, Param> params) {
        context.setParamData("before", 1);
    }

    @Override
    public void beforeCount(Context context, Map<String, Param> params, Count count) {
        System.out.println(123);
        context.setParamData("count", 1);
    }

    @Override
    public void afterQuery(Context context, Map<String, Param> params, QueryResponseObject<Object> responseObject) {
        context.setParamData("after", 1);;
    }
}
