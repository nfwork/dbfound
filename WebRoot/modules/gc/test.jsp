<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%!int cellWidth = 60;
	int cellHeight = 40;

	int getX(int i) {
		return (i - 1) * cellWidth + i;
	}

	int getY(int i) {
		return i * cellHeight + i + 4;
	}

	private Map[] getMonthDays(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		Map[] Calendars = new Map[maxDay];
		int week_in_month = 1;
		for (int i = 1; i <= maxDay; i++) {
			Calendar a = Calendar.getInstance();
			a.set(Calendar.YEAR, year);
			a.set(Calendar.MONTH, month - 1);
			a.set(Calendar.DATE, i);

			Map map = new HashMap();
			int day = a.get(Calendar.DATE);
			int day_of_week = a.get(Calendar.DAY_OF_WEEK);

			map.put("day", day);
			map.put("top", getY(week_in_month));
			map.put("left", getX(day_of_week));
			Calendars[i - 1] = map;
			if (day_of_week == 7) {
				week_in_month++;
			}
		}
		return Calendars;
	}%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
		<style type="text/css">
			.calendar-body {
				position: absolute;
				left: 0;
				top: 0;
				width: 428px;
				height: 330px;
				background-image:url(images/calendar/body.jpg);
			}
			
			.calendar-cell {
			    text-align:center;
				padding-top: 12px;
				position: absolute;
				width: 60px;
				height: 28px ;
				height: 40px\9 ;
				background: #11aa22;
				cursor: pointer;
			}
			
			.calendar-cell-head {
			    background-image:url(images/calendar/header.jpg);
			    text-align:center;
				padding-top: 12px;
				position: absolute;
				width: 60px;
				height: 28px ;
				height: 40px\9 ;
			}
			
			.calendar-bottom {
				padding-top: 5px;
				top: 296px;
				vertical-align:middle;
				text-align:center;
				position: absolute;
				background: #a34544;
				width: 50px;
				height: 26px\9;
				height: 21px;
			}
		</style>
	</head>
	<d:initProcedure>
		<d:query modelName="gc/workRegist" queryName="displayQuery" rootPath="das" />
	</d:initProcedure>
	<%
	    int year = 2013;
	    int month = 6;
	    
	    String dateString = request.getParameter("date") ;
	    try{
	    	year = Integer.parseInt(dateString.substring(0,4));
	    	month = Integer.parseInt(dateString.substring(5));
	    }catch(Exception e){
	    }
	    Map[] dates =  getMonthDays(year, month);
	    List<Map> das = (List<Map> )request.getAttribute("das");
	    for(Map map : das){
	    	int day = Integer.parseInt( map.get("work_date").toString().substring(8));
	    	
	    	for(Map m : dates){
	    		int dd =(Integer) m.get("day");
	    		if(dd==day){
	    			m.put("style","background:#a34544");
	    		}
	    	}
	    }
		request.setAttribute("cells",dates);
	%>
	<body>
		<div class="calendar-body">
			<div class="calendar-cell-head" style="left:<%=getX(1)%>;">
				星期天
			</div>
			<div class="calendar-cell-head" style="left:<%=getX(2)%>;">
				星期一
			</div>
			<div class="calendar-cell-head" style="left:<%=getX(3)%>;">
				星期二
			</div>
			<div class="calendar-cell-head" style="left:<%=getX(4)%>;">
				星期三
			</div>
			<div class="calendar-cell-head" style="left:<%=getX(5)%>;">
				星期四
			</div>
			<div class="calendar-cell-head" style="left:<%=getX(6)%>;">
				星期五
			</div>
			<div class="calendar-cell-head" style="left:<%=getX(7)%>;">
				星期六
			</div>

			<d:forEach items="${cells}" var="cell">
				<div onclick="check(this)" class="calendar-cell" style="left:${cell.left}px;top:${cell.top}px;${cell.style}">
					${cell.day}
				</div>
			</d:forEach>

			<div style="left: 150px;" class="calendar-bottom">
				已报工
			</div>
			<div style="left: 210px; background: #11aa22;" class="calendar-bottom">
				未报工
			</div>
		</div>
		<script type="text/javascript">
			function check(div) {
				var b = div.style.background;
				var day = div.innerHTML.trim();
		
				var params = {};
				params.employee_id = ${param.employee_id};
				params.project_id = ${param.project_id};
				params.work_date = "${param.date}-" + day;
				
				var regiest = false;
				if(Ext.isIE){
					if(b != "#a34544"){
						regiest = true;
					}
				}else if(b=="" || b.indexOf("163")==-1){
					regiest = true;
				}
				
				if (regiest == true) {
					$D.request("gc/workRegist.execute!regiest", params, function(res) {
						if (res.success == true)div.style.background = "#a34544";
						else $D.showError(res.message);
					});
				} else {
					$D.request("gc/workRegist.execute!unAddRegiest", params, function(res) {
						if (res.success == true)div.style.background = "#11aa22";
						else $D.showError(res.message);
					});
				}
			}
		</script>
	</body>
</html>
