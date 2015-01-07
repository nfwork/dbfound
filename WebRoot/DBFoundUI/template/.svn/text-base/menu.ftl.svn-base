<@compress>
<script type="text/javascript">
	var ${menu.id} = new Ext.menu.Menu({           
		id: '${menu.id}',              
		<#if menu.width??>width:${menu.width},</#if>
		<#if menu.height??>height:${menu.height},</#if>
		items: [  
		    <#list menu.items as item>   
		    {              
		    	<#if item.id??>id:'${item.id}', </#if>   
		    	<#if item.menu??>menu:${item.menu},</#if>   
				<#if item.icon??>icon:"${item.icon}", </#if>   
		    	<#if item.click??>handler:${item.click},</#if>
		    	<#if item.disabled>disabled:true,</#if>
		    	text: '${item.title}'
			}     
		    <#if item_has_next> ,</#if>
		    </#list>
		]          
	});     
</script>
</@compress>