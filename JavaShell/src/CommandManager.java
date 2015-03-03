import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.ArrayList;

public class CommandManager {
	List<String> historyList = new ArrayList<String>();
	List<String> directoryList = new ArrayList<String>();

	public CommandManager(List<String> history, List<String> directoryList) {
		this.historyList = historyList;
		this.directoryList = directoryList;
	}

	public boolean evaluate(String commandLineInput)
			throws InterruptedException, FileNotFoundException,
			UnsupportedEncodingException {

		// Variables
		historyList.add(commandLineInput);
		List<String> commandList = parseyPants(commandLineInput);
		BlockingQueue<Object> pipein = new LinkedBlockingQueue<Object>();
		BlockingQueue<Object> pipeout = new LinkedBlockingQueue<Object>();
		boolean lc = false, sleep = false;
		Thread t1 = new Thread();

		// Error check
		if (errorCheck(commandList))
			return false;
		if (commandLineInput.contains("lc"))
			lc = true;

		// Check for first input command
		// PWD commnand
		if (commandList.get(0).equals("pwd")) {
			t1 = new PWD(pipein, pipeout, commandList, directoryList);
			t1.start();
			if (lc)
				t1.join();

			// CAT commnad
		} else if (commandList.get(0).equals("cat")) {
			t1 = new Cat(pipein, pipeout, commandList, directoryList);
			t1.start();
			if (lc)
				t1.join();

			// CD command with some minor error checking
		} else if (commandList.get(0).equals("cd")) {
			if (commandList.size() > 2) {
				System.out.println("Invalid argument: invalid pipe order.");
				return false;
			}
			CD cdClass = new CD(commandList, directoryList);
			try {
				this.directoryList = cdClass
						.changeDirectory(commandList.get(1));
			} catch (Exception e) {
				System.out.println("no directory specified.");
				return false;
			}
			return false;

			// LS command
		} else if (commandList.get(0).equals("ls")) {
			t1 = new LS(pipein, pipeout, directoryList);
			t1.start();
			if (lc)
				t1.join();

			// HISTORY command
		} else if (commandList.get(0).equals("history")) {
			if (historyList.size() == 1)
				return false;
			t1 = new History(pipein, pipeout, historyList);
			t1.start();
			if (lc || sleep)
				t1.join();

			// SLEEP command
		} else if (commandList.get(0).equals("sleep")) {
			sleep = true;
			try {
				t1 = new SubCmd_Sleep(pipein, pipeout,
						Integer.parseInt(commandList.get(1)));
			} catch (Exception e) {
				System.out.println("Invalid sleep argument.");
				return false;
			}
			t1.start();
			t1.join();

			// EXIT command
		} else if (commandList.get(0).equals("exit")) {
			if (commandList.size() == 1) {
				System.out.println("REPL exits. Bye.");
				return true;
			} else {
				System.out.println("Bad argument: invalid pipe order 76.");
				return false;
			}
		} else {

			// Check for invalid first commands
			System.out.println("Bad argument: invalid command.");
			return false;
		}

		// Check for grep or LC commands. Also checks if they're in the proper
		// order
		int orderCount = 1;
		if (commandLineInput.contains("grep")) {
			if (orderCount > commandList.lastIndexOf("grep")) {
				System.out.println("invalid pipe order 19.");
				return false;
			} else
				orderCount = commandList.lastIndexOf("grep");
			t1 = new Grep(pipein, pipeout, findSearchString(commandList));
			t1.start();
			if (lc || sleep)
				t1.join();
		} else {

			// Use switch stack (move data from in queue to out queue) if GREP
			// is not called
			t1 = new SwitchStack(pipein, pipeout);
			t1.start();
			if (lc || sleep)
				t1.join();
		}

		// LC command with some minor error checking
		for (int i = 1; i < commandList.size(); i++) {
			if (commandList.get(i).equals("|")
					&& commandList.get(i + 1).equals("lc")) {
				if (orderCount > commandList.lastIndexOf("lc")) {
					System.out.println("invalid pipe order 22.");
					return false;
				} else
					orderCount = commandList.lastIndexOf("lc");
				t1 = new LC(pipein, pipeout);
				t1.start();
				t1.join();
			}
		}

		// Determine output commands
		if (commandLineInput.contains(">")) { // OUTPUT REDIRECT
			t1 = new OutputRedirect(pipein, pipeout,
					commandList.get(commandList.size() - 1), directoryList);
			t1.start();
			if (lc || sleep)
				t1.join();
		} else {
			Thread.sleep(50);
			t1 = new PrintBlockingQueue(pipein, pipeout);
			t1.start();
			if (lc || sleep)
				t1.join();
		}
		t1.join();
		Thread.sleep(50);
		return false;

	}

