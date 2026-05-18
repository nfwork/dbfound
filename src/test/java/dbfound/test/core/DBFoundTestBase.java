package dbfound.test.core;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.DBFoundInitToken;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class DBFoundTestBase {

    private static DBFoundInitToken dbfoundInitToken;

    @BeforeClass
    public static void initDBFound() {
        dbfoundInitToken = DBFoundConfig.init();
    }

    @AfterClass
    public static void destroyDBFound() {
        if (dbfoundInitToken != null) {
            DBFoundConfig.destroy(dbfoundInitToken);
            dbfoundInitToken = null;
        }
    }
}
