package com.italo.risystem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer
{
    private IndexWriter writer;
    private Analyzer analyzer;
    private Directory indexDirectory;

    public Indexer(String indexPath) throws IOException {
        Path path = Paths.get(indexPath);
        this.indexDirectory = FSDirectory.open(path);
    }

    private Document makeDocument(File file) throws IOException {
        Document doc = new Document();
        FileReader reader = new FileReader(file);

        doc.add(new TextField("content", reader));
        doc.add(new StoredField("fileName", file.getName()));
        doc.add(new StoredField("filePath", file.getCanonicalPath()));
        doc.add(new StoredField("lastModified", file.lastModified()));

        return doc;
    }

    public int createIndex(String filesPath) throws IOException {
        File[] files = new File(filesPath).listFiles();

        for (File file : files) {
            if (!file.isDirectory() && !file.isHidden() && file.canRead() && file.exists()) {
                Document doc = makeDocument(file);
                writer.addDocument(doc);
            }
        }

        return writer.numDocs();
    }

    /**
     * @return the analyzer
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * @param stopword use stopwords
     * @param stemming use stemming
     */
    public void setAnalyzer(boolean stopword, boolean stemming) {
        if (stemming) {
            if (stopword) {
                analyzer = new BrazilianAnalyzer();
            } else {
                analyzer = new BrazilianAnalyzer(new CharArraySet(Collections.emptyList(), true));
            }
        } else {
            if (stopword) {
                analyzer = new StandardAnalyzer(new BrazilianAnalyzer().getStopwordSet());
            } else {
                analyzer = new StandardAnalyzer(new CharArraySet(Collections.emptyList(), true));
            }
        }
    }

    /**
     * @return the writer
     */
    public IndexWriter getWriter() {
        return writer;
    }

    /**
     * @param analyzer the analyzer used by the writer
     */
    public void setWriter(Analyzer analyzer) {
        IndexWriterConfig conf = new IndexWriterConfig(analyzer);
        try {
            writer = new IndexWriter(indexDirectory, conf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void closeWriter() throws IOException {
        writer.close();
    }
}