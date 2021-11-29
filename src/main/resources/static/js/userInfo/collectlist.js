layui.use(['form', 'element', 'util', 'carousel', 'laypage', 'layer','table'], function () {
    var table = layui.table;
    table.render({
        elem: '#collect'
        , url: basePath+'/user/collect/queryall'
        , page: {
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip']
            , groups: 3
            , limits: [20, 50, 100]
            , limit: 20
        }, cols: [[
            {field: 'qid', title: 'ID',width:60, align:'center'}
            , {field: 'commname', title: '名称', width: 250, align:'center'}
            , {field: 'commdesc', title: '描述', width: 600, align:'center'}
            , {field: 'username', title: '发布者', width: 110, align:'center'}
            , {field: 'school', title: '学校', width: 130, align:'center'}
            , {field: 'soldtime', title: '收藏时间', width: 160, align:'center'}
            , {fixed: 'right', title: '操作', toolbar: '#barDemo', width:140, align:'center'}
        ]]
        ,height: 500
        , done: function (res, curr, count) {
            var i=1;
            $("[data-field='qid']").children().each(function () {
                if($(this).text() == 'ID') {
                    $(this).text("ID")
                }else{
                    $(this).text(i++)
                }
            });
        }
    });
    //监听行工具事件
    table.on('tool(test)', function (obj) {
        var data = obj.data;
        if (obj.event === 'xiangqing') {
            $.ajax({
                url: basePath+'/product-detail-isnull/'+data.commid,
                data: "",
                contentType: "application/json;charset=UTF-8", //发送数据的格式
                type: "get",
                dataType: "json", //回调
                beforeSend: function () {
                    layer.load(1, { //icon支持传入0-2
                        content: '请稍等...',
                        success: function (layero) {
                            layero.find('.layui-layer-content').css({
                                'padding-top': '39px',
                                'width': '60px'
                            });
                        }
                    });
                },
                complete: function () {
                    layer.closeAll('loading');
                },
                success: function (shuju) {
                    console.log(shuju)
                    if(shuju.status===200){
                        window.open(basePath+"/product-detail/"+data.commid)
                    }else {
                        layer.msg(shuju.message, {
                            time: 1000,
                            icon: 2,
                            offset: '50px'
                        });
                    }
                }
            });
        }else if(obj.event === 'quxiaoshoucang'){
            layer.confirm('确认取消收藏该商品吗？', {
                btn: ['确定','算了'], //按钮
                title:"取消收藏",
                offset:"50px"
            }, function(){
                layer.closeAll();
                $.ajax({
                    url: basePath+'/collect/delete/'+data.id,
                    data: "",
                    contentType: "application/json;charset=UTF-8", //发送数据的格式
                    type: "put",
                    dataType: "json", //回调
                    beforeSend: function () {
                        layer.load(1, { //icon支持传入0-2
                            content: '请稍等...',
                            success: function (layero) {
                                layero.find('.layui-layer-content').css({
                                    'padding-top': '39px',
                                    'width': '60px'
                                });
                            }
                        });
                    },
                    complete: function () {
                        layer.closeAll('loading');
                    },
                    success: function (data) {
                        console.log(data)
                        if(data.status===200){
                            layer.msg(data.message, {
                                time: 1000,
                                icon: 1,
                                offset: '50px'
                            }, function () {
                                location.reload();
                            });
                        }else {
                            layer.msg(data.message, {
                                time: 1000,
                                icon: 2,
                                offset: '50px'
                            });
                        }
                    }
                });
            }, function(){
            });
        }
    });
});