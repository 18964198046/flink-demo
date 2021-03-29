package clickhouse.question4;

import org.apache.kudu.ColumnSchema;
import org.apache.kudu.Schema;
import org.apache.kudu.Type;
import org.apache.kudu.client.CreateTableOptions;
import org.apache.kudu.client.KuduClient;
import org.apache.kudu.client.KuduException;

import java.util.ArrayList;

public class CreateTable {

    public static void main(String[] args) {

        String masterAddresses = "linux121";
        KuduClient.KuduClientBuilder kuduClientBuilder = new KuduClient.KuduClientBuilder(masterAddresses);

        ArrayList<String> columnRules = new ArrayList<>();
        columnRules.add("id");

        CreateTableOptions options = new CreateTableOptions(); //设定当前的副本数量为1
        options.setNumReplicas(1);
        options.addHashPartitions(columnRules,3);

        ArrayList<ColumnSchema> columnSchemas = new ArrayList<>();
        ColumnSchema id = new ColumnSchema.ColumnSchemaBuilder("id", Type.INT32).key(true).build();
        ColumnSchema name = new ColumnSchema.ColumnSchemaBuilder("name", Type.STRING).key(false).build();
        columnSchemas.add(id);
        columnSchemas.add(name);

        String tableName = "student";
        Schema schema = new Schema(columnSchemas);
        KuduClient client = kuduClientBuilder.build();
        try {
            client.createTable(tableName,schema,options);
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
