// JavaScript Document//显示灰色JS遮罩层


//关闭灰色JS遮罩层和操作窗口

//显示下个层
function showBg(ct, content) {
	index = 0;
	if (content != null && content != '') {
		upVm($(content).prev().val());
	}
	var bH = $("body").height();
	var bW = $("body").width();
	var objWH = getObjWh(ct);

	$("#fullbg").css({width: bW, height: bH, display: "block"});
	var tbT = objWH.split("|")[0] + "px";
	var tbL = objWH.split("|")[1] + "px";
	$("#" + ct).css({top: tbT, left: tbL, display: "block"});
	$(window).scroll(function () {
		resetBg()
	});
	$(window).resize(function () {
		resetBg()
	});
}
function getObjWh(obj) {
	var st = document.documentElement.scrollTop;//滚动条距顶部的距离
	var sl = document.documentElement.scrollLeft;//滚动条距左边的距离
	var ch = document.documentElement.clientHeight;//屏幕的高度
	var cw = document.documentElement.clientWidth;//屏幕的宽度
	var objH = $("#" + obj).height();//浮动对象的高度
	var objW = $("#" + obj).width();//浮动对象的宽度
	var objT = Number(st) + (Number(ch) - Number(objH)) / 2 - 70;
	var objL = Number(sl) + (Number(cw) - Number(objW)) / 2;
	return objT + "|" + objL;
}
function resetBg() {
	var fullbg = $("#fullbg").css("display");
	if (fullbg == "block") {
		var bH2 = $("body").height();
		var bW2 = $("body").width() + 16;
		$("#fullbg").css({width: bW2, height: bH2});
		var objV1 = getObjWh("dialogSet");
		var tbT1 = objV1.split("|")[0] + "px";
		var tbL1 = objV1.split("|")[1] + "px";
		var objV = getObjWh("dialog");
		var tbL = objV.split("|")[1] + "px";
		$("#dialog").css({top: 30, left: tbL});
	}
}

