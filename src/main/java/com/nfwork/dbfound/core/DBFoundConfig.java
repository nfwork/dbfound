package com.nfwork.dbfound.core;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;

import jakarta.servlet.ServletContext;
import javax.sql.DataSource;

import com.nfwork.dbfound.db.ConnectionProvideManager;
import com.nfwork.dbfound.model.ModelEngine;
import com.nfwork.dbfound.model.ModelOperator;
import com.nfwork.dbfound.model.dsql.DSqlConfig;
import com.nfwork.dbfound.model.enums.EnumHandlerFactory;
import com.nfwork.dbfound.model.reflector.Reflector;
import com.nfwork.dbfound.util.*;
import com.nfwork.dbfound.web.ExceptionHandlerFacade;
import com.nfwork.dbfound.web.ListenerFacade;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.DataSourceConnectionProvide;
import com.nfwork.dbfound.db.JdbcConnectionProvide;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.web.action.ActionEngine;
import com.nfwork.dbfound.web.InterceptorFacade;
import com.nfwork.dbfound.web.i18n.MultiLangFacade;

public class DBFoundConfig {

	public static final String VERSION = "4.4.2" ;
	public static final String CLASSPATH = "${@classpath}";
	public static final String PROJECT_ROOT = "${@projectRoot}";
	private static final String JVM_PARAM_PREFIX = "dbfound.";
	private static final DBFoundInitToken dbfoundInitToken = new DBFoundInitToken();
	private static boolean inited = false;
	private static ConfigState config = new ConfigState();

	private static class ConfigState {
		private String modelRootPath;
		private String classpath;
		private String projectRoot;
		private boolean underscoreToCamelCase = false;
		private boolean camelCaseToUnderscore = false;
		private boolean modelModifyCheck = false;
		private boolean jsonStringAutoCover = true;
		private final Set<String> jsonStringForceCoverSet = CollectionUtil.asSet("GridData","parameters","columns");
		private final Set<String> sensitiveParamSet = CollectionUtil.asSet("password","new_password","old_password","newPassword","oldPassword","api_key","api_secret","secret_key","apiKey","secretKey","apiSecret");
		private String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
		private String dateFormat = "yyyy-MM-dd";
		private String timeFormat = "HH:mm:ss";
		private boolean openSession = true;
		private boolean openLog = true;
		private boolean logWithParamSql = false;
		private String encoding = "UTF-8";
		private Integer maxUploadSize = 10; // 单位M
		private String basePath = "${@contextPath}";
		private List<String> apiAllowUrls = Collections.emptyList();
	}


	public static void checkInitToken(DBFoundInitToken token) {
		if (dbfoundInitToken != token) {
			throw new DBFoundRuntimeException("dbfound init token invalid");
		}
	}

	public static void destroy(DBFoundInitToken token) {
		checkInitToken(token);
		doDestroy();
	}

	private static void doDestroy() {
		ListenerFacade.destroy(dbfoundInitToken);
		ActionEngine.destroy(dbfoundInitToken);
		MultiLangFacade.destroy(dbfoundInitToken);
		ExceptionHandlerFacade.destroy(dbfoundInitToken);
		InterceptorFacade.destroy(dbfoundInitToken);
		ConnectionProvideManager.destroy(dbfoundInitToken);
		ModelEngine.destroy(dbfoundInitToken);
		Reflector.clearCache(dbfoundInitToken);
		EnumHandlerFactory.clearCache(dbfoundInitToken);
		DSqlConfig.reset(dbfoundInitToken);
		DBFoundConfig.reset();
	}

	private static void reset(){
		config = new ConfigState();
		inited = false;
	}

	public static DBFoundInitToken init() {
		return init(null,null);
	}

	public static DBFoundInitToken init(String configFilePath, ServletContext servletContext) {
		checkNotInited();
		initProjectRoot(servletContext);
		return doInit(configFilePath,servletContext);
	}

	public static DBFoundInitToken initSpringBoot(Document document) {
		return initSpringBoot(document, null);
	}

