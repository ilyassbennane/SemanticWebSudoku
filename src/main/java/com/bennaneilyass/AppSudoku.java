package com.bennaneilyass;

import javax.swing.*;

public class AppSudoku {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfaceApp().setVisible(true);
            }
        });
    }
}
