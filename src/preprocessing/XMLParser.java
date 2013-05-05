import java.io.*;
import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.*;


public class XMLParser{
	static HashMap<String, Integer> wordMap = new HashMap<String, Integer>();	

	public static void parse(File xmlFile){
		String name = xmlFile.getName();
		String year = name.substring(2,6);
		try{
			Document doc = Jsoup.parse(xmlFile, "utf-8");
			// System.out.println(doc.select("topic").size());
			int count = 0;
			for(Element topic : doc.select("topic")){
				// System.out.println(topic.attr("id"));
				for(Element wordE : topic.getElementsByTag("word")){
					// System.out.printf("%s: %s\n", word.text(), word.attr("weight"));
					String word = wordE.text();
					Integer ind = wordMap.get(word);
					if(ind == null){
						count++;
						ind = 0;
					}
					wordMap.put(word,ind);

				}
				// System.out.println("");
			}
			System.out.printf("%d, %d\n", count, wordMap.size());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		 try { 
			File dir = new File(args[0]);
			for(File xmlFile : dir.listFiles()){				
				parse(xmlFile);
			}			
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	}
}