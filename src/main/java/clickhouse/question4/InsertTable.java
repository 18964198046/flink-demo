package clickhouse.question4;

import org.apache.kudu.client.*;

public class InsertTable {

    public static void main(String[] args) {
        String masterAddress = "linux122";
        KuduClient.KuduClientBuilder builder = new KuduClient.KuduClientBuilder(masterAddress);
        builder.defaultSocketReadTimeoutMs(6000);
        KuduClient client = builder.build();
        try {
            KuduTable stuTable = client.openTable("student");
            KuduSession kuduSession = client.newSession();
            kuduSession.setFlushMode(SessionConfiguration.FlushMode.MANUAL_FLUSH);
            Insert insert = stuTable.newInsert();
            insert.getRow().addInt("id", 1);
            insert.getRow().addString("name", "lucas");
            kuduSession.flush();
            kuduSession.apply(insert);
            kuduSession.close();
        } catch (KuduException e) {
            e.printStackTrace();
        }
    }

}
