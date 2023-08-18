package com.nfwork.dbfound.core;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.sql.DataSource;

import com.nfwork.dbfound.model.dsql.DSqlConfig;
import com.nfwork.dbfound.util.CollectionUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nfwork.dbfound.db.ConnectionProvide;
import com.nfwork.dbfound.db.DataSourceConnectionProvide;
import com.nfwork.dbfound.db.JdbcConnectionProvide;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;
import com.nfwork.dbfound.web.ActionEngine;
import com.nfwork.dbfound.web.DispatcherFilter;
import com.nfwork.dbfound.web.InterceptorEngine;
import com.nfwork.dbfound.web.file.FileUtil;
import com.nfwork.dbfound.web.i18n.MultiLangUtil;

public class DBFoundConfig {

	public static final String VERSION = "4.0.6" ;

	private static String listenerClass;
	private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
	private final static List<DataSourceConnectionProvide> dsp = new ArrayList<>();

	public static final String CLASSPATH = "${@classpath}";
	public static final String PROJECT_ROOT = "${@projectRoot}";

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
	private final static Set<String> sensitiveParamSet = CollectionUtil.asSet("password","api_key","secret_key");
	private static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	private static String dateFormat = "yyyy-MM-dd";
	private static String timeFormat = "HH:mm:ss";
	private static boolean openSession = true;
	private static boolean openLog = true;
	private static String encoding = "UTF-8";
	private static Integer maxUploadSize = 10; // 单位M
	private static String basePath ;

