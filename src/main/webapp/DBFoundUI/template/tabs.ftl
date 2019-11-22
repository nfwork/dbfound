<@compress>
<div id="${t.id}_outdiv" style="overflow:hidden;margin-left:5px;margin-right:5px;margin-top:5px;padding-bottom:5px;${t.style}">
<div id="${t.id}_div"></div>
</div>
<script type="text/javascript">
		var ${t.id} = new Ext.TabPanel( {
			<#if t.height??>height : ${t.height},</#if>
			id : "${t.id}" ,
			<#if t.width??>width : ${t.width},
			<#else>width: Ext.get("${t.id}_div").getWidth(), 
			</#if>
			activeTab : 0,
			animScroll : true,  
			enableTabScroll : true,
			deferredRender : false,
			applyTo : '${t.id}_div',
			<#if t.plain>plain:true,</#if>
	        defaults:{autoScroll: true},
			items : [ 
				<#list tabs as tab>
				   { title: '${tab.title}',
					 id : '${tab.id}',
					 <#if tab.height??>height : ${tab.height},</#if>
					 <#if tab.contentCmp??>contentCmp : [${tab.contentCmp}],</#if>
					 <#if tab.closable>closable:true,</#if>
					 <#if tab.initUrl?? && tab.initUrl != "">inited:true,</#if>
					 <#if tab.url??>
				         url:'${tab.url}',
				         html:"<div style='height:100%;overflow:hidden'><iframe id='${tab.id}_iframe' src='${tab.initUrl}' frameBorder=0 width='100%' height='100%'></iframe></div>"
				     <#else>
					     <#if tab.height??==false && t.height??==false>autoHeight :true,</#if>
				         html:"${tab.html}"
				     </#if> 
				    }
				  <#if tab_has_next> ,</#if>
				</#list>
			 ]
		});
		
	    Ext.onReady(function() {
	    	var cmp = ${t.id};
			<#if t.width?? == false>
				$D.adjustCmpWidth(cmp);	
				Ext.fly(window).on("resize", function(){
					$D.adjustCmpWidth(cmp);
			    });
			</#if>  
			cmp.show();
	    });  
</script>
</@compress>