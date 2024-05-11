package com.nfwork.dbfound.web.action;

import java.io.File;
import java.util.*;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.web.base.ActionController;
import com.nfwork.dbfound.web.base.ActionMapping;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nfwork.dbfound.util.LogUtil;

public class ActionEngine {

	private static boolean devMode;

	private static File file;

	private static long fileLastModify;

	static Map<String, ActionBean> actionBeans = new HashMap<>();

	public static void initMappings(String paths){
		List<String> classPaths = new ArrayList<>();
		String[] pathArray = paths.split(";");
		for (String path : pathArray){
			classPaths.addAll(getClassPaths(path.trim()));
		}
		for (String classPath : classPaths){
			try {
				Class<?> clazz = Class.forName(classPath);
				if(!ActionController.class.isAssignableFrom(clazz)){
					throw new DBFoundRuntimeException("@ActionMapping can only be used in subclasses of ActionController");
				}
				ActionMapping mapping = clazz.getAnnotation(ActionMapping.class);
				if (mapping != null && DataUtil.isNotNull(mapping.value())) {
					ActionBean actionBean = new ActionBean();
					actionBean.setName(mapping.value());
					actionBean.setClassName(classPath);
					actionBean.setSingleton(true);
					actionBeans.put(actionBean.getName(), actionBean);
				}
			}catch (ClassNotFoundException exception){
				throw new DBFoundRuntimeException(exception);
			}
		}
	}

	private static List<String> getClassPaths(String packageName) {
		List<String> classPaths = new ArrayList<>();
		String packagePath = packageName.replace(".", "/");
		File packageDir = new File(Thread.currentThread().getContextClassLoader().getResource(packagePath).getPath());
		File[] files = packageDir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					classPaths.addAll(getClassPaths(packageName + "." + file.getName()));
				} else if (file.isFile()) {
					String name = packageName + "." + file.getName();
					if(name.endsWith(".class")) {
						classPaths.add(name.substring(0, name.length() - 6));
					}
				}
			}
		}
		return classPaths;
	}

	/**
	 * 将配置文件dbfound-mvc.xml中的Action的信息放到Map actionBeans中
	 *
	 */
	public static void init(File f) {
		file = f;
		fileLastModify = file.lastModified();
		SAXReader reader = new SAXReader();
		Document doc;
		try {
			doc = reader.read(file);
			Element root = doc.getRootElement();

			Element mode = root.element("devMode");
			if (mode != null && "true".equals(mode.attributeValue("value"))) {
				devMode = true;
			}

			List<File> files = new ArrayList<>();
			files.add(file);
			File fileFold = file.getParentFile();

			List<Element> importFile = root.elements("import");
			for (Element element : importFile) {
				String path = element.attributeValue("file");
				files.add(new File(fileFold, path));
			}

			while (!files.isEmpty()) {
				File file = files.get(0);
				readFile(reader, file);
				files.remove(file);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private static void readFile(SAXReader reader, File file) {
		Document doc;
		try {
			doc = reader.read(file);
			Element root = doc.getRootElement();

			/*
			 * 把Action的配置放到ActionMap
			 */
			List<Element> actionElemts = root.elements("action");
			for (Element element : actionElemts) {
				ActionBean actionBean = new ActionBean();
				actionBean.setName(element.attributeValue("name"));
				actionBean.setClassName(element.attributeValue("class"));
				actionBean.setSingleton("true".equals(element.attributeValue("singleton")));
				actionBeans.put(actionBean.getName(), actionBean);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void refresh() {
		if (file.lastModified() > fileLastModify) {
			LogUtil.info("refresh file dbfound-mvc.xml");
			fileLastModify = file.lastModified();
			SAXReader reader = new SAXReader();
			try {
				readFile(reader, file);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 查找ActionBean
	 */
	public static ActionBean findActionBean(String key) {
		if (devMode) {
			refresh();
		}
		return actionBeans.get(key);
	}

}
