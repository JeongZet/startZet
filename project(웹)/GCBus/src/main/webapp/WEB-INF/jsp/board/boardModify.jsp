<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE>
<html>
<head>
<%@include file="/WEB-INF/include/include-header.jspf" %>
<title>글 수정하기</title>
</head>
<body>
	<%@include file="/WEB-INF/include/include-gnb.jspf" %>
	<div class="boardBox">
		<form id="frm" enctype="multipart/form-data">
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
					<td colspan="4">${post.BOARD_IDX }<input type="hidden" value="${post.BOARD_IDX }" name="BOARD_IDX"></td>
					<th>Hit.</th>
					<td colspan="4">${post.HIT_CNT }</td>
				</tr>
				<tr>
					<th>Tag</th>
					<td colspan="4">
						<select name="TAG">
							<option>테스트</option>
							<option>문의</option>
						</select></td>
					<th>Creator</th>
					<td colspan="4"><input type="hidden" value="${post.CREA_ID}" name="CREATOR">${post.CREA_ID }</td>
				</tr>
				<tr>
					<th>Date</th>
					<td colspan="4">${post.CREA_DTM }</td>
					<th>ModDate</th>
					<td colspan="4">${post.UPDA_DTM }</td>
				</tr>
				<tr>
					<th>Title</th>
					<td colspan="9"><input type="text" value="${post.TITLE }" name="TITLE"></td>
				</tr>
				<tr>
					<th>Contents</th>
					<td colspan="9"><textarea rows="30" cols="100" style="width:100%;" name="CONTENTS">${post.CONTENTS }</textarea></td>
				</tr>
				<tr>
					<th>Attachments</th>
					<td colspan="9">
						<c:choose>
							<c:when test="${fn:length(file)>0 }">
								<input type="hidden" name="FILE_IDX" value="${file.FILE_IDX }">
								<input type="file" class="invisible" name="file">
								<div id="fileInfo" class="inline" ><a href="#this" id="file">${file.ORIGINAL_FILE_NAME }</a>
								(${file.FILE_SIZE/1000 }byte)</div>
								<a href="#this" id="fileAlter" class="boardBtn">파일 추가 및 변경</a>
							</c:when>
							<c:otherwise>
								<input type="file" class="invisible" name="file">
								<a href="#this" id="fileAlter" class="boardBtn">파일 추가 및 변경</a>
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<th>Password.</th>
					<td colspan="3"><input type="password" maxlength="10" id="password"></td>
				</tr>
			</table>
		</form>
		<hr/>
		
		<a href="#this" class="boardBtn" id="modify">수정하기</a>
		<a href="#this" class="boardBtn" id="delete">삭제하기</a>
		<a href="#this" class="boardBtn" id="list">목록으로</a>
		
		<%@include file="/WEB-INF/include/include-body.jspf" %>
		<script type="text/javascript">
			$("document").ready(function(){
				$("#modify").on("click",function(e){
					e.preventDefault();
					fn_modifyBoard();
				})
				$("#delete").on("click",function(e){
					e.preventDefault();
					fn_deleteBoard();
				})
				$("#list").on("click",function(e){
					e.preventDefault();
					fn_openBoardList();
				})
				$("#fileAlter").on("click",function(e){
					e.preventDefault();
					fn_clickFileAlter($(this));
				})

				function fn_openBoardList(){
					var comSubmit = new ComSubmit();
					comSubmit.setUrl("<c:url value='/openBoardList.do'/>");
					comSubmit.submit();
				}
				function fn_modifyBoard(){
					if(${post.PASSWORD}==($("#password").val())){
						var comSubmit = new ComSubmit("frm");
						comSubmit.setUrl("<c:url value='/modifyBoard.do'/>");
						comSubmit.submit();
					}else{
						alert("비밀번호가 틀립니다.");
					}
				}
				function fn_deleteBoard(){
					if(${post.PASSWORD}==($("#password").val())){
						var comSubmit = new ComSubmit();
						comSubmit.setUrl("<c:url value='/deleteBoard.do'/>");
						comSubmit.addParam("BOARD_IDX",${post.BOARD_IDX});
						comSubmit.submit();
					}else{
						alert("비밀번호가 틀립니다.");
					}
				}
				function fn_clickFileAlter(obj){
					if(obj.html()=="취소"){
						$("input[name=file]").addClass("invisible");
						$("#fileInfo").removeClass("invisible");
						obj.html("파일 추가 및 변경");
					}else{
						$("input[name=file]").removeClass("invisible");
						$("#fileInfo").addClass("invisible");
						obj.html("취소");
					}
				}
			})
		</script>
	</div>
</body>
</html>