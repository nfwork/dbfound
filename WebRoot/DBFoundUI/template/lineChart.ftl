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
		height = Ext.get("${chart.id}_div").getHeight()-35;
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
            	type: '${chart.type}'
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
            	tickInterval: items.length>${chart.maxLable}?Math.floor(items.length/${chart.maxLable}):1,
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
            	 plotLines: [{
                     value: 0,
                     width: 1,
                     color: '#808080'
                 }],
                title: {
                    text: chart.xAxis.name
                }
            },
            tooltip: {
                valueSuffix: '${chart.valueSuffix}'
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -10,
                y: 100,
                borderWidth: 0
            },
             plotOptions : {
				spline : {
					lineWidth : 2, 
					states : {
						hover : {
							lineWidth : 3
						}
					},
					marker : {
						enabled : items.length<${chart.maxPoint}?true:false
					}
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