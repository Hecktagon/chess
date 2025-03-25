package ui;

public class DisplayChessBoard {
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

    private static void printBlack(String[][] stringBoard){

    }

    private static void printWhite(String[][] stringBoard){

    }

    public static void printGame(String[][] stringBoard, String color){
        if (stringBoard == null){
            stringBoard = startingBoardWhite;
        }

        boolean black = (color.equalsIgnoreCase("black"));

        if (black){
            stringBoard = flipBoard(stringBoard);
        }

        String[] rowLabels = {"a", "b", "c" ,"d" ,"e" ,"f" ,"g", "h"};

        String ans = (black) ?  printBlack(stringBoard) : printWhite(stringBoard)

    }
}