	// Parse the commandLine input into a List
	private List<String> parseyPants(String commandLine) {
		String temp = commandLine.replace("|", " | ");
		commandLine = temp.replace(">", " > ");
		List<String> startTemp = Arrays
				.asList(commandLine.split("\\s*[ ]\\s*"));
		return startTemp;
	}

	// Determine any search keywords in case grep is called
	private List<String> findSearchString(List<String> commandLine) {
		List<String> searchQueries = new ArrayList<String>();
		for (int i = 0; i < commandLine.size(); i++) {
			if (commandLine.get(i).equals("|")
					&& commandLine.get(i + 1).equals("grep"))
				try {
					if (!commandLine.get(i + 2).equals("|")
							&& !commandLine.get(i + 2).equals(">"))
						searchQueries.add(commandLine.get(i + 2));
				} catch (Exception e) {
					System.out
							.println("Input error: missing argument or invalid pipe order.");
				}
		}
		return searchQueries;
	}

	// Error checking.
	private boolean errorCheck(List<String> commandLine) {
		next: if (commandLine.get(0).equals("cat")) {
			if (commandLine.get(1).equals("|")
					|| commandLine.get(1).equals(">")) {
				System.out.println("Error with input: missing argument.");
				return true;
			}
			for (int i = 1; i < commandLine.size(); i++) {
				if (commandLine.get(i).equals("|")
						|| commandLine.get(i).equals(">"))
					break next;
				try {
					File tempFile = new File(directoryListTransform(),
							commandLine.get(i));
					if (!tempFile.isFile()) {
						System.out.println("Invalid argument: file not found.");
						return true;
					}
				} catch (Exception e) {
				}
				if (commandLine.get(1).equals("|")
						|| commandLine.get(1).equals(">")) {
					System.out.println("Error with input: missing argument.");
					return true;
				}

			}
			if (commandLine.get(1).equals("|")
					|| commandLine.get(1).equals(">")) {
				System.out.println("missing argument.");
				return true;
			}
		}
		if (commandLine.get(0).equals("grep")
				|| commandLine.get(0).equals("lc")) {
			System.out.println("Bad argument: invalid pipe order 22.");
			return true;
		}
		if (commandLine.get(0).equals("sleep"))
			if (Integer.parseInt(commandLine.get(1)) < 0) {
				System.out.println("Sleep error: invalid argument.");
				return true;
			}
		for (int i = 0; i < commandLine.size(); i++) {
			if (commandLine.get(i).equals("|"))
				if (!commandLine.get(i + 1).equals("grep")
						&& !commandLine.get(i + 1).equals("lc")) {
					System.out.println("invalid pipe order 25.");
					return true;
				}
			// try {
			// if (commandLine.get(i + 1).equals("grep"))
			// if (!commandLine.get(i + 3).equals("|")
			// && !commandLine.get(i + 3).equals(">")) {
			// System.out.println("Grep error!  invalid argument");
			// return true;
			// }
			// } catch (Exception e) {
			// System.out.println("Grep error!  invalid argument");
			// return true;
			// }
			if (commandLine.get(i).equals("|")
					&& commandLine.get(i + 1).equals("grep"))
				try {
					if (!commandLine.get(i + 3).equals("|")
							&& !commandLine.get(i + 3).equals(">")) {
						System.out
								.println("Bad grep argument: invalid pipe order.");
						return true;
					}
				} catch (Exception e) {
				}
			if (commandLine.get(i).equals(">"))
				try {
					String temp = commandLine.get(i + 1);
				} catch (Exception e) {
					System.out
							.println("Output redirect error: missing argument.");
					return true;
				}
		}
		if ((commandLine.indexOf("cat") > 0 || commandLine.indexOf("sleep") > 0)) {
			System.out.println("Invalid argument order.");
			return true;
		}
		if (commandLine.indexOf("pwd") > 0 || commandLine.indexOf("cd") > 0) {
			System.out.println("Invalid argument order.");
			return true;
		}
		return false;
	}

	// Convert the current directory into a usable string
	private String directoryListTransform() {
		String historyOutput = directoryList.get(0);
		for (int i = 1; i < directoryList.size(); i++) {
			historyOutput += System.getProperty("file.separator");
			historyOutput += directoryList.get(i);
		}
		return historyOutput;
	}

	/*
	 * This is for Part 4
	 */
	public void kill() {
	}
}
