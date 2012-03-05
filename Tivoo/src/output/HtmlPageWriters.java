package output;

import input.Event;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import processor.SortByStartDate;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.*;

public abstract class HtmlPageWriters {

    private Html start; //will need to have options for multiple html threads at same time in a single object
    private Body myBody;
    private Node other;
    
    public HtmlPageWriters(String title,Body body, Node o){
        start = writeHeader(title);
        myBody=body;
        other=o;
    }

    public void write(List<Event> events) {
        events=applyFilter(events);
        DetailPage details=new DetailPage();
        for (Event e:events){
            details.writeEvent(e); //automatically writes detailed pages
            attachEvent(e, other);
        }
        closePages(); 
        System.out.println("finished all writing to file");
    }

    protected abstract List<Event> applyFilter(List<Event> events);

    //TODO: REMOVE
    protected List<Event> sortByStartDate(List<Event> events) {
        SortByStartDate startsort=new SortByStartDate();
        return startsort.sort(events);
    }
    
    protected abstract void attachEvent(Event e, Node other2);

    protected Html writeHeader(String text) {
        Html start = new Html();
        Head head = new Head();
        start.appendChild(head);

        Title title = new Title();
        title.appendText(text);
        head.appendChild(title);
        return start;
    }
    
    protected void closePages(){
        myBody.appendChild(other);
        start.appendChild(myBody);
        File filename=new File(getFileName());
        writeToFile(filename, start);
        start.removeChild(myBody); //reset for multiple pages
        myBody.removeChild(other);
    }

    /**
     * Where to save the html file
     */
    public abstract String getFileName();
    
    protected void writeToFile(File filename, Html start) {
        BufferedWriter out = null;
        try {
            out = openFile(filename);
            out.write(start.write());
        } catch (IOException e) {
            System.err.println("unable to write to file " + filename.getName());
            e.printStackTrace();
        } finally {
            if (out != null) {
//                System.out.println("Closing Buffered Writer in HtmlFunctions");
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err
                            .println("Buffered Writer file didn't close properly");
                }
            } else {
                System.out
                        .println("Buffered Writer in HtmlFunctions never opened");
            }
        }
    }
    protected void swapNodes(Node n){
        other=n;
    }


    private BufferedWriter openFile(File filename) throws IOException {
        FileWriter fstream = new FileWriter(filename);
        BufferedWriter out = new BufferedWriter(fstream);
        return out;
    }
}