package ui;

import chess.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static ui.EscapeSequences.*;

public class DisplayChessBoard {
    ChessGame game;
    ChessGame.TeamColor team;
    ChessPosition position;

    // a map of piece shortcuts to chess piece images
    private static String shortcutConverter(String shortcut) {
        HashMap<String, String> converter = new HashMap<>();
        converter.put(" ", EMPTY);
        converter.put("", EMPTY);
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



    public static String[][] flipBoard(String[][] stringBoard, boolean white) {
        int rows = stringBoard.length;
        int cols = stringBoard[0].length;
        String[][] flippedBoard = new String[rows][cols];

        for (int i = rows-1; i >= 0 ; i--) {
            for (int j = cols-1; j >= 0; j--) {
                if (white) {
                    flippedBoard[rows - 1 - i][j] = stringBoard[i][j];
                } else {
                    flippedBoard[i][cols - 1 - j] = stringBoard[i][j];
                }
            }
        }
        return flippedBoard;
    }

    private static void printHelper(String[][] stringBoard, String[] rowLabels, String[] colLabels, boolean white){
        int tileColor = 0;
        int rowIndex = 0;
        int colIndex;
        String lowRightBG = (white) ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_WHITE;
        String lowLeftBG = (white) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_LIGHT_GREY;
        String lowRightHighlight = (white) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
        String lowLeftHighlight = (white) ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
        String highlight = "";

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
                highlight = "";

                if (colIndex == 0){
                    System.out.print(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + rowLabels[rowIndex]);
                }
                String currPiece = (tile == null) ? " " : tile;
                if (currPiece.contains("v")) {
                    highlight = "v";
                    currPiece = currPiece.replaceAll("v", "");
                }
                else if (currPiece.contains("s")) {
                    highlight = "s";
                    currPiece = currPiece.replaceAll("s", "");
                }

                currPiece = (shortcutConverter(currPiece));
                if (tileColor % 2 == 0){
                    switch (highlight) {
                        case "v":
                            System.out.print(lowLeftHighlight + currPiece);
                            break;
                        case "s":
                            System.out.print(SET_BG_COLOR_YELLOW + currPiece);
                            break;
                        default:
                            System.out.print(lowLeftBG + currPiece);
                            break;

                    }
                } else {
                    switch (highlight) {
                        case "v":
                            System.out.print(lowRightHighlight + currPiece);
                            break;
                        case "s":
                            System.out.print(SET_BG_COLOR_YELLOW + currPiece);
                            break;
                        default:
                            System.out.print(lowRightBG + currPiece);
                            break;
                    }
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

    public static void printChessGame(ChessGame game, ChessGame.TeamColor team, ChessPosition validMovesPos){
        String[][] stringBoard = gameToStringArray(game, validMovesPos);
        printStringBoard(stringBoard, team);
    }

    private static String[][] gameToStringArray(ChessGame game, ChessPosition validMovesPos){
        final ChessPiece[][] boardArray = game.getBoard().getBoard();
        Collection<ChessMove> valids = new HashSet<>();
        HashSet<ChessPosition> endPosSet = new HashSet<>();
        String[][] stringBoard = new String[8][8];

        if (validMovesPos != null) {
            valids = game.validMoves(validMovesPos);
        }

        if (valids == null) {valids = new HashSet<>();}

        for (ChessMove move : valids){
            endPosSet.add(move.getEndPosition());
        }

        for(int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                ChessPiece curPiece = (ChessPiece) boardArray[i][j];
                ChessPosition curPos = new ChessPosition(i+1, j+1);
                // marks squares to be highlighted with a "v" or "s" if selected piece
                String highlightTag = (endPosSet.contains(curPos)) ? "v" : "";
                if (curPos.equals(validMovesPos)) { highlightTag = "s"; }
                // gets chessboards shortcut for a piece, and adds a highlight tag if needed.
                stringBoard[i][j] = ChessBoard.shortcutMap(curPiece).replaceAll("\\s", "") + highlightTag;
            }
        }
        return stringBoard;
    }


    private static void printStringBoard(String[][] stringBoard, ChessGame.TeamColor color){
        if (stringBoard == null){
            stringBoard = startingBoardWhite;
        }

        String[] rowLabels;
        String[] colLabels;

        boolean white = true;

        if (color == ChessGame.TeamColor.BLACK){
            white = false;
            stringBoard = flipBoard(stringBoard, white);
            colLabels = new String[]{" h\u2003", " g\u2003", " f\u2003", " e\u2003", " d\u2003", " c\u2003", " b\u2003", " a\u2003"};
            rowLabels = new String[]{" 1\u2003", " 2\u2003", " 3\u2003", " 4\u2003", " 5\u2003", " 6\u2003", " 7\u2003", " 8\u2003"};
        } else {
            stringBoard = flipBoard(stringBoard, white);
            colLabels = new String[]{" a\u2003", " b\u2003", " c\u2003", " d\u2003", " e\u2003", " f\u2003", " g\u2003", " h\u2003"};
            rowLabels = new String[]{" 8\u2003", " 7\u2003", " 6\u2003", " 5\u2003", " 4\u2003", " 3\u2003", " 2\u2003", " 1\u2003"};
        }
        printHelper(stringBoard, rowLabels, colLabels, true);
    }
}
