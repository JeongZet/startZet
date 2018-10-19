<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
	<title>게시글</title>
	<%@include file="/WEB-INF/include/include-header.jspf" %>
</head>
<body>
	<%@include file="/WEB-INF/include/include-gnb.jspf" %>
	
	<div class="boardBox" >
		<table class="board_list" >
			<colgroup>
				<col width="10%">
				<col width="5%">
				<col width="10%">
				<col width="50%">
				<col width="15%">
				<col width="10%">
			</colgroup>
			<tr>
				<th>No.</th>
				<th>Tag</th>
				<th>Creator</th>
				<th>Title</th>
				<th>Date</th>
				<th>Hit.</th>
			</tr>
			<c:choose>
				<c:when test="${fn:length(list)>0 }">
					<c:forEach items="${list }" var="post">
						<tr onmouseover="this.style.backgroundColor='#F5F5F5'" onmouseout="this.style.backgroundColor=''">
							<td class="center idx">${post.BOARD_IDX }</td>
							<td class="center tag">${post.TAG }</td>
							<td class="center creator">${post.CREA_ID }</td>
							<td class="title">
								<input type="hidden" id="board_Idx" value="${post.BOARD_IDX }">
								<a href="#this" class="titleClick">${post.TITLE }</a>
							</td>
							<td class="center date">${post.CREA_DTM }</td>
							<td class="center hit">${post.HIT_CNT }</td>
						</tr>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<tr><td colspan="6">게시글이 존재하지 않습니다.</td></tr>
				</c:otherwise>
			</c:choose>
		</table>
		<hr/>
		<a href="#this" id="write" class="boardBtn">글쓰기</a>
	</div>
	
	<%@ include file="/WEB-INF/include/include-body.jspf" %>
	<script type="text/javascript">
		$("#board").addClass("selected");
		$("document").ready(function(){
			$("#write").on("click",function(e){
				e.preventDefault();
				fn_openBoardWrite();
			})
			$(".titleClick").on("click",function(e){
				e.preventDefault();
				fn_openBoardDetail($(this));
			})
		})
		
		function fn_openBoardWrite(){
			var comSubmit= new ComSubmit();
			comSubmit.setUrl("<c:url value='/openBoardWrite.do'/>");
			comSubmit.submit();
		}

		function fn_openBoardDetail(obj){
			var comSubmit = new ComSubmit();
			comSubmit.addParam("BOARD_IDX",obj.parent().find("#board_Idx").val());
			comSubmit.setUrl("<c:url value='/openBoardDetail.do'/>");
			comSubmit.submit();
		}
		
	</script>
</body>
</html>