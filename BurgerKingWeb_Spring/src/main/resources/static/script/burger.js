function login_chk(){
	if(document.frm.id.value==""){
		alert("아이디를 입력하여 주세요.");
		document.frm.id.focus();
		return false;
	}else if(document.frm.pwd.value==""){
		alert("비밀번호를 입력하여 주세요.");
		document.frm.pwd.focus();
		return false;
	}
	return true;
}


function check_input1(){
	var a = document.getElementById("name");
	var b = document.getElementById("name_coment");
	if(a.value != ""){
		b.innerText = "";
	}else{
		b.innerText = "이름을 입력해 주세요.";
	}
}

function check_input2(){
	var a = document.getElementById("phone");
	var b = document.getElementById("phone_coment");
	if(a.value != ""){
		b.innerText = "";
	}else{
		b.innerText = "휴대폰 번호를 입력해 주세요.";
	}
}

function check_input3(){
	var a = document.getElementById("id");
	var b = document.getElementById("id_coment");
	if(a.value != ""){
		b.innerText = "";
	}else{
		b.innerText = "아이디를 입력해 주세요.";
	}
}

function check_input4(){
	var a = document.getElementById("pwd");
	var b = document.getElementById("pwd_coment");
	if(a.value != ""){
		b.innerText = "";
	}else{
		b.innerText = "새 비밀번호를 입력해 주세요.";
	}
}
function check_input5(){
	var a = document.getElementById("pwd_chk");
	var b = document.getElementById("pwd_chk_coment");
	if(a.value != ""){
		b.innerText = "";
	}else{
		b.innerText = "비밀번호 재확인";
	}
}

/*function inputidChk(){
	if(document.frm.name.value==""){
		alert("이름을 입력해주세요.");
		document.frm.name.focus();
		return false;
	}else if(document.frm.phone.value==""){
		alert("핸드폰 번호를 입력해주세요.");
		document.frm.phone.focus();
		return false;
	}
	return true;
}*/
/*function inputpwdChk(){
	if(document.frm.name.value==""){
		alert("이름을 입력해주세요.");
		document.frm.name.focus();
		return false;
	}else if(document.frm.id.value==""){
		alert("아이디를 입력해주세요.");
		document.frm.id.focus();
		return false;
	}
	return true;
}*/
function updatepwdChk(){
	if(document.frm.pwd.value==""){
		alert("비밀번호를 입력해주세요.");
		document.frm.pwd.focus();
		return false;
	}else if(document.frm.pwd.value!=document.frm.pwd_chk.value){
		alert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
		document.frm.pwd_chk.focus();
		return false;
	}
	return true;
}

function deliveryDetail(pseq){
	var url = "deliveryDetail?pseq="+pseq;
	var opt = "toolbar=no,menubar=no,scrollbars=yes,resizable=no,height=800,width=580,top=300, left=300";
	window.open(url, "delivery", opt);
}


function add_or_cart(kind1, pseq){
	var url="";
	if(kind1 == "1" || kind1 == "6" || kind1 == "7" || kind1 == "8"){
		url="noMeterialCart?pseq="+pseq;
		opener.location.href = url;
		self.close();
	}else{
		url="deliveryAddMaterial?pseq="+pseq;
		window.location.href = url;
	}
}

function go_cart(pseq){
	opener.window.location.href="noMeterialCart?pseq=" + pseq;
	self.close();
}

function go_add_Meterial(pseq){
	var checkboxes = document.getElementsByName('Meterial');
	var cb = [];
	var j = 1;
	cb[0] = pseq;
	for(var i = 0; i < checkboxes.length; i++){
		if(checkboxes[i].checked == true){
			cb[j++] = checkboxes[i].value;
		}
	}
	if(cb.length == 1){
		alert("추가 메뉴를 선택해주세요.");
	}else{
		opener.location.href="insertAddMeterial?addM=" + cb;
		self.close();
	}
}

function go_cart02(){
	document.cartForm.action="deliveryCartForm";
	document.cartForm.submit();
}


function go_cart_delete(cseq){
	var url="cartDelete?cseq="+cseq;
	window.location.replace(url);
}


function menu_plus(){
	var url="deliveryForm&kind1=1";
	window.location.replace(url);
}

function go_order_insert(){
	location.href="deliveryCartOrder";
}

function go_order_select(){
	location.href="deliveryOrderList";	
}

function del_qna(){
	var count = 0;  //  체크된 체크박스의 갯수를 카운트 하기위한 변수
	if(document.frm.delete.length==undefined){   // 장바구니에 물건이 하나일때, 체크박스가 하나일때
		if( document.frm.delete.checked == true)   // 그 체크박스만 체크되어 있는지 확인
			count++;	 
	}else{
		for( var i=0; i<document.frm.delete.length; i++){
			if( document.frm.delete[i].checked==true)
				count++; 
		}
	}
	// 지금의 스크립트 명령은 체크박스가 하나도 체크되지 않았다면 원래로 되돌아 가기위한 코드들입니다
	if( count == 0 ){
		alert("삭제할 항목을 선택해주세요");
	} else{
		document.frm.action = "qnaDelete";
	    document.frm.submit();
	}
}

