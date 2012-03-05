package output;

import input.Event;

import java.io.File;
import java.util.List;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.Br;
import com.hp.gagawa.java.elements.Html;
import com.hp.gagawa.java.elements.P;

public class DetailPage extends HtmlPageWriters{

    public DetailPage() {
        super(null,null,null); //not necessary for super to have this
    }

    protected void writeEvent(Event e) {
        Html start=writeHeader(e.getTitle());
        Body body = new Body();
        body.appendChild(writeAllInEvent(e));
        start.appendChild(body);

        File filename = new File("output/" + e.getNameForFile() + ".html");
        writeToFile(filename,start);
    }

    private Node writeAllInEvent(Event e) {
        P p = new P();
        A a = new A();
        a.setHref(e.getLink());
        a.appendText("Title : " + e.getTitle());
        p.appendChild(a);
        p.appendChild(new Br());
        p.appendText("Start Time : " + e.getFormattedStartTime());
        p.appendChild(new Br());
        p.appendText("End Time : " + e.getFormattedEndTime());
        p.appendChild(new Br());
        p.appendText("Description : " + e.getDetail());
        p.appendChild(new Br());
        p.appendText("Link : " + e.getLink());
        p.appendChild(new Br());

        return p;
    }

    @Override
    protected void closePages() {
        //Do nothing, it closes after every writeEvent
    }

    @Override
	public String getFileName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void attachEvent(Event e, Node other2) {
        System.err.println("This should never have happened, DetailPage was called explicitly outside of HtmlPageWriters");
    }

    @Override
    protected List<Event> applyFilter(List<Event> events) {
        return events; //no filter
    }

}