package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.Partner;
import cn.tsinghua.zjptfw.utils.object.CheckObjectFiled;
import org.apache.commons.lang.StringUtils;
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

import java.util.Arrays;
import java.util.List;

import static cn.tsinghua.zjptfw.pojo.sql.support.PartnerDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class PartnerDao  implements IBaseDao<Partner>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        partnerId,
        partnerName,
        partnerEname,
        partnerVname,
        enableFlag,
    };
     private static final  String[] fileds = {
        "partnerId",
        "partnerName",
        "partnerEname",
        "partnerVname",
        "enableFlag",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(Partner entity) {
        InsertStatementProvider<Partner> is = insert(entity)
        .into(tablePartner)
        .map(partnerId).toProperty("partnerId")
        .map(partnerName).toProperty("partnerName")
        .map(partnerEname).toProperty("partnerEname")
        .map(partnerVname).toProperty("partnerVname")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<Partner> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<Partner> batchInsert = insert(entitys)
        .into(tablePartner)
        .map(partnerId).toProperty("partnerId")
        .map(partnerName).toProperty("partnerName")
        .map(partnerEname).toProperty("partnerEname")
        .map(partnerVname).toProperty("partnerVname")
        .map(enableFlag).toProperty("enableFlag")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(Partner entity) {
        if(entity.getPartnerId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tablePartner)
                    .set(partnerId).equalToWhenPresent(entity::getPartnerId)
                    .set(partnerName).equalToWhenPresent(entity::getPartnerName)
                    .set(partnerEname).equalToWhenPresent(entity::getPartnerEname)
                    .set(partnerVname).equalToWhenPresent(entity::getPartnerVname)
                    .set(enableFlag).equalToWhenPresent(entity::getEnableFlag)
                    .where(partnerId,  isEqualTo(entity::getPartnerId))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                 String str_id = id+"";
                 if(StringUtils.isEmpty(str_id)) return 0;
                 DeleteStatementProvider dsp = deleteFrom(tablePartner)
                 .where(tablePartner.partnerId, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public Partner findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new Partner();
        SelectStatementProvider render = select(basicColumns)
        .from(tablePartner)
        .where(tablePartner.partnerId, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<Partner> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<Partner>(Partner.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new Partner();
    }

    @Override
    public List<Partner> findByConditions(Partner entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tablePartner)
            .where(partnerId, isEqualToWhenPresent(entity::getPartnerId))
            .and(partnerName, isEqualToWhenPresent(entity::getPartnerName))
            .and(partnerEname, isEqualToWhenPresent(entity::getPartnerEname))
            .and(partnerVname, isEqualToWhenPresent(entity::getPartnerVname))
            .and(enableFlag, isEqualToWhenPresent(entity::getEnableFlag))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<Partner>(Partner.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<Partner> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tablePartner)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<Partner>(Partner.class));
    }

}