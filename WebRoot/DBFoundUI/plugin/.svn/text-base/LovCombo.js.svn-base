/**
 * 添加 lovcombo类型
 */
if('function' !== typeof RegExp.escape) {    
    RegExp.escape = function(s) {    
        if('string' !== typeof s) {    
            return s;    
        }    
        return s.replace(/([.*+?^=!:${}()|[\]\/\\])/g, '\\$1');    
    }; 
}    
Ext.ns('Ext.ux.form');
Ext.ux.form.LovCombo = Ext.extend(Ext.form.ComboBox, {
	 checkField:'checked',
	 separator:',',
	 constructor:function(config) {
		config = config || {};
		config.listeners = config.listeners || {};
		Ext.applyIf(config.listeners, {
			 scope:this
			,beforequery:this.onBeforeQuery
			,blur:this.onRealBlur
		});
		Ext.ux.form.LovCombo.superclass.constructor.call(this, config);
	},
	initComponent:function() {
		if(!this.tpl) {
			this.tpl = 
				 '<tpl for=".">'
				+'<div class="x-combo-list-item">'
				+'<img src="' + Ext.BLANK_IMAGE_URL + '" '
				+'class="ux-lovcombo-icon ux-lovcombo-icon-'
				+'{[values.' + this.checkField + '?"checked":"unchecked"' + ']}">'
				+'<div class="ux-lovcombo-item-text">{' + (this.displayField || 'text' )+ ':htmlEncode}</div>'
				+'</div>'
				+'</tpl>'
			;
		}
        Ext.ux.form.LovCombo.superclass.initComponent.apply(this, arguments);
		this.onLoad = this.onLoad.createSequence(function() {
			if(this.el) {
				var v = this.el.dom.value;
				this.el.dom.value = '';
				this.el.dom.value = v;
			}
		});
 
    },
    initEvents:function() {
		Ext.ux.form.LovCombo.superclass.initEvents.apply(this, arguments);
		this.keyNav.tab = false;
	},
	clearValue:function() {
		this.value = '';
		this.setRawValue(this.value);
		this.store.clearFilter();
		this.store.each(function(r) {
			r.set(this.checkField, false);
		}, this);
		if(this.hiddenField) {
			this.hiddenField.value = '';
		}
		this.applyEmptyText();
	},
	getCheckedDisplay:function() {
		var re = new RegExp(this.separator, "g");
		return this.getCheckedValue(this.displayField).replace(re, this.separator + ' ');
	},
	getCheckedValue:function(field) {
		field = field || this.valueField;
		var c = [];
		var snapshot = this.store.snapshot || this.store.data;
		snapshot.each(function(r) {
			if(r.get(this.checkField)) {
				c.push(r.get(field));
			}
		}, this);
		return c.join(this.separator);
	},
	onBeforeQuery:function(qe) {
		qe.query = qe.query.replace(new RegExp(RegExp.escape(this.getCheckedDisplay()) + '[ ' + this.separator + ']*'), '');
	},
	onRealBlur:function() {
		this.list.hide();
		var rv = this.getRawValue();
		var rva = rv.split(new RegExp(RegExp.escape(this.separator) + ' *'));
		var va = [];
		var snapshot = this.store.snapshot || this.store.data;
		Ext.each(rva, function(v) {
			snapshot.each(function(r) {
				if(v === r.get(this.displayField)) {
					va.push(r.get(this.valueField));
				}
			}, this);
		}, this);
		this.setValue(va.join(this.separator));
		this.store.clearFilter();
	},
	onSelect:function(record, index) {
        if(this.fireEvent('beforeselect', this, record, index) !== false){
			record.set(this.checkField, !record.get(this.checkField));
			if(this.store.isFiltered()) {
				this.doQuery(this.allQuery);
			}
			this.setValue(this.getCheckedValue());
            this.fireEvent('select', this, record, index);
        }
	},
	setValue:function(v) {
		if(v) {
			v = '' + v;
			if(this.valueField) {
				this.store.clearFilter();
				this.store.each(function(r) {
					var checked = !(!v.match(
						 '(^|' + this.separator + ')' + RegExp.escape(r.get(this.valueField))
						+'(' + this.separator + '|$)'))
					;

					r.set(this.checkField, checked);
				}, this);
				this.value = this.getCheckedValue();
				this.setRawValue(this.getCheckedDisplay());
				if(this.hiddenField) {
					this.hiddenField.value = this.value;
				}
			}
			else {
				this.value = v;
				this.setRawValue(v);
				if(this.hiddenField) {
					this.hiddenField.value = v;
				}
			}
			if(this.el) {
				this.el.removeClass(this.emptyClass);
			}
		}
		else {
			this.clearValue();
		}
	},
	selectAll:function() {
        this.store.each(function(record){
            record.set(this.checkField, true);
        }, this);
        this.doQuery(this.allQuery);
        this.setValue(this.getCheckedValue());
    },
    deselectAll:function() {
		this.clearValue();
    },
    assertValue:Ext.emptyFn
});
Ext.reg('lovcombo', Ext.ux.form.LovCombo); 


Ext.ux.form.TreeCombo = Ext.extend(Ext.form.ComboBox, {
	onViewClick : function(doFocus) {
		var index = this.view.getSelectedIndexes()[0], s = this.store, r = s.getAt(index);
		if (r) {
			this.onSelect(r, index);
		}
		if (doFocus !== false) {
			this.el.focus();
		}
	},
	tree : null,
	hiddenValue : null,
	getHiddenValue : function() {
		return this.hiddenValue;
	},
	setHiddenValue : function(code, dispText) {
		this.setValue(code);
		Ext.form.ComboBox.superclass.setValue.call(this, dispText);
		this.hiddenValue = code;
	},
	initComponent : function() {
		var _this = this;
		var tplRandomId = 'deptcombo_' + Math.floor(Math.random() * 1000) + this.tplId
		this.tpl = "<div id='" + tplRandomId + "'></div>"
		this.tree = new Ext.tree.TreePanel( {
    		bindTarget : this.store,
    		parentField:_this.parentField,
    		idField:_this.valueField,
    		displayField:_this.displayField,
    		height:230,
    		useArrows : true,
    		autoScroll : true,
    		selModel: new Ext.tree.MultiSelectionModel(),
    		animate : true,
    		containerScroll : true,
    		rootVisible : false,
    		root : {
    			nodeType : 'node'
    		}
    	}).init();
    	this.store.on("load",function(){_this.tree.refresh();});
		this.tree.on('click', function(node) {
			var dispText = node.text;
			var code = node.id;
			_this.setHiddenValue(code, dispText);
			_this.collapse();
		});
		this.on('expand', function() {
			this.tree.render(tplRandomId);
		});
		Ext.ux.form.TreeCombo.superclass.initComponent.call(this);
	}
});
Ext.reg("treecombo", Ext.ux.form.TreeCombo);