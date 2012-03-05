package output;

import input.Event;

import java.util.HashMap;
import java.util.List;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.Body;
import com.hp.gagawa.java.elements.Td;
import com.hp.gagawa.java.elements.Tr;

public abstract class CalendarPage extends HtmlPageWriters {
    protected String key = null;
    protected HashMap<String, Node> datePages;

    public CalendarPage(String title, Body body, Node o) {
        super(title, body, o);
        datePages= new HashMap<String, Node>();
    }

    public void closePages() {
        for (String k : datePages.keySet()) {
            key=k;
            swapNodes(datePages.get(k));
            super.closePages();
        }
//        swapNodes(null);
//        datePages.clear();
    }

    public String getFileName() {
        return "output/"+getType() + key+".html";
    }
    protected abstract String getType();

    protected abstract void createEmptyTemplate(Node o);
    protected abstract String detectDate(Event e);

    @Override
    protected List<Event> applyFilter(List<Event>events){
        return sortByStartDate(events);
    }
    protected Node writeDaysOfWeekHeader() {
        Tr tr = new Tr();
        tr.appendChild(new Td().appendText("Sunday"));
        tr.appendChild(new Td().appendText("Monday"));
        tr.appendChild(new Td().appendText("Tuesday"));
        tr.appendChild(new Td().appendText("Wednesday"));
        tr.appendChild(new Td().appendText("Thursday"));
        tr.appendChild(new Td().appendText("Friday"));
        tr.appendChild(new Td().appendText("Saturday"));
        return tr;
    }

}