	public static void destroy() {
		for (DataSourceConnectionProvide provide : dsp) {
			DataSource dataSource = provide.getDataSource();
			if (dataSource != null) {
				try {
					System.out.println(simpleDateFormat.format(new Date()) + " dbfound close dataSource :" + provide.getProvideName());
					MethodUtils.invokeMethod(dataSource, "close", new Object[] {});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void init() {
		if (inited) {
			return;
		}
		init(getConfigFilePath());
	}

	public synchronized static void init(String confFile) {
		if (confFile == null || "".equals(confFile)) {
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
			System.out.println("**************************************************************************");
			System.out.println(simpleDateFormat.format(new Date()) + " NFWork dbfound "+VERSION+" service init begin");
			SAXReader reader = new SAXReader();
			File file = new File(getRealPath(confFile));
			Document doc = null;
			if (file.exists()) {
				System.out.println(simpleDateFormat.format(new Date()) + " user config file: "
						+ PathFormat.format(file.getAbsolutePath()));
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
							System.out.println(simpleDateFormat.format(new Date()) + " user config file: "
									+ PathFormat.format(file.getAbsolutePath()));
							doc = reader.read(file);
						} else {
							System.out.println(simpleDateFormat.format(new Date()) + " user config file: "
									+ PathFormat.format(url.getFile()));
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
				if (system != null) {
					initSystem(system);
				}

				// 数据库初始化
				Element database = root.element("database");
				if (database != null) {
					initDB(database);
				}

				// web参数初始化
				Element web = root.element("web");
				if (web != null) {
					initWeb(web);
				}

				if (listenerClass != null) {
					try {
						StartListener listener = (StartListener) Class.forName(listenerClass).newInstance();
						listener.execute();
						System.out.println(simpleDateFormat.format(new Date()) + " invoke listenerClass success");
					} catch (Exception e) {
						LogUtil.error("invoke listenerClass faild", e);
					}
				}
			} else {
				System.out.println(simpleDateFormat.format(new Date())
						+ " config file init skiped, because file not found. filePath:" + file.getAbsolutePath());
			}
			System.out.println(simpleDateFormat.format(new Date()) + " NFWork dbfound service init success");
			System.out.println("**************************************************************************");
		} catch (Exception e) {
			LogUtil.error("dbfound init faild，please check config", e);
			if (e instanceof RuntimeException) {
				throw (RuntimeException) e;
			}
		}

	}

	private static void initDB(Element database)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException {

		List<Element> jdbcProvides = database.elements("jdbcConnectionProvide");
		for (Element element : jdbcProvides) {
			String provideName = getString(element, "provideName");
			String url = getString(element, "url");
			String driverClass = getString(element, "driverClass");
			String username = getString(element, "username");
			String password = getString(element, "password");
			String dialect = getString(element, "dialect");
			if (provideName == null || provideName.equals("")) {
				provideName = "_default";
			}
			if (dialect != null && url != null && driverClass != null && username != null && !"".equals(dialect)
					&& !"".equals(driverClass) && !"".equals(url) && !"".equals(username)) {
				ConnectionProvide provide = new JdbcConnectionProvide(provideName, url, driverClass, dialect, username,
						password);
				provide.regist();
				System.out.println(
						simpleDateFormat.format(new Date()) + " regist jdbcConnProvide success, provideName:" + provideName);
			} else {
				throw new DBFoundRuntimeException("user jdbc type，url driverClass username dialect can not be null");
			}
		}

		List<Element> dataSourceProvides = database.elements("dataSourceConnectionProvide");
		for (Element element : dataSourceProvides) {
			String provideName = getString(element, "provideName");
			String dataSource = getString(element, "dataSource");
			String dialect = getString(element, "dialect");
			String className = getString(element, "className");
			if (provideName == null || provideName.equals("")) {
				provideName = "_default";
			}
			if (dialect != null && dataSource != null && !"".equals(dialect) && !"".equals(dataSource)) {
				ConnectionProvide provide = new DataSourceConnectionProvide(provideName, dataSource, dialect);
				provide.regist();
				System.out.println(simpleDateFormat.format(new Date()) + " regist dataSourceConnProvide success, provideName:"
						+ provideName);
			} else if (dialect != null && className != null && !"".equals(dialect) && !"".equals(className)) {
				DataSource ds = (DataSource) Class.forName(className).newInstance();
				List<Element> properties = element.element("properties").elements("property");
				for (Element property : properties) {
					BeanUtils.setProperty(ds, property.attributeValue("name"), property.attributeValue("value"));
				}
				DataSourceConnectionProvide provide = new DataSourceConnectionProvide(provideName, ds, dialect);
				provide.regist();
				System.out.println(simpleDateFormat.format(new Date()) + " regist dataSourceConnProvide success, provideName:"
						+ provideName);
			} else {
				throw new DBFoundRuntimeException("user dataSource type，dataSource dialect can not null");
			}
		}
	}

	private static void initWeb(Element web) {
		StringBuilder info = new StringBuilder();
		info.append(simpleDateFormat.format(new Date())).append(" set web Param:");

		// i18n 初始化
		Element provide = web.element("i18nProvide");
		if (provide != null) {
			String className = provide.getTextTrim();
			if (!"".equals(className)) {
				MultiLangUtil.init(className);
				info.append("(i18nProvide = ").append(className).append(")");
			}
		}

		// 编码初始化
		Element enco = web.element("encoding");
		if (enco != null) {
			String encoding = enco.getTextTrim();
			if (!"".equals(encoding)) {
				DBFoundConfig.encoding = encoding;
				info.append("(encoding = ").append(encoding).append(")");
			}
		}

		// jsonStringAutoCover 初始化
		Element jsonElement = web.element("jsonStringAutoCover");
		if (jsonElement != null) {
			String autoCover = jsonElement.getTextTrim();
			if ("true".equals(autoCover)) {
				jsonStringAutoCover = true;
			}else{
				jsonStringAutoCover = false;
			}
			info.append("(jsonStringAutoCover = ").append(jsonStringAutoCover).append(")");
		}

		// 文件上传路径
		Element folder = web.element("uploadFolder");
		if (folder != null) {
			String path = folder.getTextTrim();
			if (!"".equals(path)) {
				path = getRealPath(path);
				FileUtil.init(path);
				info.append("(uploadFolder = ").append(path).append(")");
			}
		}

		// 文件上传大小
		Element size = web.element("maxUploadSize");
		if (size != null) {
			String maxUploadSize = size.getTextTrim();
			if (DataUtil.isNotNull(maxUploadSize)) {
				DBFoundConfig.maxUploadSize = DataUtil.intValue(maxUploadSize);
				info.append("(maxUploadSize = ").append(DBFoundConfig.maxUploadSize).append(")");
			}
		}

		// basePath 初始化
		Element basePathEl = web.element("basePath");
		if (basePathEl != null) {
			String basePath = basePathEl.getTextTrim();
			if (!"".equals(basePath)) {
				DBFoundConfig.basePath = basePath;
				info.append("(basePath = ").append(basePath).append(")");
			}
		}

		// interceptor 初始化
		Element filter = web.element("interceptor");
		if (filter != null) {
			String className = filter.getTextTrim();
			if (!"".equals(className)) {
				InterceptorEngine.init(className);
				info.append("(interceptor = ").append(className).append(")");
			}
		}

		// interceptor 初始化
		Element openSession = web.element("openSession");
		if (openSession != null) {
			String open = openSession.getTextTrim();
			if ("true".equals(open)) {
				DBFoundConfig.openSession = true;
				info.append("(openSession = true)");
			}else{
				DBFoundConfig.openSession = false;
				info.append("(openSession = false)");
			}
		}

		System.out.println(info);

		// 初始化dbfound mvc
		Element mvc = web.element("mvcConfigFile");
		String mvcFile = null;
		if (mvc == null || "".equals(mvc.getTextTrim())) {
			mvcFile = CLASSPATH + "/dbfound-mvc.xml";
		} else {
			mvcFile = mvc.getTextTrim();
		}
		File file = new File(getRealPath(mvcFile));
		if (!file.exists() && mvcFile.startsWith(CLASSPATH)) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource(mvcFile.substring(CLASSPATH.length() + 1));
			if (url != null) {
				if (url.getFile() != null) {
					file = new File(url.getFile());
				}
			}
		}
		if (file.exists()) {
			System.out.println(simpleDateFormat.format(new Date()) + " init mvc success, config file: "
					+ PathFormat.format(file.getAbsolutePath()) + ")");
			ActionEngine.init(file);
		} else {
			System.out.println(simpleDateFormat.format(new Date()) + " init mvc cancel, because file: "
					+ PathFormat.format(getRealPath(mvcFile)) + ") not found");
		}
	}

	private static void initSystem(Element system) {
		StringBuilder info = new StringBuilder();
		info.append(simpleDateFormat.format(new Date())).append(" set system Param:");

		// 设置日志开关
		Element log = system.element("openLog");
		if (log != null) {
			String openLog = log.getTextTrim();
			if ("false".equals(openLog.trim())) {
				DBFoundConfig.openLog = false;
				info.append("(openLog=false) ");
			} else if ("true".equals(openLog.trim())) {
				DBFoundConfig.openLog = true;
				info.append("(openLog=true) ");
			}
		}

		// 设置驼峰转化开关
		Element underscoreToCamelCase = system.element("underscoreToCamelCase");
		if (underscoreToCamelCase != null) {
			String open = underscoreToCamelCase.getTextTrim();
			if ("false".equals(open.trim())) {
				DBFoundConfig.underscoreToCamelCase = false;
				info.append("(underscoreToCamelCase=false) ");
			} else if ("true".equals(open.trim())) {
				DBFoundConfig.underscoreToCamelCase = true;
				info.append("(underscoreToCamelCase=true) ");
			}
		}

		// 设置下划线转化开关
		Element camelCaseToUnderscore = system.element("camelCaseToUnderscore");
		if (camelCaseToUnderscore != null) {
			String open = camelCaseToUnderscore.getTextTrim();
			if ("false".equals(open.trim())) {
				DBFoundConfig.camelCaseToUnderscore = false;
				info.append("(camelCaseToUnderscore=false) ");
			} else if ("true".equals(open.trim())) {
				DBFoundConfig.camelCaseToUnderscore = true;
				info.append("(camelCaseToUnderscore=true) ");
			}
		}

		// 设置model跟目录
		Element modeRoot = system.element("modeRootPath");
		if (modeRoot != null) {
			String modeRootPath = modeRoot.getTextTrim();
			if (!"".equals(modeRootPath)) {
				DBFoundConfig.modelLoadRoot = modeRootPath;
				info.append("(modeRootPath = ").append(modeRootPath).append(")");
			}
		}

		// 设置启动监听类
		Element listener = system.element("startListener");
		if (listener != null) {
			String className = listener.getTextTrim();
			if (!"".equals(className)) {
				listenerClass = className;
				info.append("(listenerClass = ").append(listenerClass).append(")");
			}
		}

		Element modelModifyCheckElement = system.element("modelModifyCheck");
		if (modelModifyCheckElement != null) {
			String modelModifyCheckConfig = modelModifyCheckElement.getTextTrim();
			if (!"".equals(modelModifyCheckConfig)) {
				if ("true".equals(modelModifyCheckConfig)){
					modelModifyCheck = true;
				}else{
					modelModifyCheck = false;
				}
				info.append("(modelModifyCheck = ").append(modelModifyCheckConfig).append(")");
			}
		}

		Element dateFormatElement = system.element("dateFormat");
		if (dateFormatElement != null) {
			String dateFormatConfig = dateFormatElement.getTextTrim();
			if (!"".equals(dateFormatConfig)) {
				dateFormat = dateFormatConfig;
				info.append("(dateFormat = ").append(dateFormatConfig).append(")");
			}
		}

		Element dateTimeFormatElement = system.element("dateTimeFormat");
		if (dateTimeFormatElement != null) {
			String dateTimeFormatConfig = dateTimeFormatElement.getTextTrim();
			if (!"".equals(dateTimeFormatConfig)) {
				dateTimeFormat = dateTimeFormatConfig;
				info.append("(dateTimeFormat = ").append(dateTimeFormatConfig).append(")");
			}
		}

		Element compareIgnoreCase = system.element("sqlCompareIgnoreCase");
		if (compareIgnoreCase != null) {
			String compareIgnoreCaseConfig = compareIgnoreCase.getTextTrim();
			DSqlConfig.setCompareIgnoreCase("true".equals(compareIgnoreCaseConfig));
			info.append("(sqlEqualsIgnoreCase = ").append(DSqlConfig.isCompareIgnoreCase()).append(")");
		}

		Element openDSql = system.element("openDSql");
		if (openDSql != null) {
			String openDSqlConfig = openDSql.getTextTrim();
			DSqlConfig.setOpenDSql("true".equals(openDSqlConfig));
			info.append("(openDSql = ").append(DSqlConfig.isOpenDSql()).append(")");
		}

		System.out.println(info);
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

	public static boolean isInited() {
		return inited;
	}

	public static void setInited(boolean inited) {
		DBFoundConfig.inited = inited;
	}

	public static String getClasspath() {
		if (classpath == null || "".equals(classpath)) {
			String cp = Thread.currentThread().getContextClassLoader().getResource("").getFile();
			File file = new File(cp);
			classpath = file.getAbsolutePath();
			classpath = PathFormat.format(classpath);
		}
		return classpath;
	}

	public static String getProjectRoot() {
		if (projectRoot == null || "".equals(projectRoot)) {
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
			if (configFilePath == null || "".equals(configFilePath)) {
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

	public static String getListenerClass() {
		return listenerClass;
	}

	public static void setListenerClass(String listenerClass) {
		DBFoundConfig.listenerClass = listenerClass;
	}

	public static List<DataSourceConnectionProvide> getDsp() {
		return dsp;
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

	public static String getTimeFormat() {
		return timeFormat;
	}

	public static void setTimeFormat(String timeFormat) {
		DBFoundConfig.timeFormat = timeFormat;
	}
}
