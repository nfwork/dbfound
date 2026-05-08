package dbfound.test.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.QueryResponseObject;
import com.nfwork.dbfound.model.adapter.ObjectQueryAdapter;
import com.nfwork.dbfound.model.bean.Param;

import java.util.*;

public class HandleQueryTestAdapter implements ObjectQueryAdapter {

    @Override
    public QueryResponseObject<Object> handleQuery(Context context, Map<String, Param> params) {
        Param cacheParam = params.get("use_cache");
        if (cacheParam != null && "true".equals(cacheParam.getStringValue())) {
            context.setParamData("handled", 1);
            QueryResponseObject<Object> response = new QueryResponseObject<>();
            Map<String, Object> row = new HashMap<>();
            row.put("user_id", 0);
            row.put("user_name", "cached_user");
            response.setDatas(Collections.singletonList(row));
            response.setSuccess(true);
            response.setMessage("from cache");
            return response;
        }
        return null;
    }

    @Override
    public void beforeQuery(Context context, Map<String, Param> params) {
        context.setParamData("before", 1);
    }

    @Override
    public void afterQuery(Context context, Map<String, Param> params, QueryResponseObject<Object> responseObject) {
        context.setParamData("after", 1);
    }
}
