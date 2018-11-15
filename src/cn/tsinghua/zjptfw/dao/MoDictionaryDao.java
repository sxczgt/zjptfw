package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.MoDictionary;
import cn.tsinghua.zjptfw.utils.object.CheckObjectFiled;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.MoDictionaryDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class MoDictionaryDao  implements IBaseDao<MoDictionary>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        xh,
        dfvalue,
        dic,
        dkey,
        dvalue,
        memo,
    };
     private static final  String[] fileds = {
        "xh",
        "dfvalue",
        "dic",
        "dkey",
        "dvalue",
        "memo",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(MoDictionary entity) {
        InsertStatementProvider<MoDictionary> is = insert(entity)
        .into(tableMoDictionary)
        .map(xh).toProperty("xh")
        .map(dfvalue).toProperty("dfvalue")
        .map(dic).toProperty("dic")
        .map(dkey).toProperty("dkey")
        .map(dvalue).toProperty("dvalue")
        .map(memo).toProperty("memo")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<MoDictionary> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<MoDictionary> batchInsert = insert(entitys)
        .into(tableMoDictionary)
        .map(xh).toProperty("xh")
        .map(dfvalue).toProperty("dfvalue")
        .map(dic).toProperty("dic")
        .map(dkey).toProperty("dkey")
        .map(dvalue).toProperty("dvalue")
        .map(memo).toProperty("memo")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(MoDictionary entity) {
        if(entity.getXh() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableMoDictionary)
                    .set(xh).equalToWhenPresent(entity::getXh)
                    .set(dfvalue).equalToWhenPresent(entity::getDfvalue)
                    .set(dic).equalToWhenPresent(entity::getDic)
                    .set(dkey).equalToWhenPresent(entity::getDkey)
                    .set(dvalue).equalToWhenPresent(entity::getDvalue)
                    .set(memo).equalToWhenPresent(entity::getMemo)
                    .where(xh,  isEqualTo(entity::getXh))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                if(id == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(tableMoDictionary)
                .where(tableMoDictionary.xh, isEqualTo(id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public MoDictionary findById(Long id) {
        SelectStatementProvider render = select(basicColumns)
        .from(tableMoDictionary)
        .where(tableMoDictionary.xh, isEqualTo(id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<MoDictionary> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<MoDictionary>(MoDictionary.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new MoDictionary();
    }

    @Override
    public List<MoDictionary> findByConditions(MoDictionary entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableMoDictionary)
            .where(xh, isEqualToWhenPresent(entity::getXh))
            .and(dfvalue, isEqualToWhenPresent(entity::getDfvalue))
            .and(dic, isEqualToWhenPresent(entity::getDic))
            .and(dkey, isEqualToWhenPresent(entity::getDkey))
            .and(dvalue, isEqualToWhenPresent(entity::getDvalue))
            .and(memo, isEqualToWhenPresent(entity::getMemo))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<MoDictionary>(MoDictionary.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<MoDictionary> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableMoDictionary)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<MoDictionary>(MoDictionary.class));
    }

}