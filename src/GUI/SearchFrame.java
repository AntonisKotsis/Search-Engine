package GUI;

import Index.CreateW2V;
import Index.Search;
import Index.SearchEngine;
import org.omg.SendingContext.RunTime;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

import org.deeplearning4j.models.word2vec.Word2Vec;

public class SearchFrame extends Thread implements ActionListener  {
    private static   JTextField searchBar=new JTextField(30);
    private  static JTextArea  relatedQueries=new JTextArea(25,30);
    private static int renewSuggestions=0;
    public static String searchString;
    private static String fieldOfSearch;
    private static String showResults;
    private SearchEngine engine=new SearchEngine();
    //searchField buttons
    JRadioButton titleButton=new JRadioButton("Title");
    JRadioButton defaultButton=new JRadioButton("Plot");
    //results representation buttons
    JRadioButton highLow=new JRadioButton("High->Low");
    JRadioButton lowHigh=new JRadioButton("Low->High");

    private void createSearchFrame() {
        JFrame searchFrame=new JFrame("Search");
        searchFrame.getContentPane().setLayout(new BoxLayout(searchFrame.getContentPane(), BoxLayout.Y_AXIS));
        //dont let the user edit the suggested queries
        relatedQueries.setEditable(false);

        JButton searchButton=new JButton("GO");
        searchButton.addActionListener(this);
        JPanel panel=new JPanel();
        JPanel relatedQPanel=new JPanel();
        JPanel buttonSearchPanel=new JPanel();
        JPanel buttonResultsPanel=new JPanel();
        JPanel blankPanel=new JPanel();
        buttonSearchPanel.setLayout(new BoxLayout(buttonSearchPanel,BoxLayout.X_AXIS));
        buttonResultsPanel.setLayout(new BoxLayout(buttonResultsPanel,BoxLayout.X_AXIS));
        //search field buttons

        //buttons for the search field
        ButtonGroup bg=new ButtonGroup();
        defaultButton.setEnabled(true);
        bg.add(titleButton);
        bg.add(defaultButton);
        //set default search field
        defaultButton.setSelected(true);
        buttonSearchPanel.add(titleButton);
        buttonSearchPanel.add(defaultButton);
        buttonSearchPanel.setBorder(BorderFactory.createTitledBorder("Search Field"));


        //buttons for the result representation

        ButtonGroup bgR=new ButtonGroup();
        bgR.add(highLow);
        bgR.add(lowHigh);
        highLow.setSelected(true);
        buttonResultsPanel.add(highLow);
        buttonResultsPanel.add(lowHigh);
        buttonResultsPanel.setBorder(BorderFactory.createTitledBorder("Show results from:"));



        panel.add(new JLabel("Search:"));
        panel.add(searchBar);
        panel.add(searchButton);

        relatedQPanel.add(new JLabel("You may also like:"));
        relatedQPanel.add(relatedQueries);

        //add caretListener so the user can select a suggestion and instantly send it to the search bar
        relatedQueries.addCaretListener(new CaretListener() {
            @Override
            public void caretUpdate(CaretEvent caretEvent) {
                //System.out.println("Word is :"+relatedQueries.getSelectedText());
                try {
                    if(!relatedQueries.getSelectedText().equals(null)){
                        updateSearchBar(relatedQueries.getSelectedText());

                    }
                }catch (NullPointerException e){
                }

            }
        });

        searchFrame.add(blankPanel);
        searchFrame.add(panel);
        searchFrame.add(relatedQPanel);
        searchFrame.add(buttonSearchPanel);
        searchFrame.add(buttonResultsPanel);
        searchFrame.setSize(400,700);
        searchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        searchFrame.setVisible(true);
    }


    public static void main(String args[]){
        SearchFrame sf=new SearchFrame();
        ResultsFrame rf=new ResultsFrame();
        sf.createSearchFrame();

        //following code deletes the html files with the results when the program exits
        Runtime.getRuntime().addShutdownHook(new Thread(){

            @Override
            public void run() {
               rf.deleteHtmlFile();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String search_word=searchBar.getText();
        SearchFrame thread=new SearchFrame();
        //start the thread for the suggested queries
        renewSuggestions++;
        clearSuggestions();
        thread.start();

        setFieldOfSearch();
        setResultRepresetantion();
        ResultsFrame rf=new ResultsFrame();
        Search search=new Search();
        //clear the lists that we gonna show to the user
        //to take the new results
        rf.emptyList();
        search.EmptyHighlighList();
        //set the index to zero so we can create the sub-lists
        rf.setIndexToZero();
        //start searching
        searchString=searchBar.getText();
        engine.startQuery(search_word);
        //delete the last results to get the new ones
        rf.deleteHtmlFile();
        //create the frame that holds the results
        try {
            rf.createResultsFrame(search_word);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getSearchString(){

        return searchString;
    }

    private void setFieldOfSearch(){
        if (titleButton.isSelected()){

            fieldOfSearch=titleButton.getText();
        }
        else if(defaultButton.isSelected()){
            fieldOfSearch=defaultButton.getText();
        }
    }

    private void setResultRepresetantion(){
        if(highLow.isSelected()){
            showResults=highLow.getText();

        }
        else if(lowHigh.isSelected()){
            showResults=lowHigh.getText();
        }
    }

    public String getFieldOfSearch(){
        return fieldOfSearch;
    }

    public String getShowResults(){return showResults;}






    @Override
    public void run() {
        CreateW2V createW2V=new CreateW2V();
        try {
            ArrayList<String> top3Suggs = createW2V.start(searchBar.getText());
            for (String st: top3Suggs){
                relatedQueries.append(st+"\n");

            }
            relatedQueries.append("----------------------------------------------------------------------------------------------------\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (IndexOutOfBoundsException e){

        }

    }
    //clears the searchBar after every search
    public void clearSearchBar(){
        searchBar.setText("");
    }

    //clears suggestions after 4 searches
    private void clearSuggestions(){
        if (renewSuggestions==6){
            renewSuggestions=0;
            relatedQueries.setText("");
        }

    }

    private void updateSearchBar(String suggestion){
        searchBar.setText(suggestion);
    }


}
