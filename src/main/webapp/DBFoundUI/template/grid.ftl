<@compress>
<#if div??>${div}</#if>
<script type="text/javascript">
   
    var ${grid.id}_ds = new Ext.data.JsonStore( {
    	idProperty: '_id',
		url : "${grid.queryUrl}",
		root : "datas",
		<#if grid.queryForm??>queryForm:${grid.queryForm},</#if>
		totalProperty : "totalCounts",
		<#if grid.navBar>baseParams: {start:0,limit: ${grid.pagerSize}},</#if>
		fields : [ 
			<#list columns as column>
	            "${column.name}" <#if column_has_next> ,</#if>
	        </#list>
        ]
	});
   
    <#if grid.selectable>
    	<#if grid.selectFirstRow>
		    ${grid.id}_ds.on("load",function(){
		    	try{${grid.id}.getSelectionModel().selectFirstRow();}catch(e){}
		    });
	    </#if>
	    var ${grid.id}_sm = new Ext.grid.CheckboxSelectionModel(<#if grid.singleSelect>{singleSelect:true}</#if>); 
	</#if>	
	
    var ${grid.id}_cm = new Ext.grid.ColumnModel( {
    	  <#if grid.isCellEditable??>
    	      isCellEditable : function(col, row) {
    		     record = ${grid.id}_ds.getAt(row);
    			 cloum_name = this.config[col].dataIndex;
    			 if(col<2&&cloum_name=="")return false;
    	         return ${grid.isCellEditable}(col, row,cloum_name,record);
    		  },
    	  </#if>	  
    	      columns: [ 
                <#if grid.rowNumber>new Ext.grid.RowNumberer({width:25}),</#if>
                <#if grid.selectable>${grid.id}_sm,</#if>	
				<#list columns as column>
					{
						dataIndex :  "${column.name}",
						<#if column.id??>id :  "${column.id}",</#if>
						<#if column.align??>align : "${column.align}",</#if>
						<#if column.sortable>sortable : true, </#if>
						<#if column.hidden>hidden : true, </#if>
						width :  ${column.width}
						<#if column.editor??>
						   ,editor :${column.editor}
						   ,renderer :function(value, cellmeta, record, row, col, store){
								<#if grid.isCellEditable??>
									if(${grid.id}_cm.isCellEditable(col,row)){
										cellmeta.attr='style="BORDER: #acdaf8 1px solid;"';
								    }
								<#else>
									cellmeta.attr='style="BORDER: #acdaf8 1px solid;"';
								</#if>
								<#if column.renderer??>
									value = ${column.renderer}(value, cellmeta, record, row, col, store);
							    </#if>
							    return value;
						   }	
						<#else>
						   <#if column.renderer??>,renderer : ${column.renderer}</#if>
						</#if>
						<#if column.prompt??>  ,header :  "${column.prompt}" </#if>
					}
					<#if column_has_next> ,</#if>
				</#list>
				<#if columns[0]?exists == false>{header:"no column define"}</#if>
			   ]});
    	
	<#if buttons??>
		var ${grid.id}_tbar ={
				<#if grid.toolBar.align??>buttonAlign:'${grid.toolBar.align}',</#if>
				items:[ 
	           			<#list buttons as button>
	    				<#if button.type="add">
	    				{
	    					text : "新增",
	    					id   : "${button.id}",
	    					icon : "DBFoundUI/images/add.gif",
	    					<#if button.disabled>disabled : true,</#if>
	    					handler : function() {
	    					   <#if  button.beforeAction?? >if(! ${button.beforeAction}(${grid.id},this)) return;</#if>
	    					   ${grid.id}.addLine();
	    					   <#if button.afterAction != "null" >
	    					      var record=${grid.id}.getSelectionModel().getSelected();
	    					      ${button.afterAction}(record,${grid.id},this);
	    					   </#if>
	    					}
	    				}
	    				<#elseif  button.type="delete">
	    			    {
	    					text : "删除",
	    					id   : "${button.id}",
	    					icon : "DBFoundUI/images/remove.gif",
	    					action  : "${button.action}",
	    					<#if button.disabled>disabled : true,</#if>
	    					handler : function() {
	    						var records = ${grid.id}.getSelectionModel().getSelections();
	    						<#if  button.beforeAction?? >if(!${button.beforeAction}(records,${grid.id},this)) return;</#if>
	    						if (records.length < 1) return;
	    						var button = this;
	    						$D.showConfirm("您确定要删除吗？", function(btn) {
	    							  if(btn=="no")return;
	    							  $D.submit(records,button.action,${grid.id}_ds,${button.afterAction},"delete",button);
	    						});
	    			        }	
	    				}
	    				<#elseif  button.type="save">
	    			    {
	    					text : "保存",
	    					id   : "${button.id}",
	    					icon : "DBFoundUI/images/save.gif",
	    					action  : "${button.action}",
	    					<#if button.disabled>disabled : true,</#if>
	    					handler : function() {
	    						var record_all = ${grid.id}.getSelectionModel().getSelections();
	    						var records=[];
	    						var j=0;
	    						for (var i=0;i<record_all.length;i++){
	    						   if(record_all[i].dirty){records[j] = record_all[i];j++;}
	    						}
	    						<#if  button.beforeAction?? >if(!${button.beforeAction}(records,${grid.id},this)) return;</#if>
	    						if ($D.validate(records,${grid.id}))
	    						  $D.submit(records,this.action,${grid.id}_ds,${button.afterAction},"update",this);
	    					}
	    				}
	    			    <#elseif  button.type="excel">
	    			    {
	    			    	text : "导出",
	    					id   : "${button.id}",
	    					icon : "DBFoundUI/images/xls.gif",
	    					action  : "${button.action}",
	    					<#if button.disabled>disabled : true,</#if>
	    					handler : function (){
	    			    	   <#if  button.beforeAction?? >if(!${button.beforeAction}(${grid.id},this)) return;</#if>
	    			    	   $D.exportExcel(${grid.id},this.action);
	    			         }   
	    				}
	    			   <#elseif button.type="others">
	    			    {
	    					id   : "${button.id}",
	    					<#if button.title??>text : "${button.title}",</#if>
	    					<#if button.disabled>disabled : true,</#if>
	    					<#if  button.action?? >action  : "${button.action}",</#if>
	    					handler : function() {
	    						var records = ${grid.id}.getSelectionModel().getSelections();
	    						<#if  button.beforeAction?? >if(!${button.beforeAction}(records,${grid.id},this)) return;</#if>
	    						
	    						<#if  button.action?? >
	    						    if ($D.validate(records,${grid.id})){
	    						    	var action = this.action;
		    						    <#if button.showConfirm >
				    						$D.showConfirm("${button.comfirmMessage}", function(btn) {
				    							  if(btn=="no")return;
					    				</#if>	 
	    						    	$D.submit(records,action,${grid.id}_ds,${button.afterAction},"",this);
	    						    	<#if button.showConfirm  >
					    					});
					    				</#if>	
    								}
	    						</#if>
	    						
	    						 
	    					}
	    					<#if button.icon??> ,icon : "${button.icon}" </#if>
	    				}
	    			   </#if>
	    			   <#if button_has_next> ,</#if>
	    			</#list>
	    	 ]};
	</#if>

    var ${grid.id}  = new Ext.grid.EditorGridPanel( {
    	<#if buttons??>tbar : ${grid.id}_tbar,</#if>
    	<#if grid.viewForm??> viewForm:"${grid.viewForm}",</#if> 
    	<#if delayRender==false>renderTo : Ext.get("${grid.id}_div"),</#if>
		id : "${grid.id}",
		ds : ${grid.id}_ds,
		<#if grid.plugins??>plugins: [${grid.plugins}],</#if>
		<#if grid.selectable>sm : ${grid.id}_sm,</#if>
		<#if grid.forceFit>viewConfig:{forceFit:true,scrollOffset:20},</#if>
		cm : ${grid.id}_cm,
		align : "${grid.align}",
		<#if grid.width??>width : ${grid.width},
		<#else> width: Ext.get("${grid.id}_div").getWidth(),
		</#if>
		<#if grid.title??>title:"${grid.title}",</#if>
		listeners: {
			<#list grid.events as event>
			  '${event.name}':${event.handle}
			  <#if event_has_next> ,</#if>
			</#list>
		},
		<#if grid.navBar>bbar : new Ext.PagingToolbar( {
            plugins: new Ext.ui.plugins.ComboPageSize(),
			store : ${grid.id}_ds,
			pageSize : ${grid.pagerSize},
			displayInfo : true,
			displayMsg : "本页显示第{0} - {1}条,  一共{2}条",
			emptyMsg : "没有记录"
		}),</#if>
		height : ${grid.height}
	});
   
    Ext.onReady(function() {
       var cmp = ${grid.id};
       <#if grid.viewForm??>
	      $D.initViewForm(cmp);
	   </#if> 
	   <#if grid.autoQuery>	
	       cmp.query();
	   </#if>  
	   <#if delayRender>
	       setTimeout(function(){
	  		   cmp.render(Ext.get("${grid.id}_div"));
	  		   <#if grid.selectFirstRow>
	  		   		setTimeout(function(){try{cmp.getSelectionModel().selectFirstRow();}catch(e){}},100);
	    	   </#if>
	       },200);
	   <#else>
		   cmp.setHeight(cmp.height);
	   </#if> 
	   <#if grid.width?? == false>
	       <#if div??>
	       	   if($D.isScrolling())$D.adjustCmpWidth(cmp);
			   Ext.fly(window).on("resize", function(){
					$D.adjustCmpWidth(cmp);
			   });
		   </#if> 
	   </#if> 
	   cmp.show();
    });
</script>
</@compress>