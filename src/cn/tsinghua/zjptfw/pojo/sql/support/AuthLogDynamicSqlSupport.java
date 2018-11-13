package cn.tsinghua.zjptfw.pojo.sql.support;

import org.mybatis.dynamic.sql.SqlColumn;
import org.mybatis.dynamic.sql.SqlTable;

import java.time.LocalDateTime;

/**
 * <p>
 * 操作日志表(insert,uppdate,delete操作都要记录)
 * table select sql:
   select * from ZJJS_AUTH_LOG a where 1=1
 * </p>
 *
 * @author guotao
 * @since 2018-11-12
 */
public final class AuthLogDynamicSqlSupport {

     public static final AuthLogDynamic table = new AuthLogDynamic();
    /**
     * ID
     */
    public static final SqlColumn<Long> id = table.id;
    /**
     * 被del,insert,update的数据所在表名
     */
    public static final SqlColumn<String> dataTablename = table.dataTablename;
    /**
     * 操作时间
     */
    public static final SqlColumn<LocalDateTime> opTime = table.opTime;
    /**
     * 操作描述
     */
    public static final SqlColumn<String> opDesc = table.opDesc;
    /**
     * 数据操作的具体SQL语句
     */
    public static final SqlColumn<String> opSql = table.opSql;
    /**
     * 校内用户用校园卡号，校外用户用auth_user表的username字段
     */
    public static final SqlColumn<String> opZjh = table.opZjh;
    /**
     * 操作人姓名
     */
    public static final SqlColumn<String> opXm = table.opXm;
    /**
     * 所在IP
     */
    public static final SqlColumn<String> opIp = table.opIp;
    /**
     * 子系统名称
     */
    public static final SqlColumn<String> opSystem = table.opSystem;


    public static final class AuthLogDynamic extends SqlTable {


        public final SqlColumn<Long> id = column("ID");


        public final SqlColumn<String> dataTablename = column("DATA_TABLENAME");


        public final SqlColumn<LocalDateTime> opTime = column("OP_TIME");


        public final SqlColumn<String> opDesc = column("OP_DESC");


        public final SqlColumn<String> opSql = column("OP_SQL");


        public final SqlColumn<String> opZjh = column("OP_ZJH");


        public final SqlColumn<String> opXm = column("OP_XM");


        public final SqlColumn<String> opIp = column("OP_IP");


        public final SqlColumn<String> opSystem = column("OP_SYSTEM");

         public AuthLogDynamic() {
            super("ZJJS_AUTH_LOG");
        }
    }
}
