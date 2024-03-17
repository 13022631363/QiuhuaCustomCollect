package org.qiuhua.qiuhuacustomcollect;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.qiuhua.qiuhuacustomcollect.collect.BlockRefresh;
import org.qiuhua.qiuhuacustomcollect.database.SqliteControl;
import org.qiuhua.qiuhuacustomcollect.database.SqliteDataControl;
import org.qiuhua.qiuhuacustomcollect.event.PlayerListener;
import org.qiuhua.qiuhuacustomcollect.key.Login;

public class Main extends JavaPlugin {
    private static Main mainPlugin;
    public static Main getMainPlugin(){
        return mainPlugin;
    }


    //启动时运行
    @Override
    public void onEnable(){
        //设置主插件
        mainPlugin = this;
        Config.saveAllConfig();
        Config.reload();
        Login.main();
        SqliteControl.loadSQLite();
        SqliteControl.createTable();
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        //注册指令
        new QiuhuaCustomCollectCommand().register();
        //加载数据库
        SqliteDataControl.loadBlockData();
        //刷新方块的线程
        BlockRefresh.task();
        //sql存储线程
        SqliteDataControl.task();
    }


    //关闭时运行
    @Override
    public void onDisable(){
        SqliteControl.dropTable();
        SqliteControl.createTable();
        Main.getMainPlugin().getLogger().info("开始存储记录的方块数据....");
        SqliteDataControl.addBlockData();
        Main.getMainPlugin().getLogger().info("开始存储已被破坏的方块数据....");
        SqliteDataControl.addBlockTime();

    }

    //执行重载命令时运行
    @Override
    public void reloadConfig(){
        Config.reload();
        Main.getMainPlugin().getLogger().info("配置文件重载完成 已经进入刷新的采集物不会受到影响");
    }
}
