/**
 * CIS 120 Game HW
 * (c) University of Pennsylvania
 * @version 2.0, Mar 2013
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;

/**
 * GameCourt
 * 
 * This class holds the primary game logic for how different objects interact
 * with one another. Take time to understand how the timer interacts with the
 * different methods and how it repaints the GUI on every tick().
 * 
 */
@SuppressWarnings("serial")
public class GameScreen extends JPanel{

    public GridController gridController;
    // the state of the game logic
    private GrassTile grass; // display grass tile

    public boolean playing = false; // whether the game is running
    private JLabel status; // Current status text (i.e. Running...)

    // Game constants
    public static final int SCREEN_WIDTH = 1080;
    public static final int SCREEN_HEIGHT = 960;
    // Update interval for timer, in milliseconds
    public static final int INTERVAL = 35;

    public GameScreen(JLabel status) {
        // The timer is an object which triggers an action periodically
        // with the given INTERVAL. One registers an ActionListener with
        // this timer, whose actionPerformed() method will be called
        // each time the timer triggers. We define a helper method
        // called tick() that actually does everything that should
        // be done in a single timestep.
        Timer timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        timer.start(); // MAKE SURE TO START THE TIMER!

        // Enable keyboard focus on the court area.
        // When this component has the keyboard focus, key
        // events will be handled by its key listener.
        setFocusable(true);

        // This key listener allows the square to move as long
        // as an arrow key is pressed, by changing the square's
        // velocity accordingly. (The tick method below actually
        // moves the square.)
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                /*
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    square.v_x = -SQUARE_VELOCITY;
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    square.v_x = SQUARE_VELOCITY;
                else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    square.v_y = SQUARE_VELOCITY;
                else if (e.getKeyCode() == KeyEvent.VK_UP)
                    square.v_y = -SQUARE_VELOCITY;
               */
            }

            public void keyReleased(KeyEvent e) {
                /*
                square.v_x = 0;
                square.v_y = 0;
                */
            }
        });

        this.status = status;
    }

    /**
     * (Re-)set the game to its initial state.
     */
    public void startGame() {

        //gridController.setupGrid();
        
        playing = true;
        status.setText("Running...");

        // Make sure that this component has the keyboard focus
        requestFocusInWindow();
    }

    /**
     * This method is called every time the timer defined in the constructor
     * triggers.
     */
    void tick() {
        if (playing) {
            
            // update the display
            repaint();
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        /*square.draw(g);
        poison.draw(g);
        snitch.draw(g);*/
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, 0);
    }
}
