package dbfound.test.core;

import com.nfwork.dbfound.model.base.Count;
import com.nfwork.dbfound.model.bean.Param;
import com.nfwork.dbfound.model.bean.Query;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class QueryGetCountTest {

    private Method getCountMethod;
    private Method sqlMatchMethod;
    private Query query;

    @Before
    public void setUp() throws Exception {
        query = new Query();
        getCountMethod = Query.class.getDeclaredMethod("getCount", String.class, Map.class);
        getCountMethod.setAccessible(true);
        sqlMatchMethod = Query.class.getDeclaredMethod("sqlMatch", char[].class, int.class, char[].class);
        sqlMatchMethod.setAccessible(true);
    }

    private Count invokeGetCount(String sql) throws Exception {
        Map<String, Param> params = new HashMap<>();
        return (Count) getCountMethod.invoke(query, sql, params);
    }

    private boolean invokeSqlMatch(char[] sqls, int index, char[] match) throws Exception {
        return (boolean) sqlMatchMethod.invoke(query, sqls, index, match);
    }

    // ==================== sqlMatch 测试 ====================

    @Test
    public void testSqlMatch_lowercase() throws Exception {
        char[] sql = "select from table1 ".toCharArray();
        assert invokeSqlMatch(sql, 7, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_uppercase() throws Exception {
        char[] sql = "select FROM table1 ".toCharArray();
        assert invokeSqlMatch(sql, 7, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_mixedCase() throws Exception {
        char[] sql = "select FrOm table1 ".toCharArray();
        assert invokeSqlMatch(sql, 7, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_followedByNewline() throws Exception {
        char[] sql = "select from\ntable1 ".toCharArray();
        assert invokeSqlMatch(sql, 7, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_followedByTab() throws Exception {
        char[] sql = "select from\ttable1 ".toCharArray();
        assert invokeSqlMatch(sql, 7, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_followedByParen() throws Exception {
        char[] sql = "select from(select 1) ".toCharArray();
        assert invokeSqlMatch(sql, 7, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_notMatchWhenPartOfWord() throws Exception {
        char[] sql = "select from_table as t ".toCharArray();
        assert !invokeSqlMatch(sql, 7, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_notMatchWhenFollowedByUnderscore() throws Exception {
        char[] sql = "select order_count from t ".toCharArray();
        assert !invokeSqlMatch(sql, 7, "order".toCharArray());
    }

    @Test
    public void testSqlMatch_atEndOfString() throws Exception {
        char[] sql = "select from".toCharArray();
        assert !invokeSqlMatch(sql, 7, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_indexOutOfBounds() throws Exception {
        char[] sql = "abc".toCharArray();
        assert !invokeSqlMatch(sql, 10, "from".toCharArray());
    }

    @Test
    public void testSqlMatch_distinct() throws Exception {
        char[] sql = "select distinct col from t ".toCharArray();
        assert invokeSqlMatch(sql, 7, "distinct".toCharArray());
    }

    // ==================== getCount 基础测试 ====================

    @Test
    public void testGetCount_simpleSelect() throws Exception {
        Count count = invokeGetCount("select * from table1 where id = 1");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from table1 where id = 1");
    }

    @Test
    public void testGetCount_withOrderBy() throws Exception {
        Count count = invokeGetCount("select * from table1 where id > 0 order by id");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from table1 where id > 0 ");
    }

    @Test
    public void testGetCount_noFrom() throws Exception {
        Count count = invokeGetCount("select 1 as value ");
        assert !count.isExecuteCount();
    }

    @Test
    public void testGetCount_caseInsensitive() throws Exception {
        Count count = invokeGetCount("SELECT * FROM table1 WHERE id = 1");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) FROM table1 WHERE id = 1");
    }

    // ==================== getCount GROUP BY 测试 ====================

    @Test
    public void testGetCount_withGroupBy() throws Exception {
        Count count = invokeGetCount("select dept, count(*) from emp group by dept ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from (select dept, count(*) from emp group by dept ) v");
    }

    @Test
    public void testGetCount_withGroupByAndOrderBy() throws Exception {
        Count count = invokeGetCount("select dept, count(*) as cnt from emp group by dept order by cnt ");
        assert count.isExecuteCount();
        String expected = "select count(1) from (select dept, count(*) as cnt from emp group by dept ) v";
        assert count.getCountSql().equals(expected) : "actual: " + count.getCountSql();
    }

    @Test
    public void testGetCount_withGroupByHaving() throws Exception {
        Count count = invokeGetCount("select dept, count(*) as cnt from emp group by dept having cnt > 5 ");
        assert count.isExecuteCount();
        String expected = "select count(1) from (select dept, count(*) as cnt from emp group by dept having cnt > 5 ) v";
        assert count.getCountSql().equals(expected) : "actual: " + count.getCountSql();
    }

    @Test
    public void testGetCount_withGroupByHavingAndOrderBy() throws Exception {
        Count count = invokeGetCount(
                "select dept, count(*) as cnt from emp group by dept having cnt > 5 order by dept ");
        assert count.isExecuteCount();
        String expected = "select count(1) from (select dept, count(*) as cnt from emp group by dept having cnt > 5 ) v";
        assert count.getCountSql().equals(expected) : "actual: " + count.getCountSql();
    }

    // ==================== getCount DISTINCT 测试 ====================

    @Test
    public void testGetCount_withDistinct() throws Exception {
        Count count = invokeGetCount("select distinct dept from emp ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from (select distinct dept from emp ) v");
    }

    @Test
    public void testGetCount_withDistinctAndOrderBy() throws Exception {
        Count count = invokeGetCount("select distinct dept from emp order by dept ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from (select distinct dept from emp ) v");
    }

    // ==================== getCount UNION 测试 ====================

    @Test
    public void testGetCount_withUnion() throws Exception {
        Count count = invokeGetCount("select id from t1 union select id from t2 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from (select id from t1 union select id from t2 ) v");
    }

    @Test
    public void testGetCount_withUnionAndOrderBy() throws Exception {
        Count count = invokeGetCount("select id from t1 union select id from t2 order by id ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from (select id from t1 union select id from t2 ) v");
    }

    // ==================== getCount 子查询测试 ====================

    @Test
    public void testGetCount_fromInSubquery() throws Exception {
        Count count = invokeGetCount("select a, (select count(*) from t2) as cnt from t1 where a > 0 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from t1 where a > 0 ");
    }

    @Test
    public void testGetCount_orderByInSubquery() throws Exception {
        Count count = invokeGetCount(
                "select * from (select * from t1 order by id) v where v.id > 0 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from (select * from t1 order by id) v where v.id > 0 ");
    }

    @Test
    public void testGetCount_groupByInSubquery() throws Exception {
        Count count = invokeGetCount(
                "select * from (select dept, count(*) from emp group by dept) v ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from (select dept, count(*) from emp group by dept) v ");
    }

    @Test
    public void testGetCount_withCTE() throws Exception {
        Count count = invokeGetCount(
                "with cte as (select * from t1) select * from cte where id > 0 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from cte where id > 0 ");
    }

    // ==================== getCount 引号转义测试 ====================

    @Test
    public void testGetCount_fromInsideSingleQuotes() throws Exception {
        Count count = invokeGetCount("select * from t1 where name = 'from order' ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from t1 where name = 'from order' ");
    }

    @Test
    public void testGetCount_fromInsideDoubleQuotes() throws Exception {
        Count count = invokeGetCount("select * from t1 where \"from\" = 1 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from t1 where \"from\" = 1 ");
    }

    @Test
    public void testGetCount_doubledSingleQuoteEscape() throws Exception {
        Count count = invokeGetCount("select * from t1 where name = 'it''s from order' ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from t1 where name = 'it''s from order' ");
    }

    @Test
    public void testGetCount_doubledDoubleQuoteEscape() throws Exception {
        Count count = invokeGetCount("select * from t1 where \"col\"\"name\" = 'test' ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from t1 where \"col\"\"name\" = 'test' ");
    }

    @Test
    public void testGetCount_backslashEscape() throws Exception {
        Count count = invokeGetCount("select * from t1 where name = 'test\\'s' ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from t1 where name = 'test\\'s' ");
    }

    // ==================== getCount 列名/表名是关键字的测试 ====================

    @Test
    public void testGetCount_orderAsColumnName() throws Exception {
        Count count = invokeGetCount("select \"order\" from t1 where id > 0 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from t1 where id > 0 ");
    }

    @Test
    public void testGetCount_orderColumnWithoutQuoting() throws Exception {
        Count count = invokeGetCount("select t1.order from t1 order by t1.order ");
        assert count.isExecuteCount();
        String sql = count.getCountSql();
        assert sql.equals("select count(1) from t1 ") : "actual: " + sql;
    }

    @Test
    public void testGetCount_groupAsColumnBeforeFrom() throws Exception {
        Count count = invokeGetCount("select user_group from t1 group by user_group ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from (select user_group from t1 group by user_group ) v");
    }

    @Test
    public void testGetCount_orderBeforeFromShouldBeIgnored() throws Exception {
        Count count = invokeGetCount("select order from orders order by order ");
        assert count.isExecuteCount();
        String sql = count.getCountSql();
        assert sql.equals("select count(1) from orders ") : "actual: " + sql;
    }

    @Test
    public void testGetCount_groupBeforeFromShouldBeIgnored() throws Exception {
        Count count = invokeGetCount("select \"group\" from groups ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from groups ");
    }

    // ==================== getCount 复杂 SQL 测试 ====================

    @Test
    public void testGetCount_multipleJoins() throws Exception {
        Count count = invokeGetCount(
                "select a.id, b.name from t1 a inner join t2 b on a.id = b.id where a.id > 0 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals(
                "select count(1) from t1 a inner join t2 b on a.id = b.id where a.id > 0 ");
    }

    @Test
    public void testGetCount_windowFunction() throws Exception {
        Count count = invokeGetCount(
                "select id, ROW_NUMBER() OVER (order by id) as rn from t1 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from t1 ");
    }

    @Test
    public void testGetCount_windowFunctionWithOuterOrderBy() throws Exception {
        Count count = invokeGetCount(
                "select id, ROW_NUMBER() OVER (order by id) as rn from t1 order by id ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from t1 ");
    }

    @Test
    public void testGetCount_closingParenBeforeFrom() throws Exception {
        Count count = invokeGetCount(
                "select * from (select 1)from t1 where id > 0 ");
        assert count.isExecuteCount();
    }

    @Test
    public void testGetCount_tabSeparated() throws Exception {
        Count count = invokeGetCount("select\t*\tfrom\tt1\twhere\tid > 0 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from\tt1\twhere\tid > 0 ");
    }

    @Test
    public void testGetCount_newlineSeparated() throws Exception {
        Count count = invokeGetCount("select\n*\nfrom\nt1\nwhere\nid > 0 ");
        assert count.isExecuteCount();
        assert count.getCountSql().equals("select count(1) from\nt1\nwhere\nid > 0 ");
    }
}
