package org.qiuhua.qiuhuacustomcollect.event;


import dev.lone.itemsadder.api.Events.CustomBlockBreakEvent;
import dev.lone.itemsadder.api.Events.CustomBlockPlaceEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.qiuhua.qiuhuacustomcollect.collect.BlockCollect;

public class PlayerListener implements Listener {


    @EventHandler
    public void onCustomBlockBreak(CustomBlockBreakEvent event){
        BlockCollect.blockBreak(event);
    }

    @EventHandler
    public void onCustomBlockPlaceEvent(CustomBlockPlaceEvent event){
        BlockCollect.blockPlace(event);
    }


}
