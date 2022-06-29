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
import com.nfwork.dbfound.util.DataUtil;
import com.nfwork.dbfound.util.LogUtil;

public class ModelReader {
	static String modelLoadRoot = DBFoundConfig.CLASSPATH;

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

		if (DataUtil.isNull(modelLoadRoot)) {
			modelLoadRoot = DBFoundConfig.CLASSPATH;
		}
		String filePath = modelLoadRoot + "/" + modelName + ".xml";
		File file = new File(DBFoundConfig.getRealPath(filePath));

		if (file.exists()) {
			fileLocation = file.getAbsolutePath();
			try {
				doc = reader.read(file);
			} catch (DocumentException e) {
				String message = "modelReader exception，file:" + fileLocation;
				throw new DBFoundPackageException(message, e);
			}
		} else {
			if (filePath.startsWith(DBFoundConfig.CLASSPATH)) {
				String cPath = filePath.substring(DBFoundConfig.CLASSPATH.length() + 1);

				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				InputStream inputStream = null;

				try {
					URL url = loader.getResource(cPath);

					if (url != null) {
						if (url.getFile() != null) {
							file = new File(url.getFile());
						}
						if (file.exists()) {
							fileLocation = file.getAbsolutePath();
							try {
								doc = reader.read(file);
							} catch (Exception e) {
								String message = "modelReader exception, file:" + fileLocation;
								LogUtil.error(message,e);
								throw new DBFoundPackageException(message, e);
							}
						} else {
							fileLocation = url.getFile();
							try {
								inputStream = url.openStream();
								doc = reader.read(inputStream);
							} catch (Exception e) {
								String message = "modelReader exception, url:" + fileLocation;
								LogUtil.error(message,e);
								throw new DBFoundPackageException(message, e);
							}
						}
					} else {
						throw new DBFoundRuntimeException("ModelReader not found file：" + modelName + ".xml, please check config");
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
				throw new DBFoundRuntimeException("ModelReader not found file：" + modelLoadRoot + "/" + modelName + ".xml , please check config");
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
		LogUtil.info("read model success, model file location: " + fileLocation);
		return model;
	}

	/**
	 * 初始化 他的儿子节点 信息
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
				if(e instanceof DBFoundRuntimeException){
					LogUtil.error(e.getMessage(), e);
					throw (DBFoundRuntimeException)e;
				}else{
					String message = "ModelReader exception:" + e.getMessage();
					LogUtil.error(message, e);
					throw new DBFoundRuntimeException(message, e);
				}
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
