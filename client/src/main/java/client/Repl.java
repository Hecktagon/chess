package client;

import exception.ResponseException;

import java.lang.module.ResolutionException;
import java.util.Objects;
import java.util.Scanner;
import static ui.EscapeSequences.*;
import static ui.DisplayChessBoard.*;

public class Repl {
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(Objects.requireNonNullElse(serverUrl, "http://localhost:8080"));
    }

    public void run() throws ResponseException {
        System.out.println(SET_TEXT_BOLD + SET_TEXT_COLOR_BLUE + "Welcome to Chess! type 'help' for options." );
        System.out.print(client.help() + RESET_TEXT_COLOR + RESET_TEXT_BOLD_FAINT);

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
                String[] lineList = line.toLowerCase().split( " ");
                if(Objects.equals(lineList[0], "play") && Objects.equals(lineList[1], "black")){
                    printGame(null, "black");
                } else if (Objects.equals(lineList[0], "play") || Objects.equals(lineList[0], "observe")) {
                    printGame(null, "white");
                }
            } catch (Throwable e) {
                var msg = e.getMessage();
                System.out.print(SET_TEXT_COLOR_RED + msg);
            }

        }
        System.out.println();
    }
    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> ");
    }
}