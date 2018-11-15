package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.City;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.CityDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class CityDao  implements IBaseDao<City>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        cid,
        cityid,
        city,
        father,
    };
     private static final  String[] fileds = {
        "cid",
        "cityid",
        "city",
        "father",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(City entity) {
        InsertStatementProvider<City> is = insert(entity)
        .into(tableCity)
        .map(cid).toProperty("cid")
        .map(cityid).toProperty("cityid")
        .map(city).toProperty("city")
        .map(father).toProperty("father")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<City> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<City> batchInsert = insert(entitys)
        .into(tableCity)
        .map(cid).toProperty("cid")
        .map(cityid).toProperty("cityid")
        .map(city).toProperty("city")
        .map(father).toProperty("father")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(City entity) {
        if(entity.getCid() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableCity)
                    .set(cid).equalToWhenPresent(entity::getCid)
                    .set(cityid).equalToWhenPresent(entity::getCityid)
                    .set(city).equalToWhenPresent(entity::getCity)
                    .set(father).equalToWhenPresent(entity::getFather)
                    .where(cid,  isEqualTo(entity::getCid))
                    .build()
                    .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            SqlParameterSource parameterSource = new MapSqlParameterSource(usp.getParameters());
            return jdbcTemplate_dev.update(usp.getUpdateStatement(), parameterSource);
        }
    }

    @Override
    public int deleteById(Long id) {
                if(id == null) return 0;
                DeleteStatementProvider dsp = deleteFrom(tableCity)
                .where(tableCity.cid, isEqualTo(id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public City findById(Long id) {
        SelectStatementProvider render = select(basicColumns)
        .from(tableCity)
        .where(tableCity.cid, isEqualTo(id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<City> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<City>(City.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new City();
    }

    @Override
    public List<City> findByConditions(City entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableCity)
            .where(cid, isEqualToWhenPresent(entity::getCid))
            .and(cityid, isEqualToWhenPresent(entity::getCityid))
            .and(city, isEqualToWhenPresent(entity::getCity))
            .and(father, isEqualToWhenPresent(entity::getFather))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<City>(City.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<City> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableCity)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<City>(City.class));
    }

}