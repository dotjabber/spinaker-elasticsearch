package pl.nlpday.workshop.elastic.load;

import java.io.File;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Objects;

/**
 * http://127.0.0.1:9200/_cat/indices?v
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

//        args = new String[] {
//                // 0         // 1    // 2      // 3       // 4                    // 5      // 6
//                "localhost", "9200", "europarl", "elastic", "DnIkEFnLIQdvZlTKl0Z9", "EUPARL", "null",
//                // 7
//                "C:\\Users\\Admin\\Downloads\\europarl (1)\\europarl (1)\\europarl\\txt\\en"
//        };

        args = new String[] {
                // 0         // 1    // 2      // 3       // 4                    // 5      // 6
                "localhost", "9200", "apachelogs", "elastic", "DnIkEFnLIQdvZlTKl0Z9", "LOG", "logpipeline",
                // 7
                "C:\\Users\\Admin\\Desktop\\Spinaker\\elastic\\http_logs"
        };

        ElasticLoader loader = new ElasticLoader(
                args[0], Integer.parseInt(args[1]), args[2], args[3], args[4]
        );

        final String pipeline = args[6];
        for (File file : Objects.requireNonNull(new File(args[7]).listFiles())) {
            ContentParser.getInstance(ContentParser.ContentType.valueOf(args[5])).parse(file).forEach(document -> {
                try {
                    loader.store(document, pipeline);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        loader.close();
    }
}
