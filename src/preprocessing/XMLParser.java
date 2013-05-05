import java.io.*;
import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.*;

class Topic{
	HashMap<Integer, Double> weights;
	public Topic(){
		weights = new HashMap<Integer, Double>();
	}

	public void add(int ind, double weight){
		weights.put(ind, weight);
	}

	public double get(int ind){
		Double weight = weights.get(ind);
		if(weight==null){
			return 0.0;
		}else{
			return weight;
		}	
	}
}


public class XMLParser{
	static LinkedList<String> vocab = new LinkedList<String>();
	static LinkedList<String> years = new LinkedList<String>();
	static LinkedList<LinkedList<Topic>> yearTopics = new LinkedList<LinkedList<Topic>>();


	public static void parse(File xmlFile){
		String name = xmlFile.getName();
		String year = name.substring(2,6);
		LinkedList<Topic> topics = new LinkedList<Topic>();
		try{
			Document doc = Jsoup.parse(xmlFile, "utf-8");
			// System.out.println(doc.select("topic").size());
			int count = 0;
			for(Element topicE : doc.select("topic")){
				// System.out.println(topic.attr("id"));
				Topic topic = new Topic();
				for(Element wordE : topicE.getElementsByTag("word")){
					// System.out.printf("%s: %s\n", word.text(), word.attr("weight"));
					String word = wordE.text();
					Integer ind = vocab.indexOf(word);
					if(ind == -1){
						count++;
						ind = vocab.size();
						vocab.add(word);						
					}
					topic.add(ind, Double.parseDouble(wordE.attr("weight")));

				}
				topics.add(topic);
				// System.out.println("");
			}
			years.add(year);
			yearTopics.add(topics);
			System.out.printf("%d, %d\n", count, vocab.size());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void printCSVs(){
		for(int i = 0; i < years.size(); i++){
			System.out.printf("Year %s: \n", years.get(i));
			try{
				FileOutputStream fos = new FileOutputStream(new File("csv/"+years.get(i)+".csv"));			
				StringBuffer sb = new StringBuffer();
				for(Topic topic : yearTopics.get(i)){
					for(int j = 0; j < vocab.size(); j++){
						sb.append(topic.get(j));
						if(j==(vocab.size()-1)){
							sb.append("\n");
						}else{
							sb.append(", ");
						}
					}
				}
				fos.write(sb.toString().getBytes());
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}

	public static void printCSV(){
		try{				
			FileOutputStream fos = new FileOutputStream(new File("csv/all.csv"));
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < years.size(); i++){
				// System.out.printf("Year %s: \n", years.get(i));								
				for(Topic topic : yearTopics.get(i)){
					for(int j = 0; j < vocab.size(); j++){
						sb.append(topic.get(j));
						if(j==(vocab.size()-1)){
							sb.append("\n");
						}else{
							sb.append(", ");
						}
					}
				}				
				
			}
			fos.write(sb.toString().getBytes());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void printVocab(){
		try{				
			FileOutputStream fos = new FileOutputStream(new File("csv/vocab.csv"));
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < vocab.size(); i++){
				sb.append(vocab.get(i));
				if(i==(vocab.size()-1)){
					sb.append("\n");
				}else{
					sb.append(", ");
				}					
			}
			fos.write(sb.toString().getBytes());
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
			printCSV();
			printVocab();
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	}
}