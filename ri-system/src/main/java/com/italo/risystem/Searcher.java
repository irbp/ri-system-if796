package com.italo.risystem;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.br.BrazilianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

public class Searcher {
    private boolean stopword;
    private boolean stemming;
    private IndexSearcher indexSearcher;

    private static final int MAX_HITS = 100;

    public Searcher(String indexDirectory, boolean stopword, boolean stemming) throws IOException {
        Path path = Paths.get(indexDirectory);
        Directory indexDirectory1 = FSDirectory.open(path);
        this.stopword = stopword;
        this.stemming = stemming;

        IndexReader indexReader = DirectoryReader.open(indexDirectory1);
        this.indexSearcher = new IndexSearcher(indexReader);
    }

    public Analyzer getAnalyser() {
        Analyzer analyzer;

        if (this.stemming) {
            if (this.stopword) {
                analyzer = new BrazilianAnalyzer();
            } else {
                analyzer = new BrazilianAnalyzer(new CharArraySet(Collections.emptyList(), true));
            }
        } else {
            if (this.stopword) {
                analyzer = new StandardAnalyzer(new BrazilianAnalyzer().getStopwordSet());
            } else {
                analyzer = new StandardAnalyzer(new CharArraySet(Collections.emptyList(), true));
            }
        }

        return analyzer;
    }

    public void search(String queryString, Analyzer analyzer) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse(queryString);

        TopDocs topDocs = indexSearcher.search(query, MAX_HITS);
        ScoreDoc[] hits = topDocs.scoreDocs;

        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document doc = indexSearcher.doc(docId);
            System.out.println(doc.get("fileName") + " Score: " + hit.score);
        }
        System.out.println("Found " + hits.length);
    }
}
