package org.qiuhua.qiuhuacustomcollect.model;

import org.bukkit.entity.Player;

public class BlockRec {
    //开启方块记录模式
    private static Boolean blockRec = false;

    //开启方块记录模式
    public static void setBlockRec(Player player){
        if(blockRec){
            blockRec = false;
            player.sendMessage("[自定义采集] 已关闭记录模式");
        }else {
            blockRec = true;
            player.sendMessage("[自定义采集] 已开启记录模式");
        }

    }

    public static Boolean isBlockRec(){
        return blockRec;
    }
}