function upVm(content) {
	var id = content;
	$('#netWorkVmId').val(id);
	$.post('/admin/vm/findById', {'id': id}, function (res) {
		if (res['bool']) {
			var data = res['data'];
			if (data != null && data != '') {
				$('#upCpu').jRange('setValue', data.cpu);
				$('#upCpu').parent().append('<input type="hidden" name="id" value="' + data.id + '">');
				$('#upMem').jRange('setValue', data.mem);
				var disk = data.diskMangeBeans;
				$('.delDiv').remove();
				// index = 0;
				indexNetwork = 0;
				$.each(disk, function (i, o) {
					if (o.diskType == 1) {
						$('#upSystem').jRange('setValue', 10);
						$('#upSystem').siblings(':eq(1)').val(1);
					} else if (o.diskType == 2) {
						var $div = $('#upSystem').parent().parent().parent();
						var $html = $('<div class="mt40 clearfix delDiv"><div class="fl mt5"><input type="hidden" name="diskMangeBeans[' + index + '].diskSize" class="diskSlider"/><input type="hidden" name="diskMangeBeans[' + index + '].diskType" value="2"/><input type="hidden" name="diskMangeBeans[' + index + '].id" value="' + o.id + '"/><input type="hidden" name="diskMangeBeans[' + index + '].diskLogo" value="1" /></div><div class="fl ml10 mt5"><a href="#" class="delBtn ml5" onclick="delDiskBut(this,' + id + ')">刪除</a><span class="w15 tr ml10">盘(' + o.diskLogo + ')</span></div></div>');
						++index;
						$div.append($html);
						$html.find('input.diskSlider').val(o.diskSize).jRange({
							from: 10,
							to: config.maxDiskSize,
							step: 10,
							scale: [10, config.maxDiskSize],
							format: '%s',
							width: 400,
							showLabels: true,
							showScale: true,
							onstatechange: checkSystem
						});
					}
				});
				var netWorkCard = data.netWorkCardBeans;
				var bool = true;
				var $html;
				$('.delNetWork').remove();
				$.each(netWorkCard, function (i, o) {
					// var netWorkCardType = o.netWorkCardType == 2 ? '公网网段' : o.netWorkCardType == 3 ? '全局内网网段' : '私有内网网段';

					var $option = o.netWorkCardType == 4 ? '<option value="0">请选择</option><option value="4">私有内网</option>' : '<option value="0">请选择</option> <option value="2">公网网段</option><option value="3">全局内网</option>';
					if (bool) {
						$html = $('<div class="clearfix delNetWork"><select style="margin-left:2px" class="gyText c_bor w100 fl" onchange="addNetWork(this,' + data.networkId + ')">' + $option + '</select>' +
							'<select style="height:26px" class="gyText c_bor w150 fl ml5"><option value="0">请选择</option></select>' +
							'<div class="fl ml10">' +
							'<span hidden >带宽入:</span>' +
							'<input style="margin-left:7px;text-align:center;" hidden id="inBandwidth" class="gySelect w30 h50" /> ' +
							'<span hidden>出:</span>' +
							'<input style="margin-left:7px;text-align:center;" hidden id="outBandwidth" class="gySelect w30" align="right"/> ' +
							'<a href="#" style="margin-left:18px" class="delBtn" onclick="addNetWorkBut(this)">添加</a>' +
							'<input type="hidden" value="' + o.netWorkCardType + '"' +
							'</div></div>');
						$('.networkCard').append($html);
						bool = false;
					}
					$.each(o.netWorkCardIpBeans, function (a, b) {
						if (o.netWorkCardType == 3) {
							$html = $('<div class="mt5 clearfix delNetWork">' +
								'<input class="gySelect w100 fl" readonly value="' + o.netWorkCard + '"/> ' +
								'<select style="height:32px" class="gyText c_bor w150 fl ml5" name="netWorkCardBeans[' + indexNetwork + '].ipId" onselect="addNetWorkIp(this,' + b.ipBean.id + ',' + o.netWorkCardType + ')" ><option value="' + b.ipBean.id + '">' + b.ipBean.ip + '</option></select>' +
								'<div class="fl ml10">' +
								'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].id" value="' + o.id + '">' +
								'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].netWorkCardType" value="' + o.netWorkCardType + '">' +
								'<a href="#" style="margin-left:17px" class="delBtn" onclick="delNetWorkBut(this,' + id + ',' + b.id + ')">删除</a>' +
								'</div></div>');
						} else {
							$html = $('<div class="mt5 clearfix delNetWork">' +
								'<input class="gySelect w100 fl" readonly value="' + o.netWorkCard + '"/> ' +
								'<select style="height:32px" class="gyText c_bor w150 fl ml5" name="netWorkCardBeans[' + indexNetwork + '].ipId" onselect="addNetWorkIp(this,' + b.ipBean.id + ',' + o.netWorkCardType + ')" ><option value="' + b.ipBean.id + '">' + b.ipBean.ip + '</option></select>' +
								'<div class="fl ml10">' +
								'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].id" value="' + o.id + '">' +
								'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].netWorkCardType" value="' + o.netWorkCardType + '">' +
								'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].ifaceNum" value="' + b.ifaceNum + '">' +
								'带宽入:' +
								'<input style="margin-left:7px;text-align:center;" class="gySelect w30" name="netWorkCardBeans[' + indexNetwork + '].inBandwidth" value="' + b.inBandwidth + '"/> ' +
								'出:' +
								'<input style="margin-left:7px;text-align:center;" class="gySelect w30" name="netWorkCardBeans[' + indexNetwork + '].outBandwidth" value="' + b.outBandwidth + '"/> ' +
								'<a href="#" style="margin-left:17px" class="delBtn" onclick="delNetWorkBut(this,' + id + ',' + b.id + ')">删除</a>' +
								'</div></div>');
						}
						$('.networkCard').append($html);
						indexNetwork++;
					})
				})
				$('select').trigger('select');
			}
		} else {
			jFail(res['msg'])
		}
	})
}

