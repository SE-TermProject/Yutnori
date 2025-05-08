import model.Board;
import model.Piece;

import java.io.*;
import java.util.Stack;

public class Main {
    public static void main(String[] args) throws IOException {

        System.out.println("Hello world!");

        Board board = new Board(6);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            int row = Integer.parseInt(br.readLine());
            int col = Integer.parseInt(br.readLine());
            int step = Integer.parseInt(br.readLine());

            board.findPossiblePos(new Stack<int[]>(), row, col, step);
        }
    }
}
