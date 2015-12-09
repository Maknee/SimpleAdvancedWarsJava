import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

public abstract class Tile{
    
    private int width, height;
    
    private int x;
    
    private int y;
    
    private float costToEnter;
    
    private int defense;
    
    private TileType type;
    
    private Player controlledBy; 
    
    private Unit unit;
    
    private boolean highlighted;
    
    private List<Tile> nodes;
    
    private int hp;
    
    protected static final int MAX_HP = 20;
    
    private BufferedImage currentImage;
    
    public Tile(int width, int height, int x, int y, float costToEnter, int defense,
            TileType type, Player controlledBy, Unit unit) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.costToEnter = costToEnter;
        this.defense = defense;
        this.type = type;
        this.controlledBy = controlledBy;
        this.unit = unit;
        
        nodes = new LinkedList<Tile>();
    }
    
    public BufferedImage findImage(String image_file) {
        BufferedImage image = null;
        try {
            if (image == null) {
                image = ImageIO.read(new File(image_file));
                return image;
            }
        } catch (IOException e) {
            System.out.println("Internal Error:" + e.getMessage());
        }
        return image;
    }
    
    public void setHighlight(boolean highlighted) {
        this.highlighted = highlighted;
    }
    
    public boolean getHighlighted() {
        return highlighted;
    }
    
    public void setNodes(List<Tile> nodes) {
        this.nodes = nodes;
    }
    
    public List<Tile> getNodes() {
        return nodes;
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getDefense() {
        return defense;
    }
    
    public float getCostToEnter() {
        return costToEnter;
    }
    
    public TileType getTileType() {
        return type;
    }
    
    public void setHP(int hp) {
        this.hp = hp;
    }

    public int getHP() {
        return hp;
    }
    
    public void setController(Player player) {
        controlledBy = player;
    }
    
    public void setImage(BufferedImage image) {
        currentImage = image;
    }
    
    public Image getImage() {
        return currentImage;
    }
    
    public void setControlledBy(Player controlledBy) {
        this.controlledBy = controlledBy;
    }
    
    public Player getControlledBy() {
        return controlledBy;
    }
    
    public abstract Tile copy();
    
    public void draw(Graphics g) {
        
    }

}
