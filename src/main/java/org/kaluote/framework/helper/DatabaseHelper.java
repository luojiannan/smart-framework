package org.kaluote.framework.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.kaluote.framework.util.PropsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author ljn
 * @date 2018/6/1.
 */
public final class DatabaseHelper {

    private static final ThreadLocal<Connection> CONNECTION_HOLDER ;

    private static final QueryRunner QUERY_RUNNER ;

    private static final BasicDataSource DATA_SOURCE;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHelper.class);

    private static final String DRIVER;

    private static final String URL;

    private static final String USERNAME;

    private static final String PASSWORD;

    static {
        CONNECTION_HOLDER = new ThreadLocal<Connection>();
        QUERY_RUNNER = new QueryRunner();
        Properties config = PropsUtil.loadProps("config.properties");
        DRIVER = config.getProperty("jdbc.driver");
        URL = config.getProperty("jdbc.url");
        USERNAME = config.getProperty("jdbc.username");
        PASSWORD = config.getProperty("jdbc.password");

        DATA_SOURCE = new BasicDataSource();
        DATA_SOURCE.setDriverClassName(DRIVER);
        DATA_SOURCE.setUrl(URL);
        DATA_SOURCE.setUsername(USERNAME);
        DATA_SOURCE.setPassword(PASSWORD);
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            logger.error("can not load jdbc driver",e);
        }
    }

    public static <T> List<T> queryEntityList(Class<T> entity,String sql,Object... params) {
        List<T> entityList;
        try {
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entity),params);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error("query entity list failure",e);
            throw new RuntimeException(e);
        }
//        finally {
//            closeConnection();
//        }
        return entityList;
    }


    public static Connection getConnection() {
        Connection conn = CONNECTION_HOLDER.get();
        if (conn == null) {
            try {
                conn = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("can not connection database",e);
            }finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }

    public static void closeConnection(){
        Connection conn = CONNECTION_HOLDER.get();
         if (conn != null) {
             try {
                 conn.close();
             } catch (SQLException e) {
                 e.printStackTrace();
                 logger.error("close database connection failure",e);
             }
         }
    }

    public static <T> T queryEntity(Class<T> entityClass,String sql,Object... params) {
        T entity = null;
        try {
            Connection conn = getConnection();
            entity = QUERY_RUNNER.query(conn,sql,new BeanHandler<T>(entityClass),params);
        } catch (SQLException e) {
            logger.error("query entity failure",e);
        }
//        finally {
//            closeConnection();
//        }
        return entity;
    }

    public static List<Map<String,Object>> executeQuery(String sql, Object... params) {
        List<Map<String,Object>> result;
        Connection conn = getConnection();
        try {
            result = QUERY_RUNNER.query(conn, sql, new MapListHandler(), params);
        } catch (SQLException e) {
            logger.error("execute query failure",e);
            throw new RuntimeException(e);
        }
        return result;
    }

    public static int executeUpdate(String sql,Object... params) {
        int rows = 0;
        Connection conn = getConnection();
        try {
            rows = QUERY_RUNNER.update(conn,sql,params);
        } catch (SQLException e) {
            logger.error("execute update failure",e);
            throw new RuntimeException(e);
        }
//        finally {
//            closeConnection();
//        }
        return rows;
    }

    public static <T> boolean insertEntity(Class<T> entityClass,Map<String,Object> map) {
        if (map.isEmpty()) {
            logger.error("can not insert entity:map is empty");
            return false;
        }
        String sql = "INSERT INTO "+ getTableName(entityClass);
        StringBuilder sb = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (String fieldName : map.keySet()) {
            sb.append(fieldName).append(", ");
            values.append("?, ");
        }
        sb.replace(sb.lastIndexOf(", "),sb.length(), ")");
        values.replace(values.lastIndexOf(", "),values.length(), ")");
        sql += sb + "VALUES" + values;
        Object[] params = map.values().toArray();
        return executeUpdate(sql,params) == 1;
    }

    public static <T> boolean updateEntity(Class<T> entityClass,long id,Map<String,Object> map) {
        if (map.isEmpty()) {
            logger.error("can not update entity:map is empty");
            return false;
        }
        String sql = "UPDATE "+ getTableName(entityClass) +" SET ";
        StringBuilder columns = new StringBuilder();
        for (String  fieldName : map.keySet()) {
            columns.append(fieldName).append("= ?, ");
        }
        sql += columns.substring(0,columns.lastIndexOf(", ")) + " WHERE id = ?";
        List<Object> paramList = new ArrayList<Object>();
        paramList.addAll(map.values());
        paramList.add(id);
        Object[] params = paramList.toArray();
        return executeUpdate(sql,params) == 1 ;
    }

    public static <T> boolean deleteEntity(Class<T> entityClass,long id) {
        String sql = "DELETE FROM "+ getTableName(entityClass) +" WHERE id = ?";
        return executeUpdate(sql,id) == 1;
    }

    private static String getTableName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

    public static void executeSqlFile(String fileName) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String sql ;
            while ((sql = reader.readLine()) != null) {
                executeUpdate(sql);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("execute sql file failure ",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启事务
     */
    public static void beginTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try{
                conn.setAutoCommit(false);
            }catch (SQLException e) {
                logger.error("begin transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try {
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                logger.error("commit transaction failure",e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction() {
        Connection conn = getConnection();
        if (conn != null) {
            try{
                conn.rollback();
                conn.close();
            }catch (SQLException e) {
                logger.error("rollback transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }




}