	public synchronized static DBFoundInitToken initSpringBoot(Document document, ServletContext servletContext) {
		checkNotInited();
		try {
			initProjectRoot(servletContext);
			initDocument(document, servletContext, true);
		} catch (Exception e) {
			throw new DBFoundRuntimeException("dbfound init failed, please check config", e);
		}
		return initSuccess();
	}

	private static void checkNotInited() {
		if (inited) {
			throw new DBFoundRuntimeException("dbfound already initialized, please destroy before init again");
		}
	}

	private static void initProjectRoot(ServletContext servletContext) {
		if(servletContext != null) {
			config.projectRoot = PathFormatUtil.format(servletContext.getRealPath(""));
		}
	}

	private synchronized static DBFoundInitToken doInit(String configFilePath, ServletContext servletContext) {
		if (configFilePath == null || configFilePath.isEmpty()) {
			configFilePath = CLASSPATH + "/dbfound-conf.xml";
		}
		configFilePath = PathFormatUtil.format(configFilePath);
		checkNotInited();
		try {
			LogUtil.info("**************************************************************************");
			LogUtil.info("NFWork dbfound "+VERSION+" service init begin");
			SAXReader reader = new SAXReader();
			File file = new File(getRealPath(configFilePath));
			Document doc = null;
			if (file.exists()) {
				LogUtil.info("user config file: "+ PathFormatUtil.format(file.getAbsolutePath()));
				doc = reader.read(file);
			} else if (configFilePath.startsWith(CLASSPATH)) {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				if (loader == null) {
					loader = DBFoundConfig.class.getClassLoader();
				}
				InputStream inputStream = null;
				try {
					URL url = loader.getResource(configFilePath.substring(CLASSPATH.length() + 1));
					if (url != null) {
						if (url.getFile() != null) {
							file = new File(url.getFile());
						}
						if (file.exists()) {
							LogUtil.info("user config file: "+ PathFormatUtil.format(file.getAbsolutePath()));
							doc = reader.read(file);
						} else {
							LogUtil.info("user config file: " + PathFormatUtil.format(url.getFile()));
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
				initDocument(doc, servletContext, false);
			} else {
				LogUtil.info("config file init skipped, because file not found. filePath:" + file.getAbsolutePath());
			}
			LogUtil.info("NFWork dbfound service init success");
			LogUtil.info("**************************************************************************");
		} catch (Exception e) {
			LogUtil.error("dbfound init failed, please check config", e);
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
			return null;
		}
		return initSuccess();

	}

	private static DBFoundInitToken initSuccess() {
		inited = true;
		return dbfoundInitToken;
	}

	private static void initDocument(Document document, ServletContext servletContext, boolean isInitSpring)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
		Element root = document.getRootElement();

		// system参数初始化
		Element system = root.element("system");
		initSystem(system, isInitSpring);

		// 数据库初始化
		Element database = root.element("database");
		if (database != null && !isInitSpring) {
			initDB(database);
		}

		// web参数初始化
		Element web = root.element("web");
		initWeb(web, servletContext, isInitSpring);
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
				throw new DBFoundRuntimeException("user jdbc type, url driverClass username dialect cannot be null");
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
				throw new DBFoundRuntimeException("user dataSource type, dataSource dialect cannot null");
			}
		}
	}

