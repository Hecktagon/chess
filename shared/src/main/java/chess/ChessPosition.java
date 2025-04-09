package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    int y;
    int x;

    public ChessPosition(int row, int col) {
        y = row;
        x = col;
    }

    public boolean posInBounds(){
        return 1 <= x && x <= 8 && 1 <= y && y <= 8;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() { return y; }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() { return x; }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return y == that.y && x == that.x;
    }

    @Override
    public int hashCode() {
        return Objects.hash(y, x);
    }

    @Override
    public String toString() {
        return "ChessPosition{" +
                "row=" + y +
                ", col=" + x +
                '}';
    }
}
