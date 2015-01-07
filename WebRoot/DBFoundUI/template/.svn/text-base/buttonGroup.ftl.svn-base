<@compress>
<#if div??>${div}</#if>
<script type="text/javascript">
    var ${buttonGroup.id} = new Ext.ButtonGroup({
			 id : "${buttonGroup.id}",
			 columns: ${buttons?size},
			 frame :false,
			 renderTo  : Ext.get("${buttonGroup.id}_div"),
			 defaults :{style : "margin-left:3px;margin-right:3px;"},
		     items: [
					<#list buttons as button>
					{
						  id        : "${button.id}",
						  <#if button.disabled>disabled : true,</#if>
						  <#if button.title??>text : "${button.title}",</#if>
					      <#if button.icon??>icon : "${button.icon}",</#if>
						  <#if button.click?? && button.click != "">handler : ${button.click},</#if>
						  height    : ${button.height},
						  width     : ${button.width}
					}
					<#if button_has_next> ,</#if>
					</#list>
		      ]
	});
    
    $D.adjustCmpWidth(${buttonGroup.id});
    
	Ext.onReady(function() {
		var cmp = ${buttonGroup.id};
		<#if div??>
			if($D.isScrolling())$D.adjustCmpWidth(cmp);
			Ext.fly(window).on("resize", function(){
				$D.adjustCmpWidth(cmp);
			});
		</#if>
		cmp.show();
    }); 
</script>
</@compress>