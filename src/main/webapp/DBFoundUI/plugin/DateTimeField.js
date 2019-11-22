Ext.ns('Ext.ux.form');
Ext.ux.form.TimePickerField = function(config){
    Ext.ux.form.TimePickerField.superclass.constructor.call(this, config);
}
Ext.extend(Ext.ux.form.TimePickerField, Ext.form.Field, {
    defaultAutoCreate: {
        tag: 'div'
    },
    cls: 'x-form-timepickerfield',
    hoursSpinner: null,
    minutesSpinner: null,
    secondsSpinner: null,
    spinnerCfg: {
        width: 40
    },
    spinnerFixBoundries: function(value){
        if (value < this.field.minValue) {
            value = this.field.maxValue;
        }
        if (value > this.field.maxValue) {
            value = this.field.minValue;
        }
        return this.fixPrecision(value);
    },
    onRender: function(ct, position){
        Ext.ux.form.TimePickerField.superclass.onRender.call(this, ct, position);
        this.rendered = false;
        this.date = new Date();
        var values = {};
        if (this.value) {
            values = this._valueSplit(this.value);
            this.date.setHours(values.h);
            this.date.setMinutes(values.m);
            this.date.setSeconds(values.s);
            delete this.value;
        }
        else {
            values = {
                h: this.date.getHours(),
                m: this.date.getMinutes(),
                s: this.date.getSeconds()
            };
        }
        var spinnerWrap = Ext.DomHelper.append(this.el, {
            tag: 'div'
        });
        var cfg = Ext.apply({}, this.spinnerCfg, {
            renderTo: spinnerWrap,
            readOnly: this.readOnly,
            disabled: this.disabled,
            listeners: {
                spin: {
                    fn: this.onSpinnerChange,
                    scope: this
                },
                valid: {
                    fn: this.onSpinnerChange,
                    scope: this
                },
                afterrender: {
                    fn: function(spinner){
                        spinner.wrap.applyStyles('float: left');
                    },
                    single: true
                }
            }
        });
        this.hoursSpinner = new Ext.ux.form.SpinnerField(Ext.apply({}, cfg, {
            minValue: 0,
            maxValue: 23,
            cls: 'first',
            value: values.h
        }));
        this.minutesSpinner = new Ext.ux.form.SpinnerField(Ext.apply({}, cfg, {
            minValue: 0,
            maxValue: 59,
            value: values.m
        }));
        this.secondsSpinner = new Ext.ux.form.SpinnerField(Ext.apply({}, cfg, {
            minValue: 0,
            maxValue: 59,
            value: values.s
        }));
        Ext.DomHelper.append(spinnerWrap, {
            tag: 'div',
            cls: 'x-form-clear-left'
        });
        this.rendered = true;
    },
    _valueSplit: function(v){
        var split = v.split(':');
        return {
            h: split.length > 0 ? split[0] : 0,
            m: split.length > 1 ? split[1] : 0,
            s: split.length > 2 ? split[2] : 0
        };
    },
    onSpinnerChange: function(){
        if (!this.rendered) {
            return;
        }
        this.fireEvent('change', this, this.getRawValue());
    },
    disable: function(){
        Ext.ux.form.TimePickerField.superclass.disable.call(this);
        this.hoursSpinner.disable();
        this.minutesSpinner.disable();
        this.secondsSpinner.disable();
    },
    enable: function(){
        Ext.ux.form.TimePickerField.superclass.enable.call(this);
        this.hoursSpinner.enable();
        this.minutesSpinner.enable();
        this.secondsSpinner.enable();
    },
    setReadOnly: function(r){
        Ext.ux.form.TimePickerField.superclass.setReadOnly.call(this, r);
        this.hoursSpinner.setReadOnly(r);
        this.minutesSpinner.setReadOnly(r);
        this.secondsSpinner.setReadOnly(r);
    },
    clearInvalid: function(){
        Ext.ux.form.TimePickerField.superclass.clearInvalid.call(this);
        this.hoursSpinner.clearInvalid();
        this.minutesSpinner.clearInvalid();
        this.secondsSpinner.clearInvalid();
    },
    getRawValue: function(){
        if (!this.hoursSpinner) {
            this.date = new Date();
            return {
                h: this.date.getHours(),
                m: this.date.getMinutes(),
                s: this.date.getSeconds()
            };
        }
        else {
            return {
                h: this.hoursSpinner.getValue(),
                m: this.minutesSpinner.getValue(),
                s: this.secondsSpinner.getValue()
            };
        }
    },
    setRawValue: function(v){
        this.hoursSpinner.setValue(v.h);
        this.minutesSpinner.setValue(v.m);
        this.secondsSpinner.setValue(v.s);
    },
    isValid: function(preventMark){
        return this.hoursSpinner.isValid(preventMark) &&
        this.minutesSpinner.isValid(preventMark) &&
        this.secondsSpinner.isValid(preventMark);
    },
    validate: function(){
        return this.hoursSpinner.validate() &&
        this.minutesSpinner.validate() &&
        this.secondsSpinner.validate();
    },
    getValue: function(){
        var v = this.getRawValue();
        return String.leftPad(v.h, 2, '0') + ':' +
        String.leftPad(v.m, 2, '0') +
        ':' +
        String.leftPad(v.s, 2, '0');
    },
    setValue: function(value){
        if (!this.rendered) {
            this.value = value;
            return;
        }
        value = this._valueSplit(value);
        this.setRawValue(value);
        this.validate();
    }
});

