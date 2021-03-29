package clickhouse.question4;

import org.apache.kudu.client.*;

public class UpdateTable {

    public static void main(String[] args) {
        KuduClient client = new KuduClient.KuduClientBuilder("linux122").build();
        try {
            KuduTable stuTable = client.openTable("student");
            KuduSession kuduSession = client.newSession();
            kuduSession.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
            Update update = stuTable.newUpdate();
            update.getRow().addInt("id",1);
            update.getRow().addString("name","yufangxing");
            kuduSession.apply(update);
            kuduSession.close();
        } catch (KuduException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (KuduException e) {
                e.printStackTrace();
            }
        }
    }
}

