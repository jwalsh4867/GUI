package output;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.hp.gagawa.java.Node;
import com.hp.gagawa.java.elements.*;

import input.Event;

public class CalendarMonthPage extends CalendarPage {

    public CalendarMonthPage() {
        super("Calendar_Month", new Body(), null);
    }

    private Td blockTd(String color) {
        return new Td().setBgcolor(color).setWidth("100px").setHeight("50px");
    }

    protected void createEmptyTemplate(Node o) {
        Table t = (Table) o; // for convenience
        t.appendChild(writeDaysOfWeekHeader()); 
        t.appendChild(new Thead().appendText(key));

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(key.substring(0, 4)),
                Integer.parseInt(key.substring(4, 6)) - 1, 1);
        int totaldays = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // gets
                                                                     // last day
                                                                     // of month
        int firstday = cal.get(Calendar.DAY_OF_WEEK); // gets first day of month
        int j = firstday;

        Tr tr = new Tr();
        while (j > 1) { // block off previous month days
            tr.appendChild(blockTd("gray"));
            j -= 1;
        }
        for (int i = 1; i <= totaldays; i++) {
            //end of week, start new row
            if ((((i + firstday - 2) % 7) == 0) && (tr.children.size() != 0)) {
                t.appendChild(tr);
                tr = new Tr();
            }
            tr.appendChild(blockTd("white").appendText("" + i));
        }
        //block off next month days
        while (((totaldays + firstday - 1) % 7) != 0) {
            tr.appendChild(blockTd("gray"));
            totaldays += 1;
        }
        t.appendChild(tr);
    }

    protected void attachEvent(Event e, Node o) {
        Table table;
        if (!datePages.containsKey(detectDate(e))) {
            table = new Table().setBorder("1px solid black");
            datePages.put(detectDate(e), table);
            key = detectDate(e);
            createEmptyTemplate(table);
        } else {
            table = (Table) datePages.get(detectDate(e));
        }

        String time = e.getStartTime();
        Td td=findCoordinates(time,table);

        writeEventSummary(e, td);
    }
    private Td findCoordinates(String time,Table table){
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(time.substring(0, 4)),
                Integer.parseInt(time.substring(4, 6)) - 1,
                Integer.parseInt(time.substring(6, 8)));
        int row = cal.get(Calendar.WEEK_OF_MONTH);
        Node tr = table.getChild(row+1);
        int column = cal.get(Calendar.DAY_OF_WEEK);
        Node td = ((Tr) tr).getChild(column - 1);
        return (Td) td;
    }

    private void writeEventSummary(Event e, Td td) {
        Div div = new Div()
                .setStyle("display:block;margin:3px;background:cyan;");
        A a = new A();
        a.setHref(e.getNameForFile() + ".html");
        a.appendText(e.getTitle());
        div.appendChild(a);
        div.appendChild(new Br());
        div.appendText(e.getFormattedStartTime() + " | "
                + e.getFormattedEndTime());
        td.appendChild(div);
    }

    @Override
    protected String detectDate(Event e) {
        return e.getStartTime().substring(0,6);
    }
    
    @Override
    protected String getType() {
        return "Month_";
    }

//     public static void main(String[] args) {
//     List<Event> tester = new ArrayList<Event>();
//     tester.add(new Event("title1", "201202011100", "201202011300",
//     "www.google.com", "descrp1"));
//     tester.add(new Event("title3", "201201091100", "201201101300",
//     "www.msn.com", "descrp1"));
//     tester.add(new Event("title2", "201202011400", "201202011600",
//     "www.yahoo.com", "descrp2"));
//     CalendarMonthPage something = new CalendarMonthPage();
//     something.write(tester);
//     }
}