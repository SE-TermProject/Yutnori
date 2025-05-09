package view;

import model.Piece;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PlayerPiecesPanel extends JPanel {
    public PlayerPiecesPanel(Player player) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 4, 0));
        setOpaque(false);

        // A:, B: 레이블 한 번만
        String label = switch (player.getId()) {
            case 0 -> "A:";
            case 1 -> "B:";
            case 2 -> "C:";
            case 3 -> "D:";
            default -> "?:";
        };
        JLabel nameLabel = new JLabel(label);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(nameLabel);

        // PieceButton 여러 개 연속해서 붙이기
        List<Piece> pieces = player.getPieces();
        for (Piece piece : pieces) {
            PieceButton pb = new PieceButton(piece, player.getId());
            pb.setPreferredSize(new Dimension(16, 16));
            add(pb);
        }
    }
}