Ext.form.TimePickerField = Ext.ux.form.TimePickerField;
Ext.reg('timepickerfield', Ext.form.TimePickerField);
Ext.ns('Ext.ux.form');
Ext.DateTimePicker = Ext.extend(Ext.DatePicker, {
    timeFormat: 'g:i:s A',
    timeLabel: '时间',
    timeWidth: 100,
    initComponent: function(){
        Ext.DateTimePicker.superclass.initComponent.call(this);
        this.id = Ext.id();
    },
    onRender: function(container, position){
        Ext.DateTimePicker.superclass.onRender.apply(this, arguments);
        var table = Ext.get(Ext.DomQuery.selectNode('table tbody', container.dom));
        var tfEl = Ext.DomHelper.insertBefore(table.last(), {
            tag: 'tr',
            children: [{
                tag: 'td',
                cls: 'x-date-bottom',
                html: this.timeLabel,
                style: 'width:30;'
            }, {
                tag: 'td',
                cls: 'x-date-bottom ux-timefield',
                colspan: '2'
            }]
        }, true);
        this.tf.render(table.child('td.ux-timefield'));
        var p = this.getEl().parent('div.x-layer');
        if (p) {
			if(Ext.isIE)
            p.setStyle("height", p.getHeight()+38);
        }
    },
    setValue: function(value){
    	Ext.DateTimePicker.superclass.setValue.apply(this, arguments);
        var old = this.value;
        
        if (!this.tf) {
            this.tf = new Ext.ux.form.TimePickerField();
            this.tf.ownerCt = this;
        }
        var h = value.format('H');
        var i = value.format('i');
        var s = value.format('s');
        if(h==="00"&&i==="00"&&s==="00"){
        	 this.value = this.getDateTime(value);
        	 var a = this;
        	
        	 setTimeout(function(){
        		 a.tf.hoursSpinner.setValue(0);
    			 a.tf.minutesSpinner.setValue(0);
    			 a.tf.secondsSpinner.setValue(0);
				 if(a.precision=='i'){
					 a.tf.secondsSpinner.disable();
				 }else if(a.precision=='H'){
				     a.tf.minutesSpinner.disable();
				     a.tf.secondsSpinner.disable();
				 }
	        }, 50);
        }else{
        	var a = this;
	        setTimeout(function(){
				 a.tf.hoursSpinner.setValue(h);
				 a.tf.minutesSpinner.setValue(i);
				 a.tf.secondsSpinner.setValue(s);
				 if(a.precision=='i'){
					 a.tf.secondsSpinner.disable();
				     a.tf.secondsSpinner.setValue(0);
				 }else if(a.precision=='H'){
					 a.tf.minutesSpinner.setValue(0);
				     a.tf.minutesSpinner.disable();
				     a.tf.secondsSpinner.setValue(0);
				     a.tf.secondsSpinner.disable();
				 }
	             a.value = a.getDateTime(value);
	        }, 50);
        }
    },
    getDateTime: function(value){
        if (this.tf) {
            var dt = new Date();
            var timeval = this.tf.getValue();
            value = Date.parseDate(value.format('Y-m-d') + ' ' + this.tf.getValue(), 'Y-m-d H:i:s');
        }
        return value;
    },
    selectToday: function(){
        if (this.todayBtn && !this.todayBtn.disabled) {
            this.value = this.getDateTime(new Date());
            this.fireEvent("select", this, this.value);
        }
    }
});
Ext.reg('datetimepickerfield', Ext.DateTimePicker);
if (parseInt(Ext.version.substr(0, 1), 10) > 2) {
    Ext.menu.DateTimeItem = Ext.DateTimePicker;
    Ext.override(Ext.menu.DateMenu, {
        initComponent: function(){
            this.on('beforeshow', this.onBeforeShow, this);
            if (this.strict = (Ext.isIE7 && Ext.isStrict)) {
                this.on('show', this.onShow, this, {
                    single: true,
                    delay: 20
                });
            }
            Ext.apply(this, {
                plain: true,
                showSeparator: false,
                items: this.picker = new Ext.DatePicker(Ext.apply({
                    internalRender: this.strict || !Ext.isIE,
                    ctCls: 'x-menu-date-item'
                }, this.initialConfig))
            });
            Ext.menu.DateMenu.superclass.initComponent.call(this);
            this.relayEvents(this.picker, ["select"]);
            this.on('select', this.menuHide, this);
            if (this.handler) {
                this.on('select', this.handler, this.scope || this);
            }
        }
    });
}
else {
    Ext.menu.DateTimeItem = function(config){
        Ext.menu.DateTimeItem.superclass.constructor.call(this, new Ext.DateTimePicker(config), config);
        this.picker = this.component;
        this.addEvents('select');
        
        this.picker.on("render", function(picker){
            picker.getEl().swallowEvent("click");
            picker.container.addClass("x-menu-date-item");
        });
        
        this.picker.on("select", this.onSelect, this);
    };
    
    Ext.extend(Ext.menu.DateTimeItem, Ext.menu.DateMenu, {
        onSelect: function(picker, date){
            this.fireEvent("select", this, date, picker);
            Ext.menu.DateTimeItem.superclass.handleClick.call(this);
        }
    });
}

