/*******************************************************************************
 * 定义DBFound对象 提供公用的方法
 ******************************************************************************/
$D = DBFound = {
	FieldReadOnlyBackground:"#eee",	
	showMessage : function(message, fn) {
		Ext.MessageBox.show( {
			title : '提示',
			msg : message,
			icon : Ext.MessageBox.INFO,
			buttons : Ext.MessageBox.OK,
			fn : fn
		});
	},
	showWarning : function(message, fn) {
		Ext.MessageBox.show( {
			title : '警告',
			msg : message,
			buttons : Ext.MessageBox.OK,
			icon : Ext.MessageBox.WARNING,
			fn : fn
		});
	},
	showError : function(message, fn) {
		Ext.MessageBox.show( {
			title : '错误',
			msg : message,
			buttons : Ext.MessageBox.OK,
			icon : Ext.MessageBox.ERROR,
			fn : fn
		});
	},
	showConfirm : function(message, fn) {
		Ext.MessageBox.confirm("确认", message, fn);
	},
	request: function(url,param,callback,mask,maskTitle){
		var o = url;
		if(o instanceof Object){
			param = o.param;
			callback = o.callback;
			mask = o.mask;
			maskTitle = o.maskTitle;
			url = o.url;
		}
		if(!maskTitle){
			maskTitle = "正在提交.......";
		}
		
		for ( var attr in param) {
			var o = param[attr];
			if(o instanceof Object){
				param[attr] = Ext.encode(o);
			}
		}
		var div = null;
		if(mask&&mask==true){
			var d = Ext.getBody().getScroll() ;
			div = document.getElementById("dbfoundMask");
			if(div && div !=null){
				div.style.display = "";
				div.style.width = document.body.clientWidth; 
				div.style.height = document.body.clientHeight; 
				div.style.top = d.top;
			}else {
				div = document.createElement("div");
				div.style.width = document.body.clientWidth; 
				div.style.height = document.body.clientHeight; 
				div.style.position = "absolute";
				div.style.top = d.top;
				div.id = "dbfoundMask";
				document.body.appendChild(div);
			}
			Ext.get("dbfoundMask").mask(maskTitle, 'x-mask-loading');
		}
		Ext.Ajax.request( {
			url : url,
			success : function(response, action){
				if(mask&&mask==true){
					Ext.get("dbfoundMask").unmask();
					div.style.display = "none";
				}
				var obj = Ext.util.JSON.decode(response.responseText);
				if(callback)callback.call(this,obj,response,action);
			},
			failure : function(res) {
				if(mask&&mask==true){
					Ext.get("dbfoundMask").unmask();
					div.style.display = "none";
				}
				Ext.MessageBox.show( {
						title : '错误',
						msg : res.responseText,
						minWidth : $D.getFullWidth()-80,
						buttons : Ext.MessageBox.OK,
						icon : Ext.MessageBox.ERROR
				});
			},
			params : param
		});
	},
	submit : function(records, action, ds, afterAction, type,button) {
		var data = [];
		for ( var i = 0; i < records.length; i++) {
			var od = records[i].data;
			var oj = records[i].json;
			if (oj) {
				for ( var attr in od) {
					oj[attr] = od[attr];
				}
				oj["_status"] = "OLD";
				data[i] = oj;
			} else {
				od["_status"] = "NEW";
				data[i] = od;
				if(type!='delete'){
					type = 'save';
				}
			}
		}
		
		var successFunction = function(obj,response, action) {
			if (obj.success == true) {
				$D.showMessage(obj.message, function() {
					if (type == "update") {
						for (i = 0; i < records.length; i++) {
							records[i].commit();
						}
					} else if (type == "delete") {
						ds.remove(records);
					} else if (type == "save") {
						if (ds)
							ds.reload();
					}
					if (afterAction != null){
						afterAction.call(this, records, obj, button);
					}
				});
			} else {
				$D.showError(obj.message);
			}
		};
		/*初始化baseParam请求数据*/
		var param = ds.baseParams;
		param.GridData = data;
		var limit = param.limit;
		var start = param.start;
		delete param.limit;
		delete param.start;
	    
	    $D.request(action,param,successFunction,true);
	    
	    /*恢复baseParam请求前数据*/
	    param.limit = limit;
	    param.start = start;
	    delete param.GridData; 
	},
	openPostWindow : function(url, columns, parameters) {
		var oForm = document.createElement("form");
		oForm.method = "post";
		oForm.target = "_export_window";
		if (url)
			oForm.action = url + (url.indexOf('?') == -1 ? '?' : '&') + 'r='
					+ Math.random();
		var iframe = Ext.get('_export_window')
		|| new Ext.Template(
				'<iframe id ="_export_window" name="_export_window" style="position:absolute;left:-1000px;top:-1000px;width:1px;height:1px;display:none"></iframe>')
				.insertFirst(document.body, {}, true);
		var hasitemsids_input = document.createElement("input");
		hasitemsids_input.type = "hidden";
		hasitemsids_input.name = "columns";
		hasitemsids_input.id = "columns";
		hasitemsids_input.value = columns;
		oForm.appendChild(hasitemsids_input);
		var leavefids_input = document.createElement("input");
		leavefids_input.type = "hidden";
		leavefids_input.id = "parameters";
		leavefids_input.name = "parameters";
		leavefids_input.value = parameters;
		oForm.appendChild(leavefids_input);
		document.body.appendChild(oForm);
		oForm.submit();
		Ext.fly(oForm).remove();
	},
	exportExcel : function(grid, url) {
		queryForm  = grid.getStore().queryForm;
		if(queryForm){
		   if (!queryForm.form.isValid()) {
			   $D.showMessage('验证通不过！');
			   return;
		   }
		}
		var columns = grid.colModel.columns;
		var cls = [];
		for ( var i = 0; i < columns.length; i++) {
			if (columns[i].dataIndex != "") {
				var cl = {
					"name" : columns[i].dataIndex,
					"content" : columns[i].header,
					"width" : columns[i].width
				};
				cls[i] = cl;
			}
		}
		$D.showConfirm("导出可能花费较长时间，确定导出？",function(btn){
		    if(btn=="no")return;
		    var baseParams = grid.store.baseParams;
			var limit = baseParams.limit;
			var start = baseParams.start;
			delete baseParams.limit;
			delete baseParams.start;
		    DBFound.openPostWindow(url, Ext.util.JSON.encode(cls), Ext.util.JSON.encode(baseParams));
		    baseParams.limit = limit;
		    baseParams.start = start;
		});
	},

	/**
	 * *** 格式化 grid中 日期控件
	 */
	dateFormat : function(value) {
		if (value instanceof Date) {
			return value.format("Y-m-d");
		} else {
			return value;
		}
	},
	dateTimeFormat : function(value,name,record) {
		if (value instanceof Date) {
			value = value.format("Y-m-d H:i:s");
			record.set(name,value);
		}
		return value;
	},
	passwordHidden : function(value) {
		var v="";
		for(var i=0;i<value.length;i++) {
			v+="●";
		}
		return v;
	},
	/**
	 * 验证grid中数据是否合法 *** 格式化 grid中 日期控件
	 */
	validate : function(records, grid) {
		if (records.length < 1) {
			return;
		}
		for ( var j = 0; j < records.length; j++) {
			var record = records[j];
			var fields = record.fields.keys;
			for ( var i = 0; i < fields.length; i++) {
				var name = fields[i];
				var value = record.data[name];
				var cm = grid.getColumnModel();
				var colIndex = cm.findColumnIndex(name);
				if (colIndex == -1)
					continue;
				var rowIndex = grid.store.indexOfId(record.id);
				if (null != cm.getCellEditor(colIndex)) {
					cm.getCellEditor(colIndex).field.reset();
					var editor = cm.getCellEditor(colIndex).field;
					if(value===null){value="";}
					if (!editor.validateValue(value)) {
						$D.showWarning("字段："+ cm.config[colIndex].header+ "，验证通不过！", function(e) {
							grid.startEditing(rowIndex, colIndex);
						});
						return false;
					}
				}
			}
		}
		return true;
	},
	/**
	 * grid中 combox渲染，
	 */
	gridComboRenderer : function(value, valueField, displayField, comBoxDs) {
		var index = comBoxDs.findBy(function(record, id) {
			return record.get(valueField) == value;
		});
		if (index != -1) {
			eval("value = comBoxDs.getAt(index).data." + displayField);
			return value;
		}
		return "";
	},
	/**
	 * grid中 lovcombox渲染，
	 */
	gridLovComboRenderer:function(value, valueField, displayField, comBoxDs) {
		value+="";
		var vs = value.split(',');
		var result="";
		for(var i=0;i<vs.length;i++){
			result = result+", " + $D.gridComboRenderer(vs[i], valueField, displayField, comBoxDs);
		}
		if(result=="")return "";
		else return result.substring(2);
	},
	/**
	 * 打开一个窗口 url为显示对应的jsp文件， params为传递过去的参数
	 */
	open : function(id, title, width, height, url, closeFunction) {
		if(url=="")return;
		if(url.indexOf("?")>0){
			url = url+"&windowId="+id;
		}else{
			url = url+"?windowId="+id;
		}
		
		if (!closeFunction){
			closeFunction = function() {};
		}
		var bodyWidth = $D.getFullWidth(); // 网页可见区域宽
		var bodyHeight = $D.getFullHeight(); // 网页可见区域高
		if (width > bodyWidth - 20) {
			width = bodyWidth - 20;
		}
		if (height > bodyHeight - 18) {
			height = bodyHeight - 18;
		}
		var html = '<iframe id="'+id+'_frame" style="BACKGROUND:#FFFFFF;margin-top:-1px;" name="'
					+ id + 'frame" src="' + url + '" frameBorder=0 width="100%" height="100%"> </iframe>';
		var win = new Ext.Window( {
			id : id,
			width : width,
			height : height,
			closable : true,
			layout : 'fit',
			modal : true,
			plain : true,
			frame : true,
			html : html,
			resizable : false,
			listeners : {
				"close" : closeFunction
			}
		});
		//打开子窗口后 隐藏父窗口的滚动条
		var style = document.body.style;
		var of = style.overflow;
		style.overflow = "hidden";
		win.on("close",function(){style.overflow = of;});
		
		win.show();
		win.setTitle(title);
		return win;
	},
	openLov:function(grid,field,colName,width,height,title){
		if(grid&&grid!=null){
			field.hide();
		}
		var winId=colName+"Lov";
		$D.open(winId,title,width,height,field.lovUrl,function(){
			if(grid&&grid!=null)field.setValue(grid.getSelectionModel().getSelected().get(colName));
			if(grid&&grid!=null){
				field.show();
			}
		});
		var win = Ext.getCmp(winId);
		
		win.commit=function(json){
			field.fireEvent('commit',json,colName,field);
			win.close();
		}
	},
	/**
	 * textfield 强制大写
	 */
	keypress : function(t, e, filed) {
		var keyCode = e.browserEvent.keyCode;
		if ((keyCode >= 97 && keyCode <= 122)) {
			if (Ext.isIE) {
				e.browserEvent.keyCode = keyCode - 32;
			} else {
				var v = String.fromCharCode(keyCode - 32);
				e.stopEvent();
				var d = filed.el.dom;
				var rv = filed.getRawValue();
				var s = d.selectionStart;
				var e = d.selectionEnd;
				rv = rv.substring(0, s) + v + rv.substring(e, rv.length);
				filed.setRawValue(rv);
				d.selectionStart = s + 1;
				d.selectionEnd = d.selectionStart;
			}
		}
	},
	blurUpper : function(filed) {
		filed.setRawValue(filed.getRawValue().toUpperCase());
	},
	setFieldReadOnly : function(filedId, status) {
		if (status == false) {
			Ext.getCmp(filedId).setReadOnly(false);
			Ext.get(filedId).dom.style.background = "";
		} else {
			Ext.getCmp(filedId).setReadOnly(true);
			Ext.get(filedId).dom.style.background = $D.FieldReadOnlyBackground;
		}
	},
	getTab : function(id) {
		if (document.getElementById(id + '_iframe')) {
			return document.getElementById(id + '_iframe').contentWindow;
		} else {
			return undefined;
		}
	},
	adjustCmpWidth : function(cmp) {
		var xtype = cmp.getXType();
		if (xtype == "tabpanel") {
			try {
				cmp.setWidth(400);
				var dw = Ext.get(cmp.id+"_outdiv").getWidth();
				cmp.setWidth(dw);
				var acmp = cmp.getActiveTab();
				setTimeout(function(){cmp.fireEvent("tabchange",cmp,acmp,acmp);},80);
			} catch (e) {
			}
		} else if (xtype == "buttongroup") {
			try {
				 var el = Ext.get(cmp.id+"_table");
				 el.setWidth(0);
				 cmp.setWidth(0);
				 el.setWidth(el.parent().getWidth());
				 cmp.setWidth(Ext.get(cmp.id+"_div").getWidth());
			} catch (e) {
			}
		} else if (xtype == "panel") {
			try {
				cmp.setWidth(400);
				cmp.setWidth(Ext.get(cmp.id + "_div").getWidth());
				setTimeout(function(){
					   var cns = cmp.contentCmp;
					   if(cns){
						   for(var i=0;i<cns.length;i++){
							   var cc = Ext.getCmp(cns[i]);
							   if(cc){
								   try{$D.adjustCmpWidth(cc);}catch(e){}
							   }
						   }
					   }
				},80);
			} catch (e) {
			}
		} else if(xtype){
			try {
				cmp.setWidth(400);
				cmp.setWidth(Ext.get(cmp.id + "_div").getWidth());
			} catch (e) {
			}
		}
	},
	getTabInitWidth:function(){
		if(Ext.isIE)return '100%';
		else return '98.5%';
	},
	setTabInnerWidth:function(cmp,tab){
		 var w1 = cmp.getWidth();
		 var w2 = tab.getWidth()-29;
		 if(w1>w2){
			 if(Ext.isWebKit){
				 cmp.setWidth(w2+15);
	    	 }else {
	    		 cmp.setWidth(w2);
			 }
		 }
	},initViewForm:function(grid){
		grid.getSelectionModel().on("rowselect",function(sm, row, rec) {
			  var form = Ext.getCmp(grid.viewForm);
	    	  form.reset();
	    	  form.setData(rec.json);
	    	  form.setData(rec.data);
        });
	    grid.getStore().on("load",function() {
	    	  var items = grid.getStore().data.items;
	    	  if(items.length==0){
	    		  Ext.getCmp(grid.viewForm).reset();
	    	  }
	    });
	},getFullHeight:function(id){
		var height = 0;
		if(document.documentElement && document.documentElement.clientHeight>height){
			height = document.documentElement.clientHeight;
		}
		if(document.body && document.body.clientHeight>height){
			height=document.body.clientHeight;
		}
		
		if(!id){
			return height;
		}else{
			try{
			   var el = Ext.get(id+"_div");
			   if(el==null){
				   el = Ext.get(id);
			   }
			   var bottom = el.getMargins().bottom;
			   return height - el.getY() - bottom;
			}catch(e){
			   alert("组件："+ id +"不支持getFullHeight");
			}
		}
	},getFullWidth:function(id){
		var width = 0;
		if(document.documentElement && document.documentElement.clientWidth > width){
			width = document.documentElement.clientWidth;
		}
		if(document.body && document.body.clientWidth > width){
			width=document.body.clientWidth;
		}
		
		if(!id){
			return width;
		}else{
			try{
			   var el = Ext.get(id+"_div");
			   if(el==null){
				   el = Ext.get(id);
			   }
			   var right = el.getMargins().right;
			   return width - el.getX() - right;
			}catch(e){
			   alert("组件："+ id +"不支持getFullWidth");
			}
		}
	},isScrolling:function(){
		return document.body.clientWidth<document.body.scrollWidth;
	}
};

