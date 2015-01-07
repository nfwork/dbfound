<@compress>
<script type="text/javascript" src="DBFoundUI/plugin/TreeGrid.js" charset='utf-8'></script>
<#if div??>${div}</#if>
<script type="text/javascript">
	<#if buttons??>
		var ${tree.id}_tbar ={
				<#if tree.toolBar.align??>buttonAlign:'${tree.toolBar.align}',</#if>
				items:[ 
		       			<#list buttons as button>
					    {
							<#if button.title??>text : "${button.title}",</#if>
							<#if button.disabled>disabled : true,</#if>
							<#if  button.action?? >action  : "${button.action}",</#if>
							<#if button.icon??>icon : "${button.icon}", </#if>
							<#if button.beforeAction?? && button.beforeAction !="">handler :${button.beforeAction},</#if>
							id   : "${button.id}"
						}
					   <#if button_has_next> ,</#if>
					</#list>
		]};
	</#if>

	var ${tree.id} = new Ext.ux.tree.TreeGrid( {
		<#if buttons??>tbar : ${tree.id}_tbar,</#if>
		renderTo : '${tree.id}_div',
		<#if tree.title??>title : '${tree.title}',</#if>
		<#if tree.height??>height : ${tree.height},</#if>
		<#if tree.width??>width : ${tree.width},
		<#else>width : Ext.get("${tree.id}_div").getWidth(),
		</#if>
		<#if tree.showCheckBox>showCheckBox : true,</#if>
		<#if tree.bindTarget??>bindTarget : ${tree.bindTarget},
		<#else>bindTarget : new Ext.data.JsonStore( {
								idProperty: '_id',
								url : "${tree.queryUrl}",
								root : "datas",
								totalProperty : "totalCounts",
								fields : [ "${tree.idField}","${tree.parentField}",
									<#list columns as column>,"${column.name}"</#list>
							    ]
							}),
		</#if>
		parentField:'${tree.parentField}',
		idField:'${tree.idField}',
		columns:[ 
					<#list columns as column>
					   {dataIndex :  "${column.name}",
						width :  ${column.width},
						<#if column.id??>id :  "${column.id}",</#if>
						<#if column.align??>align : "${column.align}",</#if>
						<#if column.sortable>sortable : true, </#if>
						<#if column.hidden>hidden : true, </#if>
						<#if column.prompt??>header :  "${column.prompt}" </#if>
						}<#if column_has_next> ,</#if>
					</#list>
		        ],
		id:'${tree.id}',
		useArrows : true,
		selModel: new Ext.tree.MultiSelectionModel(),
		animate : true,
		query : function(){this.bindTarget.load();},
		listeners: {
			<#list tree.events as event>
			  '${event.name}':${event.handle}
			  <#if event_has_next> ,</#if>
			</#list>
		}
	}).init();
	
	<#if tree.queryForm??>
		${tree.id}.bindTarget.queryForm = ${tree.queryForm};
	</#if>
	
	<#if tree.viewForm??>
	   ${tree.id}.on("click",function(node){
		   var viewForm = Ext.getCmp("${tree.viewForm}");
		   if(viewForm){
			   viewForm.clear();
			   viewForm.setData(node.json);
		   }
	   });
	</#if> 
	
	${tree.id}.bindTarget.on("beforeload",function(){
		${tree.id}.getEl().mask("读取中...", 'x-mask-loading');
	});
    
	${tree.id}.bindTarget.on("load",function(){
    	${tree.id}.refresh();
    	<#if tree.viewForm??>
	    	var viewForm = Ext.getCmp("${tree.viewForm}");
	    	if(viewForm){
	    		viewForm.clear();
	    		viewForm.setData(${tree.id}.getCurrentNodeData());
	    	}	
    	</#if>
    	${tree.id}.getEl().unmask();
    });
	
	Ext.onReady(function() {
       var cmp = ${tree.id};
       <#if tree.autoQuery>	
	       cmp.query();
	   </#if> 
	   <#if tree.width?? == false>
	       <#if div??>
	           $D.adjustCmpWidth(cmp);
			   Ext.fly(window).on("resize", function(){
					$D.adjustCmpWidth(cmp);
			   });
		   </#if> 
	   </#if> 
	   cmp.show();
    });
</script>
</@compress>