Ext.menu.DateTimeMenu = function(config){
    Ext.menu.DateTimeMenu.superclass.constructor.call(this, config);
    this.plain = true;
    var di = new Ext.menu.DateTimeItem(config);
    this.add(di);
    this.picker = di;
    this.relayEvents(di, ["select"]);
    
    this.on('beforeshow', function(){
        if (this.picker) {
            this.picker.hideMonthPicker(true);
        }
    }, this);
};
Ext.extend(Ext.menu.DateTimeMenu, Ext.menu.Menu, {
    cls: 'x-date-menu',
    beforeDestroy: function(){
        this.picker.destroy();
    },
    hide: function(deep){
        if (this.picker.tf.innerList) {
            if ((Ext.EventObject.within(this.picker.tf.innerList)) || (Ext.get(Ext.EventObject.getTarget()) == this.picker.tf.innerList)) 
                return false;
        }
        if (this.el && this.isVisible()) {
            this.fireEvent("beforehide", this);
            if (this.activeItem) {
                this.activeItem.deactivate();
                this.activeItem = null;
            }
            this.el.hide();
            this.hidden = true;
            this.fireEvent("hide", this);
        }
        if (deep === true && this.parentMenu) {
            this.parentMenu.hide(true);
        }
    }
});

Ext.ux.form.DateTimeField = Ext.extend(Ext.form.DateField, {
	format:'Y-m-d H:i:s',
    dateFormat: 'Y-m-d',
    timeFormat: 'H:i:s',
    precision:'s',
    defaultAutoCreate: {
        tag: "input",
        type: "text",
        size: "20",
        autocomplete: "off"
    },
    initComponent: function(){
        Ext.ux.form.DateTimeField.superclass.initComponent.call(this);
        this.format = this.format;
        this.afterMethod('afterRender', function(){
            this.getEl().applyStyles('top:0');
        });
    },
    getValue: function(){
        return this.parseDate(Ext.form.DateField.superclass.getValue.call(this)) || '';
    },
    onTriggerClick: function(){
        if (this.disabled) {
            return;
        }
        if (this.menu == null) {
            this.menu = new Ext.menu.DateTimeMenu();
        }
        Ext.apply(this.menu.picker, {
            minDate: this.minValue,
            maxDate: this.maxValue,
            precision:this.precision,
            disabledDatesRE: this.ddMatch,
            disabledDatesText: this.disabledDatesText,
            disabledDays: this.disabledDays,
            disabledDaysText: this.disabledDaysText,
            format: this.format,
            timeFormat: this.timeFormat,
            dateFormat: this.dateFormat,
            showToday: this.showToday,
            minText: String.format(this.minText, this.formatDate(this.minValue)),
            maxText: String.format(this.maxText, this.formatDate(this.maxValue))
        });
        if (this.menuEvents) {
            this.menuEvents('on');
        }
        else {
            this.menu.on(Ext.apply({}, this.menuListeners, {
                scope: this
            }));
        }
        this.menu.picker.setValue(this.getValue() || new Date());
        this.menu.show(this.el, "tl-bl?");
    }
});
Ext.reg('datetimefield', Ext.ux.form.DateTimeField);

