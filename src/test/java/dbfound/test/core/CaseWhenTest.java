package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.model.ModelEngine;
import org.junit.Test;

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
}
