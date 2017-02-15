$(document).ready(function() {
	$("#toolbar2 #btn_new").click(newClick);
	$("#toolbar1 #btn_new").click(newClick);
});

function newClick(){
	window.location.href = basePath + "sysconf/systemConfig/timedetailpage.do?opertype=new";
}