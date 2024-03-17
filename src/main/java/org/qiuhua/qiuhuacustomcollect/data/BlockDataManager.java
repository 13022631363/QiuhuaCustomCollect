package org.qiuhua.qiuhuacustomcollect.data;

import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockDataManager {

    //全部待刷新方块数据
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<RefersBlockData>> allRefersBlockData = new ConcurrentHashMap<>();


    //全部被记录的方块
    private static final ConcurrentHashMap<String, CopyOnWriteArrayList<Location>> allRecBlockData = new ConcurrentHashMap<>();



    public static ConcurrentHashMap<String, CopyOnWriteArrayList<Location>> getAllRecBlockData(){
        return allRecBlockData;
    }

    public static ConcurrentHashMap<String, CopyOnWriteArrayList<RefersBlockData>> getAllRefersBlockData(){
        return allRefersBlockData;
    }

    public static void addRefersBlockData(String blockId, RefersBlockData data){
        if(allRefersBlockData.containsKey(blockId)){
            allRefersBlockData.get(blockId).add(data);
        }else{
            CopyOnWriteArrayList<RefersBlockData> dataList = new CopyOnWriteArrayList<>();
            dataList.add(data);
            allRefersBlockData.put(blockId, dataList);
        }
    }





}
