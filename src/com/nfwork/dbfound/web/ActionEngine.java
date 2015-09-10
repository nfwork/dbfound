package com.nfwork.dbfound.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nfwork.dbfound.util.LogUtil;

public class ActionEngine {

	private static boolean devMode;

	private static File file;

	private static long fileLastModify;

	static Map<String, ActionBean> actionBeans;

	/**
	 * 将配置文件dbfound-mvc.xml中的Action的信息放到Map actionBeans中
	 * 
	 * @param f
	 */
	@SuppressWarnings("unchecked")
	public static void init(File f) {
		file = f;
		fileLastModify = file.lastModified();
		SAXReader reader = new SAXReader();
		Document doc = null;
		try {
			doc = reader.read(file);
			Element root = doc.getRootElement();

			Element mode = root.element("devMode");
			if (mode != null && "true".equals(mode.attributeValue("value"))) {
				devMode = true;
			}

			actionBeans = new HashMap<String, ActionBean>();
			List<File> files = new ArrayList<File>();
			files.add(file);
			File fileFold = file.getParentFile();

			List<Element> importFile = root.elements("import");
			for (Element element : importFile) {
				String path = element.attributeValue("file");
				files.add(new File(fileFold, path));
			}

			while (files.isEmpty() == false) {
				File file = files.get(0);
				readFile(reader, file);
				files.remove(file);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	@SuppressWarnings("unchecked")
	static void readFile(SAXReader reader, File file) {
		Document doc = null;
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

	static void refresh() {
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
	 * 
	 * @param key
	 * @return
	 */
	public static ActionBean findActionBean(String key) {
		if (devMode) {
			refresh();
		}
		return actionBeans.get(key);
	}

	static boolean isInited() {
		if (actionBeans == null) {
			return false;
		} else {
			return true;
		}
	}

}
