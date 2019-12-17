<%@ page contentType="text/html;charset=UTF-8" %>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
<meta name="author" content="http://caini.com/"/>
<meta name="renderer" content="webkit">
<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Cache-Control" content="no-cache">
<meta http-equiv="Cache-Control" content="no-store">
<link type="image/x-icon" rel="shortcut icon" href="${ctxStatic}/images/logo.ico">

<!-- Custom and plugin css -->
<link href="${ctxStatic}/bootstrap/inspinia/css/plugins/select2/select2.min.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/plugins/ladda/ladda-themeless.min.css" rel="stylesheet">
<link href="${ctxStatic}/layui/css/layui.css" rel="stylesheet"/>
<link href="${ctxStatic}/bootstrap/inspinia/css/plugins/iCheck/custom.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/plugins/steps/jquery.steps.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/font-awesome-animation.min.css" rel="stylesheet">

<!-- Mainly css -->
<link href="${ctxStatic}/bootstrap/inspinia/css/bootstrap.min.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/font-awesome/css/font-awesome.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/animate.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/plugins/jQueryUI/jquery-ui-1.10.4.custom.min.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/style-jqgrid.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/font-awesome/css/font-awesome.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/plugins/summernote/summernote.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/plugins/summernote/summernote-bs3.css" rel="stylesheet">
<link href="${ctxStatic}/bootstrap/inspinia/css/style.css" rel="stylesheet">


<!-- JSON-js -->
<%--<script src="${ctxStatic}/JSON-js/json2.js"></script>--%>

<!-- Mainly scripts -->
<script src="${ctxStatic}/jqGrid/5.2.1/js/jquery.min.js"></script>
<script src="${ctxStatic}/jquery-ui/jquery-ui.min.js"></script>
<script src="${ctxStatic}/iframeResizer/js/iframeResizer.contentWindow.min.js"></script>
<%-- <script src="${ctxStatic}/bootstrap/inspinia/js/bootstrap.min.js"></script> --%>

<!-- Custom and plugin scripts -->
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/select2/select2.full.min.js"></script>
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/pace/pace.min.js"></script>
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/ladda/spin.min.js"></script>
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/ladda/ladda.min.js"></script>
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/ladda/ladda.jquery.min.js"></script>

<%--<script src="${ctxStatic}/layer/3.0.3/layer.js" type="text/javascript"></script>--%>
<%--<script src="${ctxStatic}/layui/layui.js" type="text/javascript"></script>--%>
<script src="${ctxStatic}/layui/layui.all.js" type="text/javascript"></script>

<script src="${ctxStatic}/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/metisMenu/jquery.metisMenu.js"></script>
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/slimscroll/jquery.slimscroll.min.js"></script>

<link href="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.css" type="text/css" rel="stylesheet"/>
<script src="${ctxStatic}/jquery-validation/1.11.0/jquery.validate.min.js" type="text/javascript"></script>
<%--<script src="${ctxStatic}/jquery/jquery.form.js" type="text/javascript"></script>--%>

<!-- SUMMERNOTE -->
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/summernote/summernote.min.js"></script>
<!-- StateMachine -->
<%--<script src="${ctxStatic}/javascript-state-machine/state-machine.js"></script>--%>
<!-- Bootstrap3 scripts -->
<script src="${ctxStatic}/bootstrap/inspinia/js/bootstrap.min.js"></script>
<script src="${ctxStatic}/bootstrap/inspinia/js/plugins/iCheck/icheck.min.js"></script>
<!-- SockJs scripts -->
<script type="text/javascript" src="${ctxStatic}/sockjs/sockjs.js"></script>
<!-- Lodash -->
<%--<script src="${ctxStatic}/lodash/lodash.js"></script>--%>
<!-- Math -->
<%--<script src="${ctxStatic}/mathjs/math.min.js"></script>--%>
<!-- Custom Function scripts -->
<script src="${ctxStatic}/common/platform.js?v=<%=Math.random()%>" type="text/javascript"></script>
<link href="${ctxStatic}/common/platform.css" type="text/css" rel="stylesheet"/>

<!-- Custom CSS -->
<style>
    input:disabled
    {
        background:#dddddd;
    }
    /*.jqGrid_wrapper {
        overflow: auto;
    }*/
</style>
<!-- Manual scripts -->
<!-- Global -->
<script type="text/javascript">
    $.extend(window, {
        layer: layui.layer,
        layerp: parent.layui.layer,
        upload: layui.upload,
        laydate: layui.laydate,
        basePath: '${ctx}',
        baseStaticPath: '${ctxStatic}',
        fullBase: '${fullBase}',
        username: '${fns:getUser().name}',
        userid: '${fns:getUser().id}',
        rootMenu: '${breadCrumbVO.menuLevel1}',
        menuId: '${breadCrumbVO.menuId}',
        menuLevel2: '${breadCrumbVO.menuLevel2}',
        ieVer: function () {
            var theUA = this.navigator.userAgent.toLowerCase();
            var ver = '';
            if (theUA.match(/trident[\s\S]?\d+/)) ver = theUA.match(/trident[\s\S]?\d+/)[0];
            if (theUA.match(/msie[\s\S]?\d+/)) ver = theUA.match(/msie[\s\S]?\d+/)[0].match(/\d+/)[0];
            return ver;
        }
    });

    var Action = {
        '100': function (msg) {
            layer.msg(msg || '操作成功');
        },
        '101': function (msg) {
            layer.msg(msg || '操作失败');
        },
        '102': function (msg) {
            layer.open({
                title: '后台错误',
                type: 1,
                content: msg || '后台错误'
            })
        }
    };
    $.ajaxSetup({
        dataType: 'text',
        type: 'POST',
        beforeSend: function () {
            layer.load()
        },
        dataFilter: function (data, type) {
            if (type == 'json') return data;
            // var res = JSON.parse(data);
            /*if (Action[res.statusCode]) {
                Action[res.statusCode](res.msg);
            }*/
            return /*res*/JSON.parse(data);
        },
        error: function (xhr, m, e) {
             layer.alert('<span style="font-weight: bold; color: red">'+ m +'</span>');
        },
        complete: function (xhr, ts) {
            layer.closeAll('loading');
        },
        cache: false // 关闭ajax的cache
    });

    layer.config({
//        skin: 'layui-layer-molv',
        shade: 0
    });
</script>
<!-- Function -->
<script>
    $.fn.extend({
        disable: function () {
            $(this).attr('disabled', true);
        },
        enable: function () {
            $(this).attr('disabled', false);
        }
    });
    $.extend({
        standardColor: ['black', 'blue', 'red', 'fuchsia', 'orange', 'purple', 'navy', 'maroon',
            'olive', 'teal', 'gray', 'silver', 'aqua', 'yellow', 'green', 'lime', 'white'],
        toJSON: function(o) {
            return JSON.stringify(o, function(key, val) {
                if (typeof val === 'function') {
                    return val.toString();
                }
                return val;
            });
        },
        fromJSON: function(s) {
            return JSON.parse(s, function(k,v){
                if(v.indexOf && v.indexOf('function') > -1){
                    return eval("(function(){return "+v+" })()")
                }
                return v;
            })
        }
    });
</script>
<!-- DOM loaded -->
<script type="text/javascript">
    jQuery(function ($) {
//        $(document).find('select').not('.no-select2').select2();
        $(document).find('a').addClass('btn-outline');
        //    $(document).find('button').addClass('btn-outline');
        $('.summernote').summernote({height: 150});
    })
</script>