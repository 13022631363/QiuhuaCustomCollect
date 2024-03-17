package org.qiuhua.qiuhuacustomcollect;


import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.net.*;

public class Tool {

    private static final String deviceCode = DeviceCodeExample();


    //序列化坐标信息
    public static String serializeLocation(Location loc){
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    //反序列化坐标信息
    public static Location deserializeLocation(String str){
        String[] parts = str.split(",");
        String world = parts[0];
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double z = Double.parseDouble(parts[3]);
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    public static String sendLogin(){
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder("http://111.230.40.162:13554/api.php");
            uriBuilder.setParameter("api", "kmlogon");
            uriBuilder.setParameter("app","10000");
            uriBuilder.setParameter("kami", Config.getStr("Key"));
            uriBuilder.setParameter("markcode", deviceCode);
            uriBuilder.setParameter("t", String.valueOf(System.currentTimeMillis()));
            URI uri = uriBuilder.build();
            HttpGet request = new HttpGet(uri);
            request.setHeader("Content-Type", "application/json");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                int index = responseBody.indexOf("{");
                if (index != -1) {
                    return responseBody.substring(index);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String DeviceCodeExample() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = networkInterface.getHardwareAddress();

            StringBuilder sb = new StringBuilder();
            for (byte b : hardwareAddress) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
        }
        return null;
    }


}
