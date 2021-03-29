package clickhouse.question4;

import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;

public class DeleteTable {

    public static void main(String[] args) {
        String masterAddress = "172.16.97.71";
        KuduClient client = new KuduClient.KuduClientBuilder(masterAddress).defaultSocketReadTimeoutMs(6000).build();
        try {
            client.deleteTable("student");
        } catch (KuduException e) {
            e.printStackTrace();
        }finally {
            try {
                client.close();
            } catch (KuduException e) {
                e.printStackTrace(); }
        }
    }

}