/**
 * window 弹出窗口滚动条错误 修改
 */
var originalIsValidHandleChild = Ext.dd.DragDrop.prototype.isValidHandleChild;
Ext.dd.DragDrop.prototype.isValidHandleChild = function(node) {
	if (!node || !node.nodeName) {
		return false;
	}
	return originalIsValidHandleChild.apply(this, [ node ]);
};
/**
 * 重写 ext checkbox readonly属性
 */
Ext.override(Ext.form.Checkbox, {
	onClick : function(a,b,c) {
		if (this.readOnly === true)
			this.el.dom.checked = this.originalValue;
		this.setValue(b.checked);
		// Ext.form.Checkbox.superclass.setValue.apply(this, arguments);
	}
});

/**
 * 添加 Form setData,reset
 */
Ext.override(Ext.TabPanel, {
	listeners : {
		'tabchange' :function(tabPanel, newCard, oldCard){
			   var cns = newCard.contentCmp;
			   if(cns){
				   for(var i=0;i<cns.length;i++){
					   var cmp = Ext.getCmp(cns[i]);
					   if(cmp){
						   try{
							   if(cmp.height && cmp.height != cmp.getHeight()){
								   cmp.setHeight(cmp.height);
							   }
						   }catch(e){}
					   }
				   }
				   for(var i=0;i<cns.length;i++){
					   var cmp = Ext.getCmp(cns[i]);
					   if(cmp){
						   try{
							   $D.adjustCmpWidth(cmp);
						   }catch(e){}
					   }
				   }
			   }
		 	   if(newCard.url&&newCard.url!=null&&!newCard.inited){
				   $D.getTab(newCard.id).location.href = newCard.url;
				   newCard.inited = true;
			   }
		  }
	}
});
/**
 * 添加 Form setData,reset
 */
