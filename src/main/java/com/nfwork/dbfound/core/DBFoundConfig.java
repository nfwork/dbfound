package com.nfwork.dbfound.core;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

import jakarta.servlet.ServletContext;
import javax.sql.DataSource;

import com.nfwork.dbfound.model.dsql.DSqlConfig;
import com.nfwork.dbfound.model.reflector.Reflector;
import com.nfwork.dbfound.util.CollectionUtil;
import com.nfwork.dbfound.web.ExceptionHandlerFacade;
import com.nfwork.dbfound.web.ListenerFacade;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.DataSourceConnectionProvide;
import com.nfwork.dbfound.db.JdbcConnectionProvide;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.util.StringUtil;
import com.nfwork.dbfound.web.action.ActionEngine;
import com.nfwork.dbfound.web.DispatcherFilter;
import com.nfwork.dbfound.web.InterceptorFacade;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class DBFoundConfig {

	public static final String VERSION = "4.4.0" ;

	private final static List<DataSourceConnectionProvide> dsp = new ArrayList<>();

	public static final String CLASSPATH = "${@classpath}";
	public static final String PROJECT_ROOT = "${@projectRoot}";
	private static final String JVM_PARAM_PREFIX = "dbfound.";
	private static final String[] SYSTEM_PARAM_KEYS = {"openLog", "logWithParamSql", "underscoreToCamelCase", "camelCaseToUnderscore",
			"modelRootPath", "modeRootPath", "modelModifyCheck", "dateFormat", "dateTimeFormat", "timeFormat",
			"sqlCompareIgnoreCase", "openDSql"};
	private static final String[] WEB_PARAM_KEYS = {"i18nProvide", "encoding", "jsonStringAutoCover", "maxUploadSize",
			"basePath", "openSession", "apiAllowUrls", "controllerPaths", "mvcConfigFile", "exceptionHandler",
			"interceptor", "listener"};

	private static String modelLoadRoot;

	private static boolean inited = false;
	private static String configFilePath;
	private static String classpath;
	private static String projectRoot;
	private static boolean underscoreToCamelCase = false;
	private static boolean camelCaseToUnderscore = false;
	private static boolean modelModifyCheck = false;
	private static boolean jsonStringAutoCover = true;
	private final static Set<String> jsonStringForceCoverSet = CollectionUtil.asSet("GridData","parameters","columns");
	private final static Set<String> sensitiveParamSet = CollectionUtil.asSet("password","new_password","old_password","api_key","secret_key");
	private static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	private static String dateFormat = "yyyy-MM-dd";
	private static String timeFormat = "HH:mm:ss";
	private static boolean openSession = true;
	private static boolean openLog = true;
	private static boolean logWithParamSql = false;
	private static String encoding = "UTF-8";
	private static Integer maxUploadSize = 10; // 单位M
	private static String basePath ;
	private static List<String> apiAllowUrls = Collections.emptyList();

	public static void destroy() {
		ListenerFacade.destroy();
		for (DataSourceConnectionProvide provide : dsp) {
			DataSource dataSource = provide.getDataSource();
			if (dataSource != null) {
				try {
					LogUtil.info("dbfound close dataSource :" + provide.getProvideName());
					Reflector reflector = Reflector.forClass(dataSource.getClass());
					reflector.getMethodInvoker("close").invoke(dataSource, new Object[] {});
				} catch (Exception e) {
					LogUtil.error("dbfound destroy error, "+ e.getMessage(),e);
				}
			}
		}
	}

	public static void init() {
		init(null);
	}

	public static void init(ServletContext servletContext) {
		if (inited) {
			return;
		}
		doInit(getConfigFilePath(),servletContext);
	}

	private synchronized static void doInit(String confFile,ServletContext servletContext) {
		if (confFile == null || confFile.isEmpty()) {
			confFile = CLASSPATH + "/dbfound-conf.xml";
		}
		if (inited) {
			return;
		} else {
			inited = true;
			if (configFilePath == null) {
				setConfigFilePath(confFile);
			}
		}
		try {
			LogUtil.info("**************************************************************************");
			LogUtil.info("NFWork dbfound "+VERSION+" service init begin");
			SAXReader reader = new SAXReader();
			File file = new File(getRealPath(confFile));
			Document doc = null;
			if (file.exists()) {
				LogUtil.info("user config file: "+ PathFormat.format(file.getAbsolutePath()));
				doc = reader.read(file);
			} else if (confFile.startsWith(CLASSPATH)) {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				InputStream inputStream = null;
				try {
					URL url = loader.getResource(confFile.substring(CLASSPATH.length() + 1));
					if (url != null) {
						if (url.getFile() != null) {
							file = new File(url.getFile());
						}
						if (file.exists()) {
							LogUtil.info("user config file: "+ PathFormat.format(file.getAbsolutePath()));
							doc = reader.read(file);
						} else {
							LogUtil.info("user config file: " + PathFormat.format(url.getFile()));
							inputStream = url.openStream();
							doc = reader.read(inputStream);
						}
					}
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
				}
			}

			if (doc != null) {
				Element root = doc.getRootElement();

				// system参数初始化
				Element system = root.element("system");
				if (system != null || hasJvmParam("system", SYSTEM_PARAM_KEYS)) {
					initSystem(system);
				}

				// 数据库初始化
				Element database = root.element("database");
				if (database != null) {
					initDB(database);
				}

				// web参数初始化
				Element web = root.element("web");
				if (web != null || hasJvmParam("web", WEB_PARAM_KEYS)) {
					initWeb(web, servletContext);
				}
			} else {
				LogUtil.info("config file init skipped, because file not found. filePath:" + file.getAbsolutePath());
				if (hasJvmParam("system", SYSTEM_PARAM_KEYS)) {
					initSystem(null);
				}
				if (hasJvmParam("web", WEB_PARAM_KEYS)) {
					initWeb(null, servletContext);
				}
			}
			LogUtil.info("NFWork dbfound service init success");
			LogUtil.info("**************************************************************************");
		} catch (Exception e) {
			LogUtil.error("dbfound init failed, please check config", e);
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
		}

	}

	private static void initDB(Element database)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {

		List<Element> jdbcProvides = database.elements("jdbcConnectionProvide");
		for (Element element : jdbcProvides) {
			String provideName = getString(element, "provideName");
			if (provideName == null || provideName.isEmpty()) {
				provideName = "_default";
			}
			String url = getDatabaseConfigValue(provideName, "url", getString(element, "url"));
			String driverClass = getDatabaseConfigValue(provideName, "driverClass", getString(element, "driverClass"));
			String username = getDatabaseConfigValue(provideName, "username", getString(element, "username"));
			String password = getDatabaseConfigValue(provideName, "password", getString(element, "password"));
			String dialect = getDatabaseConfigValue(provideName, "dialect", getString(element, "dialect"));
			if (dialect != null && url != null && driverClass != null && username != null && !dialect.isEmpty()
					&& !driverClass.isEmpty() && !url.isEmpty() && !username.isEmpty()) {
				ConnectionProvide provide = new JdbcConnectionProvide(provideName, url, driverClass, dialect, username,
						password);
				provide.register();
				LogUtil.info( "register jdbcConnProvide success, provideName:" + provideName);
			} else {
				throw new DBFoundRuntimeException("user jdbc type, url driverClass username dialect can not be null");
			}
		}

		List<Element> dataSourceProvides = database.elements("dataSourceConnectionProvide");
		for (Element element : dataSourceProvides) {
			String provideName = getString(element, "provideName");
			if (provideName == null || provideName.isEmpty()) {
				provideName = "_default";
			}
			String dataSource = getDatabaseConfigValue(provideName, "dataSource", getString(element, "dataSource"));
			String dialect = getDatabaseConfigValue(provideName, "dialect", getString(element, "dialect"));
			String className = getDatabaseConfigValue(provideName, "className", getString(element, "className"));
			if (dialect != null && dataSource != null && !dialect.isEmpty() && !dataSource.isEmpty()) {
				ConnectionProvide provide = new DataSourceConnectionProvide(provideName, dataSource, dialect);
				provide.register();
				LogUtil.info("register dataSourceConnProvide success, provideName:"+ provideName);
			} else if (dialect != null && className != null && !dialect.isEmpty() && !className.isEmpty()) {
				DataSource ds = (DataSource) Class.forName(className).getConstructor().newInstance();
				List<Element> properties = element.element("properties").elements("property");

				Reflector reflector = Reflector.forClass(ds.getClass());
				for (Element property : properties) {
					String name = property.attributeValue("name");
					String value = getDatabaseConfigValue(provideName, name, property.attributeValue("value"));
					reflector.setProperty(ds, name, value);
				}
				DataSourceConnectionProvide provide = new DataSourceConnectionProvide(provideName, ds, dialect);
				provide.register();
				LogUtil.info("register dataSourceConnProvide success, provideName:"+ provideName);
			} else {
				throw new DBFoundRuntimeException("user dataSource type, dataSource dialect can not null");
			}
		}
	}

	private static void initWeb(Element web,ServletContext servletContext) {
		StringBuilder info = new StringBuilder();
		info.append("set web Param:");

		// i18n 初始化
		String i18nProvide = getConfigValue(web, "web", "i18nProvide");
		if (DataUtil.isNotNull(i18nProvide)) {
			MultiLangUtil.init(i18nProvide);
			appendConfigInfo(info, "i18nProvide", i18nProvide);
		}

		// 编码初始化
		String encoding = getConfigValue(web, "web", "encoding");
		if (DataUtil.isNotNull(encoding)) {
			DBFoundConfig.encoding = encoding;
			appendConfigInfo(info, "encoding", encoding);
		}

		// jsonStringAutoCover 初始化
		String autoCover = getConfigValue(web, "web", "jsonStringAutoCover");
		if (DataUtil.isNotNull(autoCover)) {
			jsonStringAutoCover = "true".equals(autoCover);
			appendConfigInfo(info, "jsonStringAutoCover", jsonStringAutoCover);
		}

		// 文件上传大小
		String maxUploadSize = getConfigValue(web, "web", "maxUploadSize");
		if (DataUtil.isNotNull(maxUploadSize)) {
			DBFoundConfig.maxUploadSize = DataUtil.intValue(maxUploadSize);
			appendConfigInfo(info, "maxUploadSize", DBFoundConfig.maxUploadSize);
		}

		// basePath 初始化
		String basePath = getConfigValue(web, "web", "basePath");
		if (DataUtil.isNotNull(basePath)) {
			DBFoundConfig.basePath = basePath;
			appendConfigInfo(info, "basePath", basePath);
		}

		// openSession 初始化
		String open = getConfigValue(web, "web", "openSession");
		if (DataUtil.isNotNull(open)) {
			if ("true".equals(open)) {
				DBFoundConfig.openSession = true;
				appendConfigInfo(info, "openSession", true);
			}else{
				DBFoundConfig.openSession = false;
				appendConfigInfo(info, "openSession", false);
			}
		}

		// web api allow urls 初始化
		String apiAllowUrls = getConfigValue(web, "web", "apiAllowUrls");
		DBFoundConfig.apiAllowUrls = DataUtil.isNull(apiAllowUrls) ? Collections.emptyList() : StringUtil.splitToList(apiAllowUrls);
		if (!DBFoundConfig.apiAllowUrls.isEmpty()) {
			appendConfigInfo(info, "apiAllowUrls", DBFoundConfig.apiAllowUrls);
		}

		// dbfound mvc controller 初始化
		String controllerPaths = getConfigValue(web, "web", "controllerPaths");
		if (DataUtil.isNotNull(controllerPaths)) {
			ActionEngine.initMappings(controllerPaths);
		}

		// 初始化dbfound mvc
		String mvcFile = getConfigValue(web, "web", "mvcConfigFile");
		if (mvcFile == null || mvcFile.isEmpty()) {
			mvcFile = CLASSPATH + "/dbfound-mvc.xml";
		}
		ActionEngine.init(mvcFile);

		// interceptor 初始化
		String exceptionHandler = getConfigValue(web, "web", "exceptionHandler");
		if (DataUtil.isNotNull(exceptionHandler)) {
			ExceptionHandlerFacade.initExceptionHandler(exceptionHandler);
			appendConfigInfo(info, "exceptionHandler", exceptionHandler);
		}

		// interceptor 初始化
		String interceptor = getConfigValue(web, "web", "interceptor");
		if (DataUtil.isNotNull(interceptor)) {
			InterceptorFacade.init(interceptor);
			appendConfigInfo(info, "interceptor", interceptor);
		}

		//listener 初始化
		String listener = getConfigValue(web, "web", "listener");
		if (DataUtil.isNotNull(listener)) {
			ListenerFacade.init(listener, servletContext);
			appendConfigInfo(info, "listener", listener);
		}

		LogUtil.info(info.toString());
	}

	private static void initSystem(Element system) {
		StringBuilder info = new StringBuilder();
		info.append("set system Param:");

		// 设置日志开关
		String openLog = getConfigValue(system, "system", "openLog");
		if (DataUtil.isNotNull(openLog)) {
			if ("false".equals(openLog.trim())) {
				DBFoundConfig.openLog = false;
				appendConfigInfo(info, "openLog", false);
			} else if ("true".equals(openLog.trim())) {
				DBFoundConfig.openLog = true;
				appendConfigInfo(info, "openLog", true);
			}
		}

		String printParamSql = getConfigValue(system, "system", "logWithParamSql");
		if (DataUtil.isNotNull(printParamSql)) {
			if ("false".equals(printParamSql.trim())) {
				DBFoundConfig.logWithParamSql = false;
				appendConfigInfo(info, "logWithParamSql", false);
			} else if ("true".equals(printParamSql.trim())) {
				DBFoundConfig.logWithParamSql = true;
				appendConfigInfo(info, "logWithParamSql", true);
			}
		}

		// 设置驼峰转化开关
		String underscoreToCamelCase = getConfigValue(system, "system", "underscoreToCamelCase");
		if (DataUtil.isNotNull(underscoreToCamelCase)) {
			String open = underscoreToCamelCase;
			if ("false".equals(open.trim())) {
				DBFoundConfig.underscoreToCamelCase = false;
				appendConfigInfo(info, "underscoreToCamelCase", false);
			} else if ("true".equals(open.trim())) {
				DBFoundConfig.underscoreToCamelCase = true;
				appendConfigInfo(info, "underscoreToCamelCase", true);
			}
		}

		// 设置下划线转化开关
		String camelCaseToUnderscore = getConfigValue(system, "system", "camelCaseToUnderscore");
		if (DataUtil.isNotNull(camelCaseToUnderscore)) {
			String open = camelCaseToUnderscore;
			if ("false".equals(open.trim())) {
				DBFoundConfig.camelCaseToUnderscore = false;
				appendConfigInfo(info, "camelCaseToUnderscore", false);
			} else if ("true".equals(open.trim())) {
				DBFoundConfig.camelCaseToUnderscore = true;
				appendConfigInfo(info, "camelCaseToUnderscore", true);
			}
		}

		// 设置model根目录
		String modelRootPath = getConfigValue(system, "system", "modelRootPath");
		if (DataUtil.isNull(modelRootPath)) {
			modelRootPath = getConfigValue(system, "system", "modeRootPath");
		}
		if (DataUtil.isNotNull(modelRootPath)) {
			DBFoundConfig.modelLoadRoot = modelRootPath;
			appendConfigInfo(info, "modelRootPath", modelRootPath);
		}

		String modelModifyCheckConfig = getConfigValue(system, "system", "modelModifyCheck");
		if (DataUtil.isNotNull(modelModifyCheckConfig)) {
			modelModifyCheck = "true".equals(modelModifyCheckConfig);
			appendConfigInfo(info, "modelModifyCheck", modelModifyCheck);
		}

		String dateFormatConfig = getConfigValue(system, "system", "dateFormat");
		if (DataUtil.isNotNull(dateFormatConfig)) {
			dateFormat = dateFormatConfig;
			appendConfigInfo(info, "dateFormat", dateFormatConfig);
		}

		String dateTimeFormatConfig = getConfigValue(system, "system", "dateTimeFormat");
		if (DataUtil.isNotNull(dateTimeFormatConfig)) {
			dateTimeFormat = dateTimeFormatConfig;
			appendConfigInfo(info, "dateTimeFormat", dateTimeFormatConfig);
		}

		String timeFormatConfig = getConfigValue(system, "system", "timeFormat");
		if (DataUtil.isNotNull(timeFormatConfig)) {
			timeFormat = timeFormatConfig;
			appendConfigInfo(info, "timeFormat", timeFormatConfig);
		}

		String compareIgnoreCaseConfig = getConfigValue(system, "system", "sqlCompareIgnoreCase");
		if (DataUtil.isNotNull(compareIgnoreCaseConfig)) {
			DSqlConfig.setCompareIgnoreCase("true".equals(compareIgnoreCaseConfig));
			appendConfigInfo(info, "sqlCompareIgnoreCase", DSqlConfig.isCompareIgnoreCase());
		}

		String openDSqlConfig = getConfigValue(system, "system", "openDSql");
		if (DataUtil.isNotNull(openDSqlConfig)) {
			DSqlConfig.setOpenDSql("true".equals(openDSqlConfig));
			appendConfigInfo(info, "openDSql", DSqlConfig.isOpenDSql());
		}

		LogUtil.info(info.toString());
	}

	public static String getRealPath(String value) {
		value = value.replace(CLASSPATH, getClasspath());
		String projectRoot = getProjectRoot();
		if (projectRoot != null) {
			value = value.replace(PROJECT_ROOT, projectRoot);
		}
		return value;
	}

	private static String getString(Element element, String key) {
		return element.attributeValue(key);
	}

	private static String getConfigValue(Element parent, String group, String key) {
		String value = null;
		Element element = parent == null ? null : parent.element(key);
		if (element != null) {
			value = element.getTextTrim();
		}
		String jvmValue = getJvmParam(group + "." + key);
		if (DataUtil.isNotNull(jvmValue)) {
			value = jvmValue;
		}
		return value;
	}

	private static void appendConfigInfo(StringBuilder info, String key, Object value) {
		info.append(" (").append(key).append(" = ").append(value).append(")");
	}

	private static String getDatabaseConfigValue(String provideName, String key, String xmlValue) {
		String jvmValue = getJvmParam("datasource." + provideName + "." + key);
		if (DataUtil.isNull(jvmValue) && "_default".equals(provideName)) {
			jvmValue = getJvmParam("datasource." + key);
		}
		return DataUtil.isNotNull(jvmValue) ? jvmValue : xmlValue;
	}

	private static String getJvmParam(String key) {
		String value = System.getProperty(JVM_PARAM_PREFIX + key);
		if (value == null) {
			String kebabKey = camelToKebabCase(key);
			if (!Objects.equals(kebabKey, key)) {
				value = System.getProperty(JVM_PARAM_PREFIX + kebabKey);
			}
		}
		return value == null ? null : value.trim();
	}

	private static String camelToKebabCase(String value) {
		int firstUpperCaseIndex = -1;
		for (int i = 0; i < value.length(); i++) {
			if (Character.isUpperCase(value.charAt(i))) {
				firstUpperCaseIndex = i;
				break;
			}
		}
		if (firstUpperCaseIndex == -1) {
			return value;
		}
		StringBuilder result = new StringBuilder();
		result.append(value, 0, firstUpperCaseIndex);
		for (int i = firstUpperCaseIndex; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isUpperCase(c)) {
				result.append('-').append(Character.toLowerCase(c));
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	private static boolean hasJvmParam(String group, String... keys) {
		for (String key : keys) {
			if (DataUtil.isNotNull(getJvmParam(group + "." + key))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isInited() {
		return inited;
	}

	public static void setInited(boolean inited) {
		DBFoundConfig.inited = inited;
	}

	public static String getClasspath() {
		if (classpath == null || classpath.isEmpty()) {
			String cp = Thread.currentThread().getContextClassLoader().getResource("").getFile();
			File file = new File(cp);
			classpath = file.getAbsolutePath();
			classpath = PathFormat.format(classpath);
		}
		return classpath;
	}

	public static String getProjectRoot() {
		if (projectRoot == null || projectRoot.isEmpty()) {
			File file = new File(getClasspath());
			try {
				projectRoot = file.getParentFile().getParentFile().getAbsolutePath();
			} catch (Exception e) {
				return null;
			}
			projectRoot = PathFormat.format(projectRoot);
		}
		return projectRoot;
	}

	public static String getConfigFilePath() {
		try {
			if (configFilePath == null || configFilePath.isEmpty()) {
				configFilePath = DispatcherFilter.getConfigFilePath();
				configFilePath = PathFormat.format(configFilePath);
			}
			return configFilePath;
		} catch (Throwable e) {
			return null;
		}
	}

	public static void setConfigFilePath(String configFilePath) {
		DBFoundConfig.configFilePath = PathFormat.format(configFilePath);
	}

	public static void setClasspath(String classpath) {
		DBFoundConfig.classpath = PathFormat.format(classpath);
	}

	public static void setProjectRoot(String projectRoot) {
		DBFoundConfig.projectRoot = PathFormat.format(projectRoot);
	}

	public static List<DataSourceConnectionProvide> getDsp() {
		return dsp;
	}

	public static List<String> getApiAllowUrls() {
		return apiAllowUrls;
	}

	public static boolean isUnderscoreToCamelCase() {
		return underscoreToCamelCase;
	}

	public static void setUnderscoreToCamelCase(boolean underscoreToCamelCase) {
		DBFoundConfig.underscoreToCamelCase = underscoreToCamelCase;
	}

	public static boolean isModelModifyCheck() {
		return modelModifyCheck;
	}

	public static void setModelModifyCheck(boolean modelModifyCheck) {
		DBFoundConfig.modelModifyCheck = modelModifyCheck;
	}

	public static String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public static void setDateTimeFormat(String dateTimeFormat) {
		DBFoundConfig.dateTimeFormat = dateTimeFormat;
	}

	public static void setDateFormat(String dateFormat) {
		DBFoundConfig.dateFormat = dateFormat;
	}

	public static String getDateFormat() {
		return dateFormat;
	}

	public static boolean isJsonStringAutoCover() {
		return jsonStringAutoCover;
	}

	public static void setJsonStringAutoCover(boolean jsonStringAutoCover) {
		DBFoundConfig.jsonStringAutoCover = jsonStringAutoCover;
	}

	public static Set<String> getJsonStringForceCoverSet() {
		return jsonStringForceCoverSet;
	}

	public static Set<String> getSensitiveParamSet() {
		return sensitiveParamSet;
	}

	public static boolean isCamelCaseToUnderscore() {
		return camelCaseToUnderscore;
	}

	public static void setCamelCaseToUnderscore(boolean camelCaseToUnderscore) {
		DBFoundConfig.camelCaseToUnderscore = camelCaseToUnderscore;
	}

	public static boolean isOpenSession() {
		return openSession;
	}

	public static void setOpenSession(boolean openSession) {
		DBFoundConfig.openSession = openSession;
	}

	public static boolean isOpenLog() {
		return openLog;
	}

	public static void setOpenLog(boolean openLog) {
		DBFoundConfig.openLog = openLog;
	}

	public static String getModelLoadRoot() {
		if (DataUtil.isNull(modelLoadRoot)) {
			modelLoadRoot = DBFoundConfig.CLASSPATH + "/model";
		}
		return modelLoadRoot;
	}

	public static void setModelLoadRoot(String modelLoadRoot) {
		DBFoundConfig.modelLoadRoot = modelLoadRoot;
	}

	public static String getEncoding() {
		return encoding;
	}

	public static void setEncoding(String encoding) {
		DBFoundConfig.encoding = encoding;
	}

	public static Integer getMaxUploadSize() {
		return maxUploadSize;
	}

	public static void setMaxUploadSize(Integer maxUploadSize) {
		DBFoundConfig.maxUploadSize = maxUploadSize;
	}

	public static String getBasePath() {
		return basePath;
	}

	public static void setBasePath(String basePath) {
		DBFoundConfig.basePath = basePath;
	}

	public static boolean isLogWithParamSql() {
		return logWithParamSql;
	}

	public static void setLogWithParamSql(boolean logWithParamSql) {
		DBFoundConfig.logWithParamSql = logWithParamSql;
	}

	public static String getTimeFormat() {
		return timeFormat;
	}

	public static void setTimeFormat(String timeFormat) {
		DBFoundConfig.timeFormat = timeFormat;
	}
}
