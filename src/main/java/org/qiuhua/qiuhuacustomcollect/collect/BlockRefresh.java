package org.qiuhua.qiuhuacustomcollect.collect;


import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Bukkit;
import org.qiuhua.qiuhuacustomcollect.Config;
import org.qiuhua.qiuhuacustomcollect.Main;
import org.qiuhua.qiuhuacustomcollect.data.BlockDataManager;
import org.qiuhua.qiuhuacustomcollect.data.RefersBlockData;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockRefresh {

    public static void task(){
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                ConcurrentHashMap<String, CopyOnWriteArrayList<RefersBlockData>> map = BlockDataManager.getAllRefersBlockData();
                Main.getMainPlugin().getLogger().info("开始尝试生成方块.......");
                long start = System.currentTimeMillis();
                for(String blockId : map.keySet()){
                    List<RefersBlockData> dataList = map.get(blockId);
                    if(dataList.isEmpty()){
                        continue;
                    }
                    int am = 0;
                    for (RefersBlockData data : dataList){
                        if (data.isRefreshBlock()){
                            CustomBlock customBlock = CustomBlock.getInstance(blockId);
                            if (customBlock != null){
                                Bukkit.getScheduler().runTask(Main.getMainPlugin(), new Runnable() {
                                    public void run() {
                                        customBlock.place(data.getLocation());
                                    }
                                });
                            }
                            am = am +1;
                            dataList.remove(data);
                        }
                    }
                    Main.getMainPlugin().getLogger().info("方块 " + blockId + " => 尝试生成 " + am + " 个 剩余 " + dataList.size() + " 个未生成");
                }
                long end = System.currentTimeMillis();
                Main.getMainPlugin().getLogger().info("更新结束 耗时 => " + (end - start) + " ms");
            }
        },Config.getInt("BlockRefresh") * 20L * 60L, Config.getInt("BlockRefresh") * 20L * 60L);
    }









}