Ext.override(Ext.FormPanel, {
	setData : function(json) {
		this.getForm().setValues(json);
		var record = this.getForm().json;
		if (!record) {
			record = {};
			this.getForm().json = record;
		}
		for ( var key in json) {
			record[key] = json[key];
		}
	},
	getData : function(){
		var params = this.form.getValues();
		var json = this.form.json;
		if(!json){
			json={};
			this.form.json = json;
		}
		for(ex in  params){
			json[ex]=params[ex];
		}
		var items =this.form.items.items;
		for(var i=0;i<items.length;i++){
			var item = items[i];
			if(item.getXType()==="checkboxgroup" || item.getXType()==="radiogroup" ){
				var iv = "";
				var items = item.items.items;
				for(i=0;i<items.length;i++){
					var ci = items[i];
					if(ci.getValue()===true){
						if(iv === "")iv = iv + ci.inputValue;
						else iv = iv + "," + ci.inputValue;
					}else{
						json[ci.getName()]=null;
					}
				}
				json[item.getName()]=iv;
			}else if(item.getXType()==="checkbox"&&item.getValue()===false){
				json[item.getName()]=null;
			}
			if(item.emptyText === json[item.getName()]){
				json[item.getName()]=null;
			}
		}
		return json;
	},
	reset : function() {
		this.clear();
		this.initTarget();
	},
	clear : function() {
		this.getForm().reset();
		var json = this.getForm().json;
		for ( var key in json) {
			json[key] = null;
		}
	},
	initTarget : function() {
		if(this.bindTarget){
		  try{
			  var items = this.bindTarget.data.items;
			  if(items&&items.length>0){
				  var record = items[0];
		    	  this.setData(record.json);
		    	  this.setData(record.data);
			  }else if(items.length==0){
				   this.getForm().reset();
				   var json = this.getForm().json;
				   for ( var key in json) {
					   json[key] = null;
				   }
			  }
			}catch(e){}  
		}
	}
});

