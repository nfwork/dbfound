package com.nfwork.dbfound.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.dom4j.Document;
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

	/**
	 * 读取一个model
	 *
	 */
	static Model readerModel(String modelName) {

		SAXReader reader = new SAXReader();

		Document doc ;
		String fileLocation ;

		String filePath = DBFoundConfig.getModelLoadRoot() + "/" + modelName + ".xml";
		File file = new File(DBFoundConfig.getRealPath(filePath));

		boolean pkgModel = false;

		if (file.exists()) {
			fileLocation = file.getAbsolutePath();
			try (FileInputStream inputStream = new FileInputStream(file)){
				doc = reader.read(inputStream);
			} catch (Exception e) {
				String message = "modelReader exception，file: " + fileLocation;
				throw new DBFoundPackageException(message, e);
			}
		} else {
			if (filePath.startsWith(DBFoundConfig.CLASSPATH)) {
				String cPath = filePath.substring(DBFoundConfig.CLASSPATH.length() + 1);

				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				URL url = loader.getResource(cPath);

				if (url != null) {
					if (url.getFile() != null) {
						file = new File(url.getFile());
					}
					if (file.exists()) {
						fileLocation = file.getAbsolutePath();
						try (FileInputStream inputStream = new FileInputStream(file)){
							doc = reader.read(inputStream);
						} catch (Exception e) {
							String message = "modelReader exception, file: " + fileLocation;
							throw new DBFoundPackageException(message, e);
						}
					} else {
						fileLocation = url.getFile();
						pkgModel = true;
						try (InputStream inputStream = url.openStream()){
							doc = reader.read(inputStream);
						} catch (Exception e) {
							String message = "modelReader exception, url: " + fileLocation;
							throw new DBFoundPackageException(message, e);
						}
					}
				} else {
					throw new DBFoundRuntimeException("ModelReader can not found the file: " + modelName + ".xml, please check config");
				}

			} else {
				throw new DBFoundRuntimeException("ModelReader can not found the file: " + DBFoundConfig.getModelLoadRoot() + "/" + modelName + ".xml , please check config");
			}
		}

		Element root = doc.getRootElement();
		Model model = new Model(modelName);
		model.setFileLastModify(file.lastModified());
		model.setPkgModel(pkgModel);

		model.init(root);
		readerChild(root, model);
		model.run();

		fileLocation = PathFormat.format(fileLocation);
		model.setFileLocation(fileLocation);
		LogUtil.info("read model success, model file location: " + fileLocation);
		return model;
	}

	/**
	 * 初始化 他的儿子节点 信息
	 */
	static void readerChild(Element parent, Entity parentEntity) {
		List<Element> elements = parent.elements();
		for (Element unit : elements) {
			String className = getClassName(unit);
			Entity entity ;
			try {
				entity = (Entity) Class.forName(className).newInstance();
				entity.setParent(parentEntity);
				entity.init(unit);
				readerChild(unit, entity);
				entity.run();
			} catch (Exception e) {
				if(e instanceof DBFoundRuntimeException){
					throw (DBFoundRuntimeException)e;
				}else{
					String message = "ModelReader exception:" + e.getMessage();
					throw new DBFoundPackageException(message, e);
				}
			}
		}
	}

	/**
	 * 得到 对应的class 名字
	 *
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

}
