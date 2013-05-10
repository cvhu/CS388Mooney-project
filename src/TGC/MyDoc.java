import java.util.ArrayList;


public class MyDoc {

	/**
	 * @param args
	 */
	public String filename = "";
	public int year = -1;
	public ArrayList<Topic> topics = new ArrayList<Topic>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public MyDoc(String filename, int year){
		this.filename = filename;
		this.year = year;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return topics.toString();
//		return year+"";
	}
}
