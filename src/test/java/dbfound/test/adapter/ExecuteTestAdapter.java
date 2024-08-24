package dbfound.test.adapter;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.adapter.ExecuteAdapter;
import com.nfwork.dbfound.model.bean.Param;

import java.util.Map;

public class ExecuteTestAdapter implements ExecuteAdapter {

    @Override
    public void beforeExecute(Context context, Map<String, Param> params) {
        context.setParamData("before",1);
    }

    @Override
    public void afterExecute(Context context, Map<String, Param> params) {
        context.setParamData("after",1);
    }
}
