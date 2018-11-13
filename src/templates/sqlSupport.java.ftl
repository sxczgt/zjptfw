package ${package.Entity};

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.sql.JDBCType;
import java.util.Date;

/**
 * <p>
 * ${table.comment!}
 * table select sql:
   select * from ${table.name!} a where 1=1
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public final class ${entity}DynamicSqlSupport {

     public static final ${entity}Dynamic table = new ${entity}Dynamic();
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
<#if field.keyFlag>
<#assign keyPropertyName="${field.propertyName}"/>
</#if>
<#if field.comment!?length gt 0>
    /**
     * ${field.comment}
     */
</#if>
    public static final SqlColumn<${field.propertyType}> ${field.propertyName} = table.${field.propertyName};
</#list>
<#------------  END 字段循环遍历  ---------->


    public static final class ${entity}Dynamic extends SqlTable {

    <#list table.fields as field>

        public final SqlColumn<${field.propertyType}> ${field.propertyName} = column("${field.name}");

    </#list>
         public ${entity}Dynamic() {
            super("${table.name}");
        }
    }
}
