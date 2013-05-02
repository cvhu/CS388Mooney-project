import java.io.*;
import java.util.*;

public class MalletRunner{

	public static void main(String[] args){
		try{
			for(int i = 1981; i <= 2013; i++){
				// System.out.println(i);
				System.out.println(String.format("bin/mallet import-dir --input tm_data/robot_robotic/%d --output tm_results/rr%d.mallet --keep-sequence --remove-stopwords & ", i, i));
				// System.out.println("import");
				// System.out.println(String.format("bin/mallet train-topics --input tm_results/rr%d.mallet --num-topics 100 --num-iterations 500 --xml-topic-phrase-report tm_results/rr%d-topic-phrase-report.xml", i,i));
				// Runtime.getRuntime().exec(String.format("bin/mallet train-topics --input tm_results/rr%d.mallet --num-topics 100 --num-iterations 500 --xml-topic-phrase-report tm_results/rr%d-topic-phrase-report.xml", i,i));
				// System.out.println("train");
			}
			for(int i = 1981; i <= 2013; i++){
				// System.out.println(i);
				// System.out.println(String.format("bin/mallet import-dir --input tm_data/robot_robotic/%d --output tm_results/rr%d.mallet --keep-sequence --remove-stopwords", i, i));
				// System.out.println("import");
				System.out.println(String.format("bin/mallet train-topics --input tm_results/rr%d.mallet --num-topics 100 --num-iterations 500 --xml-topic-phrase-report tm_results/rr%d-topic-phrase-report.xml & ", i,i));
				// Runtime.getRuntime().exec(String.format("bin/mallet train-topics --input tm_results/rr%d.mallet --num-topics 100 --num-iterations 500 --xml-topic-phrase-report tm_results/rr%d-topic-phrase-report.xml", i,i));
				// System.out.println("train");
			}
		}catch(Exception e){
			System.err.println(e);
		}
		
	}
}