package dbfount.test.core;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.junit.Test;

import java.util.Iterator;

public class CountTest {

    @Test
    public void testCount() throws Exception{
        String sql = "select count(1) from sys_user u left join sys_role r on u.role_id = role_id where r.role=${@abc}";

        int whereLoc =sql.toLowerCase().lastIndexOf(" where ");
        String whereSql = "";
        String selectSql = sql;
        if(whereLoc> 0) {
            selectSql = sql.substring(0, whereLoc);
            whereSql = sql.substring(whereLoc);
        }

        Select select = (Select) CCJSqlParserUtil.parse(selectSql);
        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

        Iterator<Join> joinIterator = plainSelect.getJoins().iterator();
        while (joinIterator.hasNext()) {
            Join join = joinIterator.next();
            String name = join.getRightItem().getAlias().getName() + ".";
            if (!whereSql.contains(name)) {
                joinIterator.remove();
            }
        }
        System.out.println(select + whereSql);
    }
}
