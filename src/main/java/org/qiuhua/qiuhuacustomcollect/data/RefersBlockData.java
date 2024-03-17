package org.qiuhua.qiuhuacustomcollect.data;


import org.bukkit.Location;

//要刷新的方块数据
public class RefersBlockData {
    private Location location; //位置
    private Long refreshTime; //刷新时间




    public RefersBlockData(Location location, Long time)
    {
        this.location = location;
        this.refreshTime = time;
    }

    public Location getLocation(){
        return this.location;
    }

    public Long getRefreshTime ()
    {
        return this.refreshTime;
    }

    public void setLocation (Location location)
    {
        this.location = location;
    }

    public void setRefreshTime (long refreshTime)
    {
        this.refreshTime = refreshTime;
    }

    //判断是否可以刷新方块
    public boolean isRefreshBlock(){
        long currentTime = System.currentTimeMillis();  //获取时间
        //            Bukkit.getConsoleSender().sendMessage("方块刷新");
        return currentTime >= refreshTime;
    }





}
