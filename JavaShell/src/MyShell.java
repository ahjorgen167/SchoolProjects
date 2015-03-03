import java.io.*;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyShell {
	public static void main(String[] args) {
		
		//Defined variables and create reader
		
		String commandLine;
		List<String> directory = Arrays.asList(System.getProperty("user.dir")
				.split("\\s*\\\\s*"));
		BufferedReader console = new BufferedReader(new InputStreamReader(
				System.in));
		List<String> historyList = new ArrayList<String>();
		CommandManager input = new CommandManager(historyList, directory);
		
		//Shell input command line
		
		while (true) {
			System.out.print("> ");
			try {
				commandLine = console.readLine();
				if(input.evaluate(commandLine)) {
					break;
				}
				if (commandLine.equals(""))
					continue;
			} catch (IOException e) {
				break;
			} catch (InterruptedException e) {
				break;
			}

		}
	}
}