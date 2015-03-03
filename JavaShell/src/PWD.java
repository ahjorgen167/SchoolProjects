import java.*;
import java.util.concurrent.*;
import java.util.*;
import java.io.*;
import java.util.ArrayList;

public class PWD extends Filter {
	public List<String> commandLine;
	List<String> historyList;

	public PWD(BlockingQueue<Object> in, BlockingQueue<Object> out,
			List<String> commandLine, List<String> historyList) {
		super(in, out);
		this.historyList = historyList;
		this.commandLine = commandLine;
		this.in = in;
		this.out = out;
	}

	// Call present working directory
	@Override
	public void run() {
		Object o = "";
		try {
			o = historyListTransform();
			in.put(o);
		} catch (Exception e) {
		}
	}

	public Object transform(Object o) {
		return o;
	}

	// Convert directory list into usable string
	private String historyListTransform() {
		String historyOutput = this.historyList.get(0);
		for (int i = 1; i < historyList.size(); i++) {
			historyOutput += System.getProperty("file.separator");// "\\";
			historyOutput += historyList.get(i);
		}
		return historyOutput;
	}
}
