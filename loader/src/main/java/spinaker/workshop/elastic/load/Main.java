package spinaker.workshop.elastic.load;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;
import java.util.Scanner;

/**
 * http://127.0.0.1:9200/_cat/indices?v
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        Scanner in = new Scanner(System.in);

        System.out.print("Enter elastic hostname [default: localhost]: ");
        String hostName = in.nextLine();
        if(hostName.isEmpty()) hostName = "localhost";

        System.out.print("Enter elastic port [default: 9200]: ");
        String portNumber = in.nextLine();
        int port = 9200;
        if(!portNumber.isEmpty()) port = Integer.parseInt(portNumber);

        System.out.print("Enter index name: ");
        String index = in.nextLine();

        System.out.print("Enter user name [default: elastic]: ");
        String user = in.nextLine();
        if(user.isEmpty()) user = "elastic";

        System.out.print("Enter user password: ");
        String password = in.nextLine();

        System.out.print("Enter loader [EUPARL | LOG]: ");
        String loaderName = in.nextLine();

        System.out.print("Enter pipeline [default null]: ");
        String pipeline = in.nextLine();

        System.out.print("Enter data directory: ");
        String dataDir = in.nextLine();

        in.close();

        ElasticLoader loader = new ElasticLoader(
                hostName, port, index, user, password
        );

        for (File file : Objects.requireNonNull(new File(dataDir).listFiles())) {
            ContentParser.getInstance(ContentParser.ContentType.valueOf(loaderName)).parse(file).forEach(document -> {
                try {
                    loader.store(document, pipeline.isEmpty() ? "null" : pipeline);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        loader.close();
    }
}
