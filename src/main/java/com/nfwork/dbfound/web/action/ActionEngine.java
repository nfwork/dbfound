package com.nfwork.dbfound.web.action;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.PackageScannerUtil;
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

	private final static Map<String, ActionBean> actionBeans = new ConcurrentHashMap<>();

	public static void initMappings(String paths){
		Set<String> classPaths = new LinkedHashSet<>();
		String[] pathArray = paths.split(";");
		for (String path : pathArray){
			classPaths.addAll(PackageScannerUtil.getClassPaths(path.trim()));
		}
		int count =0;
		for (String classPath : classPaths){
			try {
				Class<?> clazz = Class.forName(classPath);
				ActionMapping mapping = clazz.getAnnotation(ActionMapping.class);
				if (mapping != null && DataUtil.isNotNull(mapping.value())) {
					if(!ActionController.class.isAssignableFrom(clazz)){
						throw new DBFoundRuntimeException("@ActionMapping can only be used in subclasses of ActionController");
					}
					ActionBean actionBean = new ActionBean();
					actionBean.setName(mapping.value());
					actionBean.setClassName(classPath);
					actionBean.setSingleton(true);
					actionBeans.put(actionBean.getName(), actionBean);
					count ++;
				}
			}catch (ClassNotFoundException exception){
				throw new DBFoundRuntimeException(exception);
			}
		}
		LogUtil.info("actionEngine initMvcMappings success, paths: "+paths+", init ActionBeans size: "+ count);
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

			List<Element> actionElements = root.elements("action");
			if(actionElements == null){
				return;
			}
			for (Element element : actionElements) {
				ActionBean actionBean = new ActionBean();
				actionBean.setName(element.attributeValue("name"));
				actionBean.setClassName(element.attributeValue("class"));
				actionBean.setSingleton("true".equals(element.attributeValue("singleton")));
				actionBeans.put(actionBean.getName(), actionBean);
			}

			LogUtil.info("actionEngine initMvcFile success, file: "+file+", init ActionBeans size: "+ actionElements.size());
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
