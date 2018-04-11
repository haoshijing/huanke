import com.huanke.iot.gateway.io.AbstractHandler;

public class TestTopic {
    public static void main(String[] args) {
        String topic = "/up/location/1";
        System.out.println(getDeviceIdFromTopic(topic));
    }
    private static String getTypeFromTopic(String topic){
        int idx = topic.lastIndexOf("/");
        String dataStr = topic.substring(0,idx);
        idx = dataStr.lastIndexOf("/");
        return dataStr.substring(idx);
    }
    protected  static Integer getDeviceIdFromTopic(String topic){
        int idx = topic.lastIndexOf("/");
        return Integer.valueOf(topic.substring(idx+1));
    }
}
