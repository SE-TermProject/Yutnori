package controller;

import model.Yut;
import view.YutBoardV2;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class YutController {

    public YutController(Yut model, YutBoardV2 view) {
        view.getThrowButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String result = model.throwYut();
                view.updateResult(result);
            }
        });
    }
}