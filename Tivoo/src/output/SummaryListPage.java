package output;

import input.Event;

import java.util.HashMap;
import java.util.List;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.*;

public class SummaryListPage extends HtmlPageWriters {

    private int day = 8;
    
    private HashMap<Integer, String> daysOfWeek = new HashMap<Integer, String>();
    {
        daysOfWeek.put(1, "Sunday");
        daysOfWeek.put(2, "Monday");
        daysOfWeek.put(3, "Tuesday");
        daysOfWeek.put(4, "Wednesday");
        daysOfWeek.put(5, "Thursday");
        daysOfWeek.put(6, "Friday");
        daysOfWeek.put(7, "Saturday");
    }


    public SummaryListPage(){
        super("Summary_List",new Body(), new Ul());
    }
    
    // page containing all events, links to detailed pages
    protected void attachEvent(Event e, Node o) {
        if (e.getDayOfWeek() != day) { // days of the week headers
            day = e.getDayOfWeek();
            ((Ul) o).appendChild(writeDayOfWeek(day));
        }

        ((Ul) o).appendChild(writeEventSummary(e));
    }

    protected Node writeDayOfWeek(int day) {
        H1 h1 = new H1();
        h1.appendText(daysOfWeek.get(day));
        return h1;
    }

    private Node writeEventSummary(Event e) {
        Li li = new Li();
        A a = new A();
        a.setHref(e.getNameForFile() + ".html");
        a.appendText(e.getTitle());
        li.appendChild(a);
        li.appendChild(new Br());
        li.appendText(e.getFormattedStartTime() + " | " + e.getFormattedEndTime());
        return li;
    }

    @Override
	public String getFileName() {
        return "output/summary_List.html";
    }
    
    @Override
    protected List<Event> applyFilter(List<Event> events) {
        return sortByStartDate(events);
    }

}