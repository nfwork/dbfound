package dbfound.test.core;

import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import org.junit.Assert;
import org.junit.Test;

public class ConnectionProvideManagerTest {

    @Test
    public void testGetConnectionProvideWhenNotInitialized() {
        try {
            ConnectionProvideManager.getConnectionProvide("_default");
            Assert.fail("Expected DBFoundRuntimeException");
        } catch (DBFoundRuntimeException exception) {
            Assert.assertEquals("dbfound is not initialized, please init dbfound before using ConnectionProvide", exception.getMessage());
        }
    }
}
