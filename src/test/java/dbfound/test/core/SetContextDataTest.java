package dbfound.test.core;

import com.nfwork.dbfound.core.Context;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.exception.ParamNotFoundException;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.util.CollectionUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SetContextDataTest {

    @Test
    public void testSetContextData() {
        Context context = new Context();
        context.setParamData("fromParam", "param-value");
        context.setParamData("source.value", "source-value");
        context.setParamData("items", CollectionUtil.asList(
                item("first", "first-value"),
                item("second", "second-value")));
        context.setParamData("labels", CollectionUtil.asList(
                label("first-label"),
                label("second-label")));

        ModelEngine.execute(context, "test/execute", "setContextData");

        Assert.assertEquals("literal-value", context.getData("outParam.literal"));
        Assert.assertEquals("param-value", context.getData("outParam.copiedParam"));
        Assert.assertEquals("source-value", context.getData("outParam.copiedSource"));
        Assert.assertEquals("prefix-source-value-suffix", context.getData("outParam.templated"));
        Assert.assertEquals("first-value", context.getData("param.items[0].copied"));
        Assert.assertEquals("second-value", context.getData("param.items[1].copied"));
        Assert.assertEquals("first", context.getData("outParam.itemName[0]"));
        Assert.assertEquals("second", context.getData("outParam.itemName[1]"));
        Assert.assertEquals("label=first-label", context.getData("outParam.label[0]"));
        Assert.assertEquals("label=second-label", context.getData("outParam.label[1]"));
    }

    @Test
    public void testNameCannotBeNull() {
        try {
            ModelEngine.execute(new Context(), "test/execute", "setContextDataMissingName");
            Assert.fail("Expected DBFoundRuntimeException");
        } catch (DBFoundRuntimeException exception) {
            Assert.assertEquals("attribute name cannot be null in setContextData tag", exception.getMessage());
        }
    }

    @Test
    public void testPathCannotBeNull() {
        try {
            ModelEngine.execute(new Context(), "test/execute", "setContextDataMissingPath");
            Assert.fail("Expected DBFoundRuntimeException");
        } catch (DBFoundRuntimeException exception) {
            Assert.assertEquals("path cannot be null in setContextData tag", exception.getMessage());
        }
    }

    @Test
    public void testParamMustBeDefined() {
        try {
            ModelEngine.execute(new Context(), "test/execute", "setContextDataMissingParam");
            Assert.fail("Expected ParamNotFoundException");
        } catch (ParamNotFoundException exception) {
            Assert.assertEquals("param: notDefined not defined", exception.getMessage());
        }
    }

    @Test
    public void testIndexMustExistInCurrentPath() {
        try {
            ModelEngine.execute(new Context(), "test/execute", "setContextDataMissingIndex");
            Assert.fail("Expected DBFoundRuntimeException");
        } catch (DBFoundRuntimeException exception) {
            Assert.assertEquals("SetContextData cannot find index in currentPath", exception.getMessage());
        }
    }

    private Map<String, String> item(String name, String value) {
        Map<String, String> item = new HashMap<>();
        item.put("name", name);
        item.put("value", value);
        return item;
    }

    private Map<String, String> label(String label) {
        Map<String, String> item = new HashMap<>();
        item.put("label", label);
        return item;
    }
}
