<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
<title>글 작성</title>
<%@ include file="/WEB-INF/include/include-header.jspf" %>
</head>
<body>
	<%@ include file="/WEB-INF/include/include-gnb.jspf" %>
	<div class="boardBox">
		<form id="frm" enctype="multipart/form-data">
			<table class="board_view">
				<colgroup>
					<col width="10%">
					<col width="40%">
					<col width="10%">
					<col width="40%">
				</colgroup>
				<tr>
					<th>Tag</th>
					<td><select name="TAG">
							<option>문의</option>
							<option>테스트</option>
						</select></td>
					<th>Creator</th>
					<td><input type="text" name="CREATOR"></td>
				</tr>
				<tr>
					<th>Title</th>
					<td colspan="3"><input type="text" name="TITLE"></td>
				</tr>
				<tr>
					<th>Contents</th>
					<td colspan="3"><textarea name="CONTENTS" rows="30" cols="100" style="width:100%"></textarea></td>
				</tr>
				<tr>
					<th>Attachment</th>
					<td colspan="3"><input type="file" name="FILE"></td>
				</tr>
				<tr>
					<th>Password.</th>
					<td colspan="3"><input type="password" maxlength="10" name="PASSWORD"></td>
				</tr>
			</table>
		</form>
		<hr/>
		<a href="#this" class="boardBtn" id="write">작성하기</a>
		<a href="#this" class="boardBtn" id="list">목록으로</a>
	</div>
	<%@include file="/WEB-INF/include/include-body.jspf" %>
	<script type="text/javascript">
		$("document").ready(function(){
			$("#write").on("click",function(e){
				e.preventDefault();
				fn_writeBoard();
			})
			$("#list").on("click",function(e){
				e.preventDefault();
				fn_openBoardList();
			})
		})
		
		function fn_writeBoard(){
			var comSubmit = new ComSubmit("frm");
			comSubmit.setUrl("<c:url value='/writeBoard.do'/>");
			comSubmit.submit();
		}
		function fn_openBoardList(){
			var comSubmit = new ComSubmit();
			comSubmit.setUrl("<c:url value='/openBoardList.do'/>");
			comSubmit.submit();
		}
	</script>
</body>
</html>