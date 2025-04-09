
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

public class DingTalkBot {

    // 钉钉机器人配置
    private static final String SECRET = "SECa9f066ee64cc67e77b72afb40c211d8e33c5ae658bfc67b397a2747424dfa682"; // 替换为您的密钥
    private static final String ACCESS_TOKEN = "8c7805b885db2718b94a95a7b314dbaeff947b3c53d5c607901bdb54b0c4f8fc"; // 替换为您的 access_token

    public static void main(String[] args) {
        try {
            // 生成时间戳和签名
            long timestamp = Instant.now().toEpochMilli();
            String stringToSign = timestamp + "\n" + SECRET;
            String sign = calculateSignature(stringToSign, SECRET);

            // 构造最终 Webhook URL
            String webhookUrl = String.format(
                "https://oapi.dingtalk.com/robot/send?access_token=%s&timestamp=%d&sign=%s",
                ACCESS_TOKEN, timestamp, URLEncoder.encode(sign, StandardCharsets.UTF_8)
            );

            // 发送消息
            sendMessage(webhookUrl, "Hello, this is a signed message from Java!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 计算签名
    private static String calculateSignature(String stringToSign, String secret) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmacData);
    }

    // 发送消息
    private static void sendMessage(String webhookUrl, String message) throws Exception {
        URL url = new URL(webhookUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        // 构造请求体
        String jsonPayload = String.format(
            "{\"msgtype\": \"text\", \"text\": {\"content\": \"%s\"}}",
            message
        );

        // 发送请求
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
