package Index;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;



public class CreateW2V {
    private ArrayList<String> w2vOutput=new ArrayList<String>();
    private static ArrayList<String> top3Suggestions=new ArrayList<String>();
    private static Logger log = LoggerFactory.getLogger(CreateW2V.class);


    public void makeFile() throws IOException {
        Process makeProc=Runtime.getRuntime().exec("make -C w2v");

    }
    public ArrayList<String> start (String searchString) throws IOException {
        top3Suggestions.clear();
        Process w2vProc=Runtime.getRuntime().exec("w2v/./distance w2v/vectors.bin "+searchString);
        BufferedReader stdIN=new BufferedReader(new InputStreamReader(w2vProc.getInputStream()));
        String s=null;
        //get suggestions
        while ((s=stdIN.readLine())!= null){
            w2vOutput.add(s);
        }
        //now select the top 3 suggestions
        try{
            for (int i=0;i<3;i++){
                top3Suggestions.add(w2vOutput.get(i));
            }
        }catch (IndexOutOfBoundsException e){

        }


        return  top3Suggestions;
    }

}