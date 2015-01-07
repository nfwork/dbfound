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
	xAxis : ${chart.xAxis},
	yAxis : ${chart.yAxis},
	init : function() {
		height = Ext.get("${chart.id}_div").getHeight()-25;
		Ext.get("${chart.id}_container").setHeight(height);
		var chart = this;
		var categories = [];
		var items = chart.bindTarget.data.items;
		var yAxis = chart.yAxis;
		for(j=0;j<yAxis.length;j++){
			yAxis[j].data =[];
		}
		for(i=0;i<items.length;i++){
			var json =items[i].json;
			categories[i] = json[chart.xAxis.field];
			for(j=0;j<yAxis.length;j++){
				yAxis[j].data[i] = json[yAxis[j].field];
			}
		}
		/*if(categories.length==0)return;*/
		
	    chart = $('#${chart.id}_container').highcharts( {
	    	chart: {
	            type: 'column'
	        },
	        title: {
	        	<#if chart.title??>text : '${chart.title}'
					<#else>text : ''
				</#if>
	        },
	        subtitle: {
	            text: ''
	        },
	        xAxis: {
	            categories:categories,
	            labels: {
                    rotation: ${chart.rotation},
                    align: <#if chart.rotation="0">'center'<#else>'right'</#if>,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
	        },
	        yAxis: {
	            min: 0,
	            title: {
	                text: chart.xAxis.name
	            }
	        },
	        tooltip: {
	            shared: true,
	            useHTML: true,
	            valueSuffix: '${chart.valueSuffix}'
	        },
	        plotOptions: {
	            column: {
	                pointPadding: 0.2,
	                borderWidth: 0
	            }
	        },
	        series: yAxis
	    });
	    
	    this.chart = chart;
	}
});
${chart.id}.init();
${chart.bindTarget}.on("load",function(){${chart.id}.init();});
</script>
</@compress>