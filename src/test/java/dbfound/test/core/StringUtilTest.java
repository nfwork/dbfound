package dbfound.test.core;

import com.nfwork.dbfound.util.StringUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StringUtilTest {

    // ==================== sqlFullTrim ====================

    @Test
    public void testSqlFullTrim_nullAndEmpty() {
        Assert.assertNull(StringUtil.sqlFullTrim(null));
        Assert.assertEquals("", StringUtil.sqlFullTrim(""));
    }

    @Test
    public void testSqlFullTrim_collapseWhitespace() {
        String sql = "select  *  \t from   \n  user   where   id = 1";
        Assert.assertEquals("select * from user where id = 1", StringUtil.sqlFullTrim(sql));
    }

    @Test
    public void testSqlFullTrim_leadingAndTrailingWhitespace() {
        Assert.assertEquals("select 1", StringUtil.sqlFullTrim("  select 1  "));
    }

    @Test
    public void testSqlFullTrim_lineComment() {
        String sql = "select * -- this is a comment\nfrom user";
        Assert.assertEquals("select * from user", StringUtil.sqlFullTrim(sql));
    }

    @Test
    public void testSqlFullTrim_multiLineComment() {
        String sql = "select * /* block comment */ from user";
        Assert.assertEquals("select * from user", StringUtil.sqlFullTrim(sql));
    }

    @Test
    public void testSqlFullTrim_preserveStringWithSpaces() {
        String sql = "select * from user where name = 'hello   world'";
        Assert.assertEquals("select * from user where name = 'hello   world'", StringUtil.sqlFullTrim(sql));
    }

    @Test
    public void testSqlFullTrim_preserveDoubleQuotedString() {
        String sql = "select * from \"my   table\"  where id = 1";
        Assert.assertEquals("select * from \"my   table\" where id = 1", StringUtil.sqlFullTrim(sql));
    }

    @Test
    public void testSqlFullTrim_carriageReturn() {
        String sql = "select *\r\nfrom  user";
        Assert.assertEquals("select * from user", StringUtil.sqlFullTrim(sql));
    }

    @Test
    public void testSqlFullTrim_consecutiveBackslashBeforeQuote() {
        // 'hello\\' — 两个反斜杠（自身转义），单引号正常闭合，后面的 from 应正常处理空白
        String sql = "select 'hello\\\\'   from user";
        Assert.assertEquals("select 'hello\\\\' from user", StringUtil.sqlFullTrim(sql));
    }

    @Test
    public void testSqlFullTrim_lineCommentNoTrailingSpace() {
        // -- 后面必须跟空白才是注释，--x 不算注释
        String sql = "select a--b from user";
        Assert.assertEquals("select a--b from user", StringUtil.sqlFullTrim(sql));
    }

    // ==================== getParamSql ====================

    @Test
    public void testGetParamSql_basicTypes() {
        String sql = "select * from user where name = ? and age = ? and active = ? and email = ?";
        List<Object> params = Arrays.asList("John", 25, true, null);
        String result = StringUtil.getParamSql(sql, params);
        Assert.assertEquals("select * from user where name = 'John' and age = 25 and active = true and email = null", result);
    }

    @Test
    public void testGetParamSql_stringWithSingleQuote() {
        String sql = "select * from user where name = ?";
        List<Object> params = Collections.singletonList("O'Reilly");
        String result = StringUtil.getParamSql(sql, params);
        Assert.assertEquals("select * from user where name = 'O\\'Reilly'", result);
    }

    @Test
    public void testGetParamSql_stringWithBackslash() {
        String sql = "select * from user where path = ?";
        List<Object> params = Collections.singletonList("C:\\Users");
        String result = StringUtil.getParamSql(sql, params);
        Assert.assertEquals("select * from user where path = 'C:\\\\Users'", result);
    }

    @Test
    public void testGetParamSql_questionMarkInsideQuotes() {
        String sql = "select * from user where name = '?' and age = ?";
        List<Object> params = Collections.singletonList(18);
        String result = StringUtil.getParamSql(sql, params);
        Assert.assertEquals("select * from user where name = '?' and age = 18", result);
    }

    @Test
    public void testGetParamSql_noParams() {
        String sql = "select * from user";
        String result = StringUtil.getParamSql(sql, Collections.emptyList());
        Assert.assertEquals("select * from user", result);
    }

    @Test
    public void testGetParamSql_paramOverflow() {
        String sql = "select * from user where id = ? and name = ?";
        List<Object> params = Collections.singletonList(1);
        String result = StringUtil.getParamSql(sql, params);
        Assert.assertEquals("select * from user where id = 1 and name = ?", result);
    }

    @Test
    public void testGetParamSql_consecutiveBackslashBeforeQuote() {
        // SQL 中 'value\\' 后面的 ? 应被正常替换
        String sql = "select * from user where a = 'val\\\\' and b = ?";
        List<Object> params = Collections.singletonList(42);
        String result = StringUtil.getParamSql(sql, params);
        Assert.assertEquals("select * from user where a = 'val\\\\' and b = 42", result);
    }

    @Test
    public void testGetParamSql_plainStringNoEscape() {
        String sql = "select * from user where name = ?";
        List<Object> params = Collections.singletonList("hello");
        String result = StringUtil.getParamSql(sql, params);
        Assert.assertEquals("select * from user where name = 'hello'", result);
    }
}
