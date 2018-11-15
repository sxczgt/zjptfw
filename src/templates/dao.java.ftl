package ${package.Entity};

import cn.tsinghua.zjptfw.pojo.AuthLog;
import cn.tsinghua.zjptfw.utils.object.CheckObjectFiled;
import cn.tsinghua.zjptfw.utils.string.SequenceParam;
import org.mybatis.dynamic.sql.BasicColumn;
import org.mybatis.dynamic.sql.delete.render.DeleteStatementProvider;
import org.mybatis.dynamic.sql.insert.render.BatchInsert;
import org.mybatis.dynamic.sql.insert.render.InsertStatementProvider;
import org.mybatis.dynamic.sql.render.RenderingStrategy;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.dynamic.sql.update.render.UpdateStatementProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.*;
import org.springframework.stereotype.Component;

import java.util.List;

import static cn.tsinghua.zjptfw.pojo.sql.support.${entity}DynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author ${author}
 * @since ${date}
 */
@Component
public class ${entity}Dao  implements IBaseDao<${entity}>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
    <#list table.fields as field>
        ${field.propertyName},
    </#list>
    };
     private static final  String[] fileds = {
<#list table.fields as field>
    <#if field.propertyName != "id">
        "${field.propertyName}",
    </#if>
</#list>
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(${entity} entity) {
        InsertStatementProvider<${entity}> is = insert(entity)
        .into(table${entity})
    <#list table.fields as field>
        <#if field.propertyName = "id">
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        </#if>
        <#if field.propertyName != "id">
            <#assign x = "${field.propertyName}" >
        .map(${field.propertyName}).toProperty("${field.propertyName}")
        </#if>
    </#list>
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<${entity}> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<${entity}> batchInsert = insert(entitys)
        .into(table${entity})
<#list table.fields as field>
    <#if field.propertyName = "id">
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
    </#if>
    <#if field.propertyName != "id">
        .map(${field.propertyName}).toProperty("${field.propertyName}")
    </#if>
</#list>
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(${entity} entity) {
    <#list table.fields as field>
        <#if field.keyFlag>
            <#if field.propertyType == "Long">
                if(entity.get${field.capitalName} == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(table${entity})
            </#if>
            <#if field.propertyType == "String">
                 String str_id = entity.get${field.capitalName}+"";
                 if(StringUtils.isEmpty(str_id)) return 0;
            </#if>
        </#if>
    </#list>
        if(entity.getId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(table${entity})
            <#list table.fields as field>
                <#if field.propertyName != "id">
                    <#assign x = "${field.propertyName}" >
                    .set(${field.propertyName}).equalToWhenPresent(entity::get${x?cap_first})
                </#if>
            </#list>
                    .where(id,  isEqualTo(entity::getId))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
         <#list table.fields as field>
             <#if field.keyFlag>
                <#if field.propertyType == "Long">
                if(id == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(table${entity})
                .where(table${entity}.${field.propertyName}, isEqualTo(id))
                </#if>
                 <#if field.propertyType == "String">
                 String str_id = id+"";
                 if(StringUtils.isEmpty(str_id)) return 0;
                 DeleteStatementProvider dsp = deleteFrom(table${entity})
                 .where(table${entity}.${field.propertyName}, isEqualTo(str_id))
                 </#if>
             </#if>
         </#list>
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public ${entity} findById(Long id) {
<#list table.fields as field>
    <#if field.keyFlag>
        <#if field.propertyType == "Long">
        SelectStatementProvider render = select(basicColumns)
        .from(table${entity})
        .where(table${entity}.${field.propertyName}, isEqualTo(id))
        </#if>
        <#if field.propertyType == "String">
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new ${entity}();
        SelectStatementProvider render = select(basicColumns)
        .from(table${entity})
        .where(table${entity}.${field.propertyName}, isEqualTo(str_id))
        </#if>
    </#if>
</#list>
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<${entity}> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<${entity}>(${entity}.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new ${entity}();
    }

    @Override
    public List<${entity}> findByConditions(${entity} entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(table${entity})
            <#list table.fields as field>
                <#assign x = "${field.propertyName}" >
                <#if field_index == 0>
            .where(${field.propertyName}, isEqualToWhenPresent(entity::get${x?cap_first}))
                </#if>
                <#if field_index != 0 >
            .and(${field.propertyName}, isEqualToWhenPresent(entity::get${x?cap_first}))
                </#if>
            </#list>
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<${entity}>(${entity}.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<${entity}> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(table${entity})
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<${entity}>(${entity}.class));
    }

}