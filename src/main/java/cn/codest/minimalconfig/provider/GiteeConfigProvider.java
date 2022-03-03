package cn.codest.minimalconfig.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class GiteeConfigProvider implements RemoteConfigProvider {

    /**
     * Gitee config repository remote URL
     * @example https://gitee.com/api/v5/repos/codest-c/config-folder/contents/disk.properties
     */
    private final String url;

    /**
     * Gitee Access Token
     */
    private final String token;

    private OkHttpClient httpClient;

    private static final String PROPERTIES_KEY = "content";

    public GiteeConfigProvider(String url, String token) {
        this.url = url;
        this.token = token;
        // only init one connection
        this.httpClient = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(1, 5L, TimeUnit.MINUTES))
                .build();
    }

    @Override
    public Properties load() {

        Request request = new Request.Builder()
                .url(String.format("%s?access_token=%s", this.url, this.token))
                .get()
                .build();

        try (Response response = this.httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                Map<String, Object> data = new ObjectMapper().readValue(response.body().string(), Map.class);
                String config = data.get(PROPERTIES_KEY).toString();
                config = new String(Base64.getDecoder().decode(config), StandardCharsets.UTF_8.name());
                Properties properties = new Properties();
                properties.load(new StringReader(config));
                return properties;
            } else {
                System.err.println(String.format("读取Gitee配置文件发生错误[%s]", response.body().string()));
            }
        } catch (IOException e) {
            System.err.println("读取Gitee配置文件发生错误");
            e.printStackTrace();
        }

        return null;
    }

}
