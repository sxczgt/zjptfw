package cn.tsinghua.zjptfw.dao;

import cn.tsinghua.zjptfw.pojo.MoDic;
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

import static cn.tsinghua.zjptfw.pojo.sql.support.MoDicDynamicSqlSupport.*;
import static org.mybatis.dynamic.sql.SqlBuilder.*;

/**
 * @author guotao
 * @since 2018-11-15
 */
@Component
public class MoDicDao  implements IBaseDao<MoDic>{

     @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate_dev;

    private BasicColumn[] basicColumns = {
        dic,
        dicname,
        systemName,
    };
     private static final  String[] fileds = {
        "dic",
        "dicname",
        "systemName",
    };

    private static final List<String> filedList = Arrays.asList(fileds);

    @Override
    public int save(MoDic entity) {
        InsertStatementProvider<MoDic> is = insert(entity)
        .into(tableMoDic)
        .map(dic).toProperty("dic")
        .map(dicname).toProperty("dicname")
        .map(systemName).toProperty("systemName")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        SqlParameterSource ps = new BeanPropertySqlParameterSource(is.getRecord());
        return jdbcTemplate_dev.update(is.getInsertStatement(), ps);
    }

     @Override
    public int[] save(List<MoDic> entitys) {
        SqlParameterSource[] batch = SqlParameterSourceUtils.createBatch(entitys.toArray());
        BatchInsert<MoDic> batchInsert = insert(entitys)
        .into(tableMoDic)
        .map(dic).toProperty("dic")
        .map(dicname).toProperty("dicname")
        .map(systemName).toProperty("systemName")
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.batchUpdate(batchInsert.getInsertStatementSQL(), batch);
    }

     @Override
    public int updateRowById(MoDic entity) {
        if(entity.getDic() == null) return 0;
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (allFieldNull){
            return 0;
        }else {
            UpdateStatementProvider usp = update(tableMoDic)
                    .set(dic).equalToWhenPresent(entity::getDic)
                    .set(dicname).equalToWhenPresent(entity::getDicname)
                    .set(systemName).equalToWhenPresent(entity::getSystemName)
                    .where(dic,  isEqualTo(entity::getDic))
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
                 DeleteStatementProvider dsp = deleteFrom(tableMoDic)
                 .where(tableMoDic.dic, isEqualTo(str_id))
                .build()
                .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.update(dsp.getDeleteStatement(), dsp.getParameters());

    }

    @Override
    public MoDic findById(Long id) {
         String str_id = id+"";
         if(StringUtils.isEmpty(str_id)) return new MoDic();
        SelectStatementProvider render = select(basicColumns)
        .from(tableMoDic)
        .where(tableMoDic.dic, isEqualTo(str_id))
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        List<MoDic> query = jdbcTemplate_dev.query(render.getSelectStatement(), render.getParameters(), new BeanPropertyRowMapper<MoDic>(MoDic.class));
        if (query.size() == 1){
            return query.get(0);
        }
        return new MoDic();
    }

    @Override
    public List<MoDic> findByConditions(MoDic entity) {
        boolean allFieldNull = CheckObjectFiled.isFieldsNull(entity,filedList);
        if (!allFieldNull){
            SelectStatementProvider ssp = select(basicColumns)
            .from(tableMoDic)
            .where(dic, isEqualToWhenPresent(entity::getDic))
            .and(dicname, isEqualToWhenPresent(entity::getDicname))
            .and(systemName, isEqualToWhenPresent(entity::getSystemName))
            .build()
            .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
            return jdbcTemplate_dev.query(ssp.getSelectStatement(),ssp.getParameters(),new BeanPropertyRowMapper<MoDic>(MoDic.class));
        }else {
            return findAll();
        }
    }


    @Override
    public List<MoDic> findAll() {
        SelectStatementProvider ssp = select(basicColumns)
        .from(tableMoDic)
        .build()
        .render(RenderingStrategy.SPRING_NAMED_PARAMETER);
        return jdbcTemplate_dev.query(ssp.getSelectStatement(), ssp.getParameters(), new BeanPropertyRowMapper<MoDic>(MoDic.class));
    }

}