<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<body data-type="index" class="theme-white">
    
    <div class="am-g tpl-g">
    	  <jsp:include page="common/head.jsp"></jsp:include>
    	  
    	    <jsp:include page="common/left.jsp"></jsp:include>
    	    
    	      <div class="tpl-content-wrapper">

            <div class="row-content am-cf">
                <div class="row  am-cf" id="cardData">
                    
                </div>

               

 <div class="am-u-sm-12 am-u-md-12 am-u-lg-6">
                        <div class="widget am-cf">
                            <div class="widget-head am-cf">
                                <div class="widget-title am-fl">最近十笔流水</div>
                                <div class="widget-function am-fr">
                                    
                                </div>
                            </div>
                            <div class="widget-body  widget-body-lg am-fr">

                                <table width="100%" class="am-table am-table-compact am-table-bordered am-table-radius am-table-striped tpl-table-black " id="example-r">
                                    <thead>
                                        <tr>
                                            <th>卡号</th>
                                            <th>时间</th>
                                            <th>金额</th>
                                            
                                        </tr>
                                    </thead>
                                    <tbody id="flow">
                                        
                                    </tbody>
                                </table>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
      <jsp:include page="common/foot.jsp"></jsp:include>
   
      <script type="text/javascript">
      
      $(document).ready(function() {
    	  flow();
    	  ten();
  	});
      
      function upload() {
  		$('#avatarForm').submit();
  		}
		
		function flow(){
			$.post('/user/Flow.do',{},back);
		}
		
		function back(data,status){
			alert("点击");
			/* var ajaxDAO=JSON.parse(data); */
			var ajax=data;
			var card="";
			var result=ajax.data;
				for (var i=0; i<result.length;i++) {
					card += '<div class="am-u-sm-12 am-u-md-6 am-u-lg-4">';
					card += '<div class="widget widget-primary am-cf">';
					card += '<div class="widget-statistic-header">';
					card += result[i].number;
					card += '</div>';
					card += '<div class="widget-statistic-body">';
					card += '<div class="widget-statistic-value">';
					card += '￥' + result[i].money;
					card += '</div>';
					card += '<div class="widget-statistic-description">';
					card += '</div>';
					card += '<span class="widget-statistic-icon am-icon-credit-card-alt"></span>';
					card += '</div>';
					card += '</div>';
					card+= '</div>';
	        }
	    	$('#cardData').html(card); 
		}
		
		function ten(){
			var param={
				};
				$.post('/user/ten.do',param,call);
		}
		
		function call(data,status){
			alert("点击");
			/* var ajaxDAO=JSON.parse(data); */
			var ajaxDAO=data;
			alert(ajaxDAO);
			var result=ajaxDAO.data;
			var msg="<thead><tr> <th>卡号</th> <th>时间</th> <th>金额</th> <th>备注</th></tr>  </thead>";
			for (var i=0; i<result.length; i++) {
                 msg+='<tr class="gradeX">';
				msg+=' <td>'+result[i].number+'</td>';
				msg+='<td>'+ result[i].createtime+'</td>';
				msg+='<td>'+result[i].money+'</td>';
				msg+='<td>'+result[i].description+'</td>';
				msg+='</tr>';
			}
			$('#example-r').html(msg);
		}
		
		function loadAvatar() {
			$('#picture').attr('src','/user/showPicture.do?'+new Date().getTime());	
    	}/*  页面中的页面 */
    	
    </script>
    
</body>

<iframe name="avatarFrame" style="display: none;">
</iframe>

</html>