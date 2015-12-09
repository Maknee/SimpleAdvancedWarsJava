import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

public class Selector {

    private BufferedImage pointer;
    private int x, y, width, height;
    private SelectorState state;

    private Tile[][] tiles;

    private Tile tileOn;

    private Tile source;

    private static final int SIZE = 40;

    public Selector(int x, int y, int width, int height, Tile tileOn, Tile[][] tiles) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tileOn = tileOn;
        this.tiles = tiles;
        findImage("mouse_pointer.png");
        state = SelectorState.IDLE;

    }

    private void findImage(String image_file) {
        try {
            if (pointer == null) {
                pointer = ImageIO.read(new File(image_file));
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
    }

    public void moveTo(int xDisplacement, int yDisplacement, Tile tileOn) {
        x += xDisplacement;
        y += yDisplacement;

        this.tileOn = tileOn;
    }

    public void setLocation(int x, int y, Tile tileOn) {
        this.x = x;
        this.y = y;

        this.tileOn = tileOn;
    }

    // returns the tiles that the player can move to
    public List<Tile> selectedTile(Player player) {
        if (state == SelectorState.IDLE) {
            drawAllPossiblePaths(player);
            return null;
        } else if (state == SelectorState.SELECTING) {
            return moveToTile(player);
        } else if (state == SelectorState.SELECTED) {
            return allPossibleActions(player);
        }
        return null;
    }

    // draws the possible paths from a unit
    public void drawAllPossiblePaths(Player player) {
        List<Tile> paths = new ArrayList<Tile>();
        // if there is a unit there at all
        if (tileOn.getUnit() != null) {
            // if the unit is controlled by the current player and checks if the
            // unit has already moved
            if (tileOn.getUnit().getControlledBy() == player) {
                if (!tileOn.getUnit().getTurn()) {
                    source = tileOn; // store the movement
                    // goes through
                    for (int x = -source.getUnit().getSpacesAllowedToMove(); x <= source.getUnit()
                            .getSpacesAllowedToMove(); x++) {
                        for (int y = -source.getUnit().getSpacesAllowedToMove(); y <= source
                                .getUnit().getSpacesAllowedToMove(); y++) {
                            if (source.getX() / SIZE + x >= 0 && source.getX() / SIZE + x < SIZE / 2
                                    && source.getY() / SIZE + y >= 0
                                    && source.getY() / SIZE + y < SIZE / 2) {
                                if (ableToMoveTo(player,
                                        tiles[source.getX() / SIZE + x][source.getY() / SIZE
                                                + y])) {
                                    paths.add(tiles[source.getX() / SIZE + x][source.getY() / SIZE
                                            + y]);
                                    tiles[source.getX() / SIZE + x][source.getY() / SIZE + y]
                                            .setHighlight(true);
                                }
                            }
                        }
                    }

                    System.out.println("Selected");
                    state = SelectorState.SELECTING;
                }
            } else {
                System.out.println("Cannot be selected - there is an enemy");
            }
        } else {
            System.out.println("Cannot be selected - there is no unit");
        }
    }

    // returns a boolean for if the unit can move to the specified grid location
    private boolean ableToMoveTo(Player player, Tile target) {
        if (target.getCostToEnter() == Float.POSITIVE_INFINITY) {
            state = SelectorState.IDLE;
            return false;
        } else if (target.getUnit() != null) {
            if (target.getUnit().getControlledBy() != player) {
                state = SelectorState.SELECTING;
                return false;
            }
        }

        Map<Tile, Float> distance = new HashMap<Tile, Float>();
        Map<Tile, Tile> previous = new HashMap<Tile, Tile>();

        List<Tile> unvisitedNodes = new ArrayList<Tile>();

        for (Tile[] tileChunk : tiles) {
            for (Tile tile : tileChunk) {
                if (tile != source) {
                    distance.put(tile, Float.POSITIVE_INFINITY);
                    previous.put(tile, null);
                }

                unvisitedNodes.add(tile);
            }
        }

        distance.put(source, 0f);

        previous.put(source, null);

        while (unvisitedNodes.size() > 0) {
            Tile unvisitedNode = null;

            for (Tile tile : unvisitedNodes) {
                if (unvisitedNode == null || distance.get(tile) < distance.get(unvisitedNode)) {
                    unvisitedNode = tile;
                }
            }

            if (unvisitedNode.equals(target)) {
                break;
            }

            unvisitedNodes.remove(unvisitedNode);

            for (Tile tile : unvisitedNode.getNodes()) {
                if (tile != null) {
                    float distanceTotal = distance.get(unvisitedNode) + tile.getCostToEnter();
                    if (tile.getUnit() != null) {
                        if (tile.getUnit().getControlledBy() != player) {
                            distanceTotal += Float.POSITIVE_INFINITY;
                        }
                    }

                    if (distanceTotal < distance.get(tile)) {
                        distance.put(tile, distanceTotal);
                        previous.put(tile, unvisitedNode);
                    }
                }
            }
        }

        if (previous.get(target) == null) {
            return false;
        }

        List<Tile> path = new LinkedList<Tile>();

        Tile linkedTile = target;

        while (linkedTile != null) {
            ((LinkedList<Tile>) path).addFirst(linkedTile);
            linkedTile = previous.get(linkedTile);
        }

        if (path.size() - 1 > source.getUnit().getSpacesAllowedToMove()) {
            return false;
        }

        return true;

    }

    // only for 1 range units
    private List<Tile> allPossibleActions(Player player) {
        List<Tile> actions = new LinkedList<Tile>();
        actions.add(source);
        if (source.getUnit().getUnitType() != UnitType.Artillery
                && source.getUnit().getUnitType() != UnitType.Rocket) {
            for (Tile tile : source.getNodes()) {
                if (tile != null) {
                    if (tile.getUnit() != null) {
                        if (tile.getUnit().getControlledBy() != player) {
                            ((LinkedList<Tile>) actions).addLast(tile);
                        }
                    }
                }
            }
        }
        return actions;
    }

    // actions for artillery/rocket
    public List<Tile> allPossibleActionsForRangedUnits(Player player, int minRange, int maxRange) {
        ArrayList<Tile> paths = new ArrayList<Tile>();
        for (int x = -maxRange; x <= maxRange; x++) {
            for (int y = -maxRange; y <= maxRange; y++) {
                if (tileOn.getX() / SIZE + x >= 0 && tileOn.getX() / SIZE + x < SIZE / 2
                        && tileOn.getY() / SIZE + y >= 0 // CHANGE STUFF HERE 20
                                                         // -> NUM OF WIDTH
                        && tileOn.getY() / SIZE + y < SIZE / 2) {
                    Tile target = tiles[tileOn.getX() / SIZE + x][tileOn.getY() / SIZE + y];
                    // if they are on same tile
                    if (target.getCostToEnter() == Float.POSITIVE_INFINITY
                            || target.getUnit() == null || target.equals(tileOn)) {
                        continue;
                    } else if (target.getUnit() != null) {
                        if (target.getUnit().getControlledBy().equals(player)) {
                            continue;
                        }
                    }

                    Map<Tile, Float> distance = new HashMap<Tile, Float>();
                    Map<Tile, Tile> previous = new HashMap<Tile, Tile>();

                    List<Tile> unvisitedNodes = new ArrayList<Tile>();

                    for (Tile[] tileChunk : tiles) {
                        for (Tile tile : tileChunk) {
                            if (tile != source) {
                                distance.put(tile, Float.POSITIVE_INFINITY);
                                previous.put(tile, null);
                            }

                            unvisitedNodes.add(tile);
                        }
                    }

                    distance.put(source, 0f);

                    previous.put(source, null);

                    while (unvisitedNodes.size() > 0) {
                        Tile unvisitedNode = null;

                        for (Tile tile : unvisitedNodes) {
                            if (unvisitedNode == null
                                    || distance.get(tile) < distance.get(unvisitedNode)) {
                                unvisitedNode = tile;
                            }
                        }

                        if (unvisitedNode.equals(target)) {
                            break;
                        }

                        unvisitedNodes.remove(unvisitedNode);

                        for (Tile tile : unvisitedNode.getNodes()) {
                            if (tile != null) {
                                float distanceTotal = distance.get(unvisitedNode) + 1;

                                if (distanceTotal < distance.get(tile)) {
                                    distance.put(tile, distanceTotal);
                                    previous.put(tile, unvisitedNode);
                                }
                            }
                        }
                    }

                    List<Tile> path = new LinkedList<Tile>();

                    Tile linkedTile = target;

                    while (linkedTile != null) {
                        ((LinkedList<Tile>) path).addFirst(linkedTile);
                        linkedTile = previous.get(linkedTile);
                    }

                    if (path.size() - 1 >= minRange && path.size() - 1 <= maxRange) {
                        paths.add(target);
                    }

                }
            }
        }
        return paths;

    }

    // moves a unit to a tile
    public List<Tile> moveToTile(Player player) {
        if (tileOn.equals(source)) {
            state = SelectorState.SELECTED;
            System.out.println("You selected the same spot");
            List<Tile> path = new LinkedList<Tile>();
            path.add(source);
            return path;
        } else {
            if (source.getHP() != 0 && source.getHP() < Tile.MAX_HP) {
                source.setHP(Tile.MAX_HP);
            }

            // moved = true
            source.getUnit().setMoved(true);
        }
        if (tileOn.getCostToEnter() == Float.POSITIVE_INFINITY) {
            state = SelectorState.SELECTING;
            System.out.println("Cannot be move to");
            return null;
        } else if (tileOn.getUnit() != null) {
            if (tileOn.getUnit().getControlledBy() != player) {
                state = SelectorState.SELECTING;
                System.out.println("You cannot move ontop of enemy unit");
                return null;
            } else if (tileOn.getUnit().getControlledBy() == player) {
                if (tileOn.getUnit().getUnitType().equals(source.getUnit().getUnitType())) {
                    int hp = source.getUnit().getHP() + tileOn.getUnit().getHP();
                    if (hp > 10) {
                        hp = 10;
                    }
                    tileOn.getUnit().setHP(hp);
                    source.getUnit().setHP(0);
                } else { // not same unit
                    state = SelectorState.SELECTING;
                    System.out.println("You cannot merge unit");
                    return null;
                }
            }
        }

        Map<Tile, Float> distance = new HashMap<Tile, Float>();
        Map<Tile, Tile> previous = new HashMap<Tile, Tile>();

        List<Tile> unvisitedNodes = new ArrayList<Tile>();

        for (Tile[] tileChunk : tiles) {
            for (Tile tile : tileChunk) {
                if (tile != source) {
                    distance.put(tile, Float.POSITIVE_INFINITY);
                    previous.put(tile, null);
                }

                unvisitedNodes.add(tile);
            }
        }

        distance.put(source, 0f);

        previous.put(source, null);

        while (unvisitedNodes.size() > 0) {
            Tile unvisitedNode = null;

            for (Tile tile : unvisitedNodes) {
                if (unvisitedNode == null || distance.get(tile) < distance.get(unvisitedNode)) {
                    unvisitedNode = tile;
                }
            }

            if (unvisitedNode.equals(tileOn)) {
                break;
            }

            unvisitedNodes.remove(unvisitedNode);

            for (Tile tile : unvisitedNode.getNodes()) {
                if (tile != null) {
                    float distanceTotal = distance.get(unvisitedNode) + tile.getCostToEnter();
                    if (tile.getUnit() != null) {
                        if (tile.getUnit().getControlledBy() != player) {
                            distanceTotal += Float.POSITIVE_INFINITY;
                        }
                    }

                    if (distanceTotal < distance.get(tile)) {
                        distance.put(tile, distanceTotal);
                        previous.put(tile, unvisitedNode);
                    }
                }
            }
        }

        if (previous.get(tileOn) == null) {
            state = SelectorState.IDLE;
            return null;
        }

        List<Tile> path = new LinkedList<Tile>();

        Tile linkedTile = tileOn;

        while (linkedTile != null) {
            ((LinkedList<Tile>) path).addLast(linkedTile);
            linkedTile = previous.get(linkedTile);
        }

        if (path.size() - 1 > source.getUnit().getSpacesAllowedToMove()) {
            state = SelectorState.SELECTING;
            System.out.println("Unit movement cannot move this far!");
            return null;
        }

        for (Tile tile : path) {
            System.out.println(tile.getX() + "        " + tile.getY());
        }

        source.getUnit().setGas(source.getUnit().getGas() - path.size() + 1);

        if (source.getUnit().getGas() <= 0) {
            for (int x = -source.getUnit().getSpacesAllowedToMove(); x <= source.getUnit()
                    .getSpacesAllowedToMove(); x++) {
                for (int y = -source.getUnit().getSpacesAllowedToMove(); y <= source.getUnit()
                        .getSpacesAllowedToMove(); y++) {
                    if (source.getX() / 40 + x >= 0 && source.getX() / 40 + x < 20
                            && source.getY() / 40 + y >= 0 && source.getY() / 40 + y < 20) {
                        tiles[source.getX() / 40 + x][source.getY() / 40 + y].setHighlight(false);

                    }
                }
            }
            source.setUnit(null);
            setState(SelectorState.IDLE);
            return null;
        }

        state = SelectorState.SELECTED;

        System.out.println("Moved to tile!");

        return path;
    }

    // Moves a unit to a location
    public void moveAUnit(Tile tile) {
        if (!source.equals(tile)) {
            boolean noUnit = false;
            Tile tempTile = null;
            if (tile.getUnit() != null) {
                tempTile = tile.copy();
                tempTile.getUnit().setLocation(source.getX(), source.getY());
                noUnit = true;
            }
            Unit unit = source.getUnit();
            unit.setLocation(tile.getX(), tile.getY());
            tile.setUnit(unit);
            if (!noUnit) {
                source.setUnit(null);
            } else {
                source.setUnit(tempTile.getUnit());
            }
            source = tile;
        }
    }

    public SelectorState getState() {
        return state;
    }

    public void setState(SelectorState state) {
        this.state = state;
    }

    public Tile getTile() {
        return tileOn;
    }

    public Tile getSourceTile() {
        return source;
    }

    public void draw(Graphics g) {
        g.drawImage(pointer, x, y, width, height, null);
    }
}
