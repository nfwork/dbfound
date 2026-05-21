package dbfound.test.core;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.model.ModelReader;
import com.nfwork.dbfound.model.bean.Model;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;

public class ModelReaderTest {

	@Test
	public void testReadClasspathModelAsPkgModelWhenModifyCheckDisabled() throws Exception {
		ConfigSnapshot snapshot = ConfigSnapshot.take();
		try {
			setConfigField("modelRootPath", DBFoundConfig.CLASSPATH + "/model");
			setConfigField("modelModifyCheck", false);

			Model model = new TestModelReader().readModel("test/user");

			Assert.assertTrue(model.isPkgModel());
			Assert.assertTrue(model.getFileLocation().contains("/model/test/user.xml"));
			Assert.assertNotNull(model.getQuery("enabled"));
		} finally {
			snapshot.restore();
		}
	}

	@Test
	public void testReadClasspathModelAsFileWhenModifyCheckEnabledAndContextClassLoaderIsNull() throws Exception {
		ConfigSnapshot snapshot = ConfigSnapshot.take();
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		try {
			setConfigField("modelRootPath", DBFoundConfig.CLASSPATH + "/model");
			setConfigField("modelModifyCheck", true);
			Thread.currentThread().setContextClassLoader(null);

			Model model = new TestModelReader().readModel("test/user");

			Assert.assertFalse(model.isPkgModel());
			Assert.assertTrue(new File(model.getFileLocation()).exists());
			Assert.assertTrue(model.getFileLastModify() > 0);
			Assert.assertNotNull(model.getQuery("enabled"));
		} finally {
			Thread.currentThread().setContextClassLoader(contextClassLoader);
			snapshot.restore();
		}
	}

	@Test
	public void testReadModelFromNonClasspathRootPath() throws Exception {
		ConfigSnapshot snapshot = ConfigSnapshot.take();
		try {
			File modelFile = new File(ModelReaderTest.class.getClassLoader().getResource("model/test/user.xml").toURI());
			File modelRoot = modelFile.getParentFile().getParentFile();
			setConfigField("modelRootPath", modelRoot.getAbsolutePath());
			setConfigField("modelModifyCheck", false);

			Model model = new TestModelReader().readModel("test/user");

			Assert.assertFalse(model.isPkgModel());
			Assert.assertTrue(new File(model.getFileLocation()).exists());
			Assert.assertEquals(modelFile.lastModified(), model.getFileLastModify());
			Assert.assertNotNull(model.getQuery("enabled"));
		} finally {
			snapshot.restore();
		}
	}

	private static Object getConfig() throws ReflectiveOperationException {
		Field field = DBFoundConfig.class.getDeclaredField("config");
		field.setAccessible(true);
		return field.get(null);
	}

	private static Object getConfigField(String name) throws ReflectiveOperationException {
		Object config = getConfig();
		Field field = config.getClass().getDeclaredField(name);
		field.setAccessible(true);
		return field.get(config);
	}

	private static void setConfigField(String name, Object value) throws ReflectiveOperationException {
		Object config = getConfig();
		Field field = config.getClass().getDeclaredField(name);
		field.setAccessible(true);
		field.set(config, value);
	}

	private static class TestModelReader extends ModelReader {
		private Model readModel(String modelName) {
			return readerModel(modelName);
		}
	}

	private static class ConfigSnapshot {
		private final String modelRootPath;
		private final boolean modelModifyCheck;

		private ConfigSnapshot() throws ReflectiveOperationException {
			this.modelRootPath = (String) getConfigField("modelRootPath");
			this.modelModifyCheck = (Boolean) getConfigField("modelModifyCheck");
		}

		private static ConfigSnapshot take() throws ReflectiveOperationException {
			return new ConfigSnapshot();
		}

		private void restore() throws ReflectiveOperationException {
			setConfigField("modelRootPath", modelRootPath);
			setConfigField("modelModifyCheck", modelModifyCheck);
		}
	}
}
