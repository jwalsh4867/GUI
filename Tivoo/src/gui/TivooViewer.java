package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.*;
import javax.swing.tree.*;	
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import main.Main;
import input.*;
import input.Event;
import output.*;
import processor.*;

public class TivooViewer extends JFrame
{
	JEditorPane pane; // from sample code
	JFileChooser fc; // from sample code
	private String input;
	private List<Event> EventList;
	private JMenuBar MenuBar = new JMenuBar();
	private JMenu file = new JMenu("File");
	private JMenuItem load = new JMenuItem("Load");
	private JMenu filter = new JMenu("Filter");  
	private JMenuItem titlefilt = new JMenuItem("Title Filter");
	private JMenuItem exclude = new JMenuItem("Exclude Word");
	private JMenuItem details = new JMenuItem("Details");
	private JMenuItem timerange = new JMenuItem("Time Range");
	private JMenuItem time = new JMenuItem("Time");
	private JMenuItem tag = new JMenuItem("Tag");
	private JMenuItem xml = new JMenuItem("File & Tag");
	private JMenu sort = new JMenu("Sort");
	private JMenuItem end = new JMenuItem("Ending Time");
	private JMenuItem start = new JMenuItem("Starting Time");
	private JMenuItem titlesort = new JMenuItem("Title Sorter");
	private JMenu preview = new JMenu("Preview");
	private JMenuItem monthprev = new JMenuItem("Monthly");
	private JMenuItem weekprev = new JMenuItem("Weekly");
	private JMenuItem dayprev = new JMenuItem("Daily");
	private JMenuItem sumprev = new JMenuItem("Events Summary");
	private JMenuItem conprev = new JMenuItem("Conflicting Events");
	private List<Event> WorkingList;
	private boolean boolparse; 
	private boolean boolhtml;
	private CalendarUtil helper; // cannot refer to parseFile func without otherwise says to make func static 
	
	// need a map so that a new listener isn't needed for each individual ListentoFilter, ListentoSort & ListentoHTML 
	// function. A map allows only one listen function for each type of map (filter,sort,htmlview). In order to pair menuitem
	// to map, an action command is needed (?). This is the only answer i found in various forums and articles. 
	private Map<String, FilterComponent> FilterTypes = new HashMap<String, FilterComponent>();  
	{
		FilterTypes.put("Details", new DetailFilter());
		details.setActionCommand("Details");
		FilterTypes.put("Exclude word", new ExcludeFilter());
		exclude.setActionCommand("Exclude Word");
		FilterTypes.put("Title Filter", new KeywordFilter());
		titlefilt.setActionCommand("Title Filter");
		FilterTypes.put("Time Range", new RangeOfDatesTimeFilter());
		timerange.setActionCommand("Time Range");
		FilterTypes.put("File & Tag", new SpecificXMLFilter());
		xml.setActionCommand("File & Tag");
		FilterTypes.put("Tag", new TagFilter());
		tag.setActionCommand("Tag");
		FilterTypes.put("Time", new TimeFilter());
		time.setActionCommand("Time");
	}
	private Map<String, Sorters> SortTypes = new HashMap<String, Sorters>();
	{
		SortTypes.put("Ending Time", new SortByEndDate());
		end.setActionCommand("Ending Time");
		SortTypes.put("Starting Time", new SortByStartDate());
		start.setActionCommand("Starting Time");
		SortTypes.put("Title Sorter", new SortByTitle());
		titlesort.setActionCommand("Title Sorter");
	}
	private Map<String, HtmlPageWriters> ViewTypes = new HashMap<String, HtmlPageWriters>();
	{
		ViewTypes.put("Monthly", new CalendarMonthPage());
		monthprev.setActionCommand("Monthly");
		ViewTypes.put("Weekly", new CalendarWeekPage());
		weekprev.setActionCommand("Weekly");
		ViewTypes.put("Daily", new CalendarDayPage());
		dayprev.setActionCommand("Daily");
		ViewTypes.put("Events Summary", new SummaryListPage());
		sumprev.setActionCommand("Events Summary");
		ViewTypes.put("Conflicting Events", new ConflictsPage());
		conprev.setActionCommand("Conflicting Events");
	}
	
