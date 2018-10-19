<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Main</title>
<%@ include file="/WEB-INF/include/include-header.jspf" %>
</head>
<body>
	<%@ include file="/WEB-INF/include/include-gnb.jspf" %>
	
	<div id="body" >
		<div id="searchMenu" class="leftBox">
			<div id="searchBox" class="leftBox" >
				<p class="margin0">위도  <input type="text" id="lat" value="36.12354" style="width:150px; height:20px;"></p>
				<p class="margin0">경도  <input type="text" id="lng" value="128.11828" style="width:150px; height:20px;"></p>
				<input type="hidden" value="${nodeid }" id="nodeidVal" >
				<input type="hidden" value="${nodename }" id="nodenameVal">
				<input type="hidden" value="${lat }" id="latVal">
				<input type="hidden" value="${lng }" id="lngVal">
				<a href="#this" id="sc_btn" class="btn" >찾기</a>
			</div>
		</div>
		<div id="resultBox" class="leftBox">
		</div>
		
		<div id="bodyBox">
			<div id="mapBox" class="mapBox">
			</div>
		</div>
	</div>
	<%@ include file="/WEB-INF/include/include-body.jspf" %>
	<script src="<c:url value='/js/daumAPI/map.js'/>" charset="utf-8"></script>

	<script type="text/javascript">
		var total_Page=1;
		var cur_Page=1;//전체 노선 볼 때의 페이지 값들이다.
		$("#node_find").addClass("selected");
		
		fn_clickSetting();
	
		//버스 노선 페이지에서 검색된 경로의 정류장의 정보보기의 기능 활성화의 경우를 위한 부분으로 실시간 노선 정보를 바로 보여줌.
		if($("#nodeidVal").val()){
			fn_nodeRealTime(null);
		}
		
		$(document).ready(function(){
			
			$("#sc_btn").on("click",function(e){
				e.preventDefault();
				fn_clickSearchButton();
			})
			$("a[name^='node']").on("click",function(e){
				e.preventDefault();
				fn_clickNode($(this));
			})
			
		})
		
		function fn_clickSearchButton(){
			
			setMarkers(null);
			
			$("#resultBox").children().remove();
			//경도 위도를 ajax를 통해 보낼 데이터에 추가			
			var data={};
			data["LAT"]=$("#lat").val();
			data["LNG"]=$("#lng").val();

			//지도 중심을 변경합니다.
			map.setCenter(new daum.maps.LatLng(data["LAT"],data["LNG"]));

			/*
			이 부분에서 위도 경도 좌표를 서버로 보내주고 서버에서는 오픈API를 통해 정류장 ID와 정류장 이름을 가져와 ajax로 리턴해준다.
			보내는 데이터 : lat,lng
			받는 데이터 : List<Map<String,Object> 정류장 정보> 정류장 정보들 
			*/
			$.ajax({
			    url: "/gcbus/nodeList.do",
			    dataType:"json",
			    type: "POST",
			    contentType : "application/json",
			    data: JSON.stringify(data),
			    success: function(result) {
					for(var i=0; i< result["list"].length;i++){
						var map=result["list"][i];
			        	var str = "<a href='#this' nodeid='"+map["NODEID"]+"' lat='"+map["LAT"]+"' lng='"+map["LNG"]+"' name='node"+i+"' id='node"+i+"' class='result sc_node_result'>"+map["NODENM"]+"</a>";
						$("#resultBox").append(str);

						//Daum API marker 생성 함수()
						fn_nodeMarkerMaker($("a[name='node"+i+"']"));
								
					}
					
					$("a[name^='node']").on("click",function(e){
						e.preventDefault();
						fn_clickNode($(this));
					})
			    },
			    error:	function(){
					alert("실패");
				}
			});
		}

		function fn_clickNode(obj){
			setMarkers(null);
			if(customOverlay!=null){
				customOverlay.setMap(null);
			}
			
			if($("a[name^='node']").hasClass("selected")){
				$("a[name^='node']").removeClass("selected");
				fn_clickSearchButton();
				
			}else{
				$("a[name^='node']").addClass("selected");
				
				fn_nodeRealTime(obj);
			}
		}
		function fn_nodeRealTime(obj){
			//버스 노선 페이지에서 검색된 경로의 정류장의 정보보기의 기능 활성화의 경우를 위한 변수들 초기화
			if($("#nodeidVal").val()!=null&&!($("#nodeidVal").hasClass("used"))){
				var str = "<a href='#this' nodeid='"+$("#nodeidVal").val()+"' lat='"+$("#latVal").val()+"' lng='"+$("#lngVal").val()+"' name='node1' id='node1' class='result sc_node_result'>"+$("#nodenameVal").val()+"</a>";
				$("#resultBox").append(str);
				obj=$("#node1");

				$(obj).addClass("selected");
				$("#nodeidVal").addClass("used");
			}
			//지도 중심을 변경합니다.
			map.setCenter(new daum.maps.LatLng(obj.attr("lat"),obj.attr("lng")));
			//선택한 정류장의 오버레이를 활성화합니다.
			fn_setOverlay(obj);

			var divStr="<div id='routeBox' class='result'><div id='pageDiv'></div></div>";
			obj.siblings().remove();	
			obj.parent().append(divStr);
			//선택한 정류장의 위치에 마커를 표시한다.
			fn_nodeMarkerMaker(obj);

			/*
			이 부분에서 정류장 ID를 서버로 보내주고 서버에서는 OPEN API를 통해 정류장 실시간 도착 노선 정보를 가져와 ajax로 리턴해준다.
			보내는 데이터 : nodeID
			받는 데이터 : 정류장 실시간 도착 정보 
			*/
			$.ajax({
				dataType:"json",
				type:"POST",
				contentType:"application/json",
				url:"/gcbus/nodeRealTime.do",
				data:JSON.stringify({NODEID:obj.attr("nodeid")}),
				success:function(result){
						for(var i=0;i<result["list"].length;i++){
							var map=result["list"][i];
							var arrtime= Math.floor(map["ARRTIME"]/60);
							var str = "<a href='#this' routeno='"+map["ROUTENO"]+"' routeid='"+map["ROUTEID"]+"' name='route"+i+"' id='route"+i+"' class='result sc_real_route_result'>"+map["ROUTENO"]
							+"("+map["VEHICLETP"]+")<p>"+map["ARRPREV"]+"정류장 전("+arrtime+"분)</p></a>";

							$("#routeBox").append(str);
						}

						$("#routeBox").css("height", 52*result["list"].length);
						$("#routeBox").append("<a href='#this' id='busList_Btn' class='btn' >모든 버스 노선 보기</a>");

						$("a[name^=route]").on("click",function(e){
							e.preventDefault();
							fn_routeInfo($(this));
						});						
						
						$("#busList_Btn").on("click",function(e){
							e.preventDefault();
							fn_nodeToRoute(obj);
						});
						},
				error:function(){
					alert("error");
				}
			})
						
		}
		
		function fn_nodeToRoute(obj){
			$("#routeBox").children().remove();

			var data={};
			data["NODEID"]= obj.attr("nodeid");
			data["PAGE_NO"]= cur_Page;	

			/*
			이 부분에서 정류장 ID와 페이지 번호를 서버로 보내주고 서버에서는 데이터베이스를 통해 정류장 전체 노선 정보를 가져와 ajax로 리턴해준다.
			보내는 데이터 : nodeID
			받는 데이터 : 정류장 전체 노선 정보 
			*/
			$.ajax({
				dataType:"json",
				contentType:"application/json",
				type:"POST",
				url: "/gcbus/nodeToRouteList.do",
				data: JSON.stringify(data),
				success:function(result){
						for(var i=0; i< result["list"].length;i++){
							var map=result["list"][i];
				        	var str = "<a href='#this' routeno='"+map["ROUTENO"]+"' routeid='"+map["ROUTEID"]+"' name='route"+i+"' id='route"+i+"' class='result sc_route_result'>"+map["ROUTENO"]+"<span class='routeSpan'>("+map["STARTNODENM"]+"~"+map["ENDNODENM"]+")</span></a>";

				        	$("#routeBox").append(str);
							
						}
						$("#routeBox").css("height", (52*result["list"].length)+20);
						if(result["list"][0]!=null){
							total_Page = (result["list"][0]["TOTAL_COUNT"]/10);
						}
						if(cur_Page>1){$("#routeBox").append("<a href='#this' id='route_Pre' class='btn routeBtn' >이전</a>");}
						if(cur_Page<total_Page){$("#routeBox").append("<a href='#this' id='route_Next' class='btn routeBtn' >다음</a>");}
						
						$("#routeBox").append("<a href='#this' id='busList_Btn' class='btn' >실시간 노선 보기</a>");

						$("#route_Pre").on("click",function(e){
							e.preventDefault();
							cur_Page--;
							fn_nodeToRoute(obj);
						});

						$("#route_Next").on("click",function(e){
							e.preventDefault();
							cur_Page++;
							fn_nodeToRoute(obj);
						});
						
						$("#busList_Btn").on("click",function(e){
							e.preventDefault();
							fn_nodeRealTime(obj);
						});

						$("a[name^=route]").on("click",function(e){
							e.preventDefault();
							fn_routeInfo($(this));
						});
					},
				error:function(){
						alert("error");
					}
			})
		}

		function fn_routeInfo(obj){
			var comSubmit = new ComSubmit();
			comSubmit.addParam("routeno",obj.attr("routeno"));
			comSubmit.addParam("routeid",obj.attr("routeid"));
			comSubmit.setUrl("<c:url value='/routeInfoPage.do'/>");
			comSubmit.submit();
		}

		/*김천시에서는 버스 실시간 위치에 대한 데이터가 존재하지 않는다고 한다.
		function fn_routeRealTime(obj){

			var data ={};
			data["ROUTEID"]= obj.attr("routeid");

			$.ajax({
				dataType:"json",
				type:"POST",
				contentType:"application/json",
				url:"/gcbus/routeRealTime.do",
				data:JSON.stringify(data),
				success:function(result){
					var markerPosition  = new daum.maps.LatLng(result["LAT"], result["LNG"]);
					routeMarker = new daum.maps.Marker({
					    position: markerPosition,
					    clickable: true 
					});
					routeMarker.setMap(map);
					console.log(result["LAT"]);
				},
				error:function(){
					alert("error");
				}
			})
			
		}
		*/
		
	</script>
	
</body>
</html>