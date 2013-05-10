import java.io.*;
import java.util.*;

import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.*;


public class XMLParser{
	public static void main(String[] args){
		 try { 
			File xmlFiles = new File(args[0]);
			File [] xmlFileArr = xmlFiles.listFiles();
			ArrayList<MyDoc> docArray = new ArrayList<MyDoc>();
			for (int i = 0 ; i < xmlFileArr.length; i++){
				File xmlFile = xmlFileArr[i];
				MyDoc mydoc = new MyDoc(xmlFile.getName(), i+1981);
				Document doc = Jsoup.parse(xmlFile, "utf-8");
				for(Element topic : doc.select("topic")){
					Topic temp_topic = new Topic();
					for(Element word : topic.getElementsByTag("word")){
						temp_topic.words.add(new Tuple<String, Double>(word.text(), Double.parseDouble(word.attr("weight"))) );
					}
					mydoc.topics.add(temp_topic);
				}    
				docArray.add(mydoc);
			}
//				System.out.println(docArray);
				ArrayList<ArrayList<Tuple<Double, Integer>>> pointerStructure =  new ArrayList<ArrayList<Tuple<Double, Integer>>>();
				// Double = prob  Integer = next node
				//initalize array
				for (int i = 0; i < docArray.size(); i++) {
					ArrayList<Tuple<Double, Integer>> temp_array = new ArrayList<Tuple<Double, Integer>>();
					for (int j = 0; j < docArray.get(1).topics.size(); j++) {
						temp_array.add(new Tuple<Double, Integer>(0.0, 0));
					}
					pointerStructure.add(temp_array);
				}
				for (int d = 0; d < docArray.size()-1; d++) {
					for( int i = 0; i < docArray.get(d).topics.size() ; i++){
						ArrayList<Double> temp_100 = new ArrayList<Double>();
						for( int j = 0; j < docArray.get(d+1).topics.size() ; j++){
//							temp_100.add( topic_similarity_rms(docArray.get(d).topics.get(i), docArray.get(d+1).topics.get(j))   );
							temp_100.add( topic_similarity_cos(docArray.get(d).topics.get(i), docArray.get(d+1).topics.get(j))   );
//							if (j==7){
//								System.out.println(docArray.get(2).topics.get(j) );
//							}
						}
						pointerStructure.get(d).get(i).x = Collections.max(temp_100) ;
						pointerStructure.get(d).get(i).y = temp_100.indexOf(Collections.max(temp_100)) ;
//						System.out.println(d +" , "+i);
					}
				}
				
				// follow a path though the pointer structure
				for (int k = 0; k < 10; k++) { // 10 years of data
					for (int j = 0; j < 10; j++) { // 10 topics per year
						int nextpos = j;
						String struct = "";
						for(int zer = 0 ; zer < k ; zer ++){ // add buffer for each skipped year
							if(zer <k-1){
								struct += "*"+",";
							}else{
								struct += "*"+"";
							}
						}
						for (int i = k; i < pointerStructure.size(); i++) {
							struct += ","+pointerStructure.get(i).get(nextpos).y;
							nextpos = pointerStructure.get(i).get(nextpos).y;
						}
						System.out.println(struct);  // print out paths
					}
				}
				
			
	    } catch (Exception e) {
			e.printStackTrace();
	    }
	}
	
	public static double topic_similarity_rms (Topic t1, Topic t2) {
		Topic bigTopic =null;
		Topic smallTopic = null;
		if(t1.words.size() > t2.words.size()){
			bigTopic = t1;
			smallTopic = t2;
		}else{
			bigTopic = t2;
			smallTopic = t1;
		}
		
		double sum = 0;
		int match = 0;
		for (int i = 0;  i < bigTopic.words.size(); i++ ){
			for (int j = 0;  j < smallTopic.words.size(); j++ ){
				if(bigTopic.words.get(i).x.equals(smallTopic.words.get(j).x ) ){// we found a matching word
					sum += Math.pow( (bigTopic.words.get(i).y - smallTopic.words.get(j).y )  , 2);
					match = 1;
				}
			}
			// We did no find an match for the word in bigtopic
			if (match == 0){
				//sum += Math.pow( (bigTopic.words.get(i).y)  , 2);
			}
			match = 0;
		}
		double mean;
		if (bigTopic.words.size() == 0){
			 mean = 0;
		}else{
			 mean = sum/bigTopic.words.size();
		}
		
		double rms =  Math.sqrt(mean);
//		rms = Math.pow(rms, bigTopic.words.size());  // take the nth root to nomalize for size
		return rms;
	}

	public static double topic_similarity_cos(Topic t1, Topic t2) {
		Topic bigTopic =null;
		Topic smallTopic = null;
		if(t1.words.size() > t2.words.size()){
			bigTopic = t1;
			smallTopic = t2;
		}else{
			bigTopic = t2;
			smallTopic = t1;
		}
		
		double dotprod = 0;
		int match = 0;
		for (int i = 0;  i < bigTopic.words.size(); i++ ){
			for (int j = 0;  j < smallTopic.words.size(); j++ ){
				if(bigTopic.words.get(i).x.equals(smallTopic.words.get(j).x ) ){// we found a matching word
					dotprod +=  bigTopic.words.get(i).y * smallTopic.words.get(j).y ;
					match = 1;
				}
			}
			// We did no find an match for the word in bigtopic
			if (match == 0){
				//sum += Math.pow( (bigTopic.words.get(i).y)  , 2);
			}
			match = 0;
		}
		
		double magBigTopic = 0;
		for (int i = 0;  i < bigTopic.words.size(); i++ ){
			magBigTopic += Math.pow(bigTopic.words.get(i).y, 2);
		}
		magBigTopic = Math.sqrt(magBigTopic);
		
		double magSmallTopic = 0;
		for (int i = 0;  i < smallTopic.words.size(); i++ ){
			magSmallTopic += Math.pow(smallTopic.words.get(i).y, 2);
		}
		magSmallTopic = Math.sqrt(magSmallTopic);
		
		
		return dotprod/((magBigTopic)*(magSmallTopic));
		
//		double mean;
//		if (bigTopic.words.size() == 0){
//			 mean = 0;
//		}else{
//			 mean = dotprod/bigTopic.words.size();
//		}
//		
//		double rms =  Math.sqrt(mean);
////		rms = Math.pow(rms, bigTopic.words.size());  // take the nth root to nomalize for size
//		return rms;
	}
	
	
	public double multiWordEquals(String w1, String w2){
		String longword = "";
		String shortword = "";
		if (w1.length() > w2.length()){longword=w1;shortword=w2;}else{longword=w2;shortword=w1;}
		
		if(w1.length() == 1 && w2.length()==1){
			if (w1.equals(w2)){
				return 1;
			}
		}else{
			String [] longtokens = longword.split(" ");
			String [] shorttokens = shortword.split(" ");
			int count = 0;
			for (int i = 0; i < longtokens.length; i++) {
				for (int j = 0; j < shorttokens.length; j++) {
					if(longtokens[i].equals(shorttokens[j])){
						count++;
					} 
				}
			}
			return count/longtokens.length;
			
		}
		return 0;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}