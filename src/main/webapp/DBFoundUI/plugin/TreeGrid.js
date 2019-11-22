Ext.ns('Ext.ux.tree');

Ext.ux.tree.TreeGridSorter = Ext.extend(Ext.tree.TreeSorter, {
    sortClasses : ['sort-asc', 'sort-desc'],
    sortAscText : 'Sort Ascending',
    sortDescText : 'Sort Descending',
    constructor : function(tree, config) {
        if(!Ext.isObject(config)) {
            config = {
                property: tree.columns[0].dataIndex || 'text',
                folderSort: true
            }
        }
        Ext.ux.tree.TreeGridSorter.superclass.constructor.apply(this, arguments);
        this.tree = tree;
        tree.on('headerclick', this.onHeaderClick, this);
        tree.ddAppendOnly = true;
        var me = this;
        this.defaultSortFn = function(n1, n2){
            var desc = me.dir && me.dir.toLowerCase() == 'desc',
                prop = me.property || 'text',
                sortType = me.sortType,
                caseSensitive = me.caseSensitive === true,
                leafAttr = me.leafAttr || 'leaf',
                attr1 = n1.attributes,
                attr2 = n2.attributes;
            if(me.folderSort){
                if(attr1[leafAttr] && !attr2[leafAttr]){
                    return 1;
                }
                if(!attr1[leafAttr] && attr2[leafAttr]){
                    return -1;
                }
            }
            var prop1 = attr1[prop],
                prop2 = attr2[prop],
                v1 = sortType ? sortType(prop1) : (caseSensitive ? prop1 : prop1.toUpperCase());
                v2 = sortType ? sortType(prop2) : (caseSensitive ? prop2 : prop2.toUpperCase());
            if(v1 < v2){
                return desc ? +1 : -1;
            }else if(v1 > v2){
                return desc ? -1 : +1;
            }else{
                return 0;
            }
        };
        tree.on('afterrender', this.onAfterTreeRender, this, {single: true});
        tree.on('headermenuclick', this.onHeaderMenuClick, this);
    },
    onAfterTreeRender : function() {
        if(this.tree.hmenu){
            this.tree.hmenu.insert(0,
                {itemId:'asc', text: this.sortAscText, cls: 'xg-hmenu-sort-asc'},
                {itemId:'desc', text: this.sortDescText, cls: 'xg-hmenu-sort-desc'}
            );
        }
        this.updateSortIcon(0, 'asc');
    },
    onHeaderMenuClick : function(c, id, index) {
        if(id === 'asc' || id === 'desc') {
            this.onHeaderClick(c, null, index);
            return false;
        }
    },
    onHeaderClick : function(c, el, i) {
        if(c && !this.tree.headersDisabled){
            var me = this;
            me.property = c.dataIndex;
            me.dir = c.dir = (c.dir === 'desc' ? 'asc' : 'desc');
            me.sortType = c.sortType;
            me.caseSensitive === Ext.isBoolean(c.caseSensitive) ? c.caseSensitive : this.caseSensitive;
            me.sortFn = c.sortFn || this.defaultSortFn;

            this.tree.root.cascade(function(n) {
                if(!n.isLeaf()) {
                    me.updateSort(me.tree, n);
                }
            });
            this.updateSortIcon(i, c.dir);
        }
    },
    updateSortIcon : function(col, dir){
        var sc = this.sortClasses,
            hds = this.tree.innerHd.select('td').removeClass(sc);
        hds.item(col).addClass(sc[dir == 'desc' ? 1 : 0]);
    }
});

