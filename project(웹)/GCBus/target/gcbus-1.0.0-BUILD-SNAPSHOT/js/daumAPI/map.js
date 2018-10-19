/**
 * 
 */
var mapContainer = document.getElementById('mapBox'), // 지도를 표시할 div 
mapOption = { 
    center: new daum.maps.LatLng(36.13245775275288, 128.11782084200846), // 지도의 중심좌표
    level: 3 // 지도의 확대 레벨
};

var map = new daum.maps.Map(mapContainer, mapOption); // 지도를 생성합니다
