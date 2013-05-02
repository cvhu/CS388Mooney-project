import java.io.*;
import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.*;


public class XMLParser{
	public static void main(String[] args){
		 try { 
			File xmlFile = new File(args[0]);
			Document doc = Jsoup.parse(xmlFile, "utf-8");
			System.out.println(doc.select("topic").size());			
			for(Element topic : doc.select("topic")){
				System.out.println(topic.attr("id"));
				for(Element word : topic.getElementsByTag("word")){
					System.out.printf("%s: %s\n", word.text(), word.attr("weight"));
				}
				System.out.println("");
			}
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	}
}