/**
 * 月份选择控件
 * 2013年7月15日9:33:29
 */
Ext.ux.form.MonthPicker =  Ext.extend(Ext.Component,{
    curYear         : (new Date()).getFullYear(),
    selYear         : (new Date()).getFullYear(),
    selMonth        : (new Date()).getMonth(),
    
    constructor: function(config){
        Ext.ux.form.MonthPicker.superclass.constructor.apply(this, arguments);
        this.addEvents('select');
        },
    onMonthClick:function(e, t){
        e.stopEvent();
        var el = new Ext.Element(t), td;
        if(el.is('a.x-date-mp-prev')){
            this.curYear -= 5;
            this.freshYear();
            }
        else if(el.is('a.x-date-mp-next')){
            this.curYear += 5;
            this.freshYear();
            }
        else if(td = el.up('td.x-date-mp-year', 2)){
            this.selYear = td.dom.num;
            this.freshYear();
            }
        else if(td = el.up('td.x-date-mp-month', 2)){
            this.selMonth = td.dom.num;
            this.freshMonth();
            var buf=[this.selYear,(this.selMonth>9 ? '' : '0')+this.selMonth];
            this.fireEvent('select', this, buf.join('-'))
            }
        },
    onRender : function(container, position){
        var m = [ '<div style="width: 160px; height:165px;"></div>' ]
        m[m.length] = '<div class="x-date-mp"></div>';
        var el = document.createElement("div");
        el.className = "x-date-picker";
        el.innerHTML = m.join("");
        container.dom.insertBefore(el, position);

        this.el = Ext.get(el);
        this.monthPicker = this.el.down('div.x-date-mp');
        this.monthPicker.enableDisplayMode('block');
        this.el.unselectable();

        this.createMonthPicker();
        var size = this.el.getSize();
        this.monthPicker.setSize(size);
        this.monthPicker.child('table').setSize(size);
        this.monthPicker.show();
        },
    createMonthPicker : function(){
        var buf = ['<table border="0" cellspacing="0">'];
        for(var i = 0; i < 6; i++){
            buf.push(
                '<tr>',
                i == 0 ?
                '<td class="x-date-mp-ybtn" align="center"><a class="x-date-mp-prev"></a></td><td class="x-date-mp-ybtn" align="center"><a class="x-date-mp-next"></a></td>' :
                '<td class="x-date-mp-year" colspan="2"><a href="#"></a></td>',
                '<td class="x-date-mp-month"><a href="#"></a></td>',
                '<td class="x-date-mp-month"><a href="#"></a></td>',
                '</tr>'
                );
            }
        buf.push('<tr class="x-date-mp-btns"><td style="height:25px" colspan="4">月份选择</td></tr>');
        buf.push('</table>');
        this.monthPicker.update(buf.join(''));
        this.monthPicker.on('click', this.onMonthClick, this);
        this.MonthTDs = this.monthPicker.select('td.x-date-mp-month');
        this.YearTDs = this.monthPicker.select('td.x-date-mp-year');
        this.freshYear();
        this.freshMonth();
        },
    freshYear:function(){
        this.YearTDs.removeClass('x-date-mp-sel');
        this.YearTDs.each(function(m, a, i){
            var y = this.curYear - 2 + i;
            m.dom.num = y;
            m.dom.firstChild.innerHTML = y + ' 年' ;
            if (y == this.selYear) m.addClass('x-date-mp-sel');
            i += 1;
            },this);
        },
    freshMonth:function(){
        this.MonthTDs.removeClass('x-date-mp-sel');
        this.MonthTDs.each(function(m, a, i){
            i += 1;
            m.dom.num = i;
            m.dom.firstChild.innerHTML = i + '月';
            if(i == this.selMonth) m.addClass('x-date-mp-sel');
            },this);
        }        
    }); 
    