Ext.tree.ColumnResizer = Ext.extend(Ext.util.Observable, {
    minWidth: 14,
    constructor: function(config){
        Ext.apply(this, config);
        Ext.tree.ColumnResizer.superclass.constructor.call(this);
    },
    init : function(tree){
        this.tree = tree;
        tree.on('render', this.initEvents, this);
    },
    initEvents : function(tree){
        tree.mon(tree.innerHd, 'mousemove', this.handleHdMove, this);
        this.tracker = new Ext.dd.DragTracker({
            onBeforeStart: this.onBeforeStart.createDelegate(this),
            onStart: this.onStart.createDelegate(this),
            onDrag: this.onDrag.createDelegate(this),
            onEnd: this.onEnd.createDelegate(this),
            tolerance: 3,
            autoStart: 300
        });
        this.tracker.initEl(tree.innerHd);
        tree.on('beforedestroy', this.tracker.destroy, this.tracker);
    },
    handleHdMove : function(e, t){
        var hw = 5,
        x = e.getPageX(),
        hd = e.getTarget('.x-treegrid-hd', 3, true);
        if(hd){                                 
            var r = hd.getRegion(),
                ss = hd.dom.style,
                pn = hd.dom.parentNode;
            if(x - r.left <= hw && hd.dom !== pn.firstChild) {
                var ps = hd.dom.previousSibling;
                while(ps && Ext.fly(ps).hasClass('x-treegrid-hd-hidden')) {
                    ps = ps.previousSibling;
                }
                if(ps) {                    
                    this.activeHd = Ext.get(ps);
    				ss.cursor = Ext.isWebKit ? 'e-resize' : 'col-resize';
                }
            } else if(r.right - x <= hw) {
                var ns = hd.dom;
                while(ns && Ext.fly(ns).hasClass('x-treegrid-hd-hidden')) {
                    ns = ns.previousSibling;
                }
                if(ns) {
                    this.activeHd = Ext.get(ns);
    				ss.cursor = Ext.isWebKit ? 'w-resize' : 'col-resize';                    
                }
            } else{
                delete this.activeHd;
                ss.cursor = '';
            }
        }
    },
    onBeforeStart : function(e){
        this.dragHd = this.activeHd;
        return !!this.dragHd;
    },
    onStart : function(e){
        this.dragHeadersDisabled = this.tree.headersDisabled;
        this.tree.headersDisabled = true;
        this.proxy = this.tree.body.createChild({cls:'x-treegrid-resizer'});
        this.proxy.setHeight(this.tree.body.getHeight());
        var x = this.tracker.getXY()[0];
        this.hdX = this.dragHd.getX();
        this.hdIndex = this.tree.findHeaderIndex(this.dragHd);
        this.proxy.setX(this.hdX);
        this.proxy.setWidth(x-this.hdX);
        this.maxWidth = this.tree.outerCt.getWidth() - this.tree.innerBody.translatePoints(this.hdX).left;
    },
    onDrag : function(e){
        var cursorX = this.tracker.getXY()[0];
        this.proxy.setWidth((cursorX-this.hdX).constrain(this.minWidth, this.maxWidth));
    },
    onEnd : function(e){
        var nw = this.proxy.getWidth(),
            tree = this.tree,
            disabled = this.dragHeadersDisabled;
        this.proxy.remove();
        delete this.dragHd;
        tree.columns[this.hdIndex].width = nw;
        tree.updateColumnWidths();
        setTimeout(function(){
            tree.headersDisabled = disabled;
        }, 100);
    }
});
Ext.ux.tree.TreeGridNodeUI = Ext.extend(Ext.tree.TreeNodeUI, {
    isTreeGridNodeUI: true,
    renderElements : function(n, a, targetNode, bulkRender){
        var t = n.getOwnerTree(),
            cols = t.columns,
            c = cols[0],
            i, buf, len;
        this.indentMarkup = n.parentNode ? n.parentNode.ui.getChildIndent() : '';
        
        var cb = Ext.isBoolean(a.checked);
        buf = [
             '<tbody class="x-tree-node">',
                '<tr ext:tree-node-id="', n.id ,'" class="x-tree-node-el x-tree-node-leaf ', a.cls, '">',
                    '<td class="x-treegrid-col">',
                        '<span class="x-tree-node-indent">', this.indentMarkup, "</span>",
                        '<img src="', this.emptyIcon, '" class="x-tree-ec-icon x-tree-elbow" />',
                        '<img src="', a.icon || this.emptyIcon, '" class="x-tree-node-icon', (a.icon ? " x-tree-node-inline-icon" : ""), (a.iconCls ? " "+a.iconCls : ""), '" unselectable="on" />',
                        cb ? ('<input class="x-tree-node-cb" type="checkbox" ' + (a.checked ? 'checked="checked" />' : '/>')) : '',
                        '<a hidefocus="on" class="x-tree-node-anchor" href="', a.href ? a.href : '#', '" tabIndex="1" ',
                            a.hrefTarget ? ' target="'+a.hrefTarget+'"' : '', '>',
                        '<span unselectable="on">', (c.tpl ? c.tpl.apply(a) : a[c.dataIndex] || c.text), '</span></a>',
                    '</td>'
        ];
        for(i = 1, len = cols.length; i < len; i++){
            c = cols[i];
            buf.push(
                    '<td class="x-treegrid-col ', (c.cls ? c.cls : ''), '">',
                        '<div unselectable="on" class="x-treegrid-text"', (c.align ? ' style="text-align: ' + c.align + ';"' : ''), '>',
                            (c.tpl ? c.tpl.apply(a) : a[c.dataIndex]),
                        '</div>',
                    '</td>'
            );
        }
        buf.push(
            '</tr><tr class="x-tree-node-ct"><td colspan="', cols.length, '">',
            '<table class="x-treegrid-node-ct-table" cellpadding="0" cellspacing="0" style="table-layout: fixed; display: none; width: ', t.innerCt.getWidth() ,'px;"><colgroup>'
        );
        for(i = 0, len = cols.length; i<len; i++) {
            buf.push('<col style="width: ', (cols[i].hidden ? 0 : cols[i].width) ,'px;" />');
        }
        buf.push('</colgroup></table></td></tr></tbody>');
        if(bulkRender !== true && n.nextSibling && n.nextSibling.ui.getEl()){
            this.wrap = Ext.DomHelper.insertHtml("beforeBegin", n.nextSibling.ui.getEl(), buf.join(''));
        }else{
            this.wrap = Ext.DomHelper.insertHtml("beforeEnd", targetNode, buf.join(''));
        }
        this.elNode = this.wrap.childNodes[0];
        this.ctNode = this.wrap.childNodes[1].firstChild.firstChild;
        var cs = this.elNode.firstChild.childNodes;
        this.indentNode = cs[0];
        this.ecNode = cs[1];
        this.iconNode = cs[2];
        this.anchor = cs[3];
        this.textNode = cs[3].firstChild;
        if (cb) {
        	       this.checkbox = cs[3];
        	        this.anchor = cs[4];
        	         this.textNode = cs[4].firstChild;
        	       } else {
        	         this.anchor = cs[3];
        	        this.textNode = cs[3].firstChild;
        	      }

    },
    animExpand : function(cb){
        this.ctNode.style.display = "";
        Ext.ux.tree.TreeGridNodeUI.superclass.animExpand.call(this, cb);
    }
});