	private static void initWeb(Element web, ServletContext servletContext, boolean isInitSpring) {
		StringBuilder info = new StringBuilder();
		info.append("set web Param:");
		int infoStartLength = info.length();

		// i18n 初始化
		String i18nProvide = getConfigValue(web, "web", "i18nProvide", isInitSpring);
		if (DataUtil.isNotNull(i18nProvide)) {
			MultiLangFacade.init(dbfoundInitToken, i18nProvide);
			appendConfigInfo(info, "i18nProvide", i18nProvide);
		}

		// 编码初始化
		String encoding = getConfigValue(web, "web", "encoding", isInitSpring);
		if (DataUtil.isNotNull(encoding)) {
			config.encoding = encoding;
			appendConfigInfo(info, "encoding", encoding);
		}

		// jsonStringAutoCover 初始化
		String autoCover = getConfigValue(web, "web", "jsonStringAutoCover", isInitSpring);
		if (DataUtil.isNotNull(autoCover)) {
			config.jsonStringAutoCover = "true".equals(autoCover);
			appendConfigInfo(info, "jsonStringAutoCover", config.jsonStringAutoCover);
		}

		// 文件上传大小
		String maxUploadSize = getConfigValue(web, "web", "maxUploadSize", isInitSpring);
		if (DataUtil.isNotNull(maxUploadSize)) {
			config.maxUploadSize = DataUtil.intValue(maxUploadSize);
			appendConfigInfo(info, "maxUploadSize", config.maxUploadSize);
		}

		// basePath 初始化
		String basePath = getConfigValue(web, "web", "basePath", isInitSpring);
		if (DataUtil.isNotNull(basePath)) {
			config.basePath = basePath;
			appendConfigInfo(info, "basePath", basePath);
		}

		// openSession 初始化
		String open = getConfigValue(web, "web", "openSession", isInitSpring);
		if (DataUtil.isNotNull(open)) {
			if ("true".equals(open)) {
				config.openSession = true;
				appendConfigInfo(info, "openSession", true);
			}else{
				config.openSession = false;
				appendConfigInfo(info, "openSession", false);
			}
		}

		// web api allow urls 初始化
		String apiAllowUrls = getConfigValue(web, "web", "apiAllowUrls", isInitSpring);
		config.apiAllowUrls = DataUtil.isNull(apiAllowUrls) ? Collections.emptyList() : StringUtil.splitToList(apiAllowUrls);
		if (!config.apiAllowUrls.isEmpty()) {
			appendConfigInfo(info, "apiAllowUrls", config.apiAllowUrls);
		}

		// dbfound mvc controller 初始化
		String controllerPaths = getConfigValue(web, "web", "controllerPaths", isInitSpring);
		String mvcFile = getConfigValue(web, "web", "mvcConfigFile", isInitSpring);
		if (mvcFile == null || mvcFile.isEmpty()) {
			mvcFile = CLASSPATH + "/dbfound-mvc.xml";
		}
		if (!isInitSpring) {
			ActionEngine.init(dbfoundInitToken, controllerPaths, mvcFile);
		}

		// exceptionHandler 初始化
		String exceptionHandler = getConfigValue(web, "web", "exceptionHandler", isInitSpring);
		if (DataUtil.isNotNull(exceptionHandler)) {
			ExceptionHandlerFacade.initExceptionHandler(dbfoundInitToken, exceptionHandler);
			appendConfigInfo(info, "exceptionHandler", exceptionHandler);
		}

		// interceptor 初始化
		String interceptor = getConfigValue(web, "web", "interceptor", isInitSpring);
		if (DataUtil.isNotNull(interceptor)) {
			InterceptorFacade.init(dbfoundInitToken, interceptor);
			appendConfigInfo(info, "interceptor", interceptor);
		}

		//listener 初始化
		String listener = getConfigValue(web, "web", "listener", isInitSpring);
		if (DataUtil.isNotNull(listener)) {
			ListenerFacade.init(dbfoundInitToken, listener, servletContext);
			appendConfigInfo(info, "listener", listener);
		}

		if (!isInitSpring && info.length() > infoStartLength) {
			LogUtil.info(info.toString());
		}
	}

