<h1> Text Mining for Hidden Relations and Trending </h1>

<h2> Directory Description </h2>
<pre>
data/robot_robotic/patents.csv -> table for the parsed patents' meta-data
data/robot_robotic/raw -> the original parsed text files
data/robot_robotic/tm_xml -> the topic modeling output in XML format

data/documentation/proposal -> the proposal tex and pdf files
data/documentation/report -> see final2.pdf for the finalized version

data/src -> the source code we wrote for this project, which is explained below
</pre>

<h2> Crawler </h2>
<pre>
/src/crawler -> Java code used to scrape down the patents data
  > java URLGenerator > robot_robotic_url.txt // generate the patent url for parsing
  > java Crawler robot_robotic_url.txt // generates the raw text data as seen in /data/robot_robotic/raw
</pre>


<h2> OLAP </h2>
<pre>
/src/OLAP -> Java code used to slice the original raw patent text by each year
  > java Slicer /data/robot_robotic/ // generate sub directory by year as seen in this folder
 </pre>

<h2> Mallet </h2>
<pre>
/src/mallet -> Java code used to generate mallet commands for LDA
  > java MalletRunner > toRun.txt // outputs the necessary commands to run LDA for all years
 </pre>

<h2> Parsing Mallet XML output  </h2>
<pre>
/src/preprocessing -> Java code used to parse the XML output from the Topic Modeling package of MALLET
  > java XMLParser /data/robot_robotic/tm_xml // generate csv file for each year, with row as year-topic and column as the word vector
 </pre>

<h2> Thematic Particle Clustering (TPC) </h2>
<pre>
/src/TPC -> Matlab code used to run all the experiments we've run for TPC
Simply uncomment the corresponding commands in the code and run particleClustering() in Matlab
 </pre>




<h2> Topic Convergence Graph (TCG) </h2>
<pre>
Included files:
	tm_results:  is a directory with all the topics from 1981 to 2013
	MyDoc.java: wrapper class for documents
	Tuple.java: wrapper class for two data pieces at a time
	Topic.java wrapper class for holding word distributions
	XMLParser: Main class where most of the work was done. Reads in documents. Make topics. Parses topics from year to year. Prints out paths topics took to STDIO.

Tree Convergence Graph Code Base
	The java code for TCG just spits out data that was used in the included TCG_graph.xlsx

	To run the  java code run the main method of  XMLParser.java with 1 command line argument: /PATH/TO/tm_results
		tm_results is included with this turnin
	This code was created in eclipse, as such I do not have the linux command line to run these file.
</pre>