import java.io.*;
import java.util.*;

// class DirManager{
// 	String dir;
// 	String header;
// 	public DirManager(String d){
// 		dir = d;	
// 	}	

// 	public void insert(String year, String patID, String line){

// 	}
// }

public class Slicer{

	public static void slice(){
		try{
			File file = new File("2013/patents.csv");
			file.mkdirs();
		}catch(Exception e){
			System.err.println("Error creating file and dirs: "+e);
		}
	}

	public static void main(String[] args){
		String dir = args[0];
		File patents = new File(dir+"patents.csv");
		try{
			BufferedReader reader = new BufferedReader(new FileReader(patents));
			String header = reader.readLine();
			String line = reader.readLine();
			while(line!=null){
				StringTokenizer st = new StringTokenizer(line, ",");
				String patID = st.nextToken().replace("\"","");
				st.nextToken();
				st.nextToken();
				st.nextToken();
				String date = st.nextToken();
				int n = date.length();
				String year = date.substring(n-5, n-1);
				System.out.printf("%s %s\n", patID, year);
				Runtime.getRuntime().exec(String.format("mkdir %s", year));
				Runtime.getRuntime().exec(String.format("cp %s%s.txt %s/%s.txt", dir, patID, year, patID));
				line = reader.readLine();
			}
		}catch(Exception e){
			System.err.println("Error reading csv input: "+e);
		}
		
	}
}