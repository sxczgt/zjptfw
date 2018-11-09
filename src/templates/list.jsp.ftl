<%@include file="../common/_meta.jsp"%>
<%
    int width=600;
    int height =360;
    String baseController="/${entity?uncap_first}";
%>
<html>
<head>
    <title>${cfg.modalsName}</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
</head>
<body>
<nav class="breadcrumb"><i class="Hui-iconfont">&#xe67f;</i> <a href="/switch/switchRole.do">首页</a> <span class="c-gray en">&gt;</span>系统设置<span class="c-gray en">&gt;</span> ${cfg.modalsName} <a class="btn btn-success radius r" style="line-height:1.6em;margin-top:3px" href="javascript:location.replace(location.href);" title="刷新" ><i class="Hui-iconfont">&#xe68f;</i></a></nav>

<div class="page-container">
    <form method="post" action="<%=baseController%>/getList.do" id="dataListForm">
        <!--需要隐藏的属性 默认生成主键 sort columnName-->
        <#list table.fields as field>
            <#if field.keyFlag>
                            <#assign keyPropertyName="${field.propertyName}"/>
            </#if>
        </#list>
        <input type="hidden" name="pageNo" id="pageNo" value="${r'${fn:escapeXml(pageNo) }'}"/>
        <input type="hidden" name="column" id="column" value="${r'${fn:escapeXml(column) }'}">
        <input type="hidden" name="sort" id="sort" value="${r'${fn:escapeXml(sort) }'}"/>
        <input type="hidden" name="columnName" id="columnName" value="${r'${fn:escapeXml(columnName )}'}"/>
    </form>
    <div class=" cl pd-5 bg-1 bk-gray mb-20">
        <button onclick="layer_show('添加${cfg.modalsName}','<%=baseController%>/toAddOne.do',<%=width%>,<%=height%>)" class="btn btn-primary"><i class="Hui-iconfont">&#xe600;</i>添加${cfg.modalsName}</button>
    </div>
    <div>
        <table class="table table-border table-bordered table-bg table-hover table-sort" id="${entity?uncap_first}Table">
            <thead>
            <tr class="text-c" id="rowData">
                <!--自动生成所有列-->
<#list table.fields as field>
                <th columnName="${field.name}"><#if field.comment??>${field.comment!}<#else>${field.propertyName}</#if></th><!--${field.comment!}-->
</#list>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${r'${page.getRecords()}'}" var="${entity?uncap_first}">
                <tr class="text-c">
        <#list table.fields as field>
                    <td>${r'${'}${entity?uncap_first}.${field.propertyName}${r'}'}</td>
        </#list>
                    <td>
                    <span><a class="btn btn-secondary radius size-MINI" href="javascript:void(0)"
                           onclick="layer_show('编辑${cfg.modalsName}信息','<%=baseController%>/toEditOne.do?id=${r'${'}${entity?uncap_first}.${keyPropertyName} }',<%=width%>,<%=height%>)">编辑</a>
                    </span>&nbsp;&nbsp;
                    <span><a class="btn btn-danger radius size-MINI" href="javascript:void(0)"
                           onclick="deletePaymentItemType('${r'${'}${entity?uncap_first}.${keyPropertyName}${r'}'}')">删除</a>
                    </span>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <!--分页-->
    <div style="float:left"><div id="pager" class="r" style="margin:10px;"></div></div>
</div>
<%@include file="../common/_footer.jsp"%>
<script type="text/javascript">

    $(document).ready(function () {
        $("#pager").pager({
            pagenumber: ${r'${fn:escapeXml('}page.current)},
            pagecount: ${r'${fn:escapeXml('}page.getPages())},
            pagemax:${r'${fn:escapeXml('}page.size)},
            maxresult:${r'${fn:escapeXml('}page.total)},
            buttonClickCallback: function (page_index) {
                $("#pageNo").val(page_index);
                $("#dataListForm").submit();
            }
        });
        $('.skin-minimal input').iCheck({
            checkboxClass: 'icheckbox-blue',
            radioClass: 'iradio-blue',
            increaseArea: '20%'
        });

        var dataTableInited = false;
        var column = '${r'${fn:escapeXml(column)}'}';
        var sort = '${r'${fn:escapeXml(sort)}'}';
        $("#${entity?uncap_first}Table").dataTable({
            "bSort":true,
            "bFilter":false,
            "searching":false,//是否允许搜索功能
            "bPaginate":false,//开关，是否显示(应用)分页器
            "bStateSave":false,
            "bInfo":false,//是否显示页脚的信息
            "aaSorting":[[column,sort]],
            "aoColumnDefs":[
                //{"bVisible":false,"aTargets":[0]}// 隐藏某些列 从0开始计数
                {"bSortable":false,"aTargets":[${table.fields?size}]}//指定列不排序 从0开始计数
            ],
            "fnDrawCallback":function () {
                if (!dataTableInited){
                    dataTableInited = true;
                } else {
                    $("#rowData th").each(function (i,o) {
                        if ($(this).attr('aria-sort')){
                            if ($(this).attr("class")=='sorting_asc'){
                                $("#sort").val("asc");
                            }else {
                                $("#sort").val("desc");
                            }
                            $("#columnName").val($(this).attr("columnName"));
                            $("#column").val(i);
                        }
                    });
                    $("#dataListForm").submit();
                }
            }
        });
    });
    function deletePaymentItemType(id){
        layer.confirm('确定要删除吗?', {icon: 3, title:'提示'},function (index) {
            $.post("<%=baseController%>/deleteOne.do",
                    {id:id},
                    function (result) {
                        console.log(result);
                        if (result.code=="ok"){
                            successMsg("操作成功!");
                            setTimeout(function () {
                                window.location.reload();
                            },1000)
                        }else {
                            failMsg("操作失敗!")
                        }
                    });
            layer.close(index);
        })
    }
</script>
</body>
</html>
