<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="dbfound-tags" prefix="d"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<d:includeLibrary />
	</head>
	<style>
	.x-panel-header {
		padding-top: 6px;
		padding-bottom: 5px;
	}
	</style>
	<script type="text/javascript">
	 	var currentNode ;

	 	function open(){
	 		openTab(currentNode);
		}
		
		function contextmenu(node,e){
			if(node.isLeaf()){
				currentNode = node;
				menu.showAt([e.getPageX(),e.getPageY()]);            
				node.select(); 
			}
		}

		function openTab(node) {
			if(node.isLeaf()){
				var html = "<div style='height:100%;overflow:hidden'><iframe id='"+node.id+"_iframe' src='"+node.json.url+"' frameBorder=0 width='100%' height='100%'></iframe></div>";
				menuTab.add({title:node.text,html:html,id:node.id,closable:true});
				menuTab.activate(node.id);
			}
		}

		function refresh(){
			var tab = $D.getTab(currentNode.id);
			menuTab.activate(currentNode.id);
			if (tab) {
				var href= tab.location.href;
				tab.location.href = href;
			}
		}

		function quit(){
			$D.showConfirm("真的要退出系统吗?",function(btn){
				if(btn =="yes")
				window.location.href="close.jsp";
			});
		}

		function openPasswordWindow(){
			$D.open("passWordWindow","密码修改",355,185,"updatePassword.jsp");
		}

		function openUserInfo(){
			$D.open("userInfoWindow","用户信息",355,235,"userInfo.jsp");
		}
	</script>
	<body style="overflow: hidden">
	    <d:initProcedure>
	    	<d:dataSet id="menuStore" modelName="sys/menu"></d:dataSet>
	    </d:initProcedure>
	    <d:menu id="menu">
	       <d:menuItem title="打开" icon="DBFoundUI/images/add.gif" click="open"></d:menuItem>
	       <d:menuItem title="刷新" icon="DBFoundUI/images/submit.gif" click="refresh"></d:menuItem>
	    </d:menu>
	    
	    <script type="text/javascript">

			var menuTree = new Ext.tree.TreePanel( {
				width:190,
				heigth:'auto',
				split:true,//显示分隔条
				region:'west',
				collapsible:true,
				title : '功能菜单',
				bindTarget : menuStore,
				parentField : 'pid',
				idField : 'id',
				displayField : 'text',
				id : 'menuTree',
				useArrows : true,
				autoScroll : true,
				animate : true,
				containerScroll : true,
				rootVisible : false,
				root : {
					nodeType : 'node'
				},
				listeners : {
					'click' : openTab,
					'contextmenu' : contextmenu
				}
			}).init();
			menuStore.on("load", function() {
				menuTree.refresh();
			});
		</script>
		
		<script type="text/javascript">
			var menuTab = new Ext.TabPanel( {
				region:'center',
				id : "menuTab",
				heigth:'auto',
				activeTab : 0,
				animScroll : true,
				enableTabScroll : true,
				deferredRender : false,
				defaults : {
					autoScroll : true
				},
				items : [ {
					title : '首页',
					id : 'TAB8YM10',
					inited : true,
					url : '${basePath}welcome.jsp',
					html : "<div style='height:100%;overflow:hidden'><iframe id='TAB8YM10_iframe' src='welcome.jsp' frameBorder=0 width='100%' height='100%'></iframe></div>"
				} ]
			});
		</script>

		<script type="text/javascript">
			var vp=new Ext.Viewport({
				layout:"border",
				items:[
					{region: 'north',height:40},
					{region: 'south',border: false,height: 25,html:'<div style="text-align:center;height:30px;background-color:#dfe8f6;padding:3px;">DBFound快速应用开发平台，更好用的开发平台</div>'}, 
					menuTree,
					menuTab
				]
			});
		</script>
		
	    <div style="background-color:#306091;position:absolute;top:0;left:0;z-index:1;width:100%;">
	        <img style="float:left;margin-left:10px;margin-top:2px;width:160px;height:35px;" src="images/logo.png"/>
	        <div style="margin-left:0px;background-color:#306091;height:40px;font-weight:bold;line-height:30px;">
	            <table style="float:right;margin-top:7px;">
	                <tr>
	                    <td>
	                        <a href="javascript:openUserInfo();" style="color:#fff;margin-left:3px;font-size: 12px;text-decoration:none;"><B>用户：${user_name}</B></a>
	                        <span style="margin-left:3px;font-size: 12px">|</span>
	                         <a href="javascript:openPasswordWindow();" style="color:#fff;margin-left:3px;font-size: 12px;text-decoration:none;"><B>密码修改</B></a>
	                        <span style="margin-left:3px;font-size: 12px">|</span>
	                        <a href="javascript:quit();" style="color:#fff;margin:5px;margin-left:3px;font-size: 12px;text-decoration:none;"><B>退出系统</B></a>
	                        <span style="margin-left:5px;"></span>
	                    </td>
	                </tr>
	            </table>
	        </div>
        </div>

	    <script type="text/javascript">
			Ext.onReady(function(){
				menuTree.getRootNode().firstChild.expand();
			});
			if("${user_id}"==""){
				location.href ="${basePath}login.jsp";
			}
		</script>
	</body>
</html>
