package com.gitee.qdbp.db.utils;

import com.gitee.qdbp.able.utils.VerifyTools;
import com.gitee.qdbp.db.condition.Paging;
import com.gitee.qdbp.db.sql.SqlBuffer;
import com.joyintech.util.ResourceUtil;

/**
 * 数据库方言
 *
 * @author zhaohuihua
 * @version 181228
 */
public class DbDialect {

    // 参考org.hibernate.dialect.pagination.LimitHelper类及LimitHandler的子类

    /** 处理分页 **/
    public static void processPagingSql(SqlBuffer buffer, Paging paging) {
        String dbType = ResourceUtil.getInstance("platform").getDbType();
        if (VerifyTools.isBlank(dbType) || dbType.equalsIgnoreCase("oracle")) {
            processPagingForOracle(buffer, paging);
        } else if (dbType.equalsIgnoreCase("mysql")) {
            processPagingForMysql(buffer, paging);
        } else if (dbType.equalsIgnoreCase("db2")) {
            processPagingForDb2(buffer, paging);
        } else if (dbType.equalsIgnoreCase("h2")) {
            processPagingForH2(buffer, paging);
        } else if (dbType.equalsIgnoreCase("PostgreSQL")) {
            processPagingForPostgreSql(buffer, paging);
        } else {
            throw new UnsupportedOperationException("Unsupported db type: " + dbType);
        }
    }

    private static void processPagingForMysql(SqlBuffer buffer, Paging paging) {
        if (paging.getStart() <= 0) {
            // limit {rows}
            buffer.append(' ').append("LIMIT").append(' ').addVariable("rows", paging.getRows());
        } else {
            // limit {start}, {rows}
            buffer.append(' ').append("LIMIT").append(' ');
            buffer.addVariable("start", paging.getStart()).append(',').addVariable("rows", paging.getRows());
        }
    }

    private static void processPagingForH2(SqlBuffer buffer, Paging paging) {
        // 逻辑参考自: org.hibernate.dialect.H2Dialect
        if (paging.getStart() <= 0) {
            // limit {rows}
            buffer.append(' ').append("LIMIT").append(' ').addVariable("rows", paging.getRows());
        } else {
            // limit {start} offset {rows}
            buffer.append(' ').append("LIMIT").append(' ');
            buffer.addVariable("start", paging.getStart());
            buffer.append(" OFFSET ").addVariable("rows", paging.getRows());
        }
    }

    private static void processPagingForPostgreSql(SqlBuffer buffer, Paging paging) {
        // 逻辑参考自: org.hibernate.dialect.PostgreSQLDialect
        if (paging.getStart() <= 0) {
            // limit {rows}
            buffer.append(' ').append("LIMIT").append(' ').addVariable("rows", paging.getRows());
        } else {
            // limit {start} offset {rows}
            buffer.append(' ').append("LIMIT").append(' ');
            buffer.addVariable("start", paging.getStart());
            buffer.append(" OFFSET ").addVariable("rows", paging.getRows());
        }
    }

    private static void processPagingForOracle(SqlBuffer buffer, Paging paging) {
        // 逻辑参考自: org.hibernate.dialect.OracleDialect
        if (paging.getStart() <= 0) {
            // SELECT T_T.* FROM ( {sql} ) T_T WHERE ROWNUM <= {end}
            buffer.prepend("SELECT T_T.* FROM ( ");
            buffer.append(") T_T WHERE ROWNUM <= ");
            buffer.addVariable("end", paging.getEnd());
        } else {
            // SELECT * FROM (
            //     SELECT ROWNUM R_N, T_T.* FROM ( {sql} ) T_T WHERE ROWNUM <= {end}
            // ) WHERE R_N > {start}
            buffer.prepend("SELECT * FROM ( SELECT ROWNUM R_N, T_T.* FROM ( ");
            buffer.append(") T_T WHERE ROWNUM <= ");
            buffer.addVariable("end", paging.getEnd());
            buffer.append(") WHERE R_N > ");
            buffer.addVariable("start", paging.getStart());
        }
    }

    private static void processPagingForDb2(SqlBuffer buffer, Paging paging) {
        // 逻辑参考自: org.hibernate.dialect.DB2Dialect
        if (paging.getStart() <= 0) {
            // FETCH FIRST {end} ROWS ONLY
            buffer.append(' ').append("FETCH FIRST").append(' ');
            buffer.addVariable("end", paging.getEnd());
            buffer.append(' ').append("ROWS ONLY");
        } else {
            // SELECT * FROM (
            //     SELECT T_T.*, ROWNUMBER() OVER(ORDER BY ORDER OF T_T) AS R_N 
            //     FROM ( {sql} FETCH FIRST {end} ROWS ONLY ) AS T_T
            // )
            // WHERE R_N > {start} ORDER BY R_N
            buffer.prepend("SELECT * FROM ( SELECT T_T.*, ROWNUMBER() OVER(ORDER BY ORDER OF T_T) AS R_N FROM ( ");
            buffer.append(' ').append("FETCH FIRST").append(' ');
            buffer.addVariable("end", paging.getEnd());
            buffer.append(' ').append("ROWS ONLY").append(' ').append(") AS T_T )");
            buffer.append(' ').append("WHERE").append(' ').append("R_N > ");
            buffer.addVariable("start", paging.getStart());
            buffer.append(' ').append("ORDER BY").append(' ').append("R_N");
        }
    }