function selectAllDelete(selectAllDelete)  {
  	const checkboxes = document.getElementsByName('menu');
  	checkboxes.forEach((checkbox) => {
    checkbox.checked = selectAllDelete.checked;
  })
}
function del_cart(){
	var count = 0;  //  체크된 체크박스의 갯수를 카운트 하기위한 변수
	var checkboxes = document.getElementsByName('menu');
	var cb = [];
	var index = 0;
	if(checkboxes.length==undefined){   // 장바구니에 물건이 하나일때, 체크박스가 하나일때
		if(checkboxes.checked == true)   // 그 체크박스만 체크되어 있는지 확인
			count++;
			cb[0] = checkboxes.value;	 
	}else{
		for( var i=0; i<checkboxes.length; i++){
			if( checkboxes[i].checked==true){
				count++;
				cb[index++] = checkboxes[i].value;
			}
		}
	}
	// 지금의 스크립트 명령은 체크박스가 하나도 체크되지 않았다면 원래로 되돌아 가기위한 코드들입니다
	if( count == 0 ){
		alert("삭제할 항목을 선택해주세요");
	} else{
	    window.location.href = "deliveryCartDelete?menu=" + cb;
	}
}

function qna_write_chk(){
		document.frm.submit();
	
}

function input_pass(qseq){
	var url = "passCheckForm?qseq=" + qseq;
	document.frm.action = url;
	document.frm.submit();
}

function passCheck_step2(qseq){
	self.close();
}

function resign(){
	return alert(`정말로 탈퇴하시겠습니까?`);
}

function post_zip(){    
	var url = "findZipNum";
	var opt = "toolbar=no, menubar=no, scrollbars=no, resizable=no, width=550,";
	opt = opt + " height=300, top=300, left=300";
	window.open( url, "우편번호 찾기", opt );
}

/*function play(){
	var x=0;
	setInterval(function(){
	    for(var i=1;i<=5;i++)document.getElementById("view"+i).style.opacity = '0';
	    x++;
	    document.getElementById("view"+x).style.opacity = '1';
	    if(x==5)x=0;
	}, 3000);
}*/

function shownonArea1(){
	document.getElementById("non_Btn1").style.color="red";
	document.getElementById("non_Btn1").style.textDecoration="underline";
	document.getElementById("non_Btn2").style.color="#000";
	document.getElementById("non_Btn2").style.textDecoration="none";
	
	document.getElementById("nonArea1").style.visibility="visible";
	document.getElementById("nonArea2").style.visibility="hidden"; 
}

function shownonArea2(){
	document.getElementById("non_Btn1").style.color="#000";
	document.getElementById("non_Btn1").style.textDecoration="none";
	document.getElementById("non_Btn2").style.color="red";
	document.getElementById("non_Btn2").style.textDecoration="underline";
	
	document.getElementById("nonArea1").style.visibility="hidden";
	document.getElementById("nonArea2").style.visibility="visible";
}

function term_open(){
	if(document.getElementById("acc_cont").style.height == "0px"){
        document.getElementById("acc_cont").style.height = "300px";
	}else{
		document.getElementById("acc_cont").style.height = "0px";
	}
}

function check_Term(){
	const query = 'input[name="guest_checkbox"]:checked';
	const selectedEls = document.querySelectorAll(query);
	let result = "";
	selectedEls.forEach((el) => { 
		result = el.value;
	});
	
	if(result != "on"){
		alert("이용약관에 동의해주세요.");
		return false;
	}			  
	else if(document.frm.name.value==""){
		alert("이름을 작성해주세요.");
		return false;
	}else if(document.frm.phone.value==""){
		alert("휴대폰 번호를 작성해주세요.");
		return false;
	}else if(document.frm.pwd.value==""){
		alert("비밀번호를 작성해주세요.");
		return false;
	}else if(document.frm.pwd.value.length<4){
		alert("비밀번호를 4자리 이상 작성해주세요.");
		return false;
	}else if(document.frm.pwd.value != document.frm.pwd_chk.value){
		alert("비밀번호와 비밀번호 확인이 다릅니다.");
		return false;
	}else{
		alert("해당 정보로 비회원 로그인 하시겠습니까?");
		return true;	
	}
}

function go_order_delete(odseq){
	var url="orderDelete?odseq="+odseq;
	window.location.replace(url);
}

function nonOrderChk(){
	if(document.frm2.oseq.value==""){
		alert("주문번호를 입력해주세요.");
		return false;
	}else if(document.frm2.pwd2.value==""){
		alert("비밀번호를 입력해주세요.");
		return false;
	}else{
		return true;
	}
}