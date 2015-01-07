package com.nfwork.dbfound.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.core.PathFormat;
import com.nfwork.dbfound.exception.DBFoundPackageException;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.model.base.Entity;
import com.nfwork.dbfound.model.bean.Model;
import com.nfwork.dbfound.util.LogUtil;

public class ModelReader {

	static String modelLoadRoot;
	static String modelLoadRootLock = "";
	static String classpath;
	static String classpathLock = "";

	/**
	 * 读取一个model
	 * 
	 * @param modelName
	 * @return
	 * @throws Exception
	 */
	static Model readerModel(String modelName) {

		SAXReader reader = new SAXReader();

		Document doc = null;
		String fileLocation = null;

		if (modelLoadRoot == null) {
			synchronized (modelLoadRootLock) {
				if (modelLoadRoot == null) {
					modelLoadRoot = DBFoundConfig.getClasspath();
				}
			}
		}
		String filePath = modelLoadRoot + "/" + modelName + ".xml";
		File file = new File(filePath);

		if (file.exists()) {
			fileLocation = file.getAbsolutePath();
			try {
				doc = reader.read(file);
			} catch (DocumentException e) {
				String message = "modelReader 读取文件异常，file:" + fileLocation;
				throw new DBFoundPackageException(message, e);
			}
		} else {
			if (classpath == null) {
				synchronized (classpathLock) {
					if (classpath == null) {
						classpath = DBFoundConfig.getClasspath();
					}
				}
			}

			if (filePath.startsWith(classpath)) {
				String cPath = filePath.substring(classpath.length() + 1);

				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				InputStream inputStream = null;

				try {
					URL url = loader.getResource(cPath);

					if (url != null) {
						try {
							inputStream = url.openStream();
							doc = reader.read(inputStream);
						} catch (Exception e) {
							String message = "modelReader读取文件流异常";
							throw new DBFoundPackageException(message, e);
						}
						fileLocation = url.getFile();
					} else {
						throw new DBFoundRuntimeException("ModelReader 没有找到文件：" + modelName
								+ ".xml, 请确认在classpath 或jar中 文件是否存在。");
					}
				} finally {
					if (inputStream != null) {
						try {
							inputStream.close();
						} catch (IOException e) {
						}
					}
				}
			} else {
				throw new DBFoundRuntimeException("ModelReader 没有找到文件：" + modelLoadRoot + "/" + modelName
						+ ".xml 请确认配置");
			}
		}

		Element root = doc.getRootElement();
		Model model = new Model(modelName);
		model.setFileLastModify(file.lastModified());
		model.init(root);
		readerChrild(root, model);
		model.run();

		fileLocation = PathFormat.format(fileLocation);
		model.setFileLocation(fileLocation);
		LogUtil.info("read model success,model file location: " + fileLocation);
		return model;
	}

	/**
	 * 初始化 他的儿子节点 信息
	 * 
	 * @param father
	 * @param fatherEntity
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	static void readerChrild(Element parent, Entity parentEntity) {
		List<Element> elements = parent.elements();
		for (Element unit : elements) {
			String className = getClassName(unit);
			Entity entity = null;
			try {
				entity = (Entity) Class.forName(className).newInstance();
				entity.setParent(parentEntity);
				entity.init(unit);
				readerChrild(unit, entity);
				entity.run();
			} catch (Exception e) {
				LogUtil.error(e.getMessage(), e);
				throw new DBFoundRuntimeException("文件读取异常，请检查xml文件语法！");
			}
		}
	}

	/**
	 * 得到 对应的class 名字
	 * 
	 * @param element
	 * @return
	 */
	static String getClassName(Element element) {
		String path = element.getNamespace().getText();
		if ("http://dbfound.googlecode.com/model".equals(path)) {
			path = "com.nfwork.dbfound.model.bean.";
		} else {
			path = path + ".";
		}
		String name = element.getName();
		return path + name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static String getModelLoadRoot() {
		return modelLoadRoot;
	}

	public static void setModelLoadRoot(String modelLoadRoot) {
		ModelReader.modelLoadRoot = modelLoadRoot;
	}
	
}
