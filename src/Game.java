/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

// imports necessary libraries for Java swing
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * Game Main class that specifies the frame and widgets of the GUI
 */
public class Game implements Runnable {
    
    public void run() {
        // NOTE : recall that the 'final' keyword notes inmutability
        // even for local variables.

        // Top-level frame in which game components live
        // Be sure to change "TOP LEVEL FRAME" to the name of your game
        final JFrame frame = new JFrame("ADVANCED WARS");
        frame.setLocation(300, 300);
        
        frame.setResizable(false);
        
        final GridController gridController = new GridController(800, 800, 20, 20);

        frame.add(gridController);
        gridController.setLayout(null);
        
        final JButton instructionButton = new JButton("Instructions");
        instructionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BufferedImage image = null;
                try {
                    if (image == null) {
                        image = ImageIO.read(new File("AdvancedWarsInstructions.png"));
                    }
                } catch (IOException ex) {
                    System.out.println("Internal Error:" + ex.getMessage());
                }
                ImageIcon icon = new ImageIcon(image);
                JOptionPane.showMessageDialog(null, null, "Instructions", JOptionPane.INFORMATION_MESSAGE, icon);
                gridController.requestFocusInWindow();
            }
        });
        frame.add(instructionButton, BorderLayout.SOUTH);
        final JButton leaderBoardButton = new JButton("Leader Board");
        leaderBoardButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String scores = null;
                try {
                    scores = LeaderBoard.getLeaderBoard();
                } catch (IOException e1) {
                    
                }
                JOptionPane.showMessageDialog(null, scores, "Leader Board", JOptionPane.INFORMATION_MESSAGE, null);
                gridController.requestFocusInWindow();
            }
        });
        frame.add(leaderBoardButton, BorderLayout.NORTH);
        // Main playing area
        //final GameScreen screen = new GameScreen(status);
        //frame.add(screen, BorderLayout.CENTER);

        // Note here that when we add an action listener to the reset
        // button, we define it as an anonymous inner class that is
        // an instance of ActionListener with its actionPerformed()
        // method overridden. When the button is pressed,
        // actionPerformed() will be called.

        // Put the frame on the screen
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Start game
        //gridController.addPlayer(new Player("Default1", PlayerColor.BLUE));
        //gridController.addPlayer(new Player("Default2", PlayerColor.GREEN));
        gridController.setupGrid();
    }

    /*
     * Main method run to start and run the game Initializes the GUI elements
     * specified in Game and runs it IMPORTANT: Do NOT delete! You MUST include
     * this in the final submission of your game.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Game());
    }
}
