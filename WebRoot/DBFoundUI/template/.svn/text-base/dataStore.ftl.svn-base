<@compress>
<script type="text/javascript">
	var ${ds.id} = new Ext.data.JsonStore( {
		idProperty: '_id',
		<#if ds.url??>url : "${ds.url}",</#if>
		<#if ds.autoLoad>autoLoad : true,</#if>
		id  : "${ds.id}",
		root : "datas",
		totalProperty : "totalCounts",
		fields : [ ${ds.fields} ]
	});
	<#if data??> ${ds.id}.loadData(${data});</#if>
</script>
</@compress>