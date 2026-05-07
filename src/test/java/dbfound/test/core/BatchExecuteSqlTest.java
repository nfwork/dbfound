package dbfound.test.core;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.bean.BatchExecuteSql;
import org.junit.Test;

import java.util.Collections;

public class BatchExecuteSqlTest {

    @Test
    public void testInvalidBatchSize() {
        assertInvalidBatchSize(null);
        assertInvalidBatchSize(0);
        assertInvalidBatchSize(-1);
    }

    private void assertInvalidBatchSize(Integer batchSize) {
        BatchExecuteSql sql = new BatchExecuteSql();
        sql.setSourcePath("users");
        sql.setBatchSize(batchSize);
        sql.setSql("insert into users(id) values #BATCH_TEMPLATE_BEGIN# (${@id}) #BATCH_TEMPLATE_END#");
        sql.doEndTag();

        try {
            sql.execute(null, Collections.emptyMap(), null);
            assert false;
        } catch (DBFoundRuntimeException exception) {
            assert exception.getMessage().equals("BatchExecuteSql attribute batchSize must be greater than 0");
        }
    }
}
