package org.qiuhua.qiuhuacustomcollect;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Config {
    private static final Plugin main = Main.getMainPlugin();
    private static FileConfiguration config;

    private static final Map<String, Long> customMap = new HashMap<>();



    public static void saveAllConfig(){
        //创建一个插件文件夹路径为基础的 并追加下一层。所以此时的文件应该是Config.yml
        //exists 代表是否存在
        if (!(new File(main.getDataFolder() ,"\\Config.yml").exists()))
            main.saveResource("Config.yml", false);
    }

    //获取全部可刷新的采集物列表
    public static void getCustomList(){
        // 获取节点的ConfigurationSection对象
        ConfigurationSection customListSection = config.getConfigurationSection("CustomList");
        // 获取节点下的所有键
        if (customListSection != null) {
            Set<String> keys = customListSection.getKeys(false);
            // 打印输出所有键
            for(String key : keys) {
                Long value = customListSection.getLong(key);
                customMap.put(key, value);
                Main.getMainPlugin().getLogger().info("记录采集物 => " + key + " 时间 " + value + " 分钟");
            }
        }

    }

    public static Map<String, Long> getCustomListMap(){
        return customMap;
    }

    public static YamlConfiguration load (File file)
    {
        return YamlConfiguration.loadConfiguration(file);
    }

    public static void reload () {
        config = load(new File(main.getDataFolder (),"\\config.yml"));
        getCustomList();
    }

    public static String getStr(String val){
        return config.getString(val);
    }

    public static int getInt(String val){
        return config.getInt(val);
    }
}
