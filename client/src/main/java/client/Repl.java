package client;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl);
    }

    public void run() {
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "Welcome to Chess! type 'help' for options." + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_MAGENTA + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }
    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }
}