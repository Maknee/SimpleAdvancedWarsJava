
import java.awt.*;
import java.awt.image.BufferedImage;

public class MechUnit extends Unit{
    
    private static final String image_file1 = "BlueMech.png";
    private static final String image_file2 = "GreenMech.png";
    private static final int SIZE = 40;

    private BufferedImage image1;
    private BufferedImage image2;
    
    public MechUnit(int x, int y, Player controlledBy) {
        super(SIZE, SIZE, x, y, 3, 1, 3000, 99, 4, UnitType.Mech, controlledBy);
        this.x = x;
        this.y = y;
        
        setMinRange(1);
        setMaxRange(1);
        
        image1 = findImage(image_file1);
        image2 = findImage(image_file2);
        
        if(controlledBy.getPlayerColor().equals(PlayerColor.BLUE)) {
            setImage(image1);
        } else if(controlledBy.getPlayerColor().equals(PlayerColor.GREEN)) {
            setImage(image2);
        }
    }
    
    public Unit copy() {
        return new MechUnit(x, y, getControlledBy());
    }
    
    @Override
    public void draw(Graphics g) {
        if(getControlledBy().getPlayerColor().equals(PlayerColor.BLUE)) {
            g.drawImage(image1, x, y, SIZE, SIZE, null);
        } else if(getControlledBy().getPlayerColor().equals(PlayerColor.GREEN)) {
            g.drawImage(image2, x, y, SIZE, SIZE, null);
        }
        
        if(getTurn()) {
            g.drawRoundRect(x, y, SIZE, SIZE, 10, 10);
        }
        
        g.drawString(Integer.toString(getHP()), x, y);
    }
}
