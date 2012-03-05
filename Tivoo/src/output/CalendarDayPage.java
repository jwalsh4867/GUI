package output;

import java.util.ArrayList;
import java.util.List;

import input.Event;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.*;

public class CalendarDayPage extends CalendarPage {

    public CalendarDayPage() {
        super("Calendar_Day", new Body(), null);
    }

    // page containing all events, links to detailed pages
    protected void attachEvent(Event e, Node other) {
        if (!datePages.containsKey(detectDate(e))) {
            Ul ul = new Ul();
            datePages.put(detectDate(e), ul);
            key=e.getFormattedStartTime().substring(0, 10);
            createEmptyTemplate(ul);
            ul.appendChild(writeEventSummary(e));
        } else {
            ((Ul) datePages.get(detectDate(e))).appendChild(writeEventSummary(e));
        }
    }

    @Override
    protected void createEmptyTemplate(Node o) { // Node o is a Ul
        ((Ul) o).appendChild(new H1().appendText(key));
    }

    private Node writeEventSummary(Event e) {
        Li li = new Li();
        A a = new A();
        a.setHref(e.getNameForFile() + ".html");
        a.appendText(e.getTitle());
        li.appendChild(a);
        li.appendChild(new Br());
        li.appendText(e.getFormattedStartTime() + " | "
                + e.getFormattedEndTime());
        return li;
    }

    @Override
    protected String detectDate(Event e) {
        return e.getStartTime().substring(0, 8);
    }

    @Override
    protected String getType() {
        return "Day_";
    }

//     public static void main(String[] args) {
//     List<Event> tester = new ArrayList<Event>();
//     tester.add(new Event("title1", "201201011100", "201201011300",
//     "www.google.com", "descrp1"));
//     tester.add(new Event("title3", "201201061100", "201201061300",
//     "www.msn.com", "descrp1"));
//     tester.add(new Event("title2", "201201011400", "201201011600",
//     "www.yahoo.com", "descrp2"));
//     CalendarDayPage something = new CalendarDayPage();
//     something.write(tester);
//     something.write(tester, "20120101");
//     }
}