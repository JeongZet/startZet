<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
<title>${post.TITLE }</title>
<%@ include file="/WEB-INF/include/include-header.jspf" %>
</head>
<body>
	<%@ include file="/WEB-INF/include/include-gnb.jspf" %>
	<div class="boardBox">
		<table class="board_view">
			<colgroup>
				<col width="10%">
				<col width="10%">
				<col width="10%">
				<col width="10%">
				<col width="10%">
				<col width="10%">
				<col width="10%">
				<col width="10%">
				<col width="10%">
				<col width="10%">
			</colgroup>
			<tr>
				<th>No.</th>
				<td colspan="4">${post.BOARD_IDX }</td>
				<th>Hit.</th>
				<td colspan="4">${post.HIT_CNT }</td>
			</tr>
			<tr>
				<th>Tag</th>
				<td colspan="4">${post.TAG }</td>
				<th>Creator</th>
				<td colspan="4">${post.CREA_ID }</td>
			</tr>
			<tr>
				<th>Date</th>
				<td colspan="4">${post.CREA_DTM }</td>
				<th>ModDate</th>
				<td colspan="4">${post.UPDA_DTM }</td>
			</tr>
			<tr>
				<th>Title</th>
				<td colspan="9">${post.TITLE }</td>
			</tr>
			<tr>
				<th>Contents</th>
				<td colspan="9">${post.CONTENTS }</td>
			</tr>
			<c:if test="${fn:length(file)>0 }">
				<tr>
					<th>Attachment</th>
					<td colspan="9"><a href="#this" id="file" >${file.ORIGINAL_FILE_NAME }</a>(${file.FILE_SIZE/1000 }byte)</td>
				</tr>
			</c:if>
			
		</table>
		<hr/>
		<a href="#this" class="boardBtn" id="list">목록으로</a>
		<a href="#this" class="boardBtn" id="modify">수정하기</a>
	</div>
	
	<%@ include file="/WEB-INF/include/include-body.jspf" %>
	<script type="text/javascript">
		$("document").ready(function(){
			$("#list").on("click",function(e){
				e.preventDefault();
				fn_openBoardList();
			})
			$("#modify").on("click",function(e){
				e.preventDefault();
				fn_openBoardModify();
			})
			$("#file").on("click",function(e){
				e.preventDefault();
				fn_downloadFile();
			})
		})
		
		function fn_openBoardList(){
			var comSubmit = new ComSubmit();
			comSubmit.setUrl("<c:url value='/openBoardList.do'/>");
			comSubmit.submit();
		}
		function fn_openBoardModify(){
			var comSubmit = new ComSubmit();
			comSubmit.addParam("BOARD_IDX",${post.BOARD_IDX});
			comSubmit.setUrl("<c:url value='/openBoardModify.do'/>");
			comSubmit.submit();
		}
		function fn_downloadFile(){
			var comSubmit = new ComSubmit();
			comSubmit.addParam("BOARD_IDX",${post.BOARD_IDX});
			comSubmit.setUrl("<c:url value='/downloadFile.do'/>");
			comSubmit.submit();
		}
	</script>
</body>
</html>