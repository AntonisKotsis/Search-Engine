package Index;



import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;

import GUI.ResultsFrame;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;

public class SearchEngine {
    String indexDir="Index";
    String dataDir="data";
    Index index;
    Search search;
    static ArrayList<String> hitDocList=new ArrayList<String>();
    ResultsFrame resultsFrame=new ResultsFrame();

    public void startQuery(String searchString) {
        try {
            createIndex();
            createW2V(searchString);
            startSearch(searchString);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (org.apache.lucene.queryparser.classic.ParseException | InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }
    }

    private void createIndex() throws IOException{
        index=new Index(indexDir);
        int numIndexed=index.createIndex(dataDir);
        index.close();
    }

    //use word2Vec to find suggestions
    private void createW2V(String searcS) throws IOException {
        CreateW2V createW2V=new CreateW2V();
        createW2V.makeFile();
        createW2V.start(searcS);
    }

    private void startSearch(String searchString) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException, InvalidTokenOffsetsException {
        hitDocList.clear();
        search=new Search(indexDir);
        TopDocs hits=search.search(searchString);
        search.createHighlights(indexDir,searchString);


       // System.out.println(hits.totalHits + " documents found.");
        for (ScoreDoc score :hits.scoreDocs){
            Document doc=search.getDoc(score);

            if(!hitDocList.contains(doc.get(LuceneConstants.FILE_PATH))){
                hitDocList.add(doc.get(LuceneConstants.FILE_PATH));
                resultsFrame.addElement(doc.get(LuceneConstants.FILE_PATH));
            }
       }

    }

    public ArrayList<String> getHitDocList(){
        return hitDocList;
    }
    public int getHitDocListSize(){return  hitDocList.size();}
}


