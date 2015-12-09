
import java.awt.*;
import java.awt.image.BufferedImage;

public class FactoryTile extends Tile{
    

    private final String image_file1 = "EmptyFactory.png";
    private final String image_file2 = "BlueFactory.png";
    private final String image_file3 = "GreenFactory.png";

    private static final int SIZE = 40;
    private int x;
    private int y;

    private BufferedImage image1;
    private BufferedImage image2;
    private BufferedImage image3;
    
    public FactoryTile(int x, int y, Player controlledBy, Unit unit) {
        super(SIZE, SIZE, x, y, 1f, 3, TileType.Factory, controlledBy, unit);
        this.x = x;
        this.y = y;
        
        setHP(MAX_HP);
        
        setUnit(unit);

        setControlledBy(controlledBy);
        
        image1 = findImage(image_file1);
        image2 = findImage(image_file2);
        image3 = findImage(image_file3);
    }
    
    public Tile copy() {
        return new FactoryTile(x, y, getControlledBy(), getUnit());
    }
    
    @Override
    public void draw(Graphics g) {
        if (getControlledBy().getPlayerColor().equals(PlayerColor.NONE)) {
            g.drawImage(image1, x, y, SIZE, SIZE, null);
            setImage(image1);
        } else if (getControlledBy().getPlayerColor().equals(PlayerColor.BLUE)) {
            g.drawImage(image2, x, y, SIZE, SIZE, null);
            setImage(image2);
        } else if (getControlledBy().getPlayerColor().equals(PlayerColor.GREEN)) {
            g.drawImage(image3, x, y, SIZE, SIZE, null);
            setImage(image3);
        }

        if(getHighlighted()) {
            g.drawRoundRect(x, y, SIZE, SIZE, 100, 100);
        }
        
        if (getUnit() != null) {
            getUnit().draw(g);
        }
        
        g.setColor(Color.CYAN);
        g.drawString(Integer.toString(getHP()), x + SIZE/2, y + SIZE);
        g.setColor(Color.BLACK);
    }
}
