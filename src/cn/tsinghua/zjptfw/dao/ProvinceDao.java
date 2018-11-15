package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.Province;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.ProvinceDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class ProvinceDao  implements IBaseDao<Province>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        sid,
        provinceid,
        province,
    };
     private static final  String[] fileds = {
        "sid",
        "provinceid",
        "province",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(Province entity) {
        InsertStatementProvider<Province> is = insert(entity)
        .into(tableProvince)
        .map(sid).toProperty("sid")
        .map(provinceid).toProperty("provinceid")
        .map(province).toProperty("province")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<Province> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<Province> batchInsert = insert(entitys)
        .into(tableProvince)
        .map(sid).toProperty("sid")
        .map(provinceid).toProperty("provinceid")
        .map(province).toProperty("province")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(Province entity) {
        if(entity.getSid() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableProvince)
                    .set(sid).equalToWhenPresent(entity::getSid)
                    .set(provinceid).equalToWhenPresent(entity::getProvinceid)
                    .set(province).equalToWhenPresent(entity::getProvince)
                    .where(sid,  isEqualTo(entity::getSid))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                if(id == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(tableProvince)
                .where(tableProvince.sid, isEqualTo(id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public Province findById(Long id) {
        SelectStatementProvider render = select(basicColumns)
        .from(tableProvince)
        .where(tableProvince.sid, isEqualTo(id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<Province> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<Province>(Province.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new Province();
    }

    @Override
    public List<Province> findByConditions(Province entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableProvince)
            .where(sid, isEqualToWhenPresent(entity::getSid))
            .and(provinceid, isEqualToWhenPresent(entity::getProvinceid))
            .and(province, isEqualToWhenPresent(entity::getProvince))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<Province>(Province.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<Province> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableProvince)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<Province>(Province.class));
    }

}