package GUI;

import Index.Search;
import Index.SearchEngine;
import com.sun.crypto.provider.HmacMD5KeyGenerator;

import javax.swing.*;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ResultsFrame implements ActionListener{

    private static DefaultListModel<String> frameList = new DefaultListModel<>();
    private static int list_index = 0,reverseListIndex;

    private static File htmlFile=new File("Results.html");
    private static ArrayList<File> htmlDir=new ArrayList<File>();
    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }

    public void createResultsFrame(String sString) throws IOException {
        //in here we have to access the list with the results and display it
        Search search=new Search();
        SearchFrame sf = new SearchFrame();
        SearchEngine se = new SearchEngine();
        JFrame resultsFrame = new JFrame("Results");
        JTabbedPane tabs = new JTabbedPane();
        SearchEngine engine = new SearchEngine();
        double number_of_tabs;
        reverseListIndex=se.getHitDocListSize();
        //get the results
        ArrayList<String> searchResults = engine.getHitDocList();
        //store them to the following list
        //so we can represent them
        JList resultsList = new JList(frameList);


        //find the number of tabs we are going to need
        number_of_tabs = Math.ceil((double) se.getHitDocListSize() / 10);
        String [] frags=search.getHighlights();

        //when we close results frame clear search bar to be ready for the next query
        resultsFrame.addWindowListener(new WindowAdapter() {

            //clear the search bar when results are on
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                SearchFrame searchFrame=new SearchFrame();
                searchFrame.clearSearchBar();
            }
        });


        //if we dont have any results the display the error page
        if(number_of_tabs==0.0){

            JPanel noFilesFoundP=getHTMLErrorFile();
            tabs.addTab("Tab1",noFilesFoundP);
        }
        for (int i = 0; i < number_of_tabs; i++) {
            getTabList(i+1);

            JPanel htmlPanel=createHTMLContent(i+1);
            tabs.addTab("Tab " + String.valueOf(i + 1), htmlPanel);

        }
        resultsFrame.add(tabs);
        resultsFrame.setSize(800, 1000);
        resultsFrame.setVisible(true);
    }




    private void getTabList(int tabNo) throws IOException {
        DefaultListModel<String> tabList=new DefaultListModel<String>();
        SearchEngine se=new SearchEngine();
        Search search=new Search();
        SearchFrame sf=new SearchFrame();
        ArrayList<String> highlights=search.getListHighlights();
        ArrayList<String> hitList=se.getHitDocList();

        List<String> subList,subListHighlights;
        //split the list to sub-lists of size 10
        //split the list from higher to lower scores
        if (sf.getShowResults().equals("High->Low")) {
            if (list_index + 10 < hitList.size()) {
                subList = hitList.subList(list_index, list_index + 10);
                subListHighlights = highlights.subList(list_index, list_index + 10);
            } else {
                subList = hitList.subList(list_index, hitList.size());
                subListHighlights = highlights.subList(list_index, highlights.size());
            }
            list_index+=10;
        }
        //split the list from lower to higher scores
        else  {
            if (reverseListIndex - 10 >= 0) {
                subList = hitList.subList(reverseListIndex - 10, reverseListIndex);
                subListHighlights = highlights.subList(reverseListIndex - 10, reverseListIndex);
            } else {
                subList = hitList.subList(0, reverseListIndex);
                subListHighlights = highlights.subList(0, reverseListIndex);
            }
            reverseListIndex-=10;
        }

        CreateHtmlFile(subList,subListHighlights,tabNo);
        for(int i=0;i<subList.size();i++){
            tabList.addElement(subList.get(i));
            tabList.addElement(highlights.get(i));
        }

    }


    public JPanel createHTMLContent(int tabNo){
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);

        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setSize(800,700);
        jEditorPane.setVisible(true);
        jEditorPane.setEditable(false);
        try {
            URL url=new File("Results"+String.valueOf(tabNo)+".html").toURI().toURL();
            jEditorPane.setPage(url);
        }catch (IOException e) {
            jEditorPane.setContentType("text/html");
            jEditorPane.setText("Error:Page content is missing");

        }
        JScrollPane jScrollPane = new JScrollPane(jEditorPane);
        jScrollPane.setPreferredSize(new Dimension(800,900));

        panel.add(jScrollPane);

        return panel;
    }

    private void CreateHtmlFile(List<String> titles,List<String> highlights,int tabNo) throws IOException {
        SearchEngine se=new SearchEngine();
        SearchFrame sf=new SearchFrame();
        String resultsRepresentation=sf.getShowResults();
        File htmlResFile=new File("Results"+String.valueOf(tabNo)+".html");
        htmlDir.add(htmlResFile);
        FileWriter fileWriter=new FileWriter(htmlResFile,true);

        fileWriter.write("<body>");

        if (resultsRepresentation.equals("High->Low")) {
            for (int i = 0; i < titles.size(); i++) {
                fileWriter.write("<p style=\"color:blue\">" + titles.get(i) + "</p>");
                fileWriter.write("<br>");

                fileWriter.write(highlights.get(i));
                fileWriter.write("<hr>");

            }
        }
        else if(resultsRepresentation.equals("Low->High")){
            for (int i=titles.size()-1;i>=0;i--){
                fileWriter.write("<p style=\"color:blue\">" + titles.get(i) + "</p>");
                fileWriter.write("<br>");

                fileWriter.write(highlights.get(i));
                fileWriter.write("<hr>");
            }
        }
        fileWriter.write("</body>");
        fileWriter.close();

    }

    private JPanel getHTMLErrorFile(){
        JPanel panel = new JPanel();
        LayoutManager layout = new FlowLayout();
        panel.setLayout(layout);

        JEditorPane jEditorPane = new JEditorPane();
        jEditorPane.setSize(800,700);
        jEditorPane.setVisible(true);
        jEditorPane.setEditable(false);
        try {
            URL url=new File("noResultsFound.html").toURI().toURL();
            jEditorPane.setPage(url);
        }catch (IOException e) {
            jEditorPane.setContentType("text/html");
            jEditorPane.setText("Error:Page content is missing");

        }
        JScrollPane jScrollPane = new JScrollPane(jEditorPane);
        jScrollPane.setPreferredSize(new Dimension(800,600));

        panel.add(jScrollPane);

        return panel;
    }


    public void deleteHtmlFile(){
        for(File f : htmlDir){
            f.delete();
        }
        htmlDir.clear();
    }

    public void addElement(String element){ frameList.addElement(element);}

    public void emptyList(){
        frameList.clear();
    }

    public void setIndexToZero(){
        list_index=0;
    }


}
