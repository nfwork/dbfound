<@compress>
<div style="margin-left:5px;margin-right:5px;margin-top:4px;margin-bottom:5px;${chart.style}" id="${chart.id}_div"></div>
<script type="text/javascript">
var ${chart.id} = new Ext.Panel({
	<#if chart.title??>title: '${chart.title}',</#if>
    renderTo: '${chart.id}_div',
    width:${chart.width},
    height:${chart.height},
    html:"<div id='${chart.id}_container'></div>",
    bindTarget : ${chart.bindTarget},
	valueField : "${chart.valueField}",
	displayField : "${chart.displayField}",
	init : function() {
    	height = Ext.get("${chart.id}_div").getHeight()-25;
		Ext.get("${chart.id}_container").setHeight(height);
		var chart = this;
		var data = [];
		var items = chart.bindTarget.data.items;
		for(i=0;i<items.length;i++){
			var json =items[i].json;
			data[i] = {name:json[chart.displayField],y:json[chart.valueField]};
			if(json.color && json.color != null && json.color != ""){
				data[i].color = json.color;
			}
		}
		/*if(data.length==0)return;*/
	    chart = $('#${chart.id}_container').highcharts( {
			chart : {
				plotBackgroundColor : null,
				plotBorderWidth : null,
				plotShadow : false
			},
			title : {
				<#if chart.title??>text : '${chart.title}'
				<#else>text : ''
				</#if>
			},
			tooltip : {
				formatter: function() {  
				    return '<b>'+ this.point.name +'</b>: '+ Highcharts.numberFormat(this.percentage, 2) +'% ('+  
				    Highcharts.numberFormat(this.y, 0, ',') +' ${chart.valueSuffix})';  
				}
			},
			plotOptions : {
				pie : {
					allowPointSelect : true,
					cursor : 'pointer',
					<#if chart.dataLabel == false>dataLabels : {enabled : false},</#if>
					showInLegend : true
				}
			},
			series : [ {
				type : 'pie',
				data : data
			} ]
		});
	    
	    this.chart = chart;
	}
});
${chart.id}.init();
${chart.bindTarget}.on("load",function(){${chart.id}.init();});
</script>
</@compress>