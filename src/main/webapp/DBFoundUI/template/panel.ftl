<@compress>
<div style="margin-left:5px;margin-right:5px;margin-top:5px;margin-bottom:5px;${panel.style}" id="${panel.id}_div"></div>
<script type="text/javascript">
var ${panel.id} = new Ext.Panel({
    <#if panel.contentCmp??>contentCmp : [${panel.contentCmp}],</#if>
    <#if panel.collapsible>collapsible:true,</#if>
	<#if panel.collapsed>collapsed:true,</#if>
	<#if panel.width??>width : ${panel.width},
	<#else>width:Ext.get("${panel.id}_div").getWidth(),
	</#if>
	height : ${panel.height},
	title: '${panel.title}',
	id : "${panel.id}",
    renderTo: '${panel.id}_div',
    html:"${panel.html}",
    autoScroll:true
});
Ext.onReady(function() {
	var cmp = ${panel.id};
	cmp.show();
	<#if panel.width?? == false>
		if($D.isScrolling())$D.adjustCmpWidth(cmp);	
		Ext.fly(window).on("resize", function(){
			$D.adjustCmpWidth(cmp);
	   });
	</#if>  
});  
</script>
</@compress>