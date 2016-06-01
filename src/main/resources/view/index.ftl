<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<#assign ctx=request.contextPath />
<html>

<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="description" content="">
<title>资源管理器</title>
<!-- Bootstrap Core CSS -->
<link href="${ctx}/css/bootstrap.min.css" rel="stylesheet">
<!-- MetisMenu CSS -->
<link href="${ctx}/css/dashboard.css" rel="stylesheet">
<link href="${ctx}/css/zTreeStyle/zTreeStyle.css" rel="stylesheet">
<link href="${ctx}/css/global.css" rel="stylesheet">
<link href="${ctx}/css/uudisk.css" rel="stylesheet">

<script src="${ctx}/js/jquery.min.js"></script>
<!-- Bootstrap Core JavaScript -->
<script src="${ctx}/js/bootstrap.min.js"></script>
<script src="${ctx}/js/jquery.ztree.core-3.5.js" type="text/javascript"></script>
<script src="${ctx}/js/dmuploader.min.js"></script>
<script src="${ctx}/js/validator.min.js"></script>
</head>

<body>

	<nav class="navbar navbar-default">
	<div class="container-fluid">
		<a class="navbar-brand" href=""><span class="glyphicon glyphicon-home"></span>资源管理器</a>

	</div>

	</nav>

	<div style="float: left;">
		<!--	id: <input id="txtId" type="text" value="" /><br /> 
		fid: <input id="txtfid" type="text" value="" /><br /> 
		名字：<input id="txtAddress" type="text" value="" />-->
	</div>

	<div class="container">
		<div class="row">
			<div class="col-lg-4">
				<div class="panel panel-primary">
					<div class="panel-body">
						<p>
							目录<input class="form-control" type="text" name="inputdir" placeholder="Windows 默认目录是C:/ &nbsp Linux 默认目录是/" id="inputdirid" value="">
						</p>
						<button id="buttonfordirinput" class="btn btn-primary">遍历目录</button>
					</div>
					<div class="panel-body">
						<ul id="treeDemo" class="ztree">
						</ul>

					</div>
				</div>
			</div>

			<div class="col-lg-8">

				<div class="panel panel-primary">

					<div class="panel-body">

						<p id="disk_path"></p>

						<div id="showContent"></div>

					</div>
				</div>

			</div>

		</div>

		<div id="rMenu">
			<li>
				<ul id="m_del" onclick="delPrivilege();">
					<li>删除</li>
				</ul> <!--  			<ul id="m_add" onclick="addPrivilege();">
					<li>增加</li>
				</ul>
				<ul id="m_del" onclick="editPrivilege();">
					<li>编辑</li>
				</ul>
				<ul id="m_del" onclick="queryPrivilege();">
					<li>查看</li>
				</ul>-->
			</li>
		</div>


		<script>
			var zTree;
			var treepath;
			var userinputpath;
			var rightclickpath;
			var date = "";

			$("#buttonfordirinput").click(
					function() {

						$('#showContent').empty();
						$('#treeDemo').empty();
						$('#disk_path').empty();
						userinputpath = $('#inputdirid').val();
						$.ajax({
							url : "${ctx}/GetSingleLevelNodes",
							type : "POST",
							data : "path=" + userinputpath,
							async : "true",
							dataType : "json",
							success : function(data) {

								zNodes = data;
								zTree = $.fn.zTree.init($("#treeDemo"),
										setting, zNodes);
							}
						});
					});

			function delPrivilege() {
				hideRMenu();
				$.ajax({
					url : "${ctx}/delTreeNode",
					type : "POST",
					data : "path=" + rightclickpath,
					async : true,
					dataType : "json",
					success : function(data) {
						if (data == true)
							$("#buttonfordirinput").trigger('click')

					}
				});
			}

			var setting = {
				view : {
					showLine : true,
				},
				data : {
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "fid",
						rootPId : ""
					}
				},
				async : {
					autoParam : [ "path" ],
					contentType : "application/x-www-form-urlencoded",
					enable : true,
					type : "post",
					url : "${ctx}/asncReadSingleLevelfiles"
				},
				callback : {
					onClick : zTreeOnClick,
					onRightClick : zTreeOnRightClick,
					beforeExpand : zTreeOnbeforeExpand
				}
			};
			//鼠标点击节点
			function zTreeOnClick(treeId, treeNode) {
				$('#showContent').empty();
				$('#disk_path').empty();
				var treeObj = $.fn.zTree.getZTreeObj(treeNode);
				var selectedNode = treeObj.getSelectedNodes()[0];
				$("#txtId").val(selectedNode.id);
				$("#txtfid").val(selectedNode.fid);
				$("#txtAddress").val(selectedNode.path);

				console.info(selectedNode.path);
				treepath = selectedNode.path;
				getFileDetail(treepath, selectedNode, treeObj);

			}

			//鼠标右键事件-创建右键菜单
			function zTreeOnRightClick(event, treeId, treeNode) {

				rightclickpath = treeNode.path;

				if (!treeNode) {
					zTree.cancelSelectedNode();
					showRMenu("root", event.clientX, event.clientY);
				} else if (treeNode && !treeNode.noR) { //noR属性为true表示禁止右键菜单
					if (treeNode.newrole && event.target.tagName != "a"
							&& $(event.target).parents("a").length == 0) {
						zTree.cancelSelectedNode();
						showRMenu("root", event.clientX, event.clientY);
					} else {
						zTree.selectNode(treeNode);
						showRMenu(
								"node",
								event.clientX
										+ (document.documentElement.scrollLeft || document.body.scrollLeft),
								event.clientY
										+ (document.documentElement.scrollTop || document.body.scrollTop));
					}
				}
			}
			//节点展开事件，用于disable Ztree缓存
			function zTreeOnbeforeExpand(treeId, treeNode) {
				var zTree = $.fn.zTree.getZTreeObj("treeDemo");
				zTree.reAsyncChildNodes(treeNode, "refresh");
			}
			//显示右键菜单
			function showRMenu(type, x, y) {
				$("#rMenu ul").show();
				$("#rMenu").css({
					"top" : y + "px",
					"left" : x + "px",
					"display" : "block"
				});
			}

			//隐藏右键菜单  
			function hideRMenu() {
				$("#rMenu").hide();
			}

			$(function() {
				$("body").bind(
						//鼠标点击事件不在节点上时隐藏右键菜单
						"mousedown",
						function(event) {
							if (!(event.target.id == "rMenu" || $(event.target)
									.parents("#rMenu").length > 0)) {
								$("#rMenu").hide();
							}
						});
			});
			//获取文件详细信息Size
			function getFileDetail(nowPath, selectedNode, treeObj) {
				$('#showContent')
						.before(
								'<p id="reload_gif"><img src="images/load.gif" width="25px"  height="25px"  align="absmiddle"/>&nbsp;&nbsp;正在加载……</p>');
				$
						.ajax({
							type : "post",
							url : "${ctx}/IterateDirChildren?date="
									+ new Date().getTime(),
							data : "path=" + nowPath,
							async : "true",
							dataType : "JSON",
							success : function(data) {
								$('#reload_gif').remove();
								$('#showContent').html("");

								$('#disk_path').html(
										'<div style="text-align: left;">'
												+ treepath + '</div>');
								$
										.each(
												data.files,
												function(n, file) {
													if (file.fileType == 'DIR') {
														$('#showContent')
																.append(
																		'<div class="fileDetail"><img src="images/floder.png" title="'+file.fileName+'"/><span>'
																				+ file.fileName
																				+ '</span></div>'
																				+ '<div  class="fileSize">'
																				+ file.fileSize
																				+ '</div><div class="clear"></div>');
													} else {
														$('#showContent')
																.append(

																		'<div class="fileDetail"><img src="images/config.png" title="大小：'+file.fileSize+'&#10;'+file.fileName+'"/><span>'
																				+ file.fileName
																				+ '</span></div>'
																				+ '<div class="fileSize">'
																				+ file.fileSize
																				+ '</div><div class="clear"></div>');
													}
												});
								//计算大小后，重新加载当前节点的目录树
								treeObj.reAsyncChildNodes(selectedNode,
										"refresh");

							}
						});

			}

			$(document).ready(function() {

			});
		</script>
</body>

</html>