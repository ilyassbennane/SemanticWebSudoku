package com.bennaneilyass;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class InterfaceApp extends JFrame {
    private static final int SIZE = 9;
    private static final int SUBGRID_SIZE = 3;
    private JTextField[][] cells = new JTextField[SIZE][SIZE];
    private Color userInputColor = new Color(102, 178, 255);
    private Color solverInputColor = new Color(255, 255, 255);

    public InterfaceApp() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Sudoku Solver");
        setSize(800, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        File imageFile = new File("src\\main\\java\\com\\bennaneilyass\\logo.png");
        if (imageFile.exists()) {
            try {
                setIconImage(ImageIO.read(imageFile));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "An error occurred while loading the icon image.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Icon image file not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Title Panel
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Welcome to Ilyas's Sudoku Game");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);

        // Main Panel with Grid and Buttons
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Grid Panel
        JPanel gridPanel = new JPanel(new GridLayout(SIZE, SIZE)) {
            @Override
            public Insets getInsets() {
                return new Insets(10, 10, 10, 10);
            }
        };
        gridPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(new Font("Arial", Font.BOLD, 20));
                cells[row][col].setBackground(Color.WHITE);

                cells[row][col].addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) {
                        JTextField source = (JTextField) evt.getSource();
                        if (!source.getText().isEmpty()) {
                            source.setBackground(userInputColor);
                        }
                    }
                });

                // Set border for each cell
                int top = (row % SUBGRID_SIZE == 0) ? 2 : 1;
                int left = (col % SUBGRID_SIZE == 0) ? 2 : 1;
                int bottom = ((row + 1) % SUBGRID_SIZE == 0) ? 2 : 1;
                int right = ((col + 1) % SUBGRID_SIZE == 0) ? 2 : 1;
                cells[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.BLACK));

                // Add a DocumentFilter to limit input to single digit numbers from 1 to 9 or empty string
                ((AbstractDocument) cells[row][col].getDocument()).setDocumentFilter(new DocumentFilter() {
                    @Override
                    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                        if (string.matches("[1-9]?")) {
                            super.insertString(fb, offset, string, attr);
                        }
                    }

                    @Override
                    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                        if (text.matches("[1-9]?")) {
                            super.replace(fb, offset, length, text, attrs);
                        }
                    }
                });

                gridPanel.add(cells[row][col]);
            }
        }

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Load icons
        ImageIcon newGameIcon = new ImageIcon("path/to/newgameicon.png");
        ImageIcon solveIcon = new ImageIcon("path/to/solveicon.png");

        JButton newGameButton = new JButton("New Game");
        newGameButton.setIcon(newGameIcon);
        newGameButton.setHorizontalAlignment(SwingConstants.LEFT);
        newGameButton.setBackground(new Color(102, 178, 255));
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFont(new Font("Arial", Font.BOLD, 14));
        newGameButton.setToolTipText("Start a new Sudoku game");
        newGameButton.setPreferredSize(new Dimension(200, 50));
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetGrid();
            }
        });

        JButton solveButton = new JButton("Solve");
        solveButton.setIcon(solveIcon);
        solveButton.setHorizontalAlignment(SwingConstants.LEFT);
        solveButton.setBackground(new Color(102, 178, 255));
        solveButton.setForeground(Color.WHITE);
        solveButton.setFont(new Font("Arial", Font.BOLD, 14));
        solveButton.setToolTipText("Solve the Sudoku puzzle");
        solveButton.setPreferredSize(new Dimension(200, 50));
        solveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });

        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(newGameButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(solveButton);

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);

        add(titlePanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void solveSudoku() {
        int[][] sudoku = new int[SIZE][SIZE];

        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                String text = cells[row][col].getText();
                if (!text.isEmpty()) {
                    sudoku[row][col] = Integer.parseInt(text);
                } else {
                    sudoku[row][col] = 0;
                }
            }
        }

        if (!isValidSudoku(sudoku)) {
            JOptionPane.showMessageDialog(this, "Contraintes SudoKu non respectée", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Jena solver = new Jena();
        if (solver.solve(sudoku)) {
            for (int row = 0; row < SIZE; row++) {
                for (int col = 0; col < SIZE; col++) {
                    if (cells[row][col].getBackground() != userInputColor) {
                        cells[row][col].setText(String.valueOf(sudoku[row][col]));
                        cells[row][col].setBackground(solverInputColor);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Impossible de résoudre le Sudoku avec cette configuration.", "Erreur", JOptionPane.ERROR_MESSAGE);
            resetGrid();
        }
    }

    private void resetGrid() {
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                cells[row][col].setText(""); // Clear the text in the cell
                cells[row][col].setBackground(Color.WHITE); // Reset the background to white
            }
        }
    }

    private boolean isValidSudoku(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            Set<Integer> rowSet = new HashSet<>();
            Set<Integer> colSet = new HashSet<>();
            for (int j = 0; j < SIZE; j++) {
                if (rowSet.contains(board[i][j]) && board[i][j] != 0) {
                    return false; 
                }
                if (colSet.contains(board[j][i]) && board[j][i] != 0) {
                    return false; 
                }
                rowSet.add(board[i][j]);
                colSet.add(board[j][i]);
            }
        }

        for (int i = 0; i < SIZE; i += SUBGRID_SIZE) {
            for (int j = 0; j < SIZE; j += SUBGRID_SIZE) {
                Set<Integer> subgridSet = new HashSet<>();
                for (int k = i; k < i + SUBGRID_SIZE; k++) {
                    for (int l = j; l < j + SUBGRID_SIZE; l++) {
                        if (subgridSet.contains(board[k][l]) && board[k][l] != 0) {
                            return false; 
                        }
                        subgridSet.add(board[k][l]);
                    }
                }
            }
        }

        return true; 
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfaceApp().setVisible(true);
            }
        });
    }
}
    