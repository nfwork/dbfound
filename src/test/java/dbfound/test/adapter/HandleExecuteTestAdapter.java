package dbfound.test.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.dto.ResponseObject;
import com.nfwork.dbfound.model.adapter.ExecuteAdapter;
import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public class HandleExecuteTestAdapter implements ExecuteAdapter {

    @Override
    public ResponseObject handleExecute(Context context, Map<String, Param> params) {
        Param skipParam = params.get("skip_execute");
        if (skipParam != null && "true".equals(skipParam.getStringValue())) {
            context.setParamData("handled", 1);
            ResponseObject response = new ResponseObject();
            response.setSuccess(true);
            response.setMessage("handled");
            return response;
        }
        return null;
    }

    @Override
    public void beforeExecute(Context context, Map<String, Param> params) {
        context.setParamData("before", 1);
    }

    @Override
    public void afterExecute(Context context, Map<String, Param> params) {
        context.setParamData("after", 1);
    }
}
