package dbfound.test.core;

import com.nfwork.dbfound.db.dialect.*;
import com.nfwork.dbfound.model.base.DataType;
import com.nfwork.dbfound.model.bean.Param;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DialectTest {

    // ==================== AbstractSqlDialect params binding ====================

    @Test
    public void testAbstractDialect_paramsBinding() {
        MySqlDialect dialect = new MySqlDialect();
        Map<String, Param> params = new HashMap<>();
        String result = dialect.getPagerSql("select * from users", 10, 20, params);

        Assert.assertEquals("select * from users limit ${@start}, ${@limit}", result);
        Assert.assertEquals(20L, params.get("start").getValue());
        Assert.assertEquals(DataType.NUMBER, params.get("start").getDataType());
        Assert.assertEquals(10, params.get("limit").getValue());
        Assert.assertEquals(DataType.NUMBER, params.get("limit").getDataType());
    }

    // ==================== MySqlDialect ====================

    @Test
    public void testMySql_pagerSql() {
        MySqlDialect dialect = new MySqlDialect();
        String result = dialect.getPagerSql("select * from users", "${@limit}", "${@start}");
        Assert.assertEquals("select * from users limit ${@start}, ${@limit}", result);
    }

    @Test
    public void testMySql_whenSql() {
        MySqlDialect dialect = new MySqlDialect();
        Assert.assertEquals("select 1=1", dialect.getWhenSql("1=1"));
    }

    // ==================== SqlServerDialect ====================

    @Test
    public void testSqlServer_pagerSql_withOrderBy() {
        SqlServerDialect dialect = new SqlServerDialect();
        String sql = "select id, name from users order by id desc";
        String result = dialect.getPagerSql(sql, "${@limit}", "${@start}");

        Assert.assertEquals(
                "select * from (select row_number() over(order by id desc) d_rm, id, name from users ) v"
                        + " where d_rm <= ${@start} + ${@limit} and d_rm > ${@start}",
                result);
    }

    @Test
    public void testSqlServer_pagerSql_withoutOrderBy() {
        SqlServerDialect dialect = new SqlServerDialect();
        String sql = "select id, name from users";
        String result = dialect.getPagerSql(sql, "${@limit}", "${@start}");

        Assert.assertEquals(
                "select * from (select row_number() over(order by getdate()) d_rm, id, name from users) v"
                        + " where d_rm <= ${@start} + ${@limit} and d_rm > ${@start}",
                result);
    }

    @Test
    public void testSqlServer_whenSql() {
        SqlServerDialect dialect = new SqlServerDialect();
        Assert.assertEquals("select case when 1=1 then 1 else 0 end ", dialect.getWhenSql("1=1"));
    }

    // ==================== SqlServerDialectV2 ====================

    @Test
    public void testSqlServerV2_pagerSql_withOrderBy() {
        SqlServerDialectV2 dialect = new SqlServerDialectV2();
        String sql = "select * from users order by id";
        String result = dialect.getPagerSql(sql, "${@limit}", "${@start}");

        Assert.assertEquals("select * from users order by id offset ${@start} rows fetch next ${@limit} rows only", result);
    }

    @Test
    public void testSqlServerV2_pagerSql_withoutOrderBy() {
        SqlServerDialectV2 dialect = new SqlServerDialectV2();
        String sql = "select * from users";
        String result = dialect.getPagerSql(sql, "${@limit}", "${@start}");

        Assert.assertEquals("select * from users order by (select null) offset ${@start} rows fetch next ${@limit} rows only", result);
    }

    @Test
    public void testSqlServerV2_pagerSql_orderByCaseInsensitive() {
        SqlServerDialectV2 dialect = new SqlServerDialectV2();
        String sql = "select * from users ORDER BY id";
        String result = dialect.getPagerSql(sql, "${@limit}", "${@start}");

        Assert.assertEquals("select * from users ORDER BY id offset ${@start} rows fetch next ${@limit} rows only", result);
    }

    @Test
    public void testSqlServerV2_whenSql() {
        SqlServerDialectV2 dialect = new SqlServerDialectV2();
        Assert.assertEquals("select case when 1=1 then 1 else 0 end ", dialect.getWhenSql("1=1"));
    }

    // ==================== OracleDialect ====================

    @Test
    public void testOracle_pagerSql() {
        OracleDialect dialect = new OracleDialect();
        String sql = "select id, name from users";
        String result = dialect.getPagerSql(sql, "${@limit}", "${@start}");

        Assert.assertEquals(
                "select * from (select v.*, rownum d_rm from (select id, name from users) v"
                        + " where rownum <= ${@start} + ${@limit}) where d_rm > ${@start}",
                result);
    }

    @Test
    public void testOracle_whenSql() {
        OracleDialect dialect = new OracleDialect();
        Assert.assertEquals("select 1 from dual where 1=1", dialect.getWhenSql("1=1"));
    }

    // ==================== PostgreSqlDialect ====================

    @Test
    public void testPostgreSql_pagerSql() {
        PostgreSqlDialect dialect = new PostgreSqlDialect();
        String sql = "select * from users";
        String result = dialect.getPagerSql(sql, "${@limit}", "${@start}");

        Assert.assertEquals("select * from users limit ${@limit} offset ${@start}", result);
    }

    @Test
    public void testPostgreSql_whenSql() {
        PostgreSqlDialect dialect = new PostgreSqlDialect();
        Assert.assertEquals("select case when 1=1 then 1 else 0 end", dialect.getWhenSql("1=1"));
    }

    // ==================== ClickHouseDialect ====================

    @Test
    public void testClickHouse_pagerSql() {
        ClickHouseDialect dialect = new ClickHouseDialect();
        String sql = "select * from users";
        String result = dialect.getPagerSql(sql, "${@limit}", "${@start}");

        Assert.assertEquals("select * from users limit ${@limit} offset ${@start}", result);
    }

    @Test
    public void testClickHouse_whenSql() {
        ClickHouseDialect dialect = new ClickHouseDialect();
        Assert.assertEquals("select 1=1", dialect.getWhenSql("1=1"));
    }

    // ==================== DialectFactory ====================

    @Test
    public void testDialectFactory_shortName() {
        SqlDialect dialect = DialectFactory.createDialect("MySqlDialect");
        Assert.assertTrue(dialect instanceof MySqlDialect);
    }

    @Test
    public void testDialectFactory_fullClassName() {
        SqlDialect dialect = DialectFactory.createDialect("com.nfwork.dbfound.db.dialect.OracleDialect");
        Assert.assertTrue(dialect instanceof OracleDialect);
    }

    @Test(expected = Exception.class)
    public void testDialectFactory_invalidDialect() {
        DialectFactory.createDialect("NonExistDialect");
    }
}