function addNetWorkBut($this) {
	var $element = $($this).parent().prev();
	var netWorkTypeId = $element.prev().val();
	var netWorkTypeText = $element.prev().find("option:selected").text();
	var ipId = $element.val();
	var ip = $element.find("option:selected").text();
	var inBandwidth = 0;
	var outBandwidth = 0;
	if ($($this).next().val() != 3) {
		inBandwidth = $($this).prev().prev().prev().val();
		outBandwidth = $($this).prev().val();
	}
	if (netWorkTypeId == 0 || ipId == 0) {
		jFail("网卡类型或者IP没有选择");
		return;
	}
	if ($($this).next().val() == 3) {
		var $html = $('<div class="mt5 clearfix delNetWork">' +
			'<input class="gySelect w100 fl" readonly value="' + netWorkTypeText + '"/> ' +
			'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].netWorkCardType" value="' + netWorkTypeId + '">' +
			'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].ipId" value="' + ipId + '">' +
			'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].ifaceNum" value="' + -1 + '">' +
			'<input type="text" readonly class="gyText c_bor w150 fl ml5" value="' + ip + '"/>' +
			'<div class="fl ml10"><a href="#" style="margin-left:157px" class="delBtn" onclick="delNetWorkBut(this)">删除</a>' +
			'</div></div>');
	} else {
		var $html = $('<div class="mt5 clearfix delNetWork">' +
			'<input class="gySelect w100 fl" readonly value="' + netWorkTypeText + '"/> ' +
			'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].netWorkCardType" value="' + netWorkTypeId + '">' +
			'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].ipId" value="' + ipId + '">' +
			'<input type="hidden" name="netWorkCardBeans[' + indexNetwork + '].ifaceNum" value="' + -1 + '">' +
			'<span style="margin-left:12px">带宽入:</span>' +
			'<input style="margin-left:7px;text-align:center;" class="gySelect w30" name="netWorkCardBeans[' + indexNetwork + '].inBandwidth" value="' + inBandwidth + '">' +
			'<span style="margin-left:5px">出:</span>' +
			'<input style="margin-left:8px;text-align:center;" class="gySelect w30" name="netWorkCardBeans[' + indexNetwork + '].outBandwidth" value="' + outBandwidth + '">' +
			'<input type="text" readonly class="gyText c_bor w140 fl ml5" value="' + ip + '"/>' +
			'<a href="#" style="margin-left:20px" class="delBtn" onclick="delNetWorkBut(this)">删除</a>' +
			'</div>');
	}
	$('.networkCard').append($html);
	++indexNetwork;
}
/*模板弹出层*/
function closeBg() {
	$("#fullbg").css("display", "none");
	$("#dialog").css("display", "none");
}
function closeBg1() {
	$("#fullbg").css("display", "none");
	$("#dialogSet").css("display", "none");
}
function showDIV() {
	$("#vmStep1").show();
	$("#vmStep2").hide();
	$("#vmStep3").hide();
	$("#vmStep4").hide();
	$("#vmStep5").hide();
}
function showDIV1() {
	var name = $('#name').val();
	var adminGroup = $('#adminGroup').val();
	var templateType = $('#templateType').val();
	var template = $('#template').val();
	if (name == '') {
		jFail("VM名称没有填写");
		return;
	}
	if (adminGroup == '' || adminGroup == 0) {
		jFail("组织没有选择");
		return;
	}
	if (templateType == '' || templateType == 0) {
		jFail("模版类型没有选择");
		return;
	}
	if (template == '' || template == 0) {
		jFail("模版没有选择");
		return;
	}
	$("#vmStep1").hide();
	$("#vmStep3").hide();
	$("#vmStep4").hide();
	$("#vmStep5").hide();
	$("#vmStep2").show();
}
function showDIV2() {
	$("#vmStep1").hide();
	$("#vmStep2").hide();
	$("#vmStep4").hide();
	$("#vmStep5").hide();
	$("#vmStep3").show();
}
function showDIV3() {
	$("#vmStep1").hide();
	$("#vmStep2").hide();
	$("#vmStep3").hide();
	$("#vmStep5").hide();
	$("#vmStep4").show();
}
function showDIV4() {
	$("#vmStep1").hide();
	$("#vmStep2").hide();
	$("#vmStep3").hide();
	$("#vmStep4").hide();
	$("#vmStep5").show();
}
function showDIV5() {
	var e = $("[class='h32Select w150 fl ml5']");
	$.each(e, function (i, o) {
		var val = $(o).val();
		if (val == 0) {
			jFail("网卡类型或者IP没有选择");
			return false;
		}
		if (i == e.length - 1) {
			blockUI();
			$.post('/admin/vm/create', $('#cerateVmId').serializeArray(), function (res) {
				unblockUI();
				if (res['bool']) {
					jSuccess("创建完成", function () {
						index = 1;
						window.location.reload();
					})
				} else {
					jFail(res['msg']);
				}
			})
		}
	});
}
