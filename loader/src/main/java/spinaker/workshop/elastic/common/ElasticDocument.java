package spinaker.workshop.elastic.common;

import java.util.HashMap;

public class ElasticDocument extends HashMap<String, Object> {

    // log fields
    public static final String MESSAGE = "message";

    // euparl fields
    public static final String FILE = "file";
    public static final String CHAPTER_ID = "chapter_id";
    public static final String SPEAKER_ID = "speaker_id";
    public static final String SPEAKER_NAME = "speaker_name";
    public static final String LANGUAGE = "language";
    public static final String CONTENT = "content";
    public static final String DATE = "date";
}