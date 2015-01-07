<@compress>
<#if div??>${div}</#if>
<script type="text/javascript">
   var  ${form.id} = new Ext.FormPanel( {
	   		<#if delayRender==false>renderTo : Ext.get("${form.id}_div"),</#if>
			id         : "${form.id}",
			<#if form.collapsible>collapsible:true,</#if>
			<#if form.collapsed>collapsed:true,</#if>
			<#if form.title??>title : "${form.title}",</#if>
   			<#if form.fileUpload>fileUpload: true,</#if>
			bodyStyle : "padding:5px 5px 0",
			<#if form.width??>width : ${form.width},
			<#else>width: Ext.get("${form.id}_div").getWidth(), 
			</#if>
			<#if form.height??> height : ${form.height},</#if>
			<#if form.labelWidth??> labelWidth : ${form.labelWidth},</#if>
			labelAlign : "right",
			defaults:{layout : "column",border : false,labelSeparator:"："},
			<#if buttons??>
			    buttonAlign :<#if form.toolBar.align??>"${form.toolBar.align}",
						     <#else>'center',
						     </#if>
				buttons: [
				    <#list buttons as button>  {
				    	text : "${button.title}",
    					id   : "${button.id}",
    					action  : "${button.action}",
    					height :23,
    					<#if button.disabled>disabled : true,</#if>
    					<#if button.icon??>icon : "${button.icon}",</#if>
    					handler : function() {
    						var data = ${form.id}.getData();
    						<#if  button.beforeAction?? >if(!${button.beforeAction}(data,${form.id},this)) return;</#if>
    						if (!${form.id}.form.isValid()) {$D.showMessage('验证通不过！');return;}
    						var button = this;
    						$D.request(this.action,data,function(obj,response, action) {
								if(obj.success==true)
								$D.showMessage(obj.message,function(){
									<#if button.afterAction!='null'>${button.afterAction}(obj,data,${form.id},button);</#if>
								});
								else $D.showError(obj.message);
		    				},true);
    					}
				     }<#if button_has_next> ,</#if>
				    </#list>
				],
		    </#if>
			items : [ 
			     <#list lines as line>
			          { defaults:{layout : "form",border : false},
			        	height : ${line.height},
						items : [
					    <#list line.fields as field>
						 {
							columnWidth :<#if field.columnWidth??> ${field.columnWidth}
							             <#else>  ${line.columnWidth}
								         </#if>,
							items : [ {
							   listeners: {
								 <#if field.upper>
									 <#list field.events as event>
								        <#if event.name =="enter">
									        specialKey :function(field,e){if (e.getKey() == Ext.EventObject.ENTER)${event.handle}(field,e);},
								        <#else>
								            ${event.name}:${event.handle},
								        </#if>
								     </#list>
								    keypress :function(t, e){DBFound.keypress(t, e,this);},	
								    blur:function(){DBFound.blurUpper(this);}
								 <#else>
								     <#list field.events as event>
								        <#if event.name =="enter">
									        specialKey :function(field,e){if (e.getKey() == Ext.EventObject.ENTER)${event.handle}(field,e);}
								        <#else>
								            ${event.name}:${event.handle}
								        </#if>
								        <#if event_has_next>,</#if>
								     </#list>
							     </#if>
							   },
							   name  : "${field.name}",
							   <#if field.hideLabel>hideLabel : true,</#if>
							   <#if field.items??>items : ${field.items},</#if>
							   <#if field.id??>id : "${field.id}",</#if>
							   <#if field.upper> enableKeyEvents : true,</#if>
							   <#if field.emptyText??> emptyText : "${field.emptyText}",</#if>
							   <#if field.prompt??> fieldLabel : "<#if field.required><font color = red>*</font> </#if>${field.prompt}",</#if>
							   <#if field.width??>width : ${field.width},
							   <#else>anchor:"${field.anchor}",</#if>
							   <#if field.height??>height : ${field.height},</#if>
							   <#if field.vtype??>vtype : "${field.vtype}",</#if>
							   <#if field.value??>value : "${field.value}",</#if>
							   <#if field.readOnly>
							      readOnly:true,
							      style:"background:"+$D.FieldReadOnlyBackground, 
							   </#if>
						       <#if field.required>allowBlank : false,</#if>
						       <#if field.editable ==false>editable : false,</#if>
						       <#if field.editor="textfield">
						       	   xtype : "textfield"
						       <#elseif field.editor="numberfield"> 
						       	   <#if field.allowDecimals==false>allowDecimals:false,</#if>
						       	   <#if field.allowNegative==false>allowNegative:false,</#if>
						       	   xtype : "numberfield"
						       <#elseif field.editor="datefield"> 
						       	   format : "Y-m-d" ,
						       	   xtype : "datefield"
							   <#elseif field.editor="combo" || field.editor="lovcombo" || field.editor="treecombo">
								   <#if field.options??> store : ${field.options},triggerAction : "all",</#if>
								   <#if field.displayField??>displayField : "${field.displayField}",</#if>
							       <#if field.valueField??>valueField : "${field.valueField}",</#if>
							       <#if field.hiddenName??>hiddenName : "${field.hiddenName}",</#if>
							       <#if field.parentField??>parentField : "${field.parentField}",</#if>
							       mode : "${field.mode}",
							       xtype : "${field.editor}"
							   <#elseif field.editor="lov">
								   <#if field.lovUrl??>lovUrl : "${field.lovUrl}",</#if>
								   <#if field.lovFunction??>onTriggerClick:${field.lovFunction},</#if>
								   triggerClass : 'x-form-search-trigger',xtype:'trigger'
							   <#elseif field.editor="spinnerfield">
								   <#if field.maxValue??>maxValue : "${field.maxValue}",</#if>
								   <#if field.minValue??>minValue : "${field.minValue}",</#if>
								   xtype:'spinnerfield'
							   <#elseif field.editor="checkbox" || field.editor="radio" >
							       <#if field.value??><#if field.value=field.checkedValue>checked: true,</#if></#if>
							       inputValue:"${field.checkedValue}",
							       xtype : "${field.editor}" 	 
							   <#elseif field.editor="label">
							       <#if field.value??>text : "${field.value}",</#if>
							       xtype : "label" 	   
							   <#elseif field.editor="password">
							       inputType : "password", xtype : "textfield"
							   <#elseif field.editor="file">
							       inputType:'file', xtype:'textfield'	  
							   <#elseif field.editor="button">
							       fieldLabel : null,
							       <#if field.prompt??>text:"${field.prompt}",</#if>
							       xtype : "button"
							   <#else> 
								   xtype : "${field.editor}"
							   </#if>
							} ]
						 }
						 <#if field_has_next> ,</#if>
					   </#list>
						 ]
					}
					 <#if line_has_next> ,</#if>
				</#list>
			 ]
	});
   <#if form.bindTarget??>
      ${form.id}.bindTarget = ${form.bindTarget};
      ${form.id}.initTarget();
      ${form.bindTarget}.on('load',function(){
    	  ${form.id}.initTarget();
      });
   </#if>
   
   Ext.onReady(function() {
	    var cmp = ${form.id};
		<#if form.width?? == false >
		    <#if div??>
		    	if($D.isScrolling())$D.adjustCmpWidth(cmp);
				Ext.fly(window).on("resize", function(){
					$D.adjustCmpWidth(cmp);
				});
			</#if>
		</#if>
		cmp.show();
		<#if delayRender>
		     setTimeout(function(){ 
			     cmp.render(Ext.get("${form.id}_div"));
		     },300);
	    </#if> 
   }); 
</script>
</@compress>