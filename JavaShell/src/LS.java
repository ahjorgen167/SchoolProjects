import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class LS extends Filter {
	List<String> directoryList;

	public LS(BlockingQueue<Object> in, BlockingQueue<Object> out,
			List<String> directoryList) {
		super(in, out);
		this.in = in;
		this.out = out;
		this.directoryList = directoryList;
	}

	@Override
	public void run() {
		// put all file names into present working directory
		File curFolder = new File(historyListTransform());
		for (File file : curFolder.listFiles()) {
			if (file.isFile() || file.isDirectory())
				try {
					in.put(file.getName());
				} catch (InterruptedException e) {
				}
		}
	}

	public Object transform(Object o) {
		return o;
	}

	// convert directory stack into usable string
	private String historyListTransform() {
		String historyOutput = directoryList.get(0);
		for (int i = 1; i < directoryList.size(); i++) {
			historyOutput += System.getProperty("file.separator");
			;
			historyOutput += directoryList.get(i);
		}
		return historyOutput;
	}
}
