<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="en">

<body data-type="index" class="theme-white">
	
	 <div class="am-g tpl-g">
        
        <!-- 侧边导航栏 -->
        <jsp:include page="common/left.jsp"></jsp:include>
        
        <jsp:include page="common/head.jsp"></jsp:include>

        <!-- 内容区域 -->
        <div class="tpl-content-wrapper">

			<div class="row-content am-cf">
				<div class="row  am-cf">

					<div class="row">

						<div class="am-u-sm-12 am-u-md-12 am-u-lg-12">
							<div class="widget am-cf">
								<div class="widget-head am-cf">
									<div class="widget-title am-fl">消息</div>
									<div class="widget-function am-fr">
										<a href="javascript:;" class="am-icon-cog"></a>
									</div>
								</div>
								<div class="widget-body am-fr">

									<div class="widget am-cf">
										<div class="widget-head am-cf">
											<div class="widget-title am-fl"></div>
											<div class="widget-function am-fr"></div>
										</div>
										<div class="widget-body  widget-body-lg am-fr">

											<table width="100%"
												class="am-table am-table-compact am-table-striped tpl-table-black "
												id="msgFlow">
												<thead>
													<tr>
														<th>用户</th>
														<th>信息</th>
														<th>时间</th>
													</tr>
												</thead>
												<tbody>
													<tr class="gradeX" v-for="msg in messageList"> 
														<td>{{msg.userId}}</td>
														<td>{{msg.message}}</td>
														<td>{{msg.createtime}}</td>

													</tr>

													<!-- more data -->
												</tbody>
											</table>

											<ul class="am-pagination">
												<li><a href="###" onclick="fist();">首页 &raquo;</a></li>
												<li><a href="###" onclick="pre();">&laquo; 上一页</a></li>
												<li><a href="###" onclick="next();">下一页 &raquo;</a></li>
												<li><a href="###" onclick="tail();">尾页 &raquo;</a></li>
												<li id="fenyeInfo">1/20</li>
											</ul>

										</div>
									</div>



								</div>

							</div>
						</div>
					</div>

				</div>

			</div>



		</div>
	</div>


	<div class="am-modal am-modal-no-btn" tabindex="-1" id="your-modal">
		<div class="am-modal-dialog">
			<div class="am-modal-hd">
				<a href="javascript: void(0)" class="am-close am-close-spin"
					data-am-modal-close>&times;</a>
			</div>
			<div class="am-modal-bd" id="errorAlert">操作失败</div>
		</div>
	</div>

	<jsp:include page="common/foot.jsp"></jsp:include>
	<script src="/js/vue.js"></script>

	<script type="text/javascript">
		var currentPage = 1;
		var totalPage= 0;

		var msg = {};	

		$(document).ready(function() {
			msg= new Vue({
				el : '#msgFlow',
				data : {
					messageList : []
				}
			})
			

			loadMessage();
			$('#my-alert').on('close.modal.amui', function() {
				window.location.href = "/user/toUserCenter.do";
			});
		});

		function loadMessage() {
			var param={
	    			currentPage:currentPage
			};
			$.post('/message/list.do',param,callback);
		}
		
		function callback(ajaxDAO,status){
			if(ajaxDAO.success){
				var result = ajaxDAO.data;
				totalPage= ajaxDAO.data.totalPage;
				$('#fenyeInfo').html(currentPage + '/' + totalPage);

				msg.messageList= result.object; 
			}
			else{
				
				$('#errorAlert').html(ajaxDAO.message);
	    		$('#your-modal').modal('toggle');
				}
			}
		

		function next() {
			if (currentPage == totalPage) {
				return false;
			}

			currentPage = parseInt(currentPage) + 1;
			loadMessage();
		}

		function pre() {

			if (currentPage == 1) {
				return false;
			}

			currentPage = parseInt(currentPage) - 1;
			loadMessage();
		}

		function fist() {
			currentPage = 1;
			loadMessage();
		}

		function tail() {
			currentPage = totalPage;
			loadMessage();
		}
	</script>


</body>

</html>