Ext.ux.form.MonthMenu = Ext.extend(Ext.menu.Menu,{
    constructor: function(config){
        Ext.apply(this,{
            plain           : true,
            showSeparator   : false,
            items           : this.picker = new Ext.ux.form.MonthPicker({})
            });
        Ext.ux.form.MonthMenu.superclass.constructor.apply(this, arguments);
        this.relayEvents(this.picker, ["select"]);
        }
    });

Ext.ux.form.MonthField = Ext.extend(Ext.form.TriggerField,{
	triggerClass : 'x-form-date-trigger',
    constructor: function(config){
        Ext.ux.form.MonthField.superclass.constructor.apply(this, arguments);
        this.addEvents('select');
        if(this.handler) this.on("select", this.handler,  this.scope || this);
    },
    onTriggerClick: function(){
        if(this.disabled) return;
        this.onFocus();
        this.menu.show(this.el, "tl-bl?");
        this.menuEvents('on');
        
        var value = this.getValue();
        if(value==null|| value==""){
	       value = ""+ new Date().format("Y-m");
        }
        this.menu.picker.selMonth = value.substring(5);
        this.menu.picker.freshMonth();
        this.menu.picker.selYear = value.substring(0,4);
        this.menu.picker.freshYear();
    },
    menuEvents: function(method){
        this.menu[method]('select', this.onSelect, this);
        this.menu[method]('hide', this.onMenuHide, this);
        this.menu[method]('show', this.onFocus, this);
    },
    onMenuHide: function(){
        this.focus(false, 60);
        this.menuEvents('un');
    },
    onSelect:function(menu, date){
        this.setValue(date);
        this.fireEvent('select', this, date);
        this.menu.hide();
    },
    menu :new Ext.ux.form.MonthMenu({})
});
Ext.reg('monthfield', Ext.ux.form.MonthField);