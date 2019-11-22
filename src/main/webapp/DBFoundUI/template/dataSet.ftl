<@compress>
<script type="text/javascript">
	var ${id} = new Ext.data.JsonStore( {
		idProperty: '_id',
		url : "${url}",
		id  : "${id}",
		root : "datas",
		totalProperty : "totalCounts",
		<#if fields??>
		  fields : [ ${fields} ]
	    <#else>
	      fields : [ <#if keySet??> <#list keySet as key> "${key}"  <#if key_has_next> ,</#if></#list></#if> ]
	    </#if>
	});
	<#if loadData> ${id}.loadData(${qro});</#if>
</script>
</@compress>