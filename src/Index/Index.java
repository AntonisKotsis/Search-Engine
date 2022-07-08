package Index;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
public class Index {
    private IndexWriter writer;
    private Analyzer analyzer=new StandardAnalyzer();
    private FileWriter Fwriter;
    private Scanner sc;

    public Index(String dirPath) throws IOException{
        Directory directory=FSDirectory.open(Paths.get(dirPath));
        IndexWriterConfig iwc=new IndexWriterConfig(analyzer);
        writer =new IndexWriter(directory,iwc);
    }

    private Document getDoc(File file) throws IOException{
        Document doc=new Document();
        TextField contentField=new TextField(LuceneConstants.CONTENTS,new String(Files.readAllBytes(file.toPath())),Field.Store.YES);
        TextField fileNameField=new TextField(LuceneConstants.FILE_NAME,file.getName(), Field.Store.YES);
        TextField filePathField=new TextField(LuceneConstants.FILE_PATH,file.getCanonicalPath(),TextField.Store.YES);
        doc.add(contentField);
        doc.add(fileNameField);
        doc.add(filePathField);


        return doc;

    }
    //This method converts the articles to indexed documents
    private void indexFile(File file) throws IOException{

        Document doc=getDoc(file);
        writer.addDocument(doc);
    }

    public int createIndex(String dirPath)throws IOException{
            //read from indexFile the first line
            //if it's true it's then we have to index the documents
            //else document have already been indexed
            sc=new Scanner(new File("indexFiles.txt"));
            String flag=sc.nextLine();
            //System.out.println("Read:"+flag);
            if (flag.equals("true")) {
                Fwriter=new FileWriter("indexFiles.txt");
                Fwriter.write("false");
                Fwriter.close();
                File[] files = new File(dirPath).listFiles();
                for (File file : files) {

                    System.out.println(file.getName());
                    indexFile(file);

                }
            }
            return writer.numRamDocs();


    }

    public void close() throws IOException{
        writer.close();
    }

}
