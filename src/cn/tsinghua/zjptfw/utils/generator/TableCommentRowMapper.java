package cn.tsinghua.zjptfw.utils.generator;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TableCommentRowMapper implements RowMapper {

    public class TableComment{
        private String tableName;
        private String comment;

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        @Override
        public String toString() {
            return
                     tableName + '*' +
                     comment  ;
        }
    }
    @Override
    public TableComment mapRow(ResultSet resultSet, int i) throws SQLException {
        TableComment tableComment = new TableComment();
        tableComment.setTableName(resultSet.getString("table_name"));
        tableComment.setComment(resultSet.getString("comments"));
        return tableComment;
    }
}
