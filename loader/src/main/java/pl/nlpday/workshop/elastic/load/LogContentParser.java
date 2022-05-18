package pl.nlpday.workshop.elastic.load;

import pl.nlpday.workshop.elastic.common.ElasticDocument;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogContentParser extends ContentParser {

    @Override
    public List<ElasticDocument> parse(File file) throws IOException {
        List<ElasticDocument> documents = new ArrayList<>();

        Scanner scanner = new Scanner(file, StandardCharsets.ISO_8859_1);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            ElasticDocument doc = new ElasticDocument();
            doc.put(ElasticDocument.MESSAGE, line);

            documents.add(doc);
        }

        scanner.close();

        return documents;
    }
}
