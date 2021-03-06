<%@include file="../common/_meta.jsp"%>
<html>
<%
    String baseController="/${entity?uncap_first}";
%>
<head>
    <title>添加${cfg.modalsName}</title>
    <meta name="keywords" content="">
    <meta name="description" content="">
</head>
<body>
<div class="page-container">
    <!--新增不需要设置主键-->
        <#list table.fields as field>
            <#if field.keyFlag>
            <#assign keyPropertyName="${field.propertyName}"/>
            </#if>
        </#list>
    <form class="form form-horizontal" action="<%=baseController%>/addOne.do" method="post" id="addForm">
        <#list table.fields as field>
            <#if cfg.jspWithMainKey>
        <div class="row cl">
            <label class="form-label col-xs-4 col-sm-3"><#if field.comment??>${field.comment!}<#else>${field.propertyName}</#if><!--${field.comment!}-->:</label>
            <div class="formControls col-xs-8 col-sm-9">
                <input class="input-text" type="text" name="${field.propertyName}" id="${field.propertyName}"/>
            </div>
        </div>
            <#else >
                <#if field.propertyName == keyPropertyName>
                <#else>
        <div class="row cl">
            <label class="form-label col-xs-4 col-sm-3"><#if field.comment??>${field.comment!}<#else>${field.propertyName}</#if></th><!--${field.comment!}-->:</label>
            <div class="formControls col-xs-8 col-sm-9">
                <input class="input-text" type="text" name="${field.propertyName}" id="${field.propertyName}"/>
            </div>
        </div>
                </#if>
            </#if>
        </#list>

        <div class="row cl">
            <div class="col-xs-8 col-sm-9 col-xs-offset-4 col-sm-offset-3">
                <input type="button" id="btnadd" class="btn btn-primary radius size-M" value="&nbsp;&nbsp;提交&nbsp;&nbsp;"/>
                <input type="button" id="btnclo"  class="btn btn-secondary radius size-M" value="&nbsp;&nbsp;关闭&nbsp;&nbsp;"/>
            </div>
        </div>
    </form>
</div>
</body>
<%@ include file="../common/_footer.jsp"%>
<script type="text/javascript">
$(document).ready(function () {
    $("#btnclo").click(function () {
       layer_close();
    });
    $("#addForm").validate({
        rules:{;
<#list table.fields as field>
            ${field.propertyName}:{
                true,
                rangelength;:[0,50]
            }<#if field_has_next>,</#if>
</#list>
        },
        {
<#list table.fields as field>
            ${field.propertyName}:{
                "请填写<#if field.comment??>${field.comment!}<#else>${field.propertyName}</#if>!",
                rangelength;:"长度{0}-{1}之间"
            }<#if field_has_next>,</#if>
</#list>
        }
})
    $("#btnadd").click(function () {
        if ($("#addForm").valid()){
            ajaxSubmit("addForm",function (data) {
                if (data.code=="ok"){
                    successMsg("操作成功！");
                    setTimeout(function () {
                        window.parent.location.reload();
                    },1000)
                } else {
                    failMsg("操作失败！")
                }
            })
        }
    });
});
</script>
</html>
