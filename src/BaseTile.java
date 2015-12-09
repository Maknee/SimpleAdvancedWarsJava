
import java.awt.*;
import java.awt.image.BufferedImage;

public class BaseTile extends Tile{
    
    private final String image_file1 = "BlueBase.png";
    private final String image_file2 = "GreenBase.png";
    private static final int SIZE = 40;
    private int x;
    private int y;

    private BufferedImage image1;
    private BufferedImage image2;
    
    public BaseTile(int x, int y, Player controlledBy, Unit unit) {
        super(SIZE, SIZE, x, y, 1f, 1, TileType.Base, controlledBy, unit);
        this.x = x;
        this.y = y;
        
        setHP(20);
        
        setUnit(unit);
        
        setControlledBy(controlledBy);
        
        image1 = findImage(image_file1);
        image2 = findImage(image_file2);
        
        setImage(image1);
    }
    
    public Tile copy() {
        return new BaseTile(x, y, getControlledBy(), getUnit());
    }
    
    @Override
    public void draw(Graphics g) {
        if(getControlledBy().getPlayerColor().equals(PlayerColor.BLUE)) {
            g.drawImage(image1, x, y, SIZE, SIZE, null);
            setImage(image1);
        } else if(getControlledBy().getPlayerColor().equals(PlayerColor.GREEN)) {
            g.drawImage(image2, x, y, SIZE, SIZE, null);
            setImage(image2);
        } 
        
        if(getUnit() != null) {
            getUnit().draw(g);
        }
    }
}

