/**
 * ext plugin gird pagerSize
 */
if (!Ext.grid.GridView.prototype.templates) {
	Ext.grid.GridView.prototype.templates = {};
}
Ext.grid.GridView.prototype.templates.cell = new Ext.Template(
		'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} x-selectable {css}" style="{style}" tabIndex="0" {cellAttr}>',
		'<div class="x-grid3-cell-inner x-grid3-col-{id}" {attr}>{value}</div>',
		'</td>');

Ext.namespace('Ext.ui.plugins');

Ext.ui.plugins.ComboPageSize = function(config) {
	Ext.apply(this, config);
};

Ext.extend(Ext.ui.plugins.ComboPageSize,
			Ext.util.Observable,
			{
				pageSizes : [ 10, 20, 50, 100, 200 ],
				prefixText : '每页显示',
				postfixText : '条',
				addToItem : true, // true添加到items中去，配合index；false则直接添加到最后
				index : 10, // 在items中的位置
				init : function(pagingToolbar) {
					var ps = this.pageSizes;
					var combo = new Ext.form.ComboBox(
							{
								typeAhead : true,
								triggerAction : 'all',
								lazyRender : true,
								mode : 'local',
								width : 60,
								store : ps,
								enableKeyEvents : true,
								editable : false,
								loadPages : function() {
									var rowIndex = 0;
									var gp = pagingToolbar
											.findParentBy(function(ct, cmp) {
												return (ct instanceof Ext.grid.GridPanel) ? true
														: false;
											});
									var sm = gp.getSelectionModel();
									if (undefined != sm
											&& sm.hasSelection()) {
										if (sm instanceof Ext.grid.RowSelectionModel) {
											rowIndex = gp.store.indexOf(sm
													.getSelected());
										} else if (sm instanceof Ext.grid.CellSelectionModel) {
											rowIndex = sm.getSelectedCell()[0];
										}
									}
									rowIndex += pagingToolbar.cursor;
									pagingToolbar
											.doLoad(Math
													.floor(rowIndex
															/ pagingToolbar.pageSize)
													* pagingToolbar.pageSize);
								},
								listeners : {
									select : function(c, r, i) {
										pagingToolbar.pageSize = ps[i];
										pagingToolbar.store.baseParams["limit"] = pagingToolbar.pageSize;
										this.loadPages();
									},
									blur : function() {
										var pagesizeTemp = Number(this
												.getValue());
										if (isNaN(pagesizeTemp)) {
											this
													.setValue(pagingToolbar.pageSize);
											return;
										}
										pagingToolbar.pageSize = Number(this
												.getValue());
										pagingToolbar.store.baseParams["limit"] = pagingToolbar.pageSize;
										// this.loadPages();
									}
								}
							});
					if (this.addToItem) {
						var inputIndex = this.index;
						if (inputIndex > pagingToolbar.items.length)
							inputIndex = pagingToolbar.items.length;
						pagingToolbar.insert(++inputIndex, '-');
						pagingToolbar.insert(++inputIndex, this.prefixText);
						pagingToolbar.insert(++inputIndex, combo);
						pagingToolbar
								.insert(++inputIndex, this.postfixText);
					} else {
						pagingToolbar.add('-');
						pagingToolbar.add(this.prefixText);
						pagingToolbar.add(combo);
						pagingToolbar.add(this.postfixText);
					}
					pagingToolbar.on( {
						beforedestroy : function() {
							combo.destroy();
						},
						change : function() {
							combo.setValue(pagingToolbar.pageSize);

						}
					});

				}
			});
/**
 * 覆盖 Ext.data.JsonStore 的 load()方法
 */