	private static void initSystem(Element system, boolean isInitSpring) {
		StringBuilder info = new StringBuilder();
		info.append("set system Param:");
		int infoStartLength = info.length();

		// 设置日志开关
		String openLog = getConfigValue(system, "system", "openLog", isInitSpring);
		if (DataUtil.isNotNull(openLog)) {
			if ("false".equals(openLog)) {
				config.openLog = false;
				appendConfigInfo(info, "openLog", false);
			} else if ("true".equals(openLog)) {
				config.openLog = true;
				appendConfigInfo(info, "openLog", true);
			}
		}

		String printParamSql = getConfigValue(system, "system", "logWithParamSql", isInitSpring);
		if (DataUtil.isNotNull(printParamSql)) {
			if ("false".equals(printParamSql)) {
				config.logWithParamSql = false;
				appendConfigInfo(info, "logWithParamSql", false);
			} else if ("true".equals(printParamSql)) {
				config.logWithParamSql = true;
				appendConfigInfo(info, "logWithParamSql", true);
			}
		}

		// 设置驼峰转化开关
		String underscoreToCamelCase = getConfigValue(system, "system", "underscoreToCamelCase", isInitSpring);
		if (DataUtil.isNotNull(underscoreToCamelCase)) {
			if ("false".equals(underscoreToCamelCase)) {
				config.underscoreToCamelCase = false;
				appendConfigInfo(info, "underscoreToCamelCase", false);
			} else if ("true".equals(underscoreToCamelCase)) {
				config.underscoreToCamelCase = true;
				appendConfigInfo(info, "underscoreToCamelCase", true);
			}
		}

		// 设置下划线转化开关
		String camelCaseToUnderscore = getConfigValue(system, "system", "camelCaseToUnderscore", isInitSpring);
		if (DataUtil.isNotNull(camelCaseToUnderscore)) {
			if ("false".equals(camelCaseToUnderscore)) {
				config.camelCaseToUnderscore = false;
				appendConfigInfo(info, "camelCaseToUnderscore", false);
			} else if ("true".equals(camelCaseToUnderscore)) {
				config.camelCaseToUnderscore = true;
				appendConfigInfo(info, "camelCaseToUnderscore", true);
			}
		}

		// 设置model根目录
		String modelRootPath = getConfigValue(system, "system", "modelRootPath", isInitSpring);
		if (DataUtil.isNotNull(modelRootPath)) {
			config.modelRootPath = modelRootPath;
			appendConfigInfo(info, "modelRootPath", modelRootPath);
		}

		String modelModifyCheckConfig = getConfigValue(system, "system", "modelModifyCheck", isInitSpring);
		if (DataUtil.isNotNull(modelModifyCheckConfig)) {
			config.modelModifyCheck = "true".equals(modelModifyCheckConfig);
			appendConfigInfo(info, "modelModifyCheck", config.modelModifyCheck);
		}

		String modelOperator = getConfigValue(system, "system", "modelOperator", isInitSpring);
		if (DataUtil.isNotNull(modelOperator)) {
			initModelOperator(modelOperator);
			appendConfigInfo(info, "modelOperator", modelOperator);
		}

		String dateFormatConfig = getConfigValue(system, "system", "dateFormat", isInitSpring);
		if (DataUtil.isNotNull(dateFormatConfig)) {
			config.dateFormat = dateFormatConfig;
			appendConfigInfo(info, "dateFormat", dateFormatConfig);
		}

		String dateTimeFormatConfig = getConfigValue(system, "system", "dateTimeFormat", isInitSpring);
		if (DataUtil.isNotNull(dateTimeFormatConfig)) {
			config.dateTimeFormat = dateTimeFormatConfig;
			appendConfigInfo(info, "dateTimeFormat", dateTimeFormatConfig);
		}

		String timeFormatConfig = getConfigValue(system, "system", "timeFormat", isInitSpring);
		if (DataUtil.isNotNull(timeFormatConfig)) {
			config.timeFormat = timeFormatConfig;
			appendConfigInfo(info, "timeFormat", timeFormatConfig);
		}

		String compareIgnoreCaseConfig = getConfigValue(system, "system", "sqlCompareIgnoreCase", isInitSpring);
		if (DataUtil.isNotNull(compareIgnoreCaseConfig)) {
			DSqlConfig.init(dbfoundInitToken, "true".equals(compareIgnoreCaseConfig), null);
			appendConfigInfo(info, "sqlCompareIgnoreCase", DSqlConfig.isCompareIgnoreCase());
		}

		String openDSqlConfig = getConfigValue(system, "system", "openDSql", isInitSpring);
		if (DataUtil.isNotNull(openDSqlConfig)) {
			DSqlConfig.init(dbfoundInitToken, null, "true".equals(openDSqlConfig));
			appendConfigInfo(info, "openDSql", DSqlConfig.isOpenDSql());
		}

		if (!isInitSpring && info.length() > infoStartLength) {
			LogUtil.info(info.toString());
		}
	}

