/**
 * 
 */
var mapContainer = document.getElementById('mapBox'), // 지도를 생성할 div 
mapOption = { 
    center: new daum.maps.LatLng(36.12354, 128.11828), // 지도의 중심좌표
    level: 3 // 지도의 확대레벨
};

var map = new daum.maps.Map(mapContainer, mapOption); // 지도를 생성합니다.

// 마커 객체를 가지고 있을 배열을 선언합니다.
var markers = [];
// 라인 값들을 가지고 있을 배열을 선언합니다. 선을 구성하는 좌표 배열입니다. 이 좌표들을 이어서 선을 표시합니다
var linePath = [];
// 선택한 정류장에 대한 이름을 알려주기 위한 오버레이 변수입니다.
var customOverlay = null; 

function fn_clickSetting(){
	var clickMarker=null;
	//노선 마커와 위치 클릭 마커
	
	var clickIcon = new daum.maps.MarkerImage(
			"./image/location.png",
			new daum.maps.Size(32,32));
	
	//지도에 클릭 이벤트를 등록합니다
	// 지도를 클릭하면 마지막 파라미터로 넘어온 함수를 호출합니다
	daum.maps.event.addListener(map, 'click', function(mouseEvent) {        
	    
		if(clickMarker!=null){
			clickMarker.setMap(null);
		}
		
	    // 클릭한 위도, 경도 정보를 가져옵니다 
	    var latlng = mouseEvent.latLng;
	    
	    var resultDiv1 = document.getElementById('lat'); 
	    resultDiv1.value = Math.floor(latlng.getLat()*100000)/100000;
	    var resultDiv2 = document.getElementById('lng');
	    resultDiv2.value = Math.floor(latlng.getLng()*100000)/100000;
	    
	    clickMarker = new daum.maps.Marker({
	    	position: new daum.maps.LatLng(resultDiv1.value,resultDiv2.value),
	    	image:clickIcon,
	    	clickable:false
	    });
	    
	    
	    clickMarker.setMap(map);
	    
	});
}

//배열에 추가된 마커들을 지도에 표시하거나 삭제하는 함수입니다.
function setMarkers(map){
	for(var i=0; i<markers.length;i++){
		markers[i].setMap(map);
	}
}


//마커를 생성하고 지도 위에 표시하는 함수입니다
function fn_nodeMarkerMaker(obj){
	
	//다음 맵 API 마커 생성하기
	var markerPosition  = new daum.maps.LatLng(obj.attr("lat"), obj.attr("lng")); 
	
	// 마커를 생성합니다
	var marker = new daum.maps.Marker({
	    position: markerPosition,
	    clickable: true 
	});
	//마커를 지도 위에 표시되도록 세팅합니다.
	marker.setMap(map);

	//생성된 마커를 배열에 추가합니다.
	markers.push(marker);
	daum.maps.event.addListener(marker,"click",function(e){
		if(obj.attr("id").slice(0,4)=="node"){
			//마커 클릭 이벤트를 선언해줍니다.
			fn_clickNode(obj);
				
			
		}else if(obj.attr("id").slice(0,4)=="path"){
			fn_clickPath(obj);
		}	
	});
	
}

function fn_clickPath(obj){
	
	$("span[name^=nodeinfo]").removeClass("markerSelected");
	obj.addClass("markerSelected");
	//지도 중심을 변경합니다.
	map.setCenter(new daum.maps.LatLng(obj.attr("lat"),obj.attr("lng")));
	
	fn_setOverlay(obj);

}

function fn_setOverlay(obj){
	
	if(customOverlay!=null){
		customOverlay.setMap(null);
	}
	
	customOverlay = new daum.maps.CustomOverlay({
	    map: map,
	    clickable: false,
	    content: '<a href="#this" onclick="fn_clickOverlay();"><span class="customOverlay">'+obj.html()+'</span></a>',
	    position: new daum.maps.LatLng(obj.attr("lat"),obj.attr("lng")),
	    xAnchor: 0.5,
	    yAnchor: 3,
	    zIndex: 3
	});
	customOverlay.setMap(map);
}

function fn_nodeLineMaker(){
	// 지도에 표시할 선을 생성합니다
	var polyline = new daum.maps.Polyline({
	    path: linePath, // 선을 구성하는 좌표배열 입니다
	    strokeWeight: 3, // 선의 두께 입니다
	    strokeColor: '#FF0000', // 선의 색깔입니다
	    strokeOpacity: 0.7, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
	    strokeStyle: 'solid' // 선의 스타일입니다
	});
	
	// 지도에 선을 표시합니다 
	polyline.setMap(map);
	
	//지도의 중심 좌표를 정류장 출발점의 중심으로 위치 변경합니다.
	map.setCenter(linePath[0]);
	
	//마커들의 배열에 폴리라인을 같이 넣어 관리합니다.
	markers.push(polyline);
}


