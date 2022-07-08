package Index;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import GUI.SearchFrame;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
public class Search {
    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;
    SearchFrame searchFrame=new SearchFrame();

    QueryScorer queryScorer;

    static String[] frags=null;
    static ArrayList<String> highlights=new ArrayList<String>();

    public Search(){}
    public Search (String IndexDirPath) throws IOException{
        Directory indexDir=FSDirectory.open(Paths.get(IndexDirPath));
        IndexReader reader=DirectoryReader.open(indexDir);
        indexSearcher=new IndexSearcher(reader);
        queryParser=getQueryParser();
    }

    public TopDocs search(String searchQuery)throws IOException,ParseException{
        query=queryParser.parse(searchQuery);

        return  indexSearcher.search(query,LuceneConstants.MAX_SEARCH);
    }

    public Document getDoc(ScoreDoc score)throws IOException,CorruptIndexException{
        return indexSearcher.doc(score.doc);
    }

    //return the right queryParser according to the Field we want to create a query
    private QueryParser getQueryParser(){
        if(searchFrame.getFieldOfSearch().equals("Title")){
           return new QueryParser(LuceneConstants.FILE_NAME,new StandardAnalyzer());
        }
        else {
            return new QueryParser(LuceneConstants.CONTENTS,new StandardAnalyzer());
        }
    }

    public String[] createHighlights(String IndexDirPath,String QueryString) throws IOException, ParseException, InvalidTokenOffsetsException {

        Directory indexDir=FSDirectory.open(Paths.get(IndexDirPath));
        IndexReader reader=DirectoryReader.open(indexDir);
        IndexSearcher searcher=new IndexSearcher(reader);
        Analyzer analyzer=new StandardAnalyzer();
        SearchFrame sf=new SearchFrame();
        String sfield=sf.getFieldOfSearch();
        String searchField;
        if (sfield.equals("Title")){
            searchField=LuceneConstants.FILE_NAME;
        }
        else{
            searchField=LuceneConstants.CONTENTS;
        }
        QueryParser qp=new QueryParser(searchField,analyzer);
        Query query= qp.parse(QueryString);
        TopDocs hits=searcher.search(query,LuceneConstants.MAX_SEARCH);

        Formatter formater=new SimpleHTMLFormatter();
        QueryScorer scorer=new QueryScorer(query);

        Highlighter highlighter=new Highlighter(formater,scorer);

        Fragmenter fragmenter=new SimpleSpanFragmenter(scorer,20);
        highlighter.setTextFragmenter(fragmenter);

        for (int i=0;i<hits.scoreDocs.length;i++){
            int docid=hits.scoreDocs[i].doc;
            Document doc=searcher.doc(docid);

            //System.out.println("Path:"+LuceneConstants.FILE_PATH);
            String text=doc.get(searchField);
            TokenStream stream=TokenSources.getAnyTokenStream(reader,docid,searchField,analyzer);

            frags = highlighter.getBestFragments(stream, text, 20);

            highlights.add(frags[0]+"...");
        }

        return frags;

    }

    public String[] getHighlights(){
        return frags;
    }
    public ArrayList<String>getListHighlights(){
        return highlights;
    }

    public void EmptyHighlighList(){
        highlights.clear();
    }

}
