import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;

//create file to write text
public class OutputRedirect extends Filter {
	String title;
	List<String> historyList;

	public OutputRedirect(BlockingQueue<Object> in, BlockingQueue<Object> out,
			String title, List<String> historyList) {
		super(in, out);
		this.title = title;
		this.historyList = historyList;
		this.in = in;
		this.out = out;
	}

	@Override
	public void run() {

		// Declare variables
		BufferedWriter writer = null;
		Object p;
		File actualFile = new File(historyListTransform(), title);
		// Make sure that there is something to output, else wait
		while (out.isEmpty()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Write to file
		while (!out.isEmpty()) {
			try {
				writer = new BufferedWriter(new FileWriter(actualFile));
				while (!out.isEmpty()) {
					p = out.take();
					writer.write(p.toString());
					writer.append(System.lineSeparator());
				}

				writer.close(); // close print stream
			} catch (IOException e) {
			} catch (InterruptedException ex) {
			}
		}

	}

	@Override
	public Object transform(Object o) {
		return o;
	}

	// Convert directory List into usable String
	private String historyListTransform() {
		String historyOutput = historyList.get(0);
		for (int i = 1; i < historyList.size(); i++) {
			historyOutput += System.getProperty("file.separator");
			historyOutput += historyList.get(i);
		}
		return historyOutput;
	}
}