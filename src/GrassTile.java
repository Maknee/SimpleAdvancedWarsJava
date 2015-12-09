
import java.awt.*;
import java.awt.image.BufferedImage;

public class GrassTile extends Tile{
    
    private static final String image_file = "GrassTile.png";
    private static final int SIZE = 40;
    private int x;
    private int y;
    
    private BufferedImage image;

    public GrassTile(int x, int y, Player controlledBy, Unit unit) {
        super(SIZE, SIZE, x, y, 1f, 1, TileType.Grass, controlledBy, unit);
        this.x = x;
        this.y = y;
        setUnit(unit);
        setControlledBy(controlledBy);
        image = findImage(image_file);
    }
    
    public Tile copy() {
        return new GrassTile(x, y, getControlledBy(), getUnit());
    }
    
    @Override
    public void draw(Graphics g) {
        g.drawImage(image, x, y, SIZE, SIZE, null);
        setImage(image);
        if(getUnit() != null) {
            getUnit().draw(g);
        }
        
        if(getHighlighted()) {
            g.drawRoundRect(x, y, SIZE, SIZE, 100, 100);
        }
    }
}

