package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

/**
 * <p>
 * 数据字典表
 * table select sql:
   select * from ZJJS_MO_DICTIONARY a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-15
 */
public final class MoDictionaryDynamicSqlSupport {

     public static final MoDictionaryDynamic tableMoDictionary = new MoDictionaryDynamic();
    /**
     * 顺序号（主键）
     */
    public static final SqlColumn<Long> xh = tableMoDictionary.xh;
    /**
     * 字典项值(全称
     */
    public static final SqlColumn<String> dfvalue = tableMoDictionary.dfvalue;
    /**
     * 字典
     */
    public static final SqlColumn<String> dic = tableMoDictionary.dic;
    /**
     * 字典项
     */
    public static final SqlColumn<String> dkey = tableMoDictionary.dkey;
    /**
     * 字典项对应值
     */
    public static final SqlColumn<String> dvalue = tableMoDictionary.dvalue;
    /**
     * 备注
     */
    public static final SqlColumn<String> memo = tableMoDictionary.memo;


    public static final class MoDictionaryDynamic extends SqlTable {


        public final SqlColumn<Long> xh = column("XH");


        public final SqlColumn<String> dfvalue = column("DFVALUE");


        public final SqlColumn<String> dic = column("DIC");


        public final SqlColumn<String> dkey = column("DKEY");


        public final SqlColumn<String> dvalue = column("DVALUE");


        public final SqlColumn<String> memo = column("MEMO");

         public MoDictionaryDynamic() {
            super("ZJJS_MO_DICTIONARY");
        }
    }
}
