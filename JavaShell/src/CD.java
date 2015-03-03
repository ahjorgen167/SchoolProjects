import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class CD {
	List<String> commandLine;
	List<String> workingDirectory;

	public CD(List<String> commandLine, List<String> workingDirectory) {
		this.commandLine = commandLine;
		this.workingDirectory = workingDirectory;
	}

	public List<String> changeDirectory(String newDirectory) {
		// declare variables and check for .. or .
		List<String> temp = new ArrayList<String>();
		temp.addAll(workingDirectory);
		if (newDirectory.equals("..")) {
			temp.remove(temp.size() - 1);
			return temp;
		} else if (newDirectory.equals(".")) {
			return temp;
		}

		// "change" new directory
		File tempFile = new File(historyListTransform(), newDirectory);
		if (!tempFile.isDirectory()) {
			System.out.println("Invalid argument: directory not found.");
			return this.workingDirectory;
		} else {
			temp.add(newDirectory);
			return temp;
		}
	}

	// convert directory stack into usable string
	private String historyListTransform() {
		String historyOutput = this.workingDirectory.get(0);
		for (int i = 1; i < workingDirectory.size(); i++) {
			historyOutput += System.getProperty("file.separator");
			historyOutput += workingDirectory.get(i);
		}
		return historyOutput;
	}
}