	public TivooViewer() // partially from sample code 
	{
		super("Tivoo GUI");
		boolparse = false; 
		pane = new JEditorPane();
		pane.setPreferredSize(new Dimension(800,600));
		pane.setEditable(false);
		JFileChooser chooser = new JFileChooser();
		fc = chooser;
		fc.setCurrentDirectory(new File("resourcesXML")); // XMLs stored in resources folder - tells load where to pull from
		fc.setFileFilter(new FileFilter(){
			public boolean accept(File file) { 
				if (file.isFile()){
					String name = file.getName();
					int pos = name.lastIndexOf('.');
					return name.substring(pos,name.length()).equals(".xml");
				}
				return false;
			}
			public String getDescription(){
				return "XML Files";
			}
		});
		fc.setMultiSelectionEnabled(true); // can select more than one file
		setJMenuBar(MenuBar); // create menu bar
		MenuBar.add(file); // build menu categories
		file.add(load); // add items to menu category 
		MenuBar.add(filter);
		filter.add(titlefilt);
		filter.add(exclude);
		filter.add(details);
		filter.add(time);
		filter.add(timerange);
		filter.add(tag);
		filter.add(xml);
		MenuBar.add(sort); 
		sort.add(titlesort);
		sort.add(start);
		sort.add(end);
		MenuBar.add(preview);
		preview.add(monthprev);
		preview.add(weekprev);
		preview.add(dayprev);
		preview.add(sumprev);
		preview.add(conprev);
		load.addActionListener(new ListentoLoad());
		titlefilt.addActionListener(new ListentoFilter());
		exclude.addActionListener(new ListentoFilter());
		details.addActionListener(new ListentoFilter());
		time.addActionListener(new ListentoFilter());
		timerange.addActionListener(new ListentoFilter());
		tag.addActionListener(new ListentoFilter());
		xml.addActionListener(new ListentoFilter());
		titlesort.addActionListener(new ListentoSorter());
		start.addActionListener(new ListentoSorter());
		end.addActionListener(new ListentoSorter());
		monthprev.addActionListener(new ListentoPreview());
		weekprev.addActionListener(new ListentoPreview());
		dayprev.addActionListener(new ListentoPreview());
		sumprev.addActionListener(new ListentoPreview());
		conprev.addActionListener(new ListentoPreview());
		EventList = new ArrayList<Event>(); 
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(pane), "Center");
	}
	public class ListentoLoad implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			boolparse = true;
			try
			{
				pickfile();
			}
			catch (Exception error)
			{
				error.printStackTrace();
			}
		}
	}
	private void pickfile() // from sample code
	{
        int returnVal = fc.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) 
        {
            File[] files = fc.getSelectedFiles();
            EventList.clear(); // must be cleared each time 
            String[] fileStrings = new String[files.length]; // parseFiles take a String[] not file[] must convert 
            for (int x=0;x<files.length;x++) 
            {
            	fileStrings[x] = "resources/"+files[x].getName(); // needed name when passed to parseFiles in calendarutil
            }
            WorkingList = helper.parseFiles(fileStrings); // parsexmlfiles
            EventList.addAll(WorkingList); //copy to eventlist
        }
	}
	public class ListentoPreview implements ActionListener{
		public void actionPerformed(ActionEvent event) {
			if(boolparse) {
				boolhtml = true;
				HtmlPageWriters writer = ViewTypes.get(event);
				writer.write(WorkingList);
				boolparse = false;
				File file = new File(writer.getFileName());
				if(file.exists()) {		
					String uri = file.toURI().toString();
//					try
//					{
//						if(file.toURI().toString().contains("Summary"))
//						{
//							HTMLExample foo = new HTMLExample(file.toURI().toString());
//						}
//					} catch (IOException e1)
//					{
//						e1.printStackTrace();
//					}
				}
				else{
					System.out.println("File doesn't exist");
				}
			}
			else{
				System.out.println("Load file first");
			}
		}
	}
	private class ListentoFilter implements ActionListener 
	{
        public void actionPerformed(ActionEvent event) 
        {
            String keyword = UserInput("Keyword to search for. Separate multiple words with ',' and no spaces!");
            String[] keywords = keyword.split(",");
            getFilter(event.getActionCommand(),keywords);
        }
    }
	public void getFilter(String name, String[] keyword) 
	{
		FilterComponent filter = FilterTypes.get(name);
		WorkingList = filter.filter(WorkingList, keyword);
	}
	public String UserInput(String text)
	{
		String userinput = JOptionPane.showInputDialog(text);
		return userinput;
	}	
	public class HTMLExample  extends JFrame 
	{

		JEditorPane pane2;

		public HTMLExample(String url) throws IOException
		{
			pane2 = new JEditorPane();
			getContentPane().setLayout(new BorderLayout());
			getContentPane().add(pane2, BorderLayout.CENTER);
			pane2.setEditable(false);
			pane2.setPreferredSize(new Dimension(800,600));
			pane2.addHyperlinkListener(new LinkFollower());
			pack();
			pane2.setPage(url);
			setVisible(true);
		}
		private class LinkFollower implements HyperlinkListener
		{
			public void hyperlinkUpdate (HyperlinkEvent evt)
			{
				if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
				{
					// user clicked a link, load it and show it
					try
					{
						pane2.setPage((evt.getURL().toString()));
					}
					catch (Exception e)
					{
						String s = evt.getURL().toString();
						JOptionPane.showMessageDialog(HTMLExample.this,
								"loading problem for " + s + " " + e,
								"Load Problem", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
	}
	private class ListentoSorter implements ActionListener
	{
		public void actionPerformed(ActionEvent event) 
		{
			String sort = UserInput("Enter 'Normal' or 'Reverse'");
			getSort(event.getActionCommand(),sort);		
		}
	}
	 public void getSort(String sort, String type) 
	 {
	        Sorters sorttype = SortTypes.get(sort);
	        if (type.equals("Normal"))
	        {
	            WorkingList = sorttype.sort(WorkingList);
	        }
	        WorkingList = sorttype.reverseSort(WorkingList);
	 }
	 public void launchGUI() 
	 { 
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			pack();
			setVisible(true);
	 }
}

