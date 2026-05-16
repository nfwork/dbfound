package dbfound.test.core;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.DBFoundInitToken;
import com.nfwork.dbfound.model.dsql.DSqlConfig;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class DBFoundConfigTest {

    @Test
    public void testInitSpringBootWithDocumentInitializesAllConfigAndSkipsDatabaseInit() throws Exception {
        DBFoundConfigSnapshot dbFoundConfigSnapshot = DBFoundConfigSnapshot.take();
        DSqlConfigSnapshot dSqlConfigSnapshot = DSqlConfigSnapshot.take();
        String oldOpenLogJvmValue = System.getProperty("dbfound.system.openLog");
        String oldBasePathJvmValue = System.getProperty("dbfound.web.basePath");
        Document document = DocumentHelper.parseText(getSpringBootConfig());
        try {
            System.setProperty("dbfound.system.openLog", "true");
            System.setProperty("dbfound.web.basePath", "/jvm");
            setStaticField(DBFoundConfig.class, "inited", false);
            DBFoundInitToken initToken = DBFoundConfig.initSpringBoot(document);
            Assert.assertNotNull(initToken);

            Assert.assertTrue(DBFoundConfig.isInited());
            Assert.assertFalse(DBFoundConfig.isOpenLog());
            Assert.assertTrue(DBFoundConfig.isLogWithParamSql());
            Assert.assertTrue(DBFoundConfig.isUnderscoreToCamelCase());
            Assert.assertTrue(DBFoundConfig.isCamelCaseToUnderscore());
            Assert.assertEquals("${@classpath}/spring-model", DBFoundConfig.getModelRootPath());
            Assert.assertTrue(DBFoundConfig.isModelModifyCheck());
            Assert.assertEquals("yyyy/MM/dd", DBFoundConfig.getDateFormat());
            Assert.assertEquals("yyyy/MM/dd HH:mm", DBFoundConfig.getDateTimeFormat());
            Assert.assertEquals("HH:mm", DBFoundConfig.getTimeFormat());
            Assert.assertFalse(DSqlConfig.isCompareIgnoreCase());
            Assert.assertFalse(DSqlConfig.isOpenDSql());
            Assert.assertEquals("GBK", DBFoundConfig.getEncoding());
            Assert.assertFalse(DBFoundConfig.isJsonStringAutoCover());
            Assert.assertEquals(Integer.valueOf(32), DBFoundConfig.getMaxUploadSize());
            Assert.assertEquals("/spring", DBFoundConfig.getBasePath());
            Assert.assertFalse(DBFoundConfig.isOpenSession());
            Assert.assertEquals(Arrays.asList("/query", "/execute"), DBFoundConfig.getApiAllowUrls());
        } finally {
            dbFoundConfigSnapshot.restore();
            dSqlConfigSnapshot.restore();
            restoreProperty("dbfound.system.openLog", oldOpenLogJvmValue);
            restoreProperty("dbfound.web.basePath", oldBasePathJvmValue);
        }
    }

    private static String getSpringBootConfig() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                + "<dbfound>\n"
                + "    <system>\n"
                + "        <openLog>false</openLog>\n"
                + "        <logWithParamSql>true</logWithParamSql>\n"
                + "        <underscoreToCamelCase>true</underscoreToCamelCase>\n"
                + "        <camelCaseToUnderscore>true</camelCaseToUnderscore>\n"
                + "        <modelRootPath>${@classpath}/spring-model</modelRootPath>\n"
                + "        <modelModifyCheck>true</modelModifyCheck>\n"
                + "        <dateFormat>yyyy/MM/dd</dateFormat>\n"
                + "        <dateTimeFormat>yyyy/MM/dd HH:mm</dateTimeFormat>\n"
                + "        <timeFormat>HH:mm</timeFormat>\n"
                + "        <sqlCompareIgnoreCase>false</sqlCompareIgnoreCase>\n"
                + "        <openDSql>false</openDSql>\n"
                + "    </system>\n"
                + "    <database>\n"
                + "        <jdbcConnectionProvide />\n"
                + "    </database>\n"
                + "    <web>\n"
                + "        <encoding>GBK</encoding>\n"
                + "        <jsonStringAutoCover>false</jsonStringAutoCover>\n"
                + "        <maxUploadSize>32</maxUploadSize>\n"
                + "        <basePath>/spring</basePath>\n"
                + "        <openSession>false</openSession>\n"
                + "        <apiAllowUrls>/query, /execute</apiAllowUrls>\n"
                + "    </web>\n"
                + "</dbfound>\n";
    }

    private static Object getStaticField(Class<?> type, String name) throws ReflectiveOperationException {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        return field.get(null);
    }

    private static void setStaticField(Class<?> type, String name, Object value) throws ReflectiveOperationException {
        Field field = type.getDeclaredField(name);
        field.setAccessible(true);
        field.set(null, value);
    }

    private static Object getField(Object target, String name) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void setField(Object target, String name, Object value) throws ReflectiveOperationException {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }

    private static Object newConfigState(Class<?> type) throws ReflectiveOperationException {
        Object config = getStaticField(type, "config");
        java.lang.reflect.Constructor<?> constructor = config.getClass().getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    private static class DBFoundConfigSnapshot {
        private final boolean inited;
        private final String modelRootPath;
        private final boolean underscoreToCamelCase;
        private final boolean camelCaseToUnderscore;
        private final boolean modelModifyCheck;
        private final boolean jsonStringAutoCover;
        private final String dateTimeFormat;
        private final String dateFormat;
        private final String timeFormat;
        private final boolean openSession;
        private final boolean openLog;
        private final boolean logWithParamSql;
        private final String encoding;
        private final Integer maxUploadSize;
        private final String basePath;
        private final List<String> apiAllowUrls;

        private DBFoundConfigSnapshot() {
            this.inited = DBFoundConfig.isInited();
            this.modelRootPath = DBFoundConfig.getModelRootPath();
            this.underscoreToCamelCase = DBFoundConfig.isUnderscoreToCamelCase();
            this.camelCaseToUnderscore = DBFoundConfig.isCamelCaseToUnderscore();
            this.modelModifyCheck = DBFoundConfig.isModelModifyCheck();
            this.jsonStringAutoCover = DBFoundConfig.isJsonStringAutoCover();
            this.dateTimeFormat = DBFoundConfig.getDateTimeFormat();
            this.dateFormat = DBFoundConfig.getDateFormat();
            this.timeFormat = DBFoundConfig.getTimeFormat();
            this.openSession = DBFoundConfig.isOpenSession();
            this.openLog = DBFoundConfig.isOpenLog();
            this.logWithParamSql = DBFoundConfig.isLogWithParamSql();
            this.encoding = DBFoundConfig.getEncoding();
            this.maxUploadSize = DBFoundConfig.getMaxUploadSize();
            this.basePath = DBFoundConfig.getBasePath();
            this.apiAllowUrls = DBFoundConfig.getApiAllowUrls();
        }

        private static DBFoundConfigSnapshot take() {
            return new DBFoundConfigSnapshot();
        }

        private void restore() throws ReflectiveOperationException {
            Object config = newConfigState(DBFoundConfig.class);
            setField(config, "modelRootPath", modelRootPath);
            setField(config, "underscoreToCamelCase", underscoreToCamelCase);
            setField(config, "camelCaseToUnderscore", camelCaseToUnderscore);
            setField(config, "modelModifyCheck", modelModifyCheck);
            setField(config, "jsonStringAutoCover", jsonStringAutoCover);
            setField(config, "dateTimeFormat", dateTimeFormat);
            setField(config, "dateFormat", dateFormat);
            setField(config, "timeFormat", timeFormat);
            setField(config, "openSession", openSession);
            setField(config, "openLog", openLog);
            setField(config, "logWithParamSql", logWithParamSql);
            setField(config, "encoding", encoding);
            setField(config, "maxUploadSize", maxUploadSize);
            setField(config, "basePath", basePath);
            setField(config, "apiAllowUrls", apiAllowUrls);
            setStaticField(DBFoundConfig.class, "config", config);
            setStaticField(DBFoundConfig.class, "inited", inited);
        }
    }

    private static class DSqlConfigSnapshot {
        private final boolean compareIgnoreCase;
        private final boolean openDSql;

        private DSqlConfigSnapshot() {
            this.compareIgnoreCase = DSqlConfig.isCompareIgnoreCase();
            this.openDSql = DSqlConfig.isOpenDSql();
        }

        private static DSqlConfigSnapshot take() {
            return new DSqlConfigSnapshot();
        }

        private void restore() throws ReflectiveOperationException {
            Object config = newConfigState(DSqlConfig.class);
            setField(config, "compareIgnoreCase", compareIgnoreCase);
            setField(config, "openDSql", openDSql);
            setStaticField(DSqlConfig.class, "config", config);
        }
    }
}
