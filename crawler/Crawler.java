import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.*;

import java.io.*;
import java.util.*;
import java.lang.*;

class Patent{
	static int count = 0;
	String id;
	String kind;
	String pn;
	String title;	
	String date;
	String file_date;
	String url;
	
	String aid; //assignee id
	LinkedList<String> inventors;	
	LinkedList<String> usc; //US class
	
	Connection connection;	
	Document doc;
	
	public Patent(String u){
		id = "pat"+count;
		count++;
		url = u;
	}
	
	public void fetch(){
		try{
			System.out.printf("[P] Fetching patent #%s\n",id);
			connection  = Jsoup.connect(url);
			connection.timeout(20000);
			System.out.println("...url connected");
			doc = connection.get();
			parseTitle();  //title
			parseUSPN();  //pn+kind+date
			parseFileDate(); //file_date
			
			parseAssignee();			
			parseInventors(); //inventors			
			parseUSClass();
			
			
			System.out.println("...done");
		}catch(Exception e){
			System.err.println("[P] Error fetching pat:"+e);
		}		
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\",", id, kind, pn.replace(",",""), title, date, file_date, url, aid));
		for(String inventor : inventors){
			sb.append(inventor+" ");
		}
		sb.append(",");
		for(String c : usc){
			sb.append(c+" ");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	public void parseText(String dir){
		File file;
		FileOutputStream fo;
		System.out.printf("Writing %s...\n", id);
		try{
			file = new File(dir+id+".txt");
			fo = new FileOutputStream(file);
			String t = doc.select("center>b").get(0).parent().nextElementSibling().text();
			String text = doc.text();
			int ci = text.indexOf("Claims");
			int di = text.indexOf("Description");
			t = t + "\n" + text.substring(ci, di);
			fo.write(t.getBytes());
			fo.close();
		}catch(Exception e){
			System.err.println("error parsing text: "+id+".."+e);
		}
	}
	
	public void parseTitle(){
		try{
			title = doc.select("font[size=+1]").get(0).text().replace(",", " ");	
		}catch(Exception e){
			title = "";
			System.err.println("[P] Error parsing pat:"+e);
		}		
	}
	
	public void parseUSPN(){
		try{
			Element table1 = doc.select("table[width=100%]").get(1);
			String usp = table1.getElementsByTag("tr").get(0).getElementsByTag("td").get(0).text();
			if(usp.equals("United States Patent")){
				kind = "USP";
			}else if(usp.equals("United States Patent Application")){
				kind = "USPA";
			}else{
				kind = "Others";
			}
			pn = table1.getElementsByTag("tr").get(0).getElementsByTag("td").get(1).text();
			date = table1.getElementsByTag("tr").last().getElementsByTag("td").get(1).text().replace(","," ");		
		}catch(Exception e){
			kind = "error";
			pn = "";
			date = "";
			System.err.println("[P] Error parsingUSPN pat:"+e);
		}		
	}
	
	public void parseFileDate(){
		try{
			file_date = doc.getElementsContainingOwnText("Filed:").get(0).nextElementSibling().text().replace(",", " ");
		}catch(Exception e){
			file_date = "";
			System.err.println("[P] Error parsingFileDate pat:"+e);
		}
	}
	
	public void parseAssignee(){
		try{
			String assigneeText = doc.getElementsContainingOwnText("Assignee:").get(0).nextElementSibling().text();
			aid = Assignee.searchRaw(assigneeText);
		}catch(Exception e){
			aid = null;
			System.err.println("[P] Error parsingAssignee pat:"+e);
		}
	}
	

	
	public void parseInventors(){
		try{
			String inventorsText = doc.getElementsContainingOwnText("Inventors:").get(0).nextElementSibling().text();
			inventors = Inventor.buildRaw(inventorsText);
		}catch(Exception e){
			inventors = new LinkedList<String>();
			System.err.println("[P] Error parsingInventors pat:"+e);
		}
	}
	
	public void parseUSClass(){
		usc = new LinkedList<String>();
		try{
			String text = doc.getElementsContainingOwnText("U.S. Class:").get(0).parent().nextElementSibling().text();
			StringTokenizer st = new StringTokenizer(text, ";");
			while(st.hasMoreTokens()){
				usc.add(st.nextToken());
			}			
		}catch(Exception e){			
			System.err.println("[P] Error parsingInventors pat:"+e);
		}
	}
	

}

class Assignee{
	static int count = 0;
	static HashMap<String, String> assigneeMap;

	public static String searchRaw(String raw){
		int aci = raw.indexOf("(");
		String co = raw.replace(",", " ").toLowerCase();
		String ci = "";
		String st = "";
		if(aci>0){
			co = raw.substring(0,aci).replace(",", " ").toLowerCase();
			String cs = raw.substring(aci+1, raw.indexOf(")"));
			ci = cs.substring(0, cs.indexOf(",")).replace(",", " ").toLowerCase();
			st = cs.substring(cs.indexOf(",")+1).replace(",", " ").toLowerCase();
		}
		return search(co+", "+ci+", "+st);
	}
	public static String search(String query){
		String aid = assigneeMap.get(query);
		if(aid==null){
			aid = "a"+count;
			count++;
			assigneeMap.put(query, aid);
		}
		return aid;
	}
}

class Inventor{
	static int count = 0;
	static HashMap<String, String> inventorMap;
	public static LinkedList<String> buildRaw(String raw){
		LinkedList<String> iids = new LinkedList<String>();
		int del = raw.indexOf(")");
		String inventor;
		while(del != -1){
			StringBuffer sba = new StringBuffer();
			inventor = raw.substring(0,del);
			sba.append(inventor.substring(0, inventor.indexOf("(")-1).trim()+",");
			sba.append(inventor.substring(inventor.indexOf("(")+1, inventor.indexOf(",")).trim()+",");
			sba.append(inventor.substring(inventor.indexOf(",")+1).trim()+",");						
			String query = sba.toString();
			iids.add(search(query));
			if(raw.length() < del+3){
				break;
			}
			raw = raw.substring(del+3);						
			del = raw.indexOf(")");				
		}
		return iids;
	}
	public static String search(String query){
		String iid = inventorMap.get(query);
		if(iid==null){
			iid = "i"+count;
			count++;
			inventorMap.put(query, iid);
		}
		return iid;
	}
	
}

public class Crawler{
	static String dir = "robot_robotic/";
	static BufferedReader csvBR;
	static OutputStream outputStream;
	public static void fetchURL(String url){
		Patent pat = new Patent(url);
		pat.fetch();
		pat.parseText(dir);
		try{
			outputStream.write(pat.toString().getBytes());
		}catch(Exception e){
			System.err.println("error writing pat: "+pat.id+"--"+e);
		}
	}
	public static void main(String[] args) throws IOException{		
		File output = new File(dir+"patents.csv");
		outputStream = new FileOutputStream(output);
		String header = "pid, kind, pn, title, date, file_date, url, aid, inventors, usc\n";
		outputStream.write(header.getBytes());
		File csvFile = new File(args[0]);
		csvBR = new BufferedReader(new FileReader(csvFile));
		String csvLine = csvBR.readLine();
		while(csvLine != null && csvLine.length() != 0){
			fetchURL(csvLine);
			csvLine = csvBR.readLine();
		}
	}
}