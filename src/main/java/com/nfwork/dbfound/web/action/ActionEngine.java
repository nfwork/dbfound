package com.nfwork.dbfound.web.action;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.PackageScannerUtil;
import com.nfwork.dbfound.util.StringUtil;
import com.nfwork.dbfound.web.base.ActionController;
import com.nfwork.dbfound.web.base.ActionMapping;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nfwork.dbfound.util.LogUtil;

public class ActionEngine {

	private final static Map<String, ActionBean> actionBeans = new ConcurrentHashMap<>();

	public static void initMappings(String paths){
		Set<String> classPaths = new LinkedHashSet<>();
		List<String> pathList = StringUtil.splitToList(paths);
		for (String path : pathList){
			classPaths.addAll(PackageScannerUtil.getClassPaths(path));
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
					if(actionBeans.get(actionBean.getName()) != null){
						throw new DBFoundRuntimeException("actionEngine initMvcMappings failed, because path: '" + actionBean.getName() +"' already exists");
					}
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
	public static void init(String mvcFile) {
		try {
			File file = new File(DBFoundConfig.getRealPath(mvcFile));
			if (file.exists()) {
				SAXReader reader = new SAXReader();
				Document doc = reader.read(file);
				readDocument(doc, file.toString());
			}else if (mvcFile.startsWith(DBFoundConfig.CLASSPATH)) {
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				URL url = loader.getResource(mvcFile.substring(DBFoundConfig.CLASSPATH.length() + 1));
				if (url != null) {
					SAXReader reader = new SAXReader();
					Document doc = reader.read(url);
					readDocument(doc, url.getFile());
				}
			}
		} catch (DocumentException e) {
			throw new DBFoundRuntimeException("actionEngine init mvcFile failed, cause by "+ e.getMessage(),e);
        }
    }

	private static void readDocument(Document doc, String filePath) {
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
			if(actionBeans.get(actionBean.getName()) != null){
				throw new DBFoundRuntimeException("actionEngine initMvcFile failed, because path: '" + actionBean.getName() +"' already exists");
			}
			actionBeans.put(actionBean.getName(), actionBean);
		}
		LogUtil.info("actionEngine initMvcFile success, file: "+ filePath +", init ActionBeans size: "+ actionElements.size());
	}

	/**
	 * 查找ActionBean
	 */
	public static ActionBean findActionBean(String key) {
		return actionBeans.get(key);
	}

}
