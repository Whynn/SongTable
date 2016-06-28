import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class MainFrame extends JFrame {
	static Vector<Song> song = new Vector<Song>();
	static JList<String> list = new JList<String>();
	static Vector<String> columns = new Vector<String>();
	static JTable songTable = new JTable();
	Container c = null;
	File file = null;
	
	public MainFrame(){
		super("Searching Song");
		
		readFile();
		setColumns();
			
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		//c.setLayout(new FlowLayout());
		
		SearchingPanel sp = new SearchingPanel();
		c.add(sp, BorderLayout.NORTH);
		//c.add(sp);
		
		refreshTable();
		setColumnWidth();
				
		c.add(new JScrollPane(songTable), BorderLayout.CENTER);
		//c.add(new JScrollPane(songTable));

		setSize(1200,1000);

		this.pack();
		setVisible(true);
		addWindowListener(new JFrameWindowClosingEventListener());
	}
	public static void setColumns(){
		columns.addElement("Song Name");
		columns.addElement("Writer Info");
		columns.addElement("Labels Pitched To");
		columns.addElement("Artist");
		columns.addElement("Placed");
		columns.addElement("Hold");
		columns.addElement("Release Date");

	}
	public static void setColumnWidth(){
		songTable.getColumn("Song Name").setPreferredWidth(200);
		songTable.getColumn("Writer Info").setPreferredWidth(200);
		songTable.getColumn("Labels Pitched To").setPreferredWidth(100);
		songTable.getColumn("Artist").setPreferredWidth(200);
		songTable.getColumn("Placed").setPreferredWidth(8);
		songTable.getColumn("Hold").setPreferredWidth(8);
		songTable.getColumn("Release Date").setPreferredWidth(10);
	}
	public static void refreshTable(){
		DefaultTableModel m = new DefaultTableModel(columns, 0);
		Iterator<Song> itS = song.iterator();
		while(itS.hasNext()){
			Vector<String> row = new Vector<String>();
			Song s = itS.next();
			
			row.addElement(s.getSongName());
			row.addElement(s.getWriterInfo());
			row.addElement(s.getLabelsPitched());
			row.addElement(s.getArtist());
			row.addElement(s.getPlaced());
			row.addElement(s.getHold());
			row.addElement(s.getReleaseDate());
			
			m.addRow(row);
		}
		songTable.setModel(m);
	}
	public static void refreshTable(Vector<Song> sng){
		DefaultTableModel m = new DefaultTableModel(columns, 0);
		Iterator<Song> itS = sng.iterator();
		
		if(sng.size() == 0){		// 받은 벡터가 비었을때
			Vector<String> row = new Vector<String>();
			row.addElement("해당 정보 없음..");
			
			m.addRow(row);
		}
		else if(itS.hasNext()){		// 받은 벡터에 정보가 있을때
			while(itS.hasNext()){
				Vector<String> row = new Vector<String>();
				Song s = itS.next();
				
				row.addElement(s.getSongName());
				row.addElement(s.getWriterInfo());
				row.addElement(s.getLabelsPitched());
				row.addElement(s.getArtist());
				row.addElement(s.getPlaced());
				row.addElement(s.getHold());
				row.addElement(s.getReleaseDate());
				
				m.addRow(row);
			}
		}

		songTable.setModel(m);
	}

	public void readFile(){
		BufferedReader in;
		int i = 0;
		file = new File("./bin/songs.txt");
		try {
			in = new BufferedReader(new FileReader(file));
			String s;
			while((s = in.readLine()) != null){
				String[] tokens = s.split("::");
				song.addElement(new Song(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6]));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("파일 없음!!");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("파일 읽기 오류..");
		}
	}
	public void saveFile(){
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(file));
			Iterator<Song> it = song.iterator();
			while(it.hasNext()){
				Song s = it.next();
				out.write(s.getSongName() + "::" + s.getWriterInfo() + "::" + s.getLabelsPitched() + "::" + s.getArtist() + "::" +
							s.getPlaced() + "::" + s.getHold() + "::" + s.getReleaseDate() + "\n");
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("파일 쓰기 오류..");
		}
	}
	public int setFilter(String s){
		int filter = 0;
		switch(s){
			case "songName":		filter = 1; break;
			case "writerInfo":		filter = 2; break;
			case "labelsPitched":	filter = 3; break;
			case "artist":			filter = 4; break;
			case "placed":			filter = 5; break;
			case "hold":			filter = 6; break;
			case "releaseDate":		filter = 7; break;
		}
		return filter;
	}
	public Vector searchingWith(String text, int filter){
		if(filter == 0){
			return null;
		}
		Vector<Song> music = new Vector<Song>();
		Iterator<Song> it = song.iterator();
		while(it.hasNext()){
			Song s = it.next();
			if(isExist(s, filter, text))	music.addElement(s);
		}
		return music;
	}
	public Song searchingPerfectMatching(String name, String writer, String singer, String date){
		Iterator<Song> it = song.iterator();
		Song s = null;
		while(it.hasNext()){
			s = it.next();
			if(s.getSongName().equals(name) && s.getWriterInfo().equals(writer) &&
					s.getArtist().equals(singer) && s.getReleaseDate().equals(date)){
				break;
			}
		}
		return s;
	}
	private boolean isExist(Song s, int filterNo, String text){
		switch(filterNo){
			case 1: return (s.getSongName().equals(text));
			case 2: return (s.getWriterInfo().equals(text));
			case 3: return (s.getLabelsPitched().equals(text));
			case 4: return (s.getArtist().equals(text));
			case 5: return (s.getPlaced().equals(text));
			case 6: return (s.getHold().equals(text));
			case 7: return (s.getReleaseDate().equals(text));
		}
		return false;	// 그 어떤 필터도 아닐때
	}	
	
	class SearchingPanel extends JPanel {
		JLabel name = new JLabel("Song Name");
		JTextField songName = new JTextField(10);
		JLabel writer = new JLabel("Writer Info");
		JTextField writerInfo = new JTextField(10);
		JLabel labels = new JLabel("Labels Pitched To");
		JTextField labelsPitched = new JTextField(10);
		JLabel artist = new JLabel("Artist Name");
		JTextField artistName = new JTextField(10);
		JCheckBox placed = new JCheckBox("Placed");
		JCheckBox hold = new JCheckBox("Hold");
		JLabel release = new JLabel("Release Date");
		JTextField releaseYear = new JTextField(4);
		JLabel year = new JLabel("년");
		JTextField releaseMonth = new JTextField(2);
		JLabel month = new JLabel("월");
		JTextField releaseDay = new JTextField(2);
		JLabel day = new JLabel("일");
		
		JButton save = new JButton("Save");
		JButton search = new JButton("Search");
		JButton showAll = new JButton("Show All");
		JButton delete = new JButton("delete");
		JLabel blank = new JLabel("       ");
		
		public SearchingPanel(){
			this.setLayout(new FlowLayout());
			
			add(name);
			add(songName);
			add(writer);
			add(writerInfo);
			add(labels);
			add(labelsPitched);
			add(artist);
			add(artistName);
			add(placed);
			add(hold);
			
			add(blank);
			add(release);
			add(releaseYear);
			add(year);
			add(releaseMonth);
			add(month);
			add(releaseDay);
			add(day);
			add(save);
			add(search);
			add(showAll);
			add(delete);
			
			save.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Song s = new Song(songName.getText(), writerInfo.getText(), labelsPitched.getText(), artistName.getText(),
							isChecked(placed.isSelected()), isChecked(hold.isSelected()),
							releaseYear.getText() + "-" + releaseMonth.getText() + "-" + releaseDay.getText());
					song.addElement(s);
					refreshTable();
				}
			});
			search.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int f = 0;
					String s = null;
					if(!songName.getText().equals("")){
						f = setFilter("songName");
						s = songName.getText();
					}
					else if(!writerInfo.getText().equals("")){
						f = setFilter("writerInfo");
						s = writerInfo.getText();
					}
					else if(!labelsPitched.getText().equals("")){
						f = setFilter("labelsPitched");
						s = labelsPitched.getText();
					}
					else if(!artistName.getText().equals("")){
						f = setFilter("artist");
						s = artistName.getText();
					}
					else if(placed.isSelected()){
						f = setFilter("placed");
						s = "Check";
					}
					else if(hold.isSelected()){
						f = setFilter("hold");
						s = "Check";
					}
					else if(!releaseYear.getText().equals("") && !releaseMonth.getText().equals("") && !releaseDay.getText().equals("")){
						f = setFilter("releaseDate");
						s = releaseYear.getText() + "-" + releaseMonth.getText() + "-" + releaseDay.getText();
					}
					
					refreshTable(searchingWith(s, f));
				}
			});
			showAll.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					refreshTable();
				}
			});
			delete.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String name = songTable.getModel().getValueAt(songTable.getSelectedRow(), 0).toString();	// 선택된 곡명
					String writer = songTable.getModel().getValueAt(songTable.getSelectedRow(), 1).toString();	// 선택된 곡의 작곡가
					String singer = songTable.getModel().getValueAt(songTable.getSelectedRow(), 3).toString();	// 선택된 곡의 가수
					String date = songTable.getModel().getValueAt(songTable.getSelectedRow(), 6).toString();	// 선택된 곡의 발매일
					
					Song s = searchingPerfectMatching(name, writer, singer, date);
					if(s == null)	return;	// 완벽하게 맞는 게없다면 그냥 끝내랴
					
					song.remove(s);		// 맞는게 있다면 지워라
					
					refreshTable();
				}
			});
		}
		public String isChecked(boolean b){
			if(b)	return "Check";
			else	return "Non Check";
		}
	}
	
	class JFrameWindowClosingEventListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			JFrame frame = (JFrame) e.getSource();
			frame.dispose();
			saveFile();
		}
	}
	
	public static void main(String[] args) {
		new MainFrame();
	}
}

class Song {
	private String songName = null;
	private String writerInfo = null;
	private String labelsPitched = null;
	private String artist = null;
	private String placed = null;
	private String hold = null;
	private String releaseDate = null;
	
	public Song(String name, String writer, String label, String artist, String placed, String hold, String release){
		this.songName = name;
		this.writerInfo = writer;
		this.labelsPitched = label;
		this.artist = artist;
		this.placed = placed;
		this.hold = hold;
		this.releaseDate = release;
	}
	public String getSongName() {
		return songName;
	}
	public String getWriterInfo() {
		return writerInfo;
	}
	public String getLabelsPitched() {
		return labelsPitched;
	}
	public String getArtist() {
		return artist;
	}
	public String getPlaced() {
		return placed;
	}
	public String getHold() {
		return hold;
	}
	public String getReleaseDate() {
		return releaseDate;
	}
}