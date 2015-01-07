<@compress>
<#if div??>${div}</#if>
<script type="text/javascript">
	var ${tree.id} = new Ext.tree.TreePanel( {
		renderTo : '${tree.id}_div',
		<#if tree.title??>title : '${tree.title}',</#if>
		<#if tree.height??>height : ${tree.height},</#if>
		<#if tree.width??>width : ${tree.width},
		<#else>width : "100%",
		</#if>
		<#if tree.showCheckBox>showCheckBox : true,</#if>
		<#if tree.bindTarget??>bindTarget : ${tree.bindTarget},</#if>
		parentField:'${tree.parentField}',
		idField:'${tree.idField}',
		displayField:'${tree.displayField}',
		id:'${tree.id}',
		useArrows : true,
		autoScroll : true,
		selModel: new Ext.tree.MultiSelectionModel(),
		animate : true,
		containerScroll : true,
		rootVisible : false,
		root : {
			nodeType : 'node'
		},
		listeners: {
			<#list tree.events as event>
			  '${event.name}':${event.handle}
			  <#if event_has_next> ,</#if>
			</#list>
		}
	}).init();
	
	<#if tree.bindTarget??>
		${tree.bindTarget}.on("load",function(){${tree.id}.refresh();});
	</#if>
</script>
</@compress>