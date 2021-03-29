package clickhouse.question4;

import org.apache.kudu.client.*;

public class DeleteRecord {

    public static void main(String[] args) {
        KuduClient client = new KuduClient.KuduClientBuilder("linux122").build();
        try {
            KuduTable stuTable = client.openTable("student");
            KuduSession kuduSession = client.newSession();
            kuduSession.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
            Delete delete = stuTable.newDelete();
            PartialRow row = delete.getRow();
            row.addInt("id", 1);
            kuduSession.flush();
            kuduSession.apply(delete);
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
