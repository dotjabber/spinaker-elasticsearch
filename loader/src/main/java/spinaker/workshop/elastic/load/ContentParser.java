package spinaker.workshop.elastic.load;

import spinaker.workshop.elastic.common.ElasticDocument;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public abstract class ContentParser {
    public enum ContentType {
        EUPARL(new EuparlContentParser()),
        LOG(new LogContentParser());

        private final ContentParser contentParser;
        ContentType(ContentParser contentParser) {
            this.contentParser = contentParser;
        }
    }

    public static ContentParser getInstance(ContentType type) {
        return type.contentParser;
    }

    public abstract List<ElasticDocument> parse(File file) throws IOException, ParseException;
}
