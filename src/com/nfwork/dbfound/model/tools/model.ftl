<?xml version="1.0" encoding="UTF-8"?>
<model xmlns="http://dbfound.googlecode.com/model" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
	xsi:schemaLocation="http://dbfound.googlecode.com/model https://raw.githubusercontent.com/nfwork/dbfound/master/tags/model.xsd">
	
	<query>
		<sql>
		 <![CDATA[
			 select ${pk.columnName},<#list table.columnlist as column>
				${column.columnName}<#if column_has_next>,</#if></#list>
			from ${table.tableName} t
			#WHERE_CLAUSE#
		 ]]>
		</sql>
	</query>
	
	<execute name="add">
		<#list table.columnlist as column>
		<param name="${column.paramName}" dataType="${column.type}" />
		</#list>
		<sqls>
			<executeSql>
			 <![CDATA[
			    insert into ${table.tableName}(
			    		<#list table.columnlist as column>
			    		${column.columnName}<#if column_has_next>,<#else>)</#if>
			    		</#list>		
				values(
						<#list table.columnlist as column>
			    		${column.express}<#if column_has_next>,<#else>)</#if>
			    		</#list>	
			 ]]>
			</executeSql>
		</sqls>
	</execute>

	<execute name="update">
		<#list table.columnlist as column>
		<param name="${column.paramName}" dataType="${column.type}" />
		</#list>
		<param name="${pk.columnName}" dataType="${pk.type}" />
		<sqls>
			<executeSql>
			  <![CDATA[
				update ${table.tableName}
				   set <#list table.columnlist as column>
					${column.columnName} = ${column.express}<#if column_has_next>,</#if></#list>		
				 where ${pk.columnName} = ${pk.express} 
			  ]]>
			</executeSql>
		</sqls>
	</execute>

	<execute name="delete">
		<param name="${pk.paramName}" dataType="${pk.type}" />
		<sqls>
			<executeSql>
			  <![CDATA[
				delete from ${table.tableName} where ${pk.columnName} = ${pk.express} 
			  ]]>
			</executeSql>
		</sqls>
	</execute>
</model>
