package controller.fx;

import app.fx.AppManager;
import model.Game;
import view.fx.YutBoard;

public class YutController {
    private final AppManager appManager;
    private final Game game;
    private final YutBoard board;

    public YutController(AppManager appManager, int sides, int playerCount, int pieceCount, YutBoard board) {
        this.appManager = appManager;
        this.game = new Game(sides, playerCount, pieceCount);
        this.board = board;
    }

    public void initializeGameUI() {
        board.setNumSides(game.getBoard().getNumSides());
        board.setBoard(game.getBoard());

        setupThrowButtons();
        setupInitialPieceButtons();
    }
}
