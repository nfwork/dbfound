package com.nfwork.dbfound.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.MethodExpression;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;

public class ELHelper {
	@SuppressWarnings("unchecked")
	public static Object evaluate(String paramString, Class paramClass,
			PageContext paramPageContext, FunctionMapper paramFunctionMapper) {
		ValueExpression localValueExpression = createValueExpression(
				paramString, paramClass, paramPageContext, paramFunctionMapper);

		return localValueExpression.getValue(paramPageContext.getELContext());
	}

	@SuppressWarnings("unchecked")
	public static ValueExpression createValueExpression(String paramString,
			Class paramClass, PageContext paramPageContext,
			FunctionMapper paramFunctionMapper) {
		ExpressionFactory localExpressionFactory = getExpressionFactory(paramPageContext);
		
		ELContext localELContextImpl = paramPageContext
				.getELContext();
		
		return localExpressionFactory.createValueExpression(localELContextImpl,
				paramString, paramClass);
	}

	@SuppressWarnings("unchecked")
	public static MethodExpression createMethodExpression(String paramString,
			Class paramClass, Class[] paramArrayOfClass,
			PageContext paramPageContext, FunctionMapper paramFunctionMapper) {
		ExpressionFactory localExpressionFactory = getExpressionFactory(paramPageContext);
		
		ELContext localELContextImpl = paramPageContext.getELContext();

		return localExpressionFactory.createMethodExpression(
				localELContextImpl, paramString, paramClass, paramArrayOfClass);
	}

	public static void mapValueExpression(PageContext paramPageContext,
			String paramString, ValueExpression paramValueExpression) {
		ELContext localELContext = paramPageContext.getELContext();
		localELContext.getVariableMapper().setVariable(paramString,
				paramValueExpression);
	}

	public static void mapMethodExpression(PageContext paramPageContext,
			String paramString, MethodExpression paramMethodExpression) {
		ExpressionFactory localExpressionFactory = getExpressionFactory(paramPageContext);
		ValueExpression localValueExpression = localExpressionFactory
				.createValueExpression(paramMethodExpression, Object.class);
		mapValueExpression(paramPageContext, paramString, localValueExpression);
	}

	private static ExpressionFactory getExpressionFactory(
			PageContext paramPageContext) {
		JspApplicationContext localJspApplicationContext = Holder.jspFactory
				.getJspApplicationContext(paramPageContext.getServletContext());

		return localJspApplicationContext.getExpressionFactory();
	}

	private static class Holder {
		static JspFactory jspFactory = JspFactory.getDefaultFactory();
	}
}