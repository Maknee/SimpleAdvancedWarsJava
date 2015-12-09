

import java.awt.*;
import java.awt.image.BufferedImage;

public class MountainTile extends Tile{
    
    private final String image_file = "MountainTile.png";
    private static final int SIZE = 40;
    private int x;
    private int y;

    private BufferedImage image;
    
    public MountainTile(int x, int y, Player controlledBy, Unit unit) {
        super(SIZE, SIZE, x, y, Float.POSITIVE_INFINITY, 5, TileType.Mountain, controlledBy, unit);
        this.x = x;
        this.y = y;
        
        setUnit(unit);
        setControlledBy(controlledBy);
        
        image = findImage(image_file);
    }
    
    public Tile copy() {
        return new MountainTile(x, y, getControlledBy(), getUnit());
    }
    
    @Override
    public void draw(Graphics g) {
        g.drawImage(image, x, y, SIZE, SIZE, null);
        setImage(image);
    }
}