Ext.override(Ext.data.JsonStore, {
	load : function() {
		/*对象转化为json字符串*/
		for ( var attr in this.baseParams) {
			var o = this.baseParams[attr];
			if(o instanceof Object){
				this.baseParams[attr] = Ext.encode(o);
			}
		}
		if (this.queryForm) {
			if (this.queryForm.form.isValid()) {
				var json = this.queryForm.getData();
				for(ex in json){
					var eo = json[ex];
					if(eo instanceof Object){
						this.baseParams[ex] = Ext.encode(eo);
					}else{
						this.baseParams[ex]=eo;
					}
				}
				Ext.data.JsonStore.superclass.load.apply(this, arguments);
			}else{
				$D.showMessage('验证通不过！');
			}
		}else{
			Ext.data.JsonStore.superclass.load.apply(this, arguments);
		}
	},
	listeners : {
		"exception":function(a,b,c,d,res){
		    try{
		    	var obj = Ext.util.JSON.decode(res.responseText);
		    	if(obj.message){
		    		$D.showError(obj.message);
		    	}else{
		    		$D.showError("加载失败");
		    	}
		    }catch(e){
		    	 Ext.MessageBox.show( {
						title : '错误',
						msg : res.responseText,
						minWidth : $D.getFullWidth()-80,
						buttons : Ext.MessageBox.OK,
						icon : Ext.MessageBox.ERROR
				});
		    }
	    }
	}
});
/**
 * 添加 Grid setCurrentRecordData,query,getSelectionsData,getModifiedData,getSelectionsModifiedData
 * 从写 属性
 */

Ext.override(Ext.grid.EditorGridPanel, {
	getCurrentRecordData : function() {
		if(!this.getSelectionModel())return {};
	    var record = this.getSelectionModel().getSelected();
	    if(record){
		    var od = record.data;
			var oj = record.json;
			if (oj) {
				for ( var attr in od) {
					oj[attr] = od[attr];
				}
				oj["_status"] = "OLD";
				return oj;
			}else{
				od["_status"] = "NEW";
				return od;
			}
	    }else{
	    	return {};
	    }
	},
	setCurrentRecordData : function(json) {
		if(!this.getSelectionModel())return;
	    var record = this.getSelectionModel().getSelected();
		if(record){
			for ( var key in json) {
				record.set(key,json[key]);
			}
			record.commit();
		}
	},
	initCurrentRecordData : function(json) {
		if(!this.getSelectionModel())return;
	    var record = this.getSelectionModel().getSelected();
		if(record){
			for ( var key in json) {
				record.set(key,json[key]);
			}
		}
	},
	getSelectionsData:function(returnJson){
		if(!this.getSelectionModel())return;
		var records = this.getSelectionModel().getSelections();
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
			}
		}
		if (returnJson && returnJson === true) {
			return data;
		} else {
			return Ext.util.JSON.encode(data);
		}
	},
	getModifiedData:function(returnJson){
		var records = this.getStore().getModifiedRecords();
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
			}
		}
		if (returnJson && returnJson === true) {
			return data;
		} else {
			return Ext.util.JSON.encode(data);
		}
	},
	getSelectionsModifiedData:function(returnJson){
		if(!this.getSelectionModel())return;
		var record_all = this.getSelectionModel().getSelections();
		var records=[];
		var j=0;
		for (var i=0;i<record_all.length;i++){
		   if(record_all[i].dirty){records[j] = record_all[i];j++;}
		}
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
			}
		}
		if (returnJson && returnJson === true) {
			return data;
		} else {
			return Ext.util.JSON.encode(data);
		}
	},
	query: function(){
		this.getStore().load();
	},
	addLine:function(json,autoCommit){
		var stores = this.getStore();
		var fieldsArr = [];
		var fields = {};
		for ( var i = 0; i < this.getColumnModel().getColumnCount(); i++) {
			var tmp = {};
			var field = this.getColumnModel().getDataIndex(i);
			tmp.name = field;
			fieldsArr.push(tmp);
			fields[field] = '';
		}
		var entry = Ext.data.Record.create(fieldsArr);
		var row = new entry(fields);
		var count = stores.getCount();
		stores.insert(count, row);
		if(json){
			var record = stores.getAt(count);
			if(autoCommit==true){
				record.json = {};
				for ( var key in json) {
					record.set(key,json[key]);
					record.json[key]=json[key];
				}
				record.commit();
			}else{
				for ( var key in json) {
					record.set(key,json[key]);
				}
			}
		}
		try {
			this.getSelectionModel().selectRow(count, false);
			this.view.focusRow(count);
			this.stopEditing();
		} catch (E) {
		}
	},
	trackMouseOver:true,
	stripeRows: true,
	loadMask:true,
	layout:"fit",
	clicksToEdit:1,
	columnLines:true
});
