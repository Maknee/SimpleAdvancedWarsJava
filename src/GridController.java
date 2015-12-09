import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class GridController extends JPanel {

    private static final int INTERVAL = 100;

    private int width, height, numOfWidthCells, numOfHeightCells;

    private Tile[][] tiles;

    boolean playing;

    private Selector selector;

    private Player[] players;

    private Player currentPlayer;

    private List<Tile> path;

    private List<Tile> actions;

    private int index;

    private static final int SIZE = 40;

    private boolean animating = false;

    private final JLabel statLabel;

    private final JLabel playerLabel;

    private boolean displayShop;

    private boolean displayedShop;

    private List<Unit> units;

    public GridController(int width, int height, final int numOfWidthCells,
            final int numOfHeightCells) {

        this.width = width;
        this.height = height;
        this.numOfWidthCells = numOfWidthCells;
        this.numOfHeightCells = numOfHeightCells;

        setLayout(null);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        players = new Player[2];

        statLabel = new JLabel();
        playerLabel = new JLabel();

        players[0] = new Player("Player 1", PlayerColor.BLUE);
        players[1] = new Player("Player 2", PlayerColor.GREEN);

        Timer timer = new Timer(INTERVAL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tick();
            }
        });
        timer.start(); // MAKE SURE TO START THE TIMER!

        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                // if the user hasn't selected a unit or if the unit is not
                // animating

                // each keyboard press checks if the user if in the shop
                // and if not, the user can move regularly
                if (selector.getState() == SelectorState.IDLE
                        || selector.getState() == SelectorState.SELECTING && !animating) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        if (!displayShop) {
                            if (selector.getTile().getX() / SIZE - 1 >= 0) {
                                selector.moveTo(-SIZE, 0, tiles[selector.getTile().getX() / SIZE
                                        - 1][selector.getTile().getY() / SIZE]);
                            }
                            displayStats();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        if (!displayShop) {
                            if (selector.getTile().getX() / SIZE + 1 < numOfWidthCells) {
                                selector.moveTo(SIZE, 0, tiles[selector.getTile().getX() / SIZE
                                        + 1][selector.getTile().getY() / SIZE]);
                            }
                            displayStats();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (!displayShop) {
                            if (selector.getTile().getY() / SIZE + 1 < numOfHeightCells) {
                                selector.moveTo(0, SIZE, tiles[selector.getTile().getX()
                                        / SIZE][selector.getTile().getY() / SIZE + 1]);
                            }
                            displayStats();
                        } else {
                            index++;
                            if (index > units.size() - 1) {
                                index = 0;
                            }
                            selector.setLocation(150,
                                    50 + index * statLabel.getComponent(index).getHeight(),
                                    selector.getTile());
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        if (!displayShop) {
                            if (selector.getTile().getY() / SIZE - 1 >= 0) {
                                selector.moveTo(0, -SIZE, tiles[selector.getTile().getX()
                                        / SIZE][selector.getTile().getY() / SIZE - 1]);
                            }
                            displayStats();
                        } else {
                            index--;
                            if (index < 0) {
                                index = units.size() - 1;
                            }
                            selector.setLocation(150,
                                    50 + index * statLabel.getComponent(index).getHeight(),
                                    selector.getTile());
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (selector.getTile().getHP() != 0
                                && selector.getState() == SelectorState.IDLE) {
                            // same color
                            System.out.println("HP is not 0");
                            if (selector.getTile().getControlledBy().equals(currentPlayer)
                                    && selector.getTile().getUnit() == null) {
                                System.out.println("Tile is owned By player");
                                if (selector.getTile().getTileType() == TileType.Factory
                                        && selector.getTile().getUnit() == null) {
                                    // display shop
                                    displayShop = true;
                                    System.out.println("Shop is open!");
                                }
                            }
                        } else {
                            displayShop = false;
                            displayedShop = false;
                        }
                        if (!displayShop) {
                            path = selector.selectedTile(currentPlayer);
                            displayStats();
                        } else {
                            // draw the shop
                            if (!displayedShop) {
                                index = 0;

                                selector.setLocation(150, SIZE, selector.getTile());

                                statLabel.setLocation(200, 20);
                                statLabel.setSize(500, 800);
                                statLabel.setText(null);
                                statLabel.setIcon(null);
                                statLabel.setLayout(null);

                                for (int i = 0; i < units.size(); i++) {
                                    JLabel label = new JLabel(new ImageIcon(units.get(i).getImage()
                                            .getScaledInstance(SIZE, SIZE, Image.SCALE_DEFAULT)));
                                    String string = new String("<html>");
                                    string += "<span style=\"color: #ff0000\">Name: "
                                            + units.get(i).getUnitType().toString() + "</span><br>";
                                    string += "Attack: " + units.get(i).getAttack() + "<br>";
                                    string += "Defense: " + units.get(i).getDefense() + "<br>";
                                    string += "Cost: " + units.get(i).getCost() + "<br>";
                                    string += "HP: " + units.get(i).getHP() + "<br>";
                                    string += "Gas: " + units.get(i).getGas() + "<br>";
                                    string += "SpacesMovement: "
                                            + units.get(i).getSpacesAllowedToMove() + "<br>";
                                    label.setText(string);
                                    label.setSize(label.getPreferredSize());
                                    label.setLocation(0,
                                            (int) (i * label.getPreferredSize().getHeight()));
                                    statLabel.add(label);
                                }
                                displayedShop = true;
                            } else {
                                // create unit
                                Unit unit = units.get(index);
                                if (unit.getCost() <= currentPlayer.getFunds()) {
                                    displayShop = false;
                                    displayedShop = false;
                                    statLabel.removeAll();
                                    for (Component comp : statLabel.getComponents()) {
                                        statLabel.remove(comp);
                                    }
                                    selector.setLocation(selector.getTile().getX(),
                                            selector.getTile().getY(), selector.getTile());
                                    displayStats();
                                    Unit newUnit = unit.copy();
                                    newUnit.setLocation(selector.getTile().getX(),
                                            selector.getTile().getY());
                                    newUnit.setTurn(true);
                                    selector.getTile().setUnit(newUnit);
                                    currentPlayer
                                            .setFunds(currentPlayer.getFunds() - unit.getCost());
                                    currentPlayer.addToUnitsBought();
                                    currentPlayer.addToFundsSpent(unit.getCost());
                                }
                            }
                        }
                        // end turn
                    } else if (e.getKeyCode() == KeyEvent.VK_E
                            && selector.getState() == SelectorState.IDLE && !displayShop) {
                        // player
                        if (currentPlayer.equals(players[0])) {
                            currentPlayer = players[1];
                        } else if (currentPlayer.equals(players[1])) {
                            currentPlayer = players[0];
                        }
                        // add funds to next player, repair and give gas to
                        // units on buildings
                        addFundsAtEndOfTurn();
                        for (int x = 0; x < numOfWidthCells; x++) {
                            for (int y = 0; y < numOfHeightCells; y++) {
                                if (tiles[x][y].getUnit() != null) {
                                    tiles[x][y].getUnit().setTurn(false);
                                    // add hp to each unit
                                    if (tiles[x][y].getControlledBy().equals(currentPlayer)
                                            && tiles[x][y].getUnit().getControlledBy()
                                                    .equals(currentPlayer)
                                            && tiles[x][y].getUnit().getHP() < 10
                                            && tiles[x][y].getUnit().getCost() / 10 < currentPlayer
                                                    .getFunds()) {
                                        tiles[x][y].getUnit()
                                                .setHP(tiles[x][y].getUnit().getHP() + 2);
                                        currentPlayer.setFunds(currentPlayer.getFunds()
                                                - tiles[x][y].getUnit().getCost() / 10);
                                        if (tiles[x][y].getUnit().getHP() > 10) {
                                            tiles[x][y].getUnit().setHP(10);
                                        }
                                    }
                                    
                                    if (tiles[x][y].getControlledBy().equals(currentPlayer)) {
                                        // add gas to each unit
                                        tiles[x][y].getUnit().setGas(tiles[x][y].getUnit().getMaxGas());
                                    }
                                }
                            }
                        }

                        // construct a new shop with the units of the
                        // currentplayer
                        units = new ArrayList<Unit>();
                        units.add(new InfantryUnit(0, 0, currentPlayer));
                        units.add(new MechUnit(0, 0, currentPlayer));
                        units.add(new ReconUnit(0, 0, currentPlayer));
                        units.add(new ArtilleryUnit(0, 0, currentPlayer));
                        units.add(new TankUnit(0, 0, currentPlayer));
                        units.add(new AntiAirUnit(0, 0, currentPlayer));
                        units.add(new RocketUnit(0, 0, currentPlayer));

                        // display a screen with the turn of the player
                        statLabel.setFont(new Font(statLabel.getName(), Font.BOLD, 60));
                        statLabel.setIcon(null);
                        statLabel.setLocation(0, 0);
                        statLabel.setText("<html>" + currentPlayer.getName() + " TURN</html>");
                        statLabel.setSize(getPreferredSize());

                        // close the shop
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        if (displayShop) {
                            displayShop = false;
                            displayedShop = false;
                            statLabel.removeAll();
                            for (Component comp : statLabel.getComponents()) {
                                statLabel.remove(comp);
                            }
                            selector.setLocation(selector.getTile().getX(),
                                    selector.getTile().getY(), selector.getTile());
                            displayStats();
                        }
                    }
                    // selecting tile to attack or wait on
                } else if (selector.getState() == SelectorState.SELECTED && !animating) {
                    if (actions == null) {
                        actions = selector.selectedTile(currentPlayer);
                        index = 0;
                        Tile baseTile = tiles[actions.get(0).getX() / SIZE][actions.get(0).getY()
                                / SIZE];
                        // if the unit is artillery or rocket, add different
                        // actions
                        if (!baseTile.getUnit().getMoved()
                                && (baseTile.getUnit().getUnitType() == UnitType.Artillery
                                        || baseTile.getUnit().getUnitType() == UnitType.Rocket)) {
                            actions = new LinkedList<Tile>();
                            actions.add(baseTile);
                            actions.addAll(selector.allPossibleActionsForRangedUnits(currentPlayer,
                                    baseTile.getUnit().getMinRange(),
                                    baseTile.getUnit().getMaxRange()));
                        } else {
                            baseTile.getUnit().setMoved(false);
                        }
                    }

                    // displays stats of the current tile and infantry
                    // display potential damage for enemy units as well.
                    if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_DOWN) {
                        index--;
                        if (index == 0) {
                            selector.setLocation(actions.get(index).getX(),
                                    actions.get(index).getY(), tiles[actions.get(index).getX()
                                            / SIZE][actions.get(index).getY() / SIZE]);
                            displayStats();
                        } else if (index > 0) {
                            selector.setLocation(actions.get(index).getX(),
                                    actions.get(index).getY(), tiles[actions.get(index).getX()
                                            / SIZE][actions.get(index).getY() / SIZE]);
                            displayStats();
                            String string = statLabel.getText();
                            string = string.substring(0, string.length() - 8);
                            string += "<br>";
                            Unit attacker = tiles[actions.get(0).getX() / SIZE][actions.get(0)
                                    .getY() / SIZE].getUnit();
                            Unit defender = tiles[actions.get(index).getX() / SIZE][actions
                                    .get(index).getY() / SIZE].getUnit();
                            string += "Potential Damage: "
                                    + Unit.calculateDamageDone((double) attacker.getAttack(),
                                            (double) attacker.getHP(),
                                            (double) defender.getDefense(),
                                            (double) defender.getHP(),
                                            tiles[actions.get(index).getX() / SIZE][actions
                                                    .get(index).getY() / SIZE].getDefense())
                                    + "<br>";
                            string += "</html>";
                            statLabel.setText(string);
                            statLabel.setSize(statLabel.getPreferredSize());
                        } else {
                            index = actions.size() - 1;
                            selector.setLocation(actions.get(index).getX(),
                                    actions.get(index).getY(), tiles[actions.get(index).getX()
                                            / SIZE][actions.get(index).getY() / SIZE]);
                            displayStats();
                            if (actions.size() != 1) {
                                String string = statLabel.getText();
                                string = string.substring(0, string.length() - 8);
                                string += "<br>";
                                Unit attacker = tiles[actions.get(0).getX() / SIZE][actions.get(0)
                                        .getY() / SIZE].getUnit();
                                Unit defender = tiles[actions.get(index).getX() / SIZE][actions
                                        .get(index).getY() / SIZE].getUnit();
                                string += "Potential Damage: "
                                        + Unit.calculateDamageDone((double) attacker.getAttack(),
                                                (double) attacker.getHP(),
                                                (double) defender.getDefense(),
                                                (double) defender.getHP(),
                                                tiles[actions.get(index).getX() / SIZE][actions
                                                        .get(index).getY() / SIZE].getDefense())
                                        + "<br>";
                                string += "</html>";
                                statLabel.setText(string);
                                statLabel.setSize(statLabel.getPreferredSize());
                            }
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT
                            || e.getKeyCode() == KeyEvent.VK_UP) {
                        index += 1;
                        if (index == actions.size()) {
                            index = 0;
                            selector.setLocation(actions.get(index).getX(),
                                    actions.get(index).getY(), tiles[actions.get(index).getX()
                                            / SIZE][actions.get(index).getY() / SIZE]);
                            displayStats();
                        } else if (index < actions.size()) {
                            selector.setLocation(actions.get(index).getX(),
                                    actions.get(index).getY(), tiles[actions.get(index).getX()
                                            / SIZE][actions.get(index).getY() / SIZE]);
                            displayStats();
                            String string = statLabel.getText();
                            string = string.substring(0, string.length() - 8);
                            string += "<br>";
                            Unit attacker = tiles[actions.get(0).getX() / SIZE][actions.get(0)
                                    .getY() / SIZE].getUnit();
                            Unit defender = tiles[actions.get(index).getX() / SIZE][actions
                                    .get(index).getY() / SIZE].getUnit();
                            string += "Potential Damage: "
                                    + Unit.calculateDamageDone((double) attacker.getAttack(),
                                            (double) attacker.getHP(),
                                            (double) defender.getDefense(),
                                            (double) defender.getHP(),
                                            tiles[actions.get(index).getX() / SIZE][actions
                                                    .get(index).getY() / SIZE].getDefense())
                                    + "<br>";
                            string += "</html>";
                            statLabel.setText(string);
                            statLabel.setSize(statLabel.getPreferredSize());
                        } else {
                            index = 0;
                            selector.setLocation(actions.get(index).getX(),
                                    actions.get(index).getY(), tiles[actions.get(index).getX()
                                            / SIZE][actions.get(index).getY() / SIZE]);
                            displayStats();
                        }
                        // reset the state
                        // if index is 0 we know the unit wants to wait and not
                        // attack.
                        // else we attack the enemy unit
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        selector.setState(SelectorState.IDLE);
                        if (index == 0) { // tile on top of unit
                            if (selector.getTile().getUnit() != null) {
                                selector.getTile().getUnit().setTurn(true);
                            }
                            System.out.println("HP:        " + selector.getTile().getHP()
                                    + selector.getTile().getUnit());
                            if (selector.getTile().getHP() != 0 && (selector.getTile().getUnit()
                                    .getUnitType() == UnitType.Infantry
                                    || selector.getTile().getUnit()
                                            .getUnitType() == UnitType.Mech)) {
                                // same color, don't do
                                if (selector.getTile().getControlledBy().equals(currentPlayer)) {

                                    // no color
                                } else if (selector.getTile().getControlledBy()
                                        .getPlayerColor() == PlayerColor.NONE) {

                                    System.out.println(selector.getTile().getUnit().getUnitType());
                                    selector.getTile().setHP(selector.getTile().getHP()
                                            - selector.getTile().getUnit().getHP());
                                    if (selector.getTile().getHP() <= 0) {
                                        selector.getTile().setController(
                                                selector.getTile().getUnit().getControlledBy());
                                        selector.getTile().setHP(Tile.MAX_HP);
                                        currentPlayer.addTobuildingsTaken();
                                    }
                                    // enemy color and win condition
                                } else {
                                    if (selector.getTile().getTileType() == TileType.Base) {
                                        selector.getTile().setHP(selector.getTile().getHP()
                                                - selector.getTile().getUnit().getHP());
                                        if (selector.getTile().getHP() <= 0) {
                                            selector.getTile().setController(
                                                    selector.getTile().getUnit().getControlledBy());
                                            selector.getTile().setHP(Tile.MAX_HP);
                                            Player loser = null;
                                            for (Player player : players) {
                                                player.setFunds(0);
                                            }
                                            
                                            if (currentPlayer.equals(players[0])) {
                                                loser = players[1];
                                            } else {
                                                loser = players[0];
                                            }
                                            
                                            JOptionPane.showMessageDialog(null,
                                                    currentPlayer.getName() + "Wins!", "Winner!",
                                                    JOptionPane.INFORMATION_MESSAGE, null);

                                            try {
                                                LeaderBoard.addToLeaderBoard(currentPlayer, loser);
                                            } catch (FileNotFoundException e1) {
                                                System.out.println("FILE NOT FOUND");
                                            } catch (IOException e1) {
                                                System.out.println("IOEXCEPTION");
                                            }

                                            for (Player player : players) {
                                                player.resetScores();
                                            }

                                            playing = false;

                                            setupGrid();
                                        }
                                        // win condition
                                    } else {
                                        selector.getTile().setHP(selector.getTile().getHP()
                                                - selector.getTile().getUnit().getHP());
                                        if (selector.getTile().getHP() <= 0) {
                                            selector.getTile().setController(
                                                    selector.getTile().getUnit().getControlledBy());
                                            selector.getTile().setHP(Tile.MAX_HP);
                                        }
                                    }
                                }
                            }
                        } else {
                            System.out.println(actions.get(0).getX() + "         "
                                    + actions.get(0).getY() + "      " + selector.getTile().getX()
                                    + "         " + selector.getTile().getY());
                            performAttack(tiles[actions.get(0).getX() / SIZE][actions.get(0).getY()
                                    / SIZE], selector.getTile());

                            displayStats();
                            String string = statLabel.getText();
                            string += "<br>";
                            Unit attacker = tiles[actions.get(0).getX() / SIZE][actions.get(0)
                                    .getY() / SIZE].getUnit();
                            Unit defender = selector.getSourceTile().getUnit();
                            string += "Potential Damage: " + Unit.calculateDamageDone(
                                    (double) attacker.getAttack(), (double) attacker.getHP(),
                                    (double) defender.getDefense(), (double) defender.getHP(),
                                    selector.getTile().getDefense()) + "<br>";
                            statLabel.setText(string);
                        }
                        // reset the actions
                        actions = null;
                    }
                }
            }

        });

    }

    public void setupGrid() {
        // creates border around the court area, JComponent method
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

        currentPlayer = players[0];

        tiles = new Tile[numOfWidthCells][numOfHeightCells];

        for (int x = 0; x < numOfWidthCells; x++) {
            for (int y = 0; y < numOfHeightCells; y++) {
                tiles[x][y] = new GrassTile(SIZE * x, SIZE * y, new Player(), null);
            }
        }

        // generates the tiles of the map

        int counter = 10;
        Random randomGenerator = new Random();
        int randomInt;
        while (counter >= 0) {
            for (int x = 0; x < numOfWidthCells; x++) {
                for (int y = 0; y < numOfHeightCells; y++) {
                    randomInt = randomGenerator.nextInt(100);
                    if (randomInt < 10) {
                        tiles[x][y] = new CityTile(SIZE * x, SIZE * y, new Player(), null);
                    }

                    randomInt = randomGenerator.nextInt(100);
                    if (randomInt < 8) {
                        tiles[x][y] = new ForestTile(SIZE * x, SIZE * y, new Player(), null);
                    }

                    randomInt = randomGenerator.nextInt(100);
                    if (randomInt < 15) {
                        tiles[x][y] = new MountainTile(SIZE * x, SIZE * y, new Player(), null);
                    }

                    if (x > 7 && x < 13 && y > 7 && y < 13) {
                        randomInt = randomGenerator.nextInt(100);
                        if (randomInt < 5) {
                            tiles[x][y] = new FactoryTile(SIZE * x, SIZE * y, new Player(), null);
                        }
                    }
                    counter--;
                }
            }
        }

        tiles[0][0] = new BaseTile(0, 0, players[0], new InfantryUnit(0, 0, players[0]));
        players[0].setBase(tiles[0][0]);

        tiles[19][19] = new BaseTile(SIZE * 19, SIZE * 19, players[1],
                new InfantryUnit(SIZE * 19, SIZE * 19, players[1]));
        players[1].setBase(tiles[19][19]);

        tiles[1][0] = new FactoryTile(SIZE * 1, 0, players[0], null);
        tiles[0][1] = new FactoryTile(0, SIZE * 1, players[0], null);
        tiles[18][19] = new FactoryTile(SIZE * 18, SIZE * 19, players[1], null);
        tiles[19][18] = new FactoryTile(SIZE * 19, SIZE * 18, players[1], null);

        tiles[7][7] = new FactoryTile(SIZE * 7, SIZE * 7, new Player(), null);

        tiles[13][13] = new FactoryTile(SIZE * 13, SIZE * 13, new Player(), null);

        playing = true;

        // set the next nodes of each tile
        for (int x = 0; x < numOfWidthCells; x++) {
            for (int y = 0; y < numOfHeightCells; y++) {
                List<Tile> nodes = new LinkedList<Tile>();
                if (x - 1 >= 0)
                    nodes.add(tiles[x - 1][y]);
                if (x + 1 < numOfWidthCells)
                    nodes.add(tiles[x + 1][y]);
                if (y - 1 >= 0)
                    nodes.add(tiles[x][y - 1]);
                if (y + 1 < numOfHeightCells)
                    nodes.add(tiles[x][y + 1]);
                tiles[x][y].setNodes(nodes);
            }
        }

        // set the first player's funds already
        addFundsAtEndOfTurn();

        selector = new Selector(0, 0, SIZE, SIZE, tiles[0][0], tiles);

        // add the labels
        add(playerLabel);
        playerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        playerLabel.setOpaque(true);
        playerLabel.setBackground(Color.WHITE);
        playerLabel.setFont(new Font(statLabel.getName(), Font.BOLD, 14));

        add(statLabel);
        statLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5, true));
        statLabel.setOpaque(true);
        statLabel.setBackground(Color.WHITE);
        statLabel.setFont(new Font(statLabel.getName(), Font.BOLD, 14));
        displayStats();

        // add items to shop
        units = new ArrayList<Unit>();
        units.add(new InfantryUnit(0, 0, currentPlayer));
        units.add(new MechUnit(0, 0, currentPlayer));
        units.add(new ReconUnit(0, 0, currentPlayer));
        units.add(new ArtilleryUnit(0, 0, currentPlayer));
        units.add(new TankUnit(0, 0, currentPlayer));
        units.add(new AntiAirUnit(0, 0, currentPlayer));
        units.add(new RocketUnit(0, 0, currentPlayer));

        // ask for keyboard focus
        requestFocusInWindow();
    }

    // animates the unit if the player moves and repaints the grid
    void tick() {
        if (playing) {
            if (path != null) {
                if (path.size() > 0) {
                    animating = true;
                    Tile tempTile = null;
                    if (path.size() == 1) {
                        if (selector.getSourceTile().getUnit().getHP() <= 0) {
                            tempTile = selector.getSourceTile();
                        }
                    }
                    if (((LinkedList<Tile>) path).getLast().getUnit() == null) {
                        selector.moveAUnit(((LinkedList<Tile>) path).getLast());
                    }
                    if (tempTile != null) {
                        tempTile.setUnit(null);
                        selector.setState(SelectorState.IDLE);
                    }
                    ((LinkedList<Tile>) path).removeLast();
                } else {
                    animating = false;
                }
                for (int x = 0; x < numOfWidthCells; x++) {
                    for (int y = 0; y < numOfWidthCells; y++) {
                        tiles[x][y].setHighlight(false);
                    }
                }
            }
            repaint();
        }
    }

    // calculates damage between two units
    private void performAttack(Tile attackingTile, Tile defendingTile) {
        Unit attacker = attackingTile.getUnit();
        Unit defender = defendingTile.getUnit();

        int damage = Unit.calculateDamageDone((double) attacker.getAttack(),
                (double) attacker.getHP(), (double) defender.getDefense(),
                (double) defender.getHP(), defendingTile.getDefense());

        defender.setHP(defender.getHP() - damage);

        attackingTile.getUnit().setTurn(true);

        if (defender.getHP() <= 0) {
            defender.getControlledBy().addToUnitsLost();
            defendingTile.setUnit(null);
            if (defendingTile.getHP() != 0) {
                defendingTile.setHP(Tile.MAX_HP);
            }
        } else if (attacker.getUnitType() != UnitType.Artillery
                && attacker.getUnitType() != UnitType.Rocket &&
                defender.getUnitType() != UnitType.Artillery
                && defender.getUnitType() != UnitType.Rocket) {
            damage = Unit.calculateDamageDone((double) defender.getAttack(),
                    (double) defender.getHP(), (double) attacker.getDefense(),
                    (double) attacker.getHP(), attackingTile.getDefense());

            attacker.setHP(attacker.getHP() - damage);

            if (attacker.getHP() <= 0) {
                attacker.getControlledBy().addToUnitsLost();
                attackingTile.setUnit(null);
                if (attackingTile.getHP() != 0) {
                    attackingTile.setHP(Tile.MAX_HP);
                }
            }
        }

    }

    private void displayStats() {
        statLabel.setFont(new Font(statLabel.getName(), Font.BOLD, 14));
        statLabel.setIcon(new ImageIcon(
                selector.getTile().getImage().getScaledInstance(80, 80, Image.SCALE_DEFAULT)));
        statLabel.setText(getInfoOnTile());
        System.out.println(selector.getTile().getX() + "--------------------"
                + selector.getTile().getY() + " --------------------" + statLabel.getLocation());

        String string = "<html><span style=\"color: #ff2341\">" + currentPlayer.getName()
                + "</span><br>";
        string += "Funds: " + currentPlayer.getFunds() + "<br>";

        string += "</html>";
        playerLabel.setText(string);
        statLabel.setSize(statLabel.getPreferredSize());
        if (selector.getTile().getX() >= 800 - statLabel.getWidth()
                && selector.getTile().getY() >= 800 - statLabel.getHeight()) {
            statLabel.setLocation(selector.getTile().getX() - statLabel.getWidth(),
                    selector.getTile().getY() - statLabel.getHeight());
        } else if (selector.getTile().getX() >= 800 - statLabel.getWidth()) {
            statLabel.setLocation(selector.getTile().getX() - statLabel.getWidth(),
                    selector.getTile().getY());
        } else if (selector.getTile().getY() >= 800 - statLabel.getHeight()) {
            statLabel.setLocation(selector.getTile().getX(),
                    selector.getTile().getY() - statLabel.getHeight());
        } else {
            statLabel.setLocation(selector.getTile().getX() + SIZE / 2,
                    selector.getTile().getY() + SIZE / 2);
        }

        playerLabel.setSize(playerLabel.getPreferredSize());
        if (selector.getTile().getX() < playerLabel.getWidth()
                && selector.getTile().getY() < playerLabel.getHeight()) {
            playerLabel.setLocation(800 - playerLabel.getWidth(), 800 - playerLabel.getHeight());
        } else {
            playerLabel.setLocation(0, 0);
        }
    }

    private String getInfoOnTile() {
        String string = "<html><span style=\"color: #ff0000\">";
        string += "Tile: " + selector.getTile().getTileType().toString() + "</span><br>";
        string += "Defense: " + selector.getTile().getDefense() + "<br>";
        if (selector.getTile().getHP() != 0) {
            string += "HP: " + selector.getTile().getHP() + "<br>";
        }

        string += "<br>State: " + selector.getState() + "<br>";

        if (selector.getTile().getUnit() != null) {
            Unit unit = selector.getTile().getUnit();
            string += "<br><span style=\"color: #ff0000\">Name: " + unit.getUnitType().toString()
                    + "</span><br>";
            string += "Attack: " + unit.getAttack() + "<br>";
            string += "Defense: " + unit.getDefense() + "<br>";
            string += "HP: " + unit.getHP() + "<br>";
            string += "Gas: " + unit.getGas() + "<br>";
            string += "Range: " + unit.getMinRange() + " ~ " + unit.getMaxRange() + "<br>";
            string += "SpacesMovement: " + unit.getSpacesAllowedToMove() + "<br>";
            string += "UsedTurn: " + unit.getTurn() + "<br>";
        }

        string += "</html>";
        return string;
    }

    private void addFundsAtEndOfTurn() {
        int earnings = 0;
        for (int x = 0; x < numOfWidthCells; x++) {
            for (int y = 0; y < numOfWidthCells; y++) {
                if (tiles[x][y].getControlledBy().equals(currentPlayer)) {
                    earnings += 1000;
                }
            }
        }
        currentPlayer.setFunds(currentPlayer.getFunds() + earnings);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < numOfWidthCells; x++) {
            for (int y = 0; y < numOfWidthCells; y++) {
                tiles[x][y].draw(g);
            }
        }
        selector.draw(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

}