	private static void initModelOperator(String className) {
		try {
			Class<? extends ModelOperator> clazz = Class.forName(className).asSubclass(ModelOperator.class);
			ModelEngine.setModelOperator(dbfoundInitToken, clazz.getConstructor().newInstance());
		} catch (Exception e) {
			throw new DBFoundRuntimeException("ModelOperator init failed, please check the class " + className + " exists and extends ModelOperator", e);
		}
	}

	public static String getRealPath(String value) {
		if (value.contains(CLASSPATH)) {
			value = value.replace(CLASSPATH, getClasspath());
		}
		if (value.contains(PROJECT_ROOT) ) {
			value = value.replace(PROJECT_ROOT, getProjectRoot());
		}
		return value;
	}

	private static String getString(Element element, String key) {
		return element.attributeValue(key);
	}

	private static String getConfigValue(Element parent, String group, String key, boolean isInitSpring) {
		String value = null;
		Element element = parent == null ? null : parent.element(key);
		if (element != null) {
			value = element.getTextTrim();
		}
		if (isInitSpring) {
			return value;
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

	public static boolean isInited() {
		return inited;
	}

	public static String getClasspath() {
		if (config.classpath == null || config.classpath.isEmpty()) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			if (loader == null) {
				loader = DBFoundConfig.class.getClassLoader();
			}
			URL url = loader.getResource("");
			if (url == null) {
				throw new DBFoundRuntimeException("classpath resource not found");
			}
			config.classpath = PathFormatUtil.format(new File(url.getFile()).getAbsolutePath());
		}
		return config.classpath;
	}

	public static String getProjectRoot() {
		if (config.projectRoot == null || config.projectRoot.isEmpty()) {
			String cp = getClasspath();
			File file = new File(cp);
			if (file.exists() && file.isDirectory() && file.getParentFile().exists() && file.getParentFile().getParentFile().exists()) {
				config.projectRoot = PathFormatUtil.format(file.getParentFile().getParentFile().getAbsolutePath());
			}else{
				throw new DBFoundRuntimeException(PROJECT_ROOT + " cannot resolve by classpath, this classpath is '" + cp +"'");
			}
		}
		return config.projectRoot;
	}

	public static List<String> getApiAllowUrls() {
		return config.apiAllowUrls;
	}

	public static boolean isUnderscoreToCamelCase() {
		return config.underscoreToCamelCase;
	}

	public static boolean isModelModifyCheck() {
		return config.modelModifyCheck;
	}

	public static String getDateTimeFormat() {
		return config.dateTimeFormat;
	}

	public static String getDateFormat() {
		return config.dateFormat;
	}

	public static boolean isJsonStringAutoCover() {
		return config.jsonStringAutoCover;
	}

	public static Set<String> getJsonStringForceCoverSet() {
		return config.jsonStringForceCoverSet;
	}

	public static Set<String> getSensitiveParamSet() {
		return config.sensitiveParamSet;
	}

	public static boolean isCamelCaseToUnderscore() {
		return config.camelCaseToUnderscore;
	}

	public static boolean isOpenSession() {
		return config.openSession;
	}

	public static boolean isOpenLog() {
		return config.openLog;
	}

	public static String getModelRootPath() {
		if (DataUtil.isNull(config.modelRootPath)) {
			config.modelRootPath = DBFoundConfig.CLASSPATH + "/model";
		}
		return config.modelRootPath;
	}

	public static String getEncoding() {
		return config.encoding;
	}

	public static Integer getMaxUploadSize() {
		return config.maxUploadSize;
	}

	public static String getBasePath() {
		return config.basePath;
	}

	public static boolean isLogWithParamSql() {
		return config.logWithParamSql;
	}

	public static String getTimeFormat() {
		return config.timeFormat;
	}
}
