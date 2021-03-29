package clickhouse.question4;

import org.apache.kudu.client.*;

public class SelectTable {

    public static void main(String[] args) {
        KuduClient.KuduClientBuilder clientBuilder = new KuduClient.KuduClientBuilder("linux122");
        KuduClient client = clientBuilder.build();
        try {
            KuduTable stuTable = client.openTable("student");
            KuduScanner scanner = client.newScannerBuilder(stuTable).build();
            while(scanner.hasMoreRows()) {
                for(RowResult result : scanner.nextRows()) {
                    int id = result.getInt("id");
                    String name = result.getString("name");
                    System.out.println(id + name);
                }
            }
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