    public static String toPinyinOrderByExpression(String columnName) {
        String dbType = ResourceUtil.getInstance("platform").getDbType();
        if (VerifyTools.isBlank(dbType) || dbType.equalsIgnoreCase("oracle")) {
            return columnName; // 系统默认排序方式就是拼音: "NLSSORT(" + columnName + ",'NLS_SORT=SCHINESE_PINYIN_M')";
        } else if (dbType.equalsIgnoreCase("mysql")) {
            return "CONVERT(" + columnName + " USING GBK)";
        } else {
            return columnName;
        }
    }

    public static SqlBuffer toLikeSql(Object fieldValue) {
        return toLikeSql(null, fieldValue);
    }

    public static SqlBuffer toLikeSql(String fieldName, Object fieldValue) {
        // TODO chooseEscapeChar
        String dbType = ResourceUtil.getInstance("platform").getDbType();
        SqlBuffer buffer = new SqlBuffer();
        buffer.append("LIKE", ' ');
        if (VerifyTools.isBlank(dbType) || dbType.equalsIgnoreCase("oracle")) {
            buffer.append("('%'||").addVariable(fieldName, fieldValue).append("||'%')");
        } else if (dbType.equalsIgnoreCase("db2")) {
            buffer.append("('%'||").addVariable(fieldName, fieldValue).append("||'%')");
        } else if (dbType.equalsIgnoreCase("PostgreSQL")) {
            buffer.append("('%'||").addVariable(fieldName, fieldValue).append("||'%')");
        } else if (dbType.equalsIgnoreCase("mysql")) {
            buffer.append("CONCAT('%',").addVariable(fieldName, fieldValue).append(",'%')");
        } else if (dbType.equalsIgnoreCase("h2")) {
            buffer.append("CONCAT('%',").addVariable(fieldName, fieldValue).append(",'%')");
        } else if (dbType.equalsIgnoreCase("SqlServer")) {
            buffer.append("('%'+").addVariable(fieldName, fieldValue).append("+'%')");
        } else {
            throw new UnsupportedOperationException("Unsupported db type: " + dbType);
        }
        return buffer;
    }

    public static SqlBuffer toStartsWithSql(Object fieldValue) {
        return toStartsWithSql(null, fieldValue);
    }

    public static SqlBuffer toStartsWithSql(String fieldName, Object fieldValue) {
        String dbType = ResourceUtil.getInstance("platform").getDbType();
        SqlBuffer buffer = new SqlBuffer();
        buffer.append("LIKE", ' ');
        if (VerifyTools.isBlank(dbType) || dbType.equalsIgnoreCase("oracle")) {
            buffer.append('(').addVariable(fieldName, fieldValue).append("||'%')");
        } else if (dbType.equalsIgnoreCase("db2")) {
            buffer.append('(').addVariable(fieldName, fieldValue).append("||'%')");
        } else if (dbType.equalsIgnoreCase("PostgreSQL")) {
            buffer.append('(').addVariable(fieldName, fieldValue).append("||'%')");
        } else if (dbType.equalsIgnoreCase("mysql")) {
            buffer.append("CONCAT(").addVariable(fieldName, fieldValue).append(",'%')");
        } else if (dbType.equalsIgnoreCase("h2")) {
            buffer.append("CONCAT(").addVariable(fieldName, fieldValue).append(",'%')");
        } else if (dbType.equalsIgnoreCase("SqlServer")) {
            buffer.append('(').addVariable(fieldName, fieldValue).append("+'%')");
        } else {
            throw new UnsupportedOperationException("Unsupported db type: " + dbType);
        }
        return buffer;
    }

    public static SqlBuffer toEndsWithSql(Object fieldValue) {
        return toEndsWithSql(null, fieldValue);

    }

    public static SqlBuffer toEndsWithSql(String fieldName, Object fieldValue) {
        String dbType = ResourceUtil.getInstance("platform").getDbType();
        SqlBuffer buffer = new SqlBuffer();
        buffer.append("LIKE", ' ');
        if (VerifyTools.isBlank(dbType) || dbType.equalsIgnoreCase("oracle")) {
            buffer.append("('%'||").addVariable(fieldName, fieldValue).append(")");
        } else if (dbType.equalsIgnoreCase("db2")) {
            buffer.append("('%'||").addVariable(fieldName, fieldValue).append(")");
        } else if (dbType.equalsIgnoreCase("PostgreSQL")) {
            buffer.append("('%'||").addVariable(fieldName, fieldValue).append(")");
        } else if (dbType.equalsIgnoreCase("mysql")) {
            buffer.append("CONCAT('%',").addVariable(fieldName, fieldValue).append(")");
        } else if (dbType.equalsIgnoreCase("h2")) {
            buffer.append("CONCAT('%',").addVariable(fieldName, fieldValue).append(")");
        } else if (dbType.equalsIgnoreCase("SqlServer")) {
            buffer.append("('%'+").addVariable(fieldName, fieldValue).append(")");
        } else {
            throw new UnsupportedOperationException("Unsupported db type: " + dbType);
        }
        return buffer;
    }
}
