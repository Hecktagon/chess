package ui;

import java.util.HashMap;

import static ui.EscapeSequences.*;

public class DisplayChessBoard {

    // a map of piece shortcuts to chess piece images
    private static String shortcutConverter(String shortcut) {
        HashMap<String, String> converter = new HashMap<>();
        converter.put(" ", EMPTY);
        converter.put("k", SET_TEXT_COLOR_DARK_BLUE + BLACK_KING);
        converter.put("r", SET_TEXT_COLOR_DARK_BLUE + BLACK_ROOK);
        converter.put("n", SET_TEXT_COLOR_DARK_BLUE + BLACK_KNIGHT);
        converter.put("q", SET_TEXT_COLOR_DARK_BLUE + BLACK_QUEEN);
        converter.put("p", SET_TEXT_COLOR_DARK_BLUE + BLACK_PAWN);
        converter.put("b", SET_TEXT_COLOR_DARK_BLUE + BLACK_BISHOP);
        converter.put("K", SET_TEXT_COLOR_LIGHT_BLUE + BLACK_KING);
        converter.put("R", SET_TEXT_COLOR_LIGHT_BLUE + BLACK_ROOK);
        converter.put("N", SET_TEXT_COLOR_LIGHT_BLUE + BLACK_KNIGHT);
        converter.put("Q", SET_TEXT_COLOR_LIGHT_BLUE + BLACK_QUEEN);
        converter.put("P", SET_TEXT_COLOR_LIGHT_BLUE + BLACK_PAWN);
        converter.put("B", SET_TEXT_COLOR_LIGHT_BLUE + BLACK_BISHOP);
        return converter.get(shortcut);
    }

    static String[][] startingBoardWhite =  {{"r", "n", "b", "q", "k", "b", "n", "r"},
    {"p", "p", "p", "p", "p", "p", "p", "p"},
    {null, null, null, null, null, null, null, null},
    {null, null, null, null, null, null, null, null},
    {null, null, null, null, null, null, null, null},
    {null, null, null, null, null, null, null, null},
    {"P", "P", "P", "P", "P", "P", "P", "P"},
    {"R", "N", "B", "Q", "K", "B", "N", "R"}};

    private static String[][] flipBoard(String[][] stringBoard) {
        int rows = stringBoard.length;
        int cols = stringBoard[0].length;
        String[][] flippedBoard = new String[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                flippedBoard[rows - 1 - i][cols - 1 - j] = stringBoard[i][j];
            }
        }
        return flippedBoard;
    }

    private static void printHelper(String[][] stringBoard, String[] rowLabels, String[] colLabels){
        int tileColor = 0;
        int rowIndex = 0;
        int colIndex;

        System.out.println();
        for (String[] row : stringBoard){
            if (rowIndex == 0) {
                System.out.print(SET_BG_COLOR_BLACK + EMPTY + SET_TEXT_COLOR_WHITE);
                for (String colLabel : colLabels){
                    System.out.print(colLabel);
                }
                System.out.print(EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
            }
            colIndex = 0;
            for (String tile: row){
                if (colIndex == 0){
                    System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + rowLabels[rowIndex]);
                }
                String currPiece = (tile == null) ? " " : tile;
                currPiece = (shortcutConverter(currPiece));
                if (tileColor % 2 == 0){
                    System.out.print(SET_BG_COLOR_WHITE + currPiece);
                } else {
                    System.out.print(SET_BG_COLOR_LIGHT_GREY + currPiece);
                }

                if (colIndex == 7){
                    System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + rowLabels[rowIndex]);
                }
                tileColor++;
                colIndex++;
            }
            System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
            if (rowIndex == 7) {
                System.out.print(SET_BG_COLOR_BLACK + EMPTY + SET_TEXT_COLOR_WHITE);
                for (String colTitle : colLabels){
                    System.out.print(colTitle);
                }
                System.out.print(EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
            }
            tileColor++;
            rowIndex++;
        }
    }

    public static void printGame(String[][] stringBoard, String color){
        if (stringBoard == null){
            stringBoard = startingBoardWhite;
        }

        String[] rowLabels;
        String[] colLabels;


        if (color.equalsIgnoreCase("black")){
            stringBoard = flipBoard(stringBoard);
            colLabels = new String[]{" h\u2003", " g\u2003", " f\u2003", " e\u2003", " d\u2003", " c\u2003", " b\u2003", " a\u2003"};
            rowLabels = new String[]{" 1\u2003", " 2\u2003", " 3\u2003", " 4\u2003", " 5\u2003", " 6\u2003", " 7\u2003", " 8\u2003"};
        } else {
            colLabels = new String[]{" a\u2003", " b\u2003", " c\u2003", " d\u2003", " e\u2003", " f\u2003", " g\u2003", " h\u2003"};
            rowLabels = new String[]{" 8\u2003", " 7\u2003", " 6\u2003", " 5\u2003", " 4\u2003", " 3\u2003", " 2\u2003", " 1\u2003"};

        }
        printHelper(stringBoard, rowLabels, colLabels);
    }
}
