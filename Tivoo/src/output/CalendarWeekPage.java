package output;

import input.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.*;

public class CalendarWeekPage extends CalendarPage {
    
    public CalendarWeekPage() {
        super("Calendar_Week",new Body(),null);
    }
    
    // page containing all events, links to detailed pages
    protected void attachEvent(Event e, Node o) {
        Table table;
        if (!datePages.containsKey(detectDate(e))) {
            table = new Table().setBorder("1px solid black");
            datePages.put(detectDate(e), table);
            key=detectDate(e);
            createEmptyTemplate(table);
        } else {
            table=(Table) datePages.get(detectDate(e));
        }
        
        Tr row=(Tr) table.getChild(2); 
        Td currentSpot = (Td) row.getChild(e.getDayOfWeek() - 1);
        writeEventSummary(e, currentSpot);
    }

    protected void createEmptyTemplate(Node o) {
        Table t=(Table)o;
        t.appendChild(writeDaysOfWeekHeader());
        Thead th=new Thead();
        th.appendText(key);
        t.appendChild(th);
        Tr tr=new Tr();
        for (int i=0;i<7;i++){
            tr.appendChild(new Td().setWidth("100px").setHeight("50px").appendText("&nbsp;"));
        }
        t.appendChild(tr);
    }

    private void writeEventSummary(Event e, Td td) {
        Div div = new Div().setStyle("display:block;margin:3px;background:cyan;");
        A a = new A();
        a.setHref(e.getNameForFile() + ".html");
        a.appendText(e.getTitle());
        div.appendChild(a);
        div.appendChild(new Br());
        div.appendText(e.getFormattedStartTime() + " | "
                + e.getFormattedEndTime());
        td.appendChild(div);
    }

    private int getWeek(String time) {
        Calendar weeks = Calendar.getInstance();
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);
        weeks.set(Integer.parseInt(year), Integer.parseInt(month) - 1,
                Integer.parseInt(day));

        int weekday = weeks.get(Calendar.WEEK_OF_YEAR);
        return weekday;
    }

    @Override
    protected String detectDate(Event e) {
        String time=e.getStartTime();
        return time.substring(0,4)+"-Week "+getWeek(time);
    }

    protected String getType(){
        return "Week_";
    }

//    public static void main(String[] args) {
//        List<Event> tester = new ArrayList<Event>();
//        tester.add(new Event("title1", "201201011100", "201201011300",
//                "www.google.com", "descrp1"));
//        tester.add(new Event("title3", "201201081100", "201201081300",
//                "www.msn.com", "descrp1"));
//        tester.add(new Event("title2", "201201011400", "201201011600",
//                "www.yahoo.com", "descrp2"));
//        CalendarWeekPage something = new CalendarWeekPage();
//        something.write(tester);
////        something.write(tester, "201201");
//    }
}