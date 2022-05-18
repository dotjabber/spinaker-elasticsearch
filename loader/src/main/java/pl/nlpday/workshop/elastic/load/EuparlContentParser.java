package pl.nlpday.workshop.elastic.load;

import pl.nlpday.workshop.elastic.common.ElasticDocument;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EuparlContentParser extends ContentParser {
    private final SimpleDateFormat DF = new SimpleDateFormat("yy-MM-dd");

    private final static Pattern CHAPTER_PATTERN = Pattern.compile("<CHAPTER ID=([0-9]+)>");
    private final static Pattern SPEAKER_NOLANG_PATTERN = Pattern.compile("<SPEAKER ID=([0-9]+) NAME=\"(.+)\">");
    private final static Pattern SPEAKER_WLANG_PATTERN = Pattern.compile("<SPEAKER ID=([0-9]+) LANGUAGE=\"([A-Z]+)\" NAME=\"(.+)\">");
    private final static Pattern PAUSE_MARK_PATTERN = Pattern.compile("<P>");

    private void add(List<ElasticDocument> list, String fileName, String chapterId, String speakerId, String speakerName,
                     String language, String content, Date date) {

        ElasticDocument doc = new ElasticDocument();
        doc.put(ElasticDocument.FILE, fileName);
        doc.put(ElasticDocument.CHAPTER_ID, chapterId);
        doc.put(ElasticDocument.SPEAKER_ID, speakerId);
        doc.put(ElasticDocument.SPEAKER_NAME, speakerName);
        doc.put(ElasticDocument.LANGUAGE, language);
        doc.put(ElasticDocument.CONTENT, content);
        doc.put(ElasticDocument.DATE, date);
        list.add(doc);
    }

    public List<ElasticDocument> parse(File file) throws IOException, ParseException {
        List<ElasticDocument> documents = new ArrayList<>();

        // parse file name and get date
        String dateStr = file.getName().replace("ep-", "").replace(".txt", "");
        Date date = DF.parse(dateStr);

        String chapterId = null;
        String speakerId = null;
        String language = "EN";
        String speakerName = null;
        StringBuilder content = new StringBuilder();

        Scanner scanner = new Scanner(file, StandardCharsets.ISO_8859_1);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if(line.trim().length() > 0) {
                if(CHAPTER_PATTERN.matcher(line).matches()) {
                    // send previous data
                    if(content.length() > 0) {
                        add(documents, file.getName(), chapterId, speakerId, speakerName, language, content.toString(), date);

                        language = "EN";
                        content = new StringBuilder();
                    }

                    // set the different chapter
                    Matcher m = CHAPTER_PATTERN.matcher(line);
                    if(m.find()) {
                        chapterId = m.group(1);
                    }

                } else if(SPEAKER_NOLANG_PATTERN.matcher(line).matches()) {
                    // send previous data
                    if(content.length() > 0) {
                        add(documents, file.getName(), chapterId, speakerId, speakerName, language, content.toString(), date);

                        language = "EN";
                        content = new StringBuilder();
                    }

                    // set the different speaker
                    Matcher m = SPEAKER_NOLANG_PATTERN.matcher(line);
                    if(m.find()) {
                        speakerId = m.group(1);
                        speakerName = m.group(2);
                    }

                } else if(SPEAKER_WLANG_PATTERN.matcher(line).matches()) {
                    // send previous data
                    if(content.length() > 0) {
                        add(documents, file.getName(), chapterId, speakerId, speakerName, language, content.toString(), date);

                        language = "EN";
                        content = new StringBuilder();
                    }

                    // set the different speaker
                    Matcher m = SPEAKER_WLANG_PATTERN.matcher(line);
                    if(m.find()) {
                        speakerId = m.group(1);
                        language = m.group(2);
                        speakerName = m.group(3);
                    }

                } else if(!PAUSE_MARK_PATTERN.matcher(line).matches() && speakerId != null){
                    content.append(content.length() > 0 ? " " : "").append(line);
                }
            }
        }

        // last to save
        if(content.length() > 0) {
            add(documents, file.getName(), chapterId, speakerId, speakerName, language, content.toString(), date);
        }

        scanner.close();

        return documents;
    }
}
