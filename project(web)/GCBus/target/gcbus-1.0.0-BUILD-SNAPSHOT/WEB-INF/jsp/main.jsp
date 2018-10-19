<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Main</title>
<%@ include file="/WEB-INF/include/include-header.jspf" %>
</head>
<body>
	<div id="gnb" style="position:fixed; display:block; left: 0px; top: 0px; bottom:0px;  background-color:#2a2d30; width: 80px;">
	</div>
	
	<div id="body" >
		<div id="searchMenu"  style="position:fixed; display:block; left:80px; top:0px; height:98px; width:220px; background-color: #e8eced; ">
			<div id="searchBox" style="margin:10px 10px;" >
				위도  <input type="text" id="lat" style="width:150px; height:20px;">
				경도  <input type="text" id="lng" style="width:150px; height:20px;">
				<a href="#this" id="sc_btn" class="btn" >찾기</a>
			</div>
		</div>
		
		<div id="resultBox" style="position:fixed; display:block; left:80px; top:100px; bottom:0px ; width:220px; background-color: #e8eced; ">
			<a href="#this" name="temp" id="temp" class="sc_result"></a>
		</div>
		
		<div id="bodyBox" style="position:fixed; left:300px; top:0px; bottom:0px; right:0px; padding:10px;">
			<div id="mapBox" style="width:100%; height: 100%;">
				
			</div>
		</div>
	</div>
	
	<%@ include file="/WEB-INF/include/include-body.jspf" %>
	<script type="text/javascript">
		<%@ include file="/js/daumAPI/map.js" %>
		
		// 지도에 클릭 이벤트를 등록합니다
		// 지도를 클릭하면 마지막 파라미터로 넘어온 함수를 호출합니다
		daum.maps.event.addListener(map, 'click', function(mouseEvent) {        
		    
		    // 클릭한 위도, 경도 정보를 가져옵니다 
		    var latlng = mouseEvent.latLng;
		    
		    var resultDiv1 = document.getElementById('search1'); 
		    resultDiv1.value = latlng.getLat();
		    var resultDiv2 = document.getElementById('search2');
		    resultDiv2.value = latlng.getLng();
		});

		$(document).ready(function(){
			$("#sc_btn").on("click",function(e){
				e.preventDefault();
				fn_clickSearchButton();
			})
		})
		
		function fn_clickSearchButton(){
			
			var str = "<a href='#this' name='temp' id='temp1' class='sc_result'></a>";
			
			$("#resultBox").append(str);

			$.ajax({
			    url: "/json/jsonList.do",
			    method: "post",
			    type: "json",
			    contentType: "application/json",
			    data: {"abc":"abc"},
			    success: function(data) {
			        console.log(data);
			    },
			    error:	function(){
					alert("as");
				}
			});
			/*
			var comSubmit = new ComSubmit();
			comSubmit.addParam("lat",$("#lat"));
			comSubmit.addParam("lng",$("#lng"));
			comSubmit.setUrl("<c:url value='/common/locationNode.do'/>");
			comSubmit.submit();
			*/
		}
	</script>
	
</body>
</html>