function blockUI(){
	$.blockUI({
		message : '<br/>正在执行操作,请稍候....<br/><br/><img src="/img/busy.gif" /><br/>&nbsp;',
		baseZ : 19891016
	});
}

function unblockUI(){
	$.unblockUI();
}

$(function(){
	$.unblockUI();
	
	$("form").submit(function(){
		blockUI();
	});
	$("#refresh").click(function(){
		blockUI();
	});
});