package org.qiuhua.qiuhuacustomcollect.collect;


import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.qiuhua.qiuhuacustomcollect.Config;
import org.qiuhua.qiuhuacustomcollect.data.BlockDataManager;
import org.qiuhua.qiuhuacustomcollect.data.RefersBlockData;
import org.qiuhua.qiuhuacustomcollect.model.BlockRec;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockCollect {
    //方块被破坏
    public static void blockBreak(CustomBlockBreakEvent event){
        Player player = event.getPlayer();
        //获取方块id
        String blockId = event.getNamespacedID();
        //获取方块的位置
        Location loc = event.getBlock().getLocation();
        if(event.isCancelled()){
            return;
        }
        ConcurrentHashMap<String, CopyOnWriteArrayList<Location>> map = BlockDataManager.getAllRecBlockData();
        if(!BlockRec.isBlockRec() && map.containsKey(blockId)){
            //未开启记录模式 记录这次破坏的方块
            Long newTime = Config.getCustomListMap().get(blockId); //获取刷新时间
            if(newTime == null){
                return;
            }
            //查找这个位置的方块是否存在
            CopyOnWriteArrayList<Location> list = map.get(blockId);
            int index = list.indexOf(loc);  // 查找本次破坏方块的位置
            if(index == -1){
                return;
            }
            long time = System.currentTimeMillis() + (newTime * 60 * 1000);
            RefersBlockData data = new RefersBlockData(loc, time); //构造数据
            BlockDataManager.addRefersBlockData(blockId, data);
//            player.sendMessage("等待刷新物品id " + blockId);
//            player.sendMessage("下次刷新时间 " + newTime + " 分钟后");
            return;
        }
        //在allRecBlockData查找这个方块id是否有过记录
        if(map.containsKey(blockId)){
            player.sendMessage("====自定义采集====");
            CopyOnWriteArrayList<Location> list = map.get(blockId);
            int index = list.indexOf(loc);  // 查找本次破坏方块的位置
            if(index == -1){
                player.sendMessage("该方块不属于记录的方块" + blockId);
            }else {
                list.remove(index);
                player.sendMessage("成功删除方块 " + blockId);
            }
            player.sendMessage("位置 世界: " + loc.getWorld().getName() + " X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());
            player.sendMessage("该方块记录数量 " + map.get(blockId).size());
            player.sendMessage("该方块设置的刷新时间 " + Config.getCustomListMap().get(blockId) + " 分钟");
            if(list.isEmpty()){
                map.remove(blockId);
                player.sendMessage("该方块记录数量为 0 已清理相关存储数据");
            }
            player.sendMessage("===============");
        }

    }

    //方块被放置 记录这个方块信息
    public static void blockPlace(CustomBlockPlaceEvent event){
        Player player = event.getPlayer();
        if(!BlockRec.isBlockRec()){
            return;
        }
        //如果改事件被取消 则不记录
        if(event.isCancelled()){
            player.sendMessage("本次记录被取消");
            return;
        }
        //获取方块id
        String blockId = event.getNamespacedID();
        //获取方块的位置
        Location loc = event.getBlock().getLocation();

        //在allRecBlockData查找这个方块id是否有过记录
        ConcurrentHashMap<String, CopyOnWriteArrayList<Location>> map = BlockDataManager.getAllRecBlockData();
        player.sendMessage("====自定义采集====");
        if(map.containsKey(blockId)){
            //获取这个方块记录的位置信息map
            player.sendMessage("成功记录方块 " + blockId);
            map.get(blockId).add(loc);
        }else {
            //如果是第一次记录
            CopyOnWriteArrayList<Location> locList = new CopyOnWriteArrayList<>();
            locList.add(loc);
            map.put(blockId, locList);
            player.sendMessage("成功新增方块类型 " + blockId);
        }

        player.sendMessage("位置 世界: " + loc.getWorld().getName() + " X: " + loc.getBlockX() + " Y: " + loc.getBlockY() + " Z: " + loc.getBlockZ());
        player.sendMessage("该方块记录数量 " + map.get(blockId).size());
        player.sendMessage("该方块设置的刷新时间 " + Config.getCustomListMap().get(blockId) + " 分钟");
        player.sendMessage("===============");
    }

}
