<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script src="/js/jquery.js"></script>
<script src="/js/theme.js"></script>
<script src="/js/amazeui.min.js"></script>
<script src="/js/amazeui.datatables.min.js"></script>
<script src="/js/dataTables.responsive.min.js"></script>
<script src="/js/app.js"></script>
<script src="/js/vue.js"></script>
<div class="am-modal am-modal-alert" tabindex="-1" id="my-alert">
	<div class="am-modal-dialog">
		<div class="am-modal-hd"></div>
		<div class="am-modal-bd" id="successAlet"></div>
		<div class="am-modal-footer">
			<span class="am-modal-btn">确定</span>
		</div>
	</div>
</div>

<div class="am-modal am-modal-no-btn" tabindex="-1" id="your-modal">
	<div class="am-modal-dialog">
		<div class="am-modal-hd">
			<a href="javascript: void(0)" class="am-close am-close-spin"
				data-am-modal-close>&times;</a>
		</div>
		<div class="am-modal-bd" id="errorAlert"></div>
	</div>
</div>
   <script>

	var host=window.location.host;
	var url="ws://"+host+"/Information";
	var ws = new WebSocket(url);/* 这里注意url地址127和localhost是完全不同的   */
	
	ws.onopen = function(){
		console.log("websocket已连接");
	};
	
	ws.onmessage = function(evt) {
		console.log('服务器端发送的消息：' + evt.data);
		loadMessage();
	};
	//特别注意这里如何实现消息的实时刷新就是通过这里的onmessage方法,当有消息发送过来时就会调用loadMessage();
	
	$(document).ready(function() {
		loadMessage();
		$('#Notice').click(flush);//注意这里的选择器的使用 ,这个方法就是每当点击 id为Notice的元素自动调用 相应的方法 ,这里用于时间实时刷新 
	});
	//这里就是之前没有 想到加载时需要刷新消息 
	ws.onclose   = function(evt)  {  console.log("close"); };
	ws.onerror   = function(evt)  {  console.log("error"); };
	
	var Message =new Vue({
		el : '#MessageFlow',
		data : {
			MessageList : []/*vue的使用数据以数组的形式保存  ,注意这里的appFlow 对应 head中的 id  flowList 同样对应 head中的 ，所以这里不是 随意定义 ,同时这里还需要注意的是 include的jsp 是分开编译 然后 拼接在一起的 ,所以要注意不同jsp的方法最好不同  */
		}
	})
	
	function loadMessage() {
		$.post("/message/Information.do",{},Messageback);
	}
	
	function Messageback(messageDTO,status){/* 这里可以知道ajax的形式可以不一样  */
		var unReadNumber=messageDTO.unReadNumber;
		/* var results=messageDTO.obj;
		var startTime = new Date().getTime();
		for (var i=0;i<results.length; i++) {
			var result = results[i];
			result.createtime = parseInt((startTime-result.createtime)/1000/60);
			result.filed = '分钟之前';
			if (result.createtime >= 60) {
				result.createtime= parseInt(result.createtime/60);
				result.filed = '小时之前';
			}
		}
		
		Message.MessageList=results; */
		Message.MessageList=messageDTO.obj;
		$("#unReadNumber").html(unReadNumber);	
	}
	
	function flush(){
		var results=Message.MessageList;
		var startTime = new Date().getTime();
		for (var i=0;i<results.length; i++) {
			var result = results[i];
			result.time = parseInt((startTime-result.createtime)/1000);
			result.filed = '秒之前 ';
			if (result.time>= 60) {
				result.time = parseInt((startTime-result.createtime)/1000/60);
				result.filed = '分钟之前';
				if(result.time >= 60){
					result.time = parseInt((startTime-result.createtime)/1000/60/60);
					result.filed = '小时之前 ';
					}
				}
			 Vue.set(Message.MessageList, i, result)
			}
	}
	/*这里需要特别注意 的就是这里的VUE的set方法因为这里如果不用这个方法它不会显示 时间 ,同时需要和head。jsp 页面相联系 ,
		注意那边的 时间是time,那边的时间time就是在这里的result中 设置的一个属性 ,写的过程中因为对于 时间变量 的来源不清楚 ,所以出现的情况就是 直接 
		对 createtime进行时间转化 ,这样出现的 结果就是世界的 转化第一次正确,但是再次点击时间格式不正确 ,注意这里的result。time */
</script>