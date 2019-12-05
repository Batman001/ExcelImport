<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <title>Excel导入</title>
    <meta name="decorator" content="jqgridV3"/>
    <script type="text/javascript">
        jQuery(function ($) {

        });
    </script>
    <script>

        jQuery(function ($) {

            $(window).resize(function () {
                resizeGridWidth();
            });

            $("#jhTable").jqGrid({
                url: '${ctx}/psms/psJHProject/griddata?type=0',
                datatype: 'json',
                editurl: '${ctx}/psms/psJHProject/editgrid',
                autowidth: true,
                shrinkToFit: false,
                autoScroll: true,
                rowNum: 10,
//                pginput:false,
                rowList: [10, 30, 50, 100],
                postData: {
                    'projectName': encodeURI(encodeURI($("#projectName").val())),
                    'projectType': encodeURI(encodeURI($("#projectType").val())),
                    'status': $("#status").val()
                },
                jsonReader: {
                    records: "count", page: "pageNo", total: "pageCount", root: "list"
                },
                prmNames: {page: "pageNo", rows: "pageSize", sort: "orderBy"},
                colModel: [
                    {
                        label: '操作', name: 'actions', title: false, align:'center', sortable: false,
                        formatter: function (cellvalue, options, rowObject, adctions) {
                            var view = " <a id='" + options.rowId + "_editF' class='btn btn-xs btn-outline btn-warning' href='${ctx}/psms/psJHProject/view?menuId=${breadCrumbVO.menuId}&subMenu=view&bootstrap_v=3&id=" + options.rowId + "&projectType=" + encodeURI(encodeURI(rowObject.projectType)) + "'><i class='fa fa-eye'></i> 查看</a>";
                            var edit = "<shiro:hasPermission name='psms:psJHProject:edit'> " +
                                "<a id='" + options.rowId + "_editF' class='btn btn-xs btn-outline btn-primary' href='${ctx}/psms/psJHProject/form?type=0&menuId=${breadCrumbVO.menuId}&subMenu=edit&bootstrap_v=3&id=" + options.rowId + "&projectType=" + encodeURI(encodeURI(rowObject.projectType)) + "'><i class='fa fa-edit'></i> 修改</a>" +
                                "</shiro:hasPermission>";
                            var del = "<shiro:hasPermission name='psms:psJHProject:edit'>" +
                                " <a id='" + options.rowId + "_delF' class='btn btn-xs btn-outline btn-danger' onclick='return confirmx(\"确认要删除吗？\", this.href)' href='${ctx}/psms/psJHProject/delete?menuId=${breadCrumbVO.menuId}&bootstrap_v=3&id=" + options.rowId + "'><i class='fa fa-trash'></i> 删除</a>" +
                                "</shiro:hasPermission>";
                            return view + edit + del;
                        }, width: 230, colmenu: false
                    },
                    {
                        label: '项目名称',
                        name: 'projectName',
                        index: 'project_name',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 300,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '项目密级',
                        name: 'projectLevelForJHGrid',
                        index: 'project_level',
                        sortable: false,
                        editable: true,
                        align:'center',
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '项目类型',
                        name: 'projectType',
                        index: 'project_type',
                        align:'center',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '主管部门',
                        name: 'administration',
                        index: 'administration',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 300,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '组织单位',
                        name: 'organization',
                        index: 'organization',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 300,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '牵头单位',
                        name: 'leadunit',
                        index: 'leadunit',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 300,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '项目周期',
                        name: 'projectCycle',
                        index: 'project_cycle',
                        sortable: false,
                        editable: true,
                        align:'center',
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '总投资<br/>(万元)',
                        name: 'totalPrice',
                        index: 'total_price',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '国家投入<br/>(万元)',
                        name: 'nationalInput',
                        index: 'national_input',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '集团投入<br/>(万元)',
                        name: 'groupInput',
                        index: 'group_input',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '单位自主投入<br/>(万元)',
                        name: 'unitInput',
                        index: 'unit_input',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '研究目标',
                        name: 'researchArms',
                        index: 'research_arms',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 300,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '研究成果',
                        name: 'researchResult',
                        index: 'research_result',
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        width: 300,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '五年规划',
                        name: 'fiveYearPlan',
                        index: 'five_year_plan',
                        sortable: false,
                        editable: true,
                        align:'center',
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    },
                    {
                        label: '项目状态',
                        name: 'status',
                        index: 'status',
                        sortable: false,
                        editable: true,
                        align:'center',
                        editrules: {required: true},
                        width: 110,
                        coloptions: {filtering: false, grouping: false, freeze: false}
                    }
                ],
                pager: "#jqPagination",
                sortname: 'a.project_name',
                sortorder: "asc",
                viewrecords: true,
                rownumbers: true,
                colMenu: false,
                height: document.body.clientHeight + 300,
                hidegrid: false
            });


            $("#btn-Submit").click(function () {
                $("#jhTable").jqGrid('setGridParam', {
                    postData: {
                        'projectName': encodeURI(encodeURI($("#projectName").val())),
                        'projectType': encodeURI(encodeURI($("#projectType").val())),
                        'status': $("#status").val(),
                    }
                }).trigger("reloadGrid");
            });

            $("#btn-Refresh").click(function () {
                $("#projectName").val('');
                $("#projectType").val('');
                $("#status").val('');
                $("#select2-projectType-container").text("全部");
                $("#select2-status-container").text("全部");
                $("#btn-Submit").click();
            });

            $("#btn-ExportData").click(function () {
                confirmx("确认要导出数据吗？", function () {
                    $("#searchForm").attr("action", "${ctx}/psms/psJHProject/export");
                    $("#searchForm").submit();
                    layer.closeAll();
                });
            });

            upload.render({
                elem: '#btn-Import'
                , url: "${ctx}/import/import"
                , accept: 'file'
                , exts: 'xls|xlsx'
                , before: function (obj) {
                    $.get("${ctx}/import/getThread?type=PsJHProject&businessId=1", function (data, status) {
                    });
                    layer.load(0, {shade: false});
                }
                , done: function (res) {
                    var state = res.state;
                    if (state == 21 || state == '21') {
                        var url = "${ctxStatic}/errorfile/" + res.detail;
                        location.href = url;
                    }
                    layer.closeAll('loading');
                    layer.alert(res.msg);
                    $("#btn-Submit").click();
                }
                , error: function (index, upload) {
                    layer.closeAll('loading');
                }
            });

        });

    </script>
