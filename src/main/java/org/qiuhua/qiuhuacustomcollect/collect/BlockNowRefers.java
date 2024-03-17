package org.qiuhua.qiuhuacustomcollect.collect;

import dev.lone.itemsadder.api.CustomBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.qiuhua.qiuhuacustomcollect.Main;
import org.qiuhua.qiuhuacustomcollect.data.BlockDataManager;
import org.qiuhua.qiuhuacustomcollect.data.RefersBlockData;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockNowRefers {
    //立即刷新全部记录的方块 不管有没有生成的倒计时
    public static void Refers(Player player){
        ConcurrentHashMap<String, CopyOnWriteArrayList<Location>> map = BlockDataManager.getAllRecBlockData();
        player.sendMessage("====自定义采集====");
        player.sendMessage("现有记录 " + map.size() + "种方块");
        long start = System.currentTimeMillis();
        for(String blockId : map.keySet()){
            int am = 0;
            CustomBlock customBlock = CustomBlock.getInstance(blockId);
            if(customBlock == null){
                continue;
            }
            List<Location> list = map.get(blockId);
            for(Location loc : list){
                Block block = player.getWorld().getBlockAt(loc);
                CustomBlock isCustomBlock = CustomBlock.byAlreadyPlaced(block);
                if(isCustomBlock == null || !isCustomBlock.getNamespacedID().equals(blockId)){
                    Bukkit.getScheduler().runTask(Main.getMainPlugin(), new Runnable() {
                        public void run() {
                            customBlock.place(loc);
                        }
                    });
                    am = am +1;
                }
            }
            player.sendMessage(blockId + " 共 " + list.size() + " 个" + " 成功 " + am + " 个");
            BlockDataManager.getAllRefersBlockData().remove(blockId);

        }
        long end = System.currentTimeMillis();
        player.sendMessage("生成结束 耗时 => " + (end - start) + " ms");
        player.sendMessage("===============");

    }

}