Ext.ux.tree.TreeGridRootNodeUI = Ext.extend(Ext.tree.TreeNodeUI, {
    isTreeGridNodeUI: true,
    render : function(){
        if(!this.rendered){
            this.wrap = this.ctNode = this.node.ownerTree.innerCt.dom;
            this.node.expanded = true;
        }
        if(Ext.isWebKit) {
            var ct = this.ctNode;
            ct.style.tableLayout = null;
            (function() {
                ct.style.tableLayout = 'fixed';
            }).defer(1);
        }
    },
    destroy : function(){
        if(this.elNode){
            Ext.dd.Registry.unregister(this.elNode.id);
        }
        delete this.node;
    },
    collapse : Ext.emptyFn,
    expand : Ext.emptyFn
});
Ext.ux.tree.TreeGridLoader = Ext.extend(Ext.tree.TreeLoader, {
    createNode : function(attr) {
        if (!attr.uiProvider) {
            attr.uiProvider = Ext.ux.tree.TreeGridNodeUI;
        }
        return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
    }
});

(function() {
    Ext.override(Ext.list.Column, {
        init : function() {    
            var types = Ext.data.Types,
                st = this.sortType;
                    
            if(this.type){
                if(Ext.isString(this.type)){
                    this.type = Ext.data.Types[this.type.toUpperCase()] || types.AUTO;
                }
            }else{
                this.type = types.AUTO;
            }

            // named sortTypes are supported, here we look them up
            if(Ext.isString(st)){
                this.sortType = Ext.data.SortTypes[st];
            }else if(Ext.isEmpty(st)){
                this.sortType = this.type.sortType;
            }
        }
    });

    Ext.tree.Column = Ext.extend(Ext.list.Column, {});
    Ext.tree.NumberColumn = Ext.extend(Ext.list.NumberColumn, {});
    Ext.tree.DateColumn = Ext.extend(Ext.list.DateColumn, {});
    Ext.tree.BooleanColumn = Ext.extend(Ext.list.BooleanColumn, {});

    Ext.reg('tgcolumn', Ext.tree.Column);
    Ext.reg('tgnumbercolumn', Ext.tree.NumberColumn);
    Ext.reg('tgdatecolumn', Ext.tree.DateColumn);
    Ext.reg('tgbooleancolumn', Ext.tree.BooleanColumn);
})();

/**
 * @class Ext.ux.tree.TreeGrid
 * @extends Ext.tree.TreePanel
 * @xtype treegrid
 */
