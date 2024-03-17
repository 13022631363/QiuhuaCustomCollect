package org.qiuhua.qiuhuacustomcollect.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.qiuhua.qiuhuacustomcollect.Main;
import org.qiuhua.qiuhuacustomcollect.Tool;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SqliteControl {

    private static HikariDataSource dataSource;
    private static Connection connection;
    //创建库
    public static void loadSQLite(){
        //创建数据库文件路径
        String dbFilePath = Main.getMainPlugin().getDataFolder().getAbsolutePath() + File.separator + "database.db";
        //创建HikariCP连接池配置
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFilePath);
        config.setDriverClassName("org.sqlite.JDBC");
        dataSource = new HikariDataSource(config);
    }


    //连接数据库
    public static void connect() {
        try {
            if(connection == null){
                connection = dataSource.getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        return connection;
    }

    //创建库
    public static void createTable() {
        try {
            connect();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "block_cache", null);
            boolean tableExists = tables.next();
            if(!tableExists){
                // 表不存在，执行创建表的操作
                Statement statement = connection.createStatement();
                String sql = "CREATE TABLE block_cache (" +
                        "location TEXT PRIMARY KEY," +
                        "blockId TEXT," +
                        "time BIGINT" +
                        ")";
                statement.executeUpdate(sql);
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //删除表
    public static void dropTable() {
        String sql = "DELETE FROM block_cache";
        try{
            connect();
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //插入数据
    public static void insert(String location, String blockId) {
        String sql = "INSERT INTO block_cache (location, blockId, time) VALUES (?, ?, ?)";
        try{
            connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, location);
            statement.setString(2, blockId);
            statement.setLong(3, -1L);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insert (ConcurrentHashMap<String, CopyOnWriteArrayList<Location>> allRecBlockData)
    {
        String sql = "INSERT INTO block_cache (location, blockId, time) VALUES (?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql))
        {
            connection.setAutoCommit(false);

            allRecBlockData.forEach( (key, locations) -> {

                locations.forEach( (location)-> {
                    var serializeLocation = Tool.serializeLocation(location);
                    try
                    {
                        statement.setString(1, serializeLocation);
                        statement.setString(2, key);
                        statement.setLong(3, -1L);
                        statement.addBatch();
                    }catch (SQLException e )
                    {
                        Bukkit.getLogger().severe(e.getMessage());
                    }

                });
            });

            statement.executeBatch();
            connection.commit();
        }catch (SQLException e)
        {
            Bukkit.getLogger().severe(e.getMessage());
        }

    }




    public static void insertTimeAndBlockIdByLocation(String location,  String blockId, long time) {
        String sql = "INSERT INTO block_cache (location, blockId, time) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (location) DO UPDATE SET blockId = ?, time = ?";
        try {
            connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            // 设置插入的参数
            statement.setString(1, location);
            statement.setString(2, blockId);
            statement.setLong(3, time);
            statement.setString(4, blockId);
            statement.setLong(5, time);
            // 执行插入操作
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static Map<String, Long> retrieveDataByLocation(String location) {
        String sql = "SELECT location, blockId, time FROM block_cache WHERE location = ?";
        Map<String, Long> map = new HashMap<>();
        try{
            connect();
            PreparedStatement statement = connection.prepareStatement(sql);
            // 设置查询条件
            statement.setString(1, location);
            // 执行查询操作
            ResultSet resultSet = statement.executeQuery();
            String blockId = resultSet.getString("blockId");
            long time = resultSet.getLong("time");
            map.put(blockId,time);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public static List<String> getAllLocations() {
        List<String> locations = new ArrayList<>();
        String sql = "SELECT location FROM block_cache";
        try {
            connect();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String location = resultSet.getString("location");
                locations.add(location);
            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return locations;
    }


}
