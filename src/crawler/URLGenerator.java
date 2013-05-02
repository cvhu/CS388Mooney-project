import java.io.*;
import java.util.*;

public class URLGenerator{
	public static void main(String[] args) throws IOException{
		String url1 = "http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&p=1&u=%2Fnetahtml%2FPTO%2Fsearch-bool.html&r=";
		String url2 = "&f=G&l=50&co1=AND&d=PTXT&s1=robot&s2=robotic&OS=robot+AND+robotic&RS=robot+AND+robotic";
		for(int i = 0; i < 11597; i++){
			System.out.printf("%s%d%s\n",url1, (i+1), url2);
		}		
	}	
	
}