/**
 * 添加 tree getSelectedText,getSelectedId,init
 */
Ext.override(Ext.tree.TreePanel, {
	parentField:null,
	idField:null,
	displayField:null,
	showCheckBox:false,
	bindTarget:null,
	getCurrentNode:function(){
		var nodes = this.getSelectionModel().getSelectedNodes();
		if(nodes.length>0){
			return nodes[0];
		}
	},
	getCurrentNodeData:function(){
		var node = this.getCurrentNode();
		if(node){
			return node.json;
		}
	},
	setCurrentNodeData:function(json){
		var node = this.getCurrentNode();
		for(ex in json){
			if(node.attributes[ex])node.attributes[ex]=json[ex];
			node.json[ex] = json[ex];
		}
	},
	getSelectedText:function(){
		nodes = this.getChecked();
		var text="";
		for(var i=0;i<nodes.length;i++){
			text = text + nodes[i].text; 
			if(i<nodes.length-1){
				text = text+", ";
			}
		}
		return text;
    },
    getSelectedId:function(){
    	nodes = this.getChecked();
		var id="";
		for(var i=0;i<nodes.length;i++){
			id = id + nodes[i].id; 
			if(i<nodes.length-1){
				id = id+",";
			}
		}
		return id;
    },
    refresh :function(){
    	this.getRootNode().removeAll(true);
    	this.inited = false;
    	this.init();
    },
    init :function() {
    	if(this.inited===true){
    		return;
    	}else{
    		this.inited = true;
    	}
		var root = this.getRootNode();
		var records = this.bindTarget.data.items;
		var nodes = [];
		var nodesObj = {};
		var ni = 0;
		for ( var i = 0; i < records.length; i++) {
			record = records[i];
			var pid = record.get(this.parentField);
			var id = record.get(this.idField);
			var text = record.get(this.displayField);
			var config = {id:id,text:text,leaf:true};
			if(this.showCheckBox){
				if(record.get('checked')>0)config['checked'] = true;
				else config['checked'] = false;
			}
			var node = new Ext.tree.TreeNode(config);
			node.json = record.json;
			nodesObj[node.id] = node;
			if (!pid || pid == "" || pid == null || pid == 0) {
				root.appendChild(node);
			} else {
				node.pid = pid;
				nodes[ni++] = node;
			}
		}
		for (var j=0;j<nodes.length;j++) {
			var node = nodes[j];
			var pnode = nodesObj[node.pid];
			if (pnode) {
				if(pnode.leaf==true){
					pnode.leaf = false;
				}
				pnode.appendChild(node);
			}else{
				root.appendChild(node);
			}
		}
		nodesObj = null;
		nodes = null;
	    return this;
	},
	listeners : {
		'checkchange' : function(node, checked) {
			if (checked) {
				node.getUI().addClass('complete');
			} else {
				node.getUI().removeClass('complete');
			}
			node.expand();
			node.attributes.checked = checked;
			node.eachChild(function(child) {
				child.ui.toggleCheck(checked);
				child.attributes.checked = checked;
				child.fireEvent('checkchange', child, checked);
				if (child.hasChildNodes()) {
					child.expand();
					child.eachChild(function(childs) {
						childs.ui.toggleCheck(checked);
						childs.attributes.checked = checked;
						childs.fireEvent('checkchange', childs, checked);
						if (childs.hasChildNodes()) {
							childs.expand();
							childs.eachChild(function(childss) {
								childss.ui.toggleCheck(checked);
								childss.attributes.checked = checked;
								childss.fireEvent('checkchange', childss, checked);
							});
						}
					});
				}
			});
		}
	}
});
/**
 * vtype 校验
 * 1.alpha //只能输入字母，无法输入其他（如数字，特殊符号等）
 * 2.alphanum//只能输入字母和数字，无法输入其他
 * 3.email//email验证，要求的格式是"usc@sina.com"
 * 4.url//url格式验证，要求的格式是http://www.sina.com
 * 5.ip //ip地址
 */

Ext.apply(Ext.form.VTypes, {
	ip:  function(v) {
  		return /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/.test(v);
	},
    ipText: '请输入正常的ip地址，如"192.168.1.1"',
    ipMask: /[\d\.]/i
});


/**
 * readOnly的field 禁用
 */
Ext.EventManager.on(Ext.isIE ? document : window, 'keydown', function(e, t) {
	var element =  e.browserEvent.srcElement;
	if (e.getKey() == e.BACKSPACE&&element&&element.readOnly===true) {
		e.stopEvent();
	}
});
