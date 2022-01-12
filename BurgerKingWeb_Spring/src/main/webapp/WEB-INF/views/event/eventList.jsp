<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ include file="../include/header.jsp"%>
<link rel="stylesheet" type="text/css" href="/css/event.css">
<article>
	<div class="location">
	<div class="web_container1">
		<ul>
			<li><a href="main">HOME</a>&nbsp;>&nbsp;</li>
			<li><a href="eventListForm">이벤트</a>&nbsp;>&nbsp;</li>
			<li>전체</li>
		</ul>
	</div>
	</div>
	<div class="event_web_container">
		<div class="subtit">
			<h1 class="event_tit">이벤트</h1>
			<ul class="submenu_right">
				<li><span style="border-bottom: 3px solid red;"><a
						href="eventListForm" style="color: red;">전체</a></span></li>
				<li><span><a href="eventTab2">진행중</a></span></li>
				<li><span><a href="eventTab3">종료</a></span></li>
			</ul>
		</div>

		<form name="frm" method="post" action="burder.do">
			<div class="eventarea">
				<ul>
					<c:forEach var="EventVO" items="${eventList}">
						<li><a
							href="eventDetailForm?eseq=${EventVO.eseq}">
								<input type="hidden" name="eseq" value="${EventVO.eseq}" /> <img
								class="eventImg" src="upload/main/event/${EventVO.image}" />
						</a>
							<p>
								${EventVO.startdate.substring(0,10)}
								~
								${EventVO.enddate.substring(0,10)}
							</p></li>
					</c:forEach>
				</ul>
			</div>
		</form>
	</div>
</article>

<%@ include file="../include/undermenu.jsp" %>
<%@ include file="../include/footer.jsp"%>
