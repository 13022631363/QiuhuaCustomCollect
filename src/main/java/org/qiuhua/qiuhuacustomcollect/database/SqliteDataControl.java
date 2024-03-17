package org.qiuhua.qiuhuacustomcollect.database;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.qiuhua.qiuhuacustomcollect.Config;
import org.qiuhua.qiuhuacustomcollect.Main;
import org.qiuhua.qiuhuacustomcollect.Tool;
import org.qiuhua.qiuhuacustomcollect.data.BlockDataManager;
import org.qiuhua.qiuhuacustomcollect.data.RefersBlockData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class SqliteDataControl {

    public static void task(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                SqliteControl.dropTable();
                SqliteControl.createTable();
                Main.getMainPlugin().getLogger().info("开始存储记录的方块数据....");
                addBlockData();
                Main.getMainPlugin().getLogger().info("开始存储已被破坏的方块数据....");
                addBlockTime();

            }
        },Config.getInt("SqliteStorage") * 20L * 60L, Config.getInt("SqliteStorage") * 20L * 60L);
    }




    //存储记录的方块数据
    public static void addBlockData(){
        ConcurrentHashMap<String, CopyOnWriteArrayList<Location>> allRecBlockData = BlockDataManager.getAllRecBlockData();//全部被记录的方块
        SqliteControl.connect();
        String sql = "INSERT INTO block_cache (location, blockId, time) VALUES (?, ?, ?)";
        try (var connection = SqliteControl.getConnection();var statement = connection.prepareStatement(sql))
        {

            for (var entry : allRecBlockData.entrySet())
            {
                var locations = entry.getValue();
                for (var location: locations)
                {
                    var serializeLocation = Tool.serializeLocation(location);
                    statement.setString(1, serializeLocation);
                    statement.setString(2, entry.getKey());
                    statement.setLong(3, -1L);
                    statement.addBatch();
                }
            }
            connection.commit();
            statement.executeBatch();
        }catch (SQLException e)
        {
            Bukkit.getLogger().severe(e.getMessage());
        }

    }

    //存储已被破坏的方块数据
    public static void addBlockTime(){
        ConcurrentHashMap<String, CopyOnWriteArrayList<RefersBlockData>> allRefersBlockData = BlockDataManager.getAllRefersBlockData();//全部待刷新方块数据
        for(String blockId : allRefersBlockData.keySet()){
            CopyOnWriteArrayList<RefersBlockData> dataList = allRefersBlockData.get(blockId);
            if(dataList.isEmpty()){
                continue;
            }
            for(RefersBlockData data : dataList){
                String loc = Tool.serializeLocation(data.getLocation());
                Long time = data.getRefreshTime();
                SqliteControl.insertTimeAndBlockIdByLocation(loc, blockId, time);
            }
        }
    }

    //加载存储的数据
    public static void loadBlockData(){
        List<String> locList = SqliteControl.getAllLocations();
        Main.getMainPlugin().getLogger().info("开始加载方块数据....");
        for(String strLoc : locList){
            Map<String,Long> mapData = SqliteControl.retrieveDataByLocation(strLoc);
            Location location = Tool.deserializeLocation(strLoc);
            for(String blockId : mapData.keySet()){
                long time = mapData.get(blockId);
                ConcurrentHashMap<String, CopyOnWriteArrayList<Location>> map = BlockDataManager.getAllRecBlockData();
                if(map.containsKey(blockId)){
                    //获取这个方块记录的位置信息map
                    map.get(blockId).add(location);
                }else {
                    //如果是第一次记录
                    CopyOnWriteArrayList<Location> a = new CopyOnWriteArrayList<>();
                    a.add(location);
                    map.put(blockId, a);
                }
                if(time != -1){
                    RefersBlockData data = new RefersBlockData(location, time); //构造数据
                    BlockDataManager.addRefersBlockData(blockId, data);
                }
            }
        }

    }



}