Ext.ux.tree.TreeGrid = Ext.extend(Ext.tree.TreePanel, {
    rootVisible : false,
    useArrows : true,
    lines : false,
    borderWidth : Ext.isBorderBox ? 0 : 2, // the combined left/right border for each cell
    cls : 'x-treegrid',
    columnResize : true,
    enableSort : false,
    reserveScrollOffset : true,
    enableHdMenu : true,
    columnsText : '列',
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
			if(this.columns && this.columns instanceof Object){
			    for(var j=0; j<this.columns.length;j++){
			    	var column = this.columns[j];
			    	config[column.dataIndex] = record.get(column.dataIndex);
			    }
			}
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
		try{root.firstChild.select();}catch(e){}
	    return this;
	},
    initComponent : function() {
        if(!this.root) {
            this.root = new Ext.tree.AsyncTreeNode({text: 'Root'});
        }
        var l = this.loader;
        if(!l){
            l = new Ext.ux.tree.TreeGridLoader({
                dataUrl: this.dataUrl,
                requestMethod: this.requestMethod,
                store: this.store
            });
        }else if(Ext.isObject(l) && !l.load){
            l = new Ext.ux.tree.TreeGridLoader(l);
        }
        this.loader = l;
        Ext.ux.tree.TreeGrid.superclass.initComponent.call(this);                    
        this.initColumns();
        if(this.enableSort) {
            this.treeGridSorter = new Ext.ux.tree.TreeGridSorter(this, this.enableSort);
        }
        if(this.columnResize){
            this.colResizer = new Ext.tree.ColumnResizer(this.columnResize);
            this.colResizer.init(this);
        }
        var c = this.columns;
        if(!this.internalTpl){                                
            this.internalTpl = new Ext.XTemplate(
                '<div class="x-grid3-header">',
                    '<div class="x-treegrid-header-inner">',
                        '<div class="x-grid3-header-offset">',
                            '<table style="table-layout: fixed;" cellspacing="0" cellpadding="0" border="0"><colgroup><tpl for="columns"><col /></tpl></colgroup>',
                            '<thead><tr class="x-grid3-hd-row">',
                            '<tpl for="columns">',
                            '<td class="x-grid3-hd x-grid3-cell x-treegrid-hd" style="text-align: {align};" id="', this.id, '-xlhd-{#}">',
                                '<div class="x-grid3-hd-inner x-treegrid-hd-inner" unselectable="on">',
                                     this.enableHdMenu ? '<a class="x-grid3-hd-btn" href="#"></a>' : '',
                                     '{header}<img class="x-grid3-sort-icon" src="', Ext.BLANK_IMAGE_URL, '" />',
                                 '</div>',
                            '</td></tpl>',
                            '</tr></thead>',
                        '</table>',
                    '</div></div>',
                '</div>',
                '<div class="x-treegrid-root-node">',
                    '<table class="x-treegrid-root-table" cellpadding="0" cellspacing="0" style="table-layout: fixed;"></table>',
                '</div>'
            );
        }
        if(!this.colgroupTpl) {
            this.colgroupTpl = new Ext.XTemplate(
                '<colgroup><tpl for="columns"><col style="width: {width}px"/></tpl></colgroup>'
            );
        }
    },
    initColumns : function() {
    	var cw = this.getTotalColumnWidth();
    	var tw = this.width;
    	if(tw && cw){
        	for(var i=0;i<this.columns.length;i++){
        		try{
        		var column= this.columns[i];
        		column.width = parseInt(column.width * (tw-22) / cw);
        		}catch(e){}
        	}
    	}
        var cs = this.columns,
            len = cs.length, 
            columns = [],
            i, c;
        for(i = 0; i < len; i++){
            c = cs[i];
            if(!c.isColumn) {
                c.xtype = c.xtype ? (/^tg/.test(c.xtype) ? c.xtype : 'tg' + c.xtype) : 'tgcolumn';
                c = Ext.create(c);
            }
            c.init(this);
            columns.push(c);
            
            if(c.sortable == true) {
                c.sortable = true;
                this.enableSort = true;
            }
        }
        this.columns = columns;
    },

    onRender : function(){
        Ext.tree.TreePanel.superclass.onRender.apply(this, arguments);
        this.el.addClass('x-treegrid');
        this.outerCt = this.body.createChild({
            cls:'x-tree-root-ct x-treegrid-ct ' + (this.useArrows ? 'x-tree-arrows' : this.lines ? 'x-tree-lines' : 'x-tree-no-lines')
        });
        this.internalTpl.overwrite(this.outerCt, {columns: this.columns});
        this.mainHd = Ext.get(this.outerCt.dom.firstChild);
        this.innerHd = Ext.get(this.mainHd.dom.firstChild);
        this.innerBody = Ext.get(this.outerCt.dom.lastChild);
        this.innerCt = Ext.get(this.innerBody.dom.firstChild);
        this.colgroupTpl.insertFirst(this.innerCt, {columns: this.columns});
        if(this.hideHeaders){
            this.el.child('.x-grid3-header').setDisplayed('none');
        }
        else if(this.enableHdMenu !== false){
            this.hmenu = new Ext.menu.Menu({id: this.id + '-hctx'});
            if(this.enableColumnHide !== false){
                this.colMenu = new Ext.menu.Menu({id: this.id + '-hcols-menu'});
                this.colMenu.on({
                    scope: this,
                    beforeshow: this.beforeColMenuShow,
                    itemclick: this.handleHdMenuClick
                });
                this.hmenu.add({
                    itemId:'columns',
                    hideOnClick: false,
                    text: this.columnsText,
                    menu: this.colMenu,
                    iconCls: 'x-cols-icon'
                });
            }
            this.hmenu.on('itemclick', this.handleHdMenuClick, this);
        }
    },
    setRootNode : function(node){
        node.attributes.uiProvider = Ext.ux.tree.TreeGridRootNodeUI;        
        node = Ext.ux.tree.TreeGrid.superclass.setRootNode.call(this, node);
        if(this.innerCt) {
            this.colgroupTpl.insertFirst(this.innerCt, {columns: this.columns});
        }
        return node;
    },
    clearInnerCt : function(){
        if(Ext.isIE){
            var dom = this.innerCt.dom;
            while(dom.firstChild){
                dom.removeChild(dom.firstChild);
            }
        }else{
            Ext.ux.tree.TreeGrid.superclass.clearInnerCt.call(this);
        }
    },
    initEvents : function() {
        Ext.ux.tree.TreeGrid.superclass.initEvents.apply(this, arguments);
        this.mon(this.innerBody, 'scroll', this.syncScroll, this);
        this.mon(this.innerHd, 'click', this.handleHdDown, this);
        this.mon(this.mainHd, {
            scope: this,
            mouseover: this.handleHdOver,
            mouseout: this.handleHdOut
        });
    },
    onResize : function(w, h) {
        Ext.ux.tree.TreeGrid.superclass.onResize.apply(this, arguments);
        var bd = this.innerBody.dom;
        var hd = this.innerHd.dom;
        if(!bd){
            return;
        }
        if(Ext.isNumber(h)){
            bd.style.height = this.body.getHeight(true) - hd.offsetHeight + 'px';
        }
        if(Ext.isNumber(w)){                        
            var sw = Ext.num(this.scrollOffset, Ext.getScrollBarWidth());
            if(this.reserveScrollOffset || ((bd.offsetWidth - bd.clientWidth) > 10)){
                this.setScrollOffset(sw);
            }else{
                var me = this;
                setTimeout(function(){
                    me.setScrollOffset(bd.offsetWidth - bd.clientWidth > 10 ? sw : 0);
                }, 10);
            }
        }
    },
    updateColumnWidths : function() {
        var cols = this.columns,
            colCount = cols.length,
            groups = this.outerCt.query('colgroup'),
            groupCount = groups.length,
            c, g, i, j;
        for(i = 0; i<colCount; i++) {
            c = cols[i];
            for(j = 0; j<groupCount; j++) {
                g = groups[j];
                g.childNodes[i].style.width = (c.hidden ? 0 : c.width) + 'px';
            }
        }
        for(i = 0, groups = this.innerHd.query('td'), len = groups.length; i<len; i++) {
            c = Ext.fly(groups[i]);
            if(cols[i] && cols[i].hidden) {
                c.addClass('x-treegrid-hd-hidden');
            }
            else {
                c.removeClass('x-treegrid-hd-hidden');
            }
        }
        var tcw = this.getTotalColumnWidth();                        
        Ext.fly(this.innerHd.dom.firstChild).setWidth(tcw + (this.scrollOffset || 0));
        this.outerCt.select('table').setWidth(tcw);
        this.syncHeaderScroll();    
    },
    getVisibleColumns : function() {
        var columns = [],
            cs = this.columns,
            len = cs.length,
            i;
        for(i = 0; i<len; i++) {
            if(!cs[i].hidden) {
                columns.push(cs[i]);
            }
        }        
        return columns;
    },
    getTotalColumnWidth : function() {
        var total = 0;
        for(var i = 0, cs = this.getVisibleColumns(), len = cs.length; i<len; i++) {
            total += cs[i].width;
        }
        return total;
    },
    setScrollOffset : function(scrollOffset) {
        this.scrollOffset = scrollOffset;                        
        this.updateColumnWidths();
    },
    // private
    handleHdDown : function(e, t){
        var hd = e.getTarget('.x-treegrid-hd');
        if(hd && Ext.fly(t).hasClass('x-grid3-hd-btn')){
            var ms = this.hmenu.items,
                cs = this.columns,
                index = this.findHeaderIndex(hd),
                c = cs[index],
                sort = c.sortable;
            e.stopEvent();
            Ext.fly(hd).addClass('x-grid3-hd-menu-open');
            this.hdCtxIndex = index;
            this.fireEvent('headerbuttonclick', ms, c, hd, index);
            this.hmenu.on('hide', function(){
                Ext.fly(hd).removeClass('x-grid3-hd-menu-open');
            }, this, {single:true});
            this.hmenu.show(t, 'tl-bl?');
        }
        else if(hd) {
            var index = this.findHeaderIndex(hd);
            this.fireEvent('headerclick', this.columns[index], hd, index);
        }
    },
    handleHdOver : function(e, t){                    
        var hd = e.getTarget('.x-treegrid-hd');                        
        if(hd && !this.headersDisabled){
            index = this.findHeaderIndex(hd);
            this.activeHdRef = t;
            this.activeHdIndex = index;
            var el = Ext.get(hd);
            this.activeHdRegion = el.getRegion();
            el.addClass('x-grid3-hd-over');
            this.activeHdBtn = el.child('.x-grid3-hd-btn');
            if(this.activeHdBtn){
                this.activeHdBtn.dom.style.height = (hd.firstChild.offsetHeight-1)+'px';
            }
        }
    },
    handleHdOut : function(e, t){
        var hd = e.getTarget('.x-treegrid-hd');
        if(hd && (!Ext.isIE || !e.within(hd, true))){
            this.activeHdRef = null;
            Ext.fly(hd).removeClass('x-grid3-hd-over');
            hd.style.cursor = '';
        }
    },
    findHeaderIndex : function(hd){
        hd = hd.dom || hd;
        var cs = hd.parentNode.childNodes;
        for(var i = 0, c; c = cs[i]; i++){
            if(c == hd){
                return i;
            }
        }
        return -1;
    },
    beforeColMenuShow : function(){
        var cols = this.columns,  
            colCount = cols.length,
            i, c;                        
        this.colMenu.removeAll();                    
        for(i = 1; i < colCount; i++){
            c = cols[i];
            if(c.hideable !== false){
                this.colMenu.add(new Ext.menu.CheckItem({
                    itemId: 'col-' + i,
                    text: c.header,
                    checked: !c.hidden,
                    hideOnClick:false,
                    disabled: c.hideable === false
                }));
            }
        }
    },
    handleHdMenuClick : function(item){
        var index = this.hdCtxIndex,
            id = item.getItemId();
        if(this.fireEvent('headermenuclick', this.columns[index], id, index) !== false) {
            index = id.substr(4);
            if(index > 0 && this.columns[index]) {
                this.setColumnVisible(index, !item.checked);
            }     
        }
        return true;
    },
    
    setColumnVisible : function(index, visible) {
        this.columns[index].hidden = !visible;        
        this.updateColumnWidths();
    },
    scrollToTop : function(){
        this.innerBody.dom.scrollTop = 0;
        this.innerBody.dom.scrollLeft = 0;
    },
    syncScroll : function(){
        this.syncHeaderScroll();
        var mb = this.innerBody.dom;
        this.fireEvent('bodyscroll', mb.scrollLeft, mb.scrollTop);
    },
    syncHeaderScroll : function(){
        var mb = this.innerBody.dom;
        this.innerHd.dom.scrollLeft = mb.scrollLeft;
        this.innerHd.dom.scrollLeft = mb.scrollLeft; // second time for IE (1/2 time first fails, other browsers ignore)
    },
    registerNode : function(n) {
        Ext.ux.tree.TreeGrid.superclass.registerNode.call(this, n);
        if(!n.uiProvider && !n.isRoot && !n.ui.isTreeGridNodeUI) {
            n.ui = new Ext.ux.tree.TreeGridNodeUI(n);
        }
    }
});
Ext.reg('treegrid', Ext.ux.tree.TreeGrid);