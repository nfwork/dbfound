package com.nfwork.dbfound.model.tools;

import java.util.Locale;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkFactory {

	private static Configuration cfg;
	private static final String ftlPath = "/com/nfwork/dbfound/model/tools";

	/**
	 * 得到config
	 * @return
	 */
	public static Configuration getConfig() {
		if (cfg == null) {
			try {
				cfg = new Configuration();
				cfg.setEncoding(Locale.getDefault(), "utf-8");
				// 设置对象包装器
				cfg.setObjectWrapper(new DefaultObjectWrapper());
				// 设置异常处理器
				cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
				// 从什么地方加载freemarker模板文件
				cfg.setClassForTemplateLoading(Configuration.class, ftlPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return cfg;
	}

}
