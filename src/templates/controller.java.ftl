package ${package.Controller};


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.ui.Model;
import cn.edu.tsinghua.web.business.common.vo.Conditions;
import cn.edu.tsinghua.web.common.json.pojo.ResultInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;


<#if restControllerStyle>
import org.springframework.web.bind.annotation.RestController;
<#else>
import org.springframework.stereotype.Controller;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import ${package.Entity}.${entity};
import ${package.Service}.${table.serviceName};

/**
 * <p>
 * ${table.comment!} 前端控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if restControllerStyle>
@RestController
<#else>
@Controller
</#if>
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")
<#if kotlin>
class ${table.controllerName}<#if superControllerClass??> : ${superControllerClass}()</#if>
<#else>
<#if superControllerClass??>
public class ${table.controllerName} extends ${superControllerClass} {
<#else>
public class ${table.controllerName} {
</#if>
    @Autowired
    private ${table.serviceName} ${table.serviceImplName?uncap_first};

    <#list table.fields as field>
        <#if field.keyFlag>
            <#assign keyPropertyName="${field.propertyName}"/>
            <#assign keyName="${field.name}"/>
        </#if>
    </#list>
    /**
     * 获取${cfg.modalsName}列表信息
     *
     * @return
     */
    @RequestMapping("getList")
    public String getList(${entity} ${entity?uncap_first}, String sort, Integer column, String columnName, Conditions conditions,Model model) {
        //排序字段设置 默认对注解排序
        if (column == null || StrUtil.isBlank(sort) || StrUtil.isBlank(columnName)) {
            sort = "asc";
            column = 0;
            columnName = "${keyName}";
        }

        //前端查询条件设置 不需要的字段删除
        QueryWrapper<${entity}> queryWrapper = new QueryWrapper<${entity}>();
        if (null != ${entity?uncap_first}){
        <#list table.fields as field>
            <#if field.propertyType == "String">
             if (StrUtil.isNotBlank(${entity?uncap_first}.get${field.capitalName}())){
                queryWrapper.eq("${field.name?upper_case}",${entity?uncap_first}.get${field.capitalName}());
            }
            <#else>
            if (ObjectUtil.isNotNull(${entity?uncap_first}.get${field.capitalName}())){
                queryWrapper.eq("${field.name?upper_case}",${entity?uncap_first}.get${field.capitalName}());
            }
            </#if>
        </#list>
        }
        queryWrapper.orderBy(true, StrUtil.equals(sort, "asc") ? true : false, columnName);
        IPage<${entity}> pages = ${table.serviceImplName?uncap_first}.page(new Page<${entity}>(conditions.getPageNo(),
                                conditions.getPageSize()),
                                queryWrapper);

        model.addAttribute("page",pages);
        model.addAttribute("column", column);
        model.addAttribute("sort", sort);
        model.addAttribute("columnName", columnName);
        //前端传递日期使用代码自动生成预留
        //model.addAttribute("startTime",startTime);
        //model.addAttribute("endTime",endTime);
        return "${entity?uncap_first}/${entity?uncap_first}List";
    }

    /**
     * 跳转到${cfg.modalsName}添加页面
     *
     * @return
     */
    @RequestMapping("toAddOne")
    public String toAddOne(Model model) {
        return "${entity?uncap_first}/${entity?uncap_first}Add";
    }

    /**
     * 添加${cfg.modalsName}
     *
     * @param ${entity?uncap_first}
     * @return
     */
    @RequestMapping("addOne")
    @ResponseBody
    public ResultInfo addOne(${entity} ${entity?uncap_first}) {
        try {
            //后台对${entity?uncap_first}进行部分字段set操作代码自动生成预留
            ${table.serviceImplName?uncap_first}.save(${entity?uncap_first});
            return ResultInfo.success("ok", "新增成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return errorInfo(e);
        }
    }

    /**
     * 跳转到编辑${cfg.modalsName}页面
     *
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("toEditOne")
    public String toEditOne(Model model, String id) {
        model.addAttribute("${entity?uncap_first}", ${table.serviceImplName?uncap_first}.getById(id));
        return "${entity?uncap_first}/${entity?uncap_first}Edit";
    }

    /**
     * 编辑${cfg.modalsName}
     *
     * @param ${entity?uncap_first}
     * @return
     */
    @RequestMapping("editOne")
    @ResponseBody
    public ResultInfo editOne(${entity} ${entity?uncap_first}) {
        try {
            boolean flag = ${table.serviceImplName?uncap_first}.updateById(${entity?uncap_first});
            if (flag) {
                return ResultInfo.success("ok", "编辑成功!");
            } else {
                return ResultInfo.fail("error", "编辑失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorInfo(e);
        }
    }

    /**
     * 根据Id删除${cfg.modalsName}
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteOne")
    @ResponseBody
    public ResultInfo deleteOne(String id) {
        try {
            boolean flag = ${table.serviceImplName?uncap_first}.removeById(id);
            if (flag) {
                return ResultInfo.success("ok", "删除成功!");
            } else {
                return ResultInfo.fail("error", "删除失败!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return errorInfo(e);
        }
    }
}
</#if>
