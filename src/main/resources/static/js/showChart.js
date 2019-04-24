var chartOutChar = null;

var option1 = {
    tooltip: {
        trigger: 'axis'
    },
    toolbox: {
        feature: {
            dataView: {show: true, readOnly: false},
            magicType: {show: true, type: ['line', 'bar']},
            restore: {show: true},
            saveAsImage: {show: true}
        }
    },
    legend: {
        data:['蒸发量','降水量','平均温度']
    },
    xAxis: [
        {
            type: 'category',
            data: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '水量',
            min: 0,
            max: 250,
            interval: 50,
            axisLabel: {
                formatter: '{value} ml'
            }
        },
        {
            type: 'value',
            name: '温度',
            min: 0,
            max: 25,
            interval: 5,
            axisLabel: {
                formatter: '{value} °C'
            }
        }
    ],
    series: [
        {
            name:'蒸发量',
            type:'bar',
            data:[]
        },
        {
            name:'降水量',
            type:'bar',
            data:[]
        },
        {
            name:'平均温度',
            type:'line',
            yAxisIndex: 1,
            data:[]
        }
    ]
};

function loadChartOut() {
    $.getJSON('/maze/rest/view/rain.html', function (data) {
        //上面这个url是重点，组成为：项目名/过滤前缀/Controller中mapping值，当页面加载此js时，它会向后台取数据
        if (data.success1) {
            chartOutChar.showLoading({text: '正在努力的读取数据中...'});
            chartOutChar.setOption({
                series: [
                    {
                        name:'蒸发量',
                        data:data.c_eva
                    },
                    {
                        name:'降水量',
                        data:data.c_rain
                    },
                    {
                        name:'平均温度',
                        data:data.c_avgt
                    }
                ]
            });
            chartOutChar.hideLoading();
        }else {
            alert('提示', data.msg);
        }
    });
}


//载入图表
$(function () {
    chartOutChar = echarts.init(document.getElementById('showChart'));
    chartOutChar.setOption(option1);
    loadChartOut();
    window.addEventListener('resize', function () {
        chartOutChar.resize();
        mychart.resize();
    });
});