package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.model.bean.SetContextData;
import org.junit.Test;

import java.util.Collections;

public class CaseWhenTest {

    @Test
    public void testWhenOtherwise(){
        Context context = new Context();
        context.setParamData("flag",0);
        ModelEngine.execute(context,"test/case","whenAndOtherwise");
        assert context.getData("outParam.result0",Integer.class) == 0;

        context = new Context();
        context.setParamData("flag",1);
        ModelEngine.execute(context,"test/case","whenAndOtherwise");
        assert context.getData("outParam.result0",Integer.class) == null;
        assert context.getData("outParam.result1",Integer.class) == 1;

        context = new Context();
        context.setParamData("flag",2);
        ModelEngine.execute(context,"test/case","whenAndOtherwise");
        assert context.getData("outParam.result0",Integer.class) == null;
        assert context.getData("outParam.result1",Integer.class) == 1;
    }

    @Test
    public void testCaseAndWhenOtherwise(){
        Context context = new Context();
        context.setParamData("flag",0);
        ModelEngine.execute(context,"test/case","caseAndWhenAndOtherwise");
        assert context.getData("outParam.result0",Integer.class) == 0;

        context = new Context();
        context.setParamData("flag",1);
        ModelEngine.execute(context,"test/case","caseAndWhenAndOtherwise");
        assert context.getData("outParam.result0",Integer.class) == null;
        assert context.getData("outParam.result1",Integer.class) == 1;

        context = new Context();
        context.setParamData("flag",2);
        ModelEngine.execute(context,"test/case","caseAndWhenAndOtherwise");
        assert context.getData("outParam.result0",Integer.class) == null;
        assert context.getData("outParam.result1",Integer.class) == null;
        assert context.getData("outParam.result2",Integer.class) == 2;

        context = new Context();
        context.setParamData("flag",3);
        ModelEngine.execute(context,"test/case","caseAndWhenAndOtherwise");
        assert context.getData("outParam.result0",Integer.class) == null;
        assert context.getData("outParam.result1",Integer.class) == null;
        assert context.getData("outParam.result2",Integer.class) == null;
        assert context.getData("outParam.result3",Integer.class) == 3;
    }

    @Test
    public void testSetContextDataFromSourcePath(){
        Context context = new Context();
        context.setParamData("source","sourceValue");
        ModelEngine.execute(context,"test/case","setContextDataFromSourcePath");
        assert "sourceValue".equals(context.getData("outParam.result"));
    }

    @Test
    public void testSetContextDataWithRelativePath(){
        Context context = new Context();
        context.setCurrentPath("outParam");

        SetContextData setContextData = new SetContextData();
        setContextData.setTargetPath("result");
        setContextData.setValue("relativeValue");
        setContextData.execute(context, Collections.emptyMap(), "_default");

        assert "relativeValue".equals(context.getData("outParam.result"));
    }

    @Test
    public void testSetContextDataWithEmptyValue(){
        Context context = new Context();

        SetContextData setContextData = new SetContextData();
        setContextData.setTargetPath("outParam.emptyValue");
        setContextData.setValue("");
        setContextData.execute(context, Collections.emptyMap(), "_default");

        assert "".equals(context.getData("outParam.emptyValue"));
    }

    @Test
    public void testSetContextDataWithIndex(){
        Context context = new Context();
        context.setCurrentPath("param.items[1]");
        context.setData("param.items[1].source","indexedValue");

        SetContextData setContextData = new SetContextData();
        setContextData.setTargetPath("outParam.results[index]");
        setContextData.setSourcePath("source");
        setContextData.execute(context, Collections.emptyMap(), "_default");

        assert "indexedValue".equals(context.getData("outParam.results[1]"));
    }
}