</head>
<body>
<div class="ibox">
    <div class="ibox-title">
        <div class="row">
            <form:form id="searchForm" action="" method="post" class="m-t-n-xs col-sm-12 form-inline">
                <div class="form-group">
                    <label class="control-label pull-left m-l-md">项目类型：
                        <select id="projectType" name="projectType" class="form-control input-xlarge"
                                style="width:150px;">
                            <option value="">全部</option>
                            <c:forEach items="${fns:getDictList('jh_project_type')}" var="jh_project_type">
                                <option value="${jh_project_type.value}">${jh_project_type.label}</option>
                            </c:forEach>
                        </select>
                    </label>
                    <label class="control-label pull-left m-l-md">项目状态：
                        <select id="status" name="status" class="form-control input-xlarge" style="width:150px;">
                            <option value="">全部</option>
                            <c:forEach items="${fns:getDictList('jh_project_status')}" var="jh_project_status">
                                <option value="${jh_project_status.value}">${jh_project_status.label}</option>
                            </c:forEach>
                        </select>

                    </label>
                    <label class="control-label pull-left m-l-md">项目名称：
                        <input id="projectName" name="projectName" type="text"
                               class="input-sm form-control input-medium" maxlength="255">

                    </label>
                </div>
                <div class="m-l-xs form-group">
                    <a id="btn-Submit" class="btn btn-sm btn-success" type="submit">查询</a>
                    <a id="btn-Refresh" class="btn btn-sm btn-warning" type="button">重置</a>

                    <shiro:hasPermission name="psms:psJHProject:edit">
                        <a class="btn btn-sm btn-primary" type="button"
                           href="${ctx}/psms/psJHProject/form?type=0&menuId=${breadCrumbVO.menuId}&subMenu=add&bootstrap_v=3">添加</a>
                        <a id="btn-Import" class="btn btn-sm btn-success" type="button">导入</a>
                        <div class="btn-group">
                            <button data-toggle="dropdown" class="btn btn-sm btn-success btn-outline dropdown-toggle">导出<span
                                    class="caret"></span></button>
                            <ul class="dropdown-menu">
                                <li><a id="btn-ExportData" type="button">导出数据</a></li>
                                <li class="divider"></li>
                                <li><a id="btn-ExportTemplate"
                                       href="${ctx}/psms/psJHProject/import/template?menuId=${breadCrumbVO.menuId}&bootstrap_v=3">导出模板</a>
                                </li>
                            </ul>
                        </div>
                    </shiro:hasPermission>

                </div>
            </form:form>
        </div>
    </div>
    <div class="ibox-content">
        <div class="full-width-scroll p-xs">
            <sys:message_V3 content="${message}"/>
            <div class="jqGrid_wrapper">
                <table id="jhTable"></table>
                <div id="jqPagination"></div>
            </div>
        </div>
    </div>
</div>

</body>
</html>