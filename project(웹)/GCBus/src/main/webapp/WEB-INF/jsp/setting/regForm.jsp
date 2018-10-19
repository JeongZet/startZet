<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
<title>정보 등록 페이지</title>
<%@ include file="/WEB-INF/include/include-header.jspf" %>
</head>
<body>
	<p>해당 페이지는 관리자만 해당 기능을 사용할 수 있습니다.</p>
	<p>관리자의 비밀번호:<input type="password" id="adminPw"></p>
	<a href="#this" name="register" id="regNodeInfo" class="btn">정류장 정보 등록</a>
	<a href="#this" name="register" id="regNodeToRoute" class="btn">정류장별 노선 정보 등록</a>
	<a href="#this" name="register" id="regRouteInfo" class="btn">노선 정보 등록</a>
	<a href="#this" name="register" id="regRouteToOrder" class="btn">노선 경로 정보 등록</a>
	<div id="registerConfirm">
		
	</div>
	<%@ include file="/WEB-INF/include/include-body.jspf" %>
	<script type="text/javascript">
		$("document").ready(function(){
			$("a[name=register]").on("click",function(e){
				e.preventDefault();
				fn_clickRegister($(this));
			})
		})
		
		function fn_clickRegister(obj){
			$("#registerConfirm").children().remove();
			var str="<p><b>"+obj.html()+"</b></p>";
			if(obj.attr("id").slice(3,8)=="Route"){
				str+="<p>노선ID 마지막 4자리 입력</p><b>MIN(XXXX)</b><input type='text' value='1252' id='MIN'><br><b>MAX(XXXX)</b> <input type='text' value='1706' id='MAX'><br><a href='#this' id='regConfirm' class='btn'>등록</a>";
			}else{
				str+="<a href='#this' id='regConfirm' class='btn'>등록</a>";
			}
			$("#registerConfirm").append(str);

			$("#regConfirm").on("click",function(e){
				e.preventDefault();
				fn_regConfirm(obj);
			})
			
		}

		function fn_regConfirm(obj){
			var comSubmit = new ComSubmit();
			comSubmit.addParam("adminPw",$("#adminPw").val());
			comSubmit.addParam("MIN",$("#MIN").val());
			comSubmit.addParam("MAX",$("#MAX").val());
			comSubmit.setUrl("<c:url value='/reg/"+obj.attr("id")+".do'/>");
			comSubmit.submit();
		}
	</script>
</body>
</html>