package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.MoGrant;
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

import java.util.Arrays;
import java.util.List;

import static cn.tsinghua.zjptfw.pojo.sql.support.MoGrantDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class MoGrantDao  implements IBaseDao<MoGrant>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        id,
        dicCode,
        roleId,
        dwbh,
    };
     private static final  String[] fileds = {
        "dicCode",
        "roleId",
        "dwbh",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(MoGrant entity) {
        InsertStatementProvider<MoGrant> is = insert(entity)
        .into(tableMoGrant)
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        .map(dicCode).toProperty("dicCode")
        .map(roleId).toProperty("roleId")
        .map(dwbh).toProperty("dwbh")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<MoGrant> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<MoGrant> batchInsert = insert(entitys)
        .into(tableMoGrant)
        .map(id).toConstant(SequenceParam.zjjs_authlog_sequence)
        .map(dicCode).toProperty("dicCode")
        .map(roleId).toProperty("roleId")
        .map(dwbh).toProperty("dwbh")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(MoGrant entity) {
        if(entity.getId() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableMoGrant)
                    .set(dicCode).equalToWhenPresent(entity::getDicCode)
                    .set(roleId).equalToWhenPresent(entity::getRoleId)
                    .set(dwbh).equalToWhenPresent(entity::getDwbh)
                    .where(id,  isEqualTo(entity::getId))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                if(id == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(tableMoGrant)
                .where(tableMoGrant.id, isEqualTo(id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public MoGrant findById(Long id) {
        SelectStatementProvider render = select(basicColumns)
        .from(tableMoGrant)
        .where(tableMoGrant.id, isEqualTo(id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<MoGrant> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<MoGrant>(MoGrant.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new MoGrant();
    }

    @Override
    public List<MoGrant> findByConditions(MoGrant entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableMoGrant)
            .where(id, isEqualToWhenPresent(entity::getId))
            .and(dicCode, isEqualToWhenPresent(entity::getDicCode))
            .and(roleId, isEqualToWhenPresent(entity::getRoleId))
            .and(dwbh, isEqualToWhenPresent(entity::getDwbh))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<MoGrant>(MoGrant.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<MoGrant> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableMoGrant)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<MoGrant>(MoGrant.class));
    }

}