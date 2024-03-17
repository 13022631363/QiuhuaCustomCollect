package org.qiuhua.qiuhuacustomcollect.key;




import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.qiuhua.qiuhuacustomcollect.Config;
import org.qiuhua.qiuhuacustomcollect.Main;
import org.qiuhua.qiuhuacustomcollect.Tool;



public class Login {

    public static void main() {
        String body = Tool.sendLogin();
        if(body == null){
            Main.getMainPlugin().getLogger().warning("未知错误");
            Main.getMainPlugin().getPluginLoader().disablePlugin(Main.getMainPlugin());
//          System.out.println("未知错误");
            return;
        }
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
        int code = jsonObject.get("code").getAsInt();
        if(code != 200){
//            System.out.println(jsonObject.get("msg").getAsString());
            Main.getMainPlugin().getLogger().warning(jsonObject.get("msg").getAsString());
            Main.getMainPlugin().getPluginLoader().disablePlugin(Main.getMainPlugin());
            return;
        }
        String kami = jsonObject.getAsJsonObject("msg").get("kami").getAsString();
        if(!kami.equals(Config.getStr("Key"))){
//            System.out.println("卡密效验错误");
            Main.getMainPlugin().getLogger().warning("卡密效验错误..........");
            Main.getMainPlugin().getPluginLoader().disablePlugin(Main.getMainPlugin());
        }
        Main.getMainPlugin().getLogger().info("验证通过....插件正常加载");
//        System.out.println("验证通过");
    }






}


