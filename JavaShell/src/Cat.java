import java.io.*;
import java.lang.String;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Cat extends Filter {
	public List<String> commandLine;
	public List<String> historyList;

	public Cat(BlockingQueue<Object> in, BlockingQueue<Object> out,
			List<String> commandLine, List<String> historyList) {
		super(in, out);
		this.commandLine = commandLine;
		this.historyList = historyList;
		this.in = in;
		this.out = out;
	}
	//Start cat command
	@Override
	public void run() {
		
		//declare variables
		Object o = "";
		o = transform(o);	
		//Minor error checking.
		if (commandLine.get(1).equals("|") || commandLine.get(1).equals(">")){
			System.out.println("Super duper error occurred: missing argument.");
			return;}
		for (int i = 1; i < commandLine.size(); i++) {
			if (commandLine.get(i).equals("|") || commandLine.get(i).equals(">"))
				return;
			try {
				File tempFile = new File(historyListTransform(), commandLine.get(i));
				if (!tempFile.isFile()) {
					System.out
							.println("Invalid argument: file not found.");
					in.clear();
					return;
				}
				FileReader fr = new FileReader(tempFile);
				BufferedReader textReader = new BufferedReader(fr);
				String s;
				while ((s = textReader.readLine()) != null) {
					this.in.put(s);
				}
				textReader.close();
				fr.close();
			} catch (IOException e) {
			} catch (InterruptedException ex) {
			}
		}
	}
	@Override
	public Object transform(Object o) {

		o = null;
		return o;
	}
	
	//interpret the directory list into a usable string
    private String historyListTransform(){
		String historyOutput = historyList.get(0);
    	for (int i = 1; i < historyList.size(); i++) {
				historyOutput += System.getProperty("file.separator");
			historyOutput += historyList.get(i);
		}
		return historyOutput;
    }
	
}
