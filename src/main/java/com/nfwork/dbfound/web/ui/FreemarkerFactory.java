package com.nfwork.dbfound.web.ui;

import java.io.File;
import java.util.Locale;

import javax.servlet.ServletContext;

import com.nfwork.dbfound.core.DBFoundConfig;
import com.nfwork.dbfound.exception.DBFoundRuntimeException;
import com.nfwork.dbfound.util.LogUtil;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.TemplateExceptionHandler;

public class FreemarkerFactory {

	private static Configuration config;

	/**
	 * 得到config
	 *
	 */
	public static Configuration getConfig(ServletContext context) {
		if (config == null) {
			init(context);
		}
		return config;
	}

	public static void setConfig(Configuration config) {
		FreemarkerFactory.config = config;
	}

	/**
	 * 初始化路径
	 */
	public synchronized static void init(ServletContext context) {
		if (config == null) {
			try {
				config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
				config.setEncoding(Locale.getDefault(), "utf-8");
				// 设置对象包装器
				config.setObjectWrapper(new DefaultObjectWrapper(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS));
				// 设置异常处理器
				config.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
				// 从什么地方加载freemarker模板文件

				String ftlPath = context.getRealPath("/DBFoundUI/template");
				if (ftlPath == null) {
					ftlPath = DBFoundConfig.getProjectRoot()+ "/DBFoundUI/template";
				}
				File fold = new File(ftlPath);
				if (!fold.exists()) {
					throw new DBFoundRuntimeException("/DBFoundUI/template 没有找到，请确认是否加入DBFoundUI到webRoot下");
				}
				config.setDirectoryForTemplateLoading(fold);
			} catch (Exception e) {
				LogUtil.error(e.getMessage(), e);
			}
		}
	}
}
