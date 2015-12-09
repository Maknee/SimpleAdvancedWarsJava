import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public abstract class Unit{
    
    UnitType type = UnitType.None;
    
    private boolean usedTurn = false;
    
    private int spacesAllowedToMove;
    
    private int width, height;
    
    protected int x;
    
    protected int y;
    
    private int attack;
    
    private int defense;
    
    private int cost;
    
    private int hp;
    
    private int gas;
    
    private boolean moved;
    
    private int minRange;
    
    private int maxRange;
    
    private int maxGas = 99;
    
    private static final int MAX_HP = 10;
    
    private Player controlledBy; 
    
    private Image currentImage;
    
    public Unit(int width, int height, int x, int y, int attack, int defense, int cost, int maxGas, int spacesAllowedToMove,
            UnitType type, Player controlledBy) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.attack = attack;
        this.defense = defense;
        this.cost = cost;
        hp = MAX_HP;
        this.maxGas = maxGas;
        gas = maxGas;
        this.spacesAllowedToMove = spacesAllowedToMove;
        this.type = type;
        this.controlledBy = controlledBy;
    }
    
    public Unit() {
        
    }
    
    public int getSpacesAllowedToMove() {
        return spacesAllowedToMove;
    }
    
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getAttack() {
        return attack;
    }
    
    public int getDefense() {
        return defense;
    }
    
    public int getHP() {
        return hp;
    }
    
    public void setHP(int hp) {
        this.hp = hp;
    }
    
    public void setTurn(boolean usedTurn) {
        this.usedTurn = usedTurn;
    }
    
    public boolean getTurn() {
        return usedTurn;
    }
    
    public UnitType getUnitType() {
        return type;
    }
    
    public void setImage(Image image) {
        currentImage = image;
    }
    
    public Image getImage() {
        return currentImage;
    }
    
    public void setCost(int cost) {
        this.cost = cost;
    }
    
    public int getCost() {
        return cost;
    }
    
    public void setGas(int gas) {
        this.gas = gas;
    }
    
    public int getGas() {
        return gas;
    }
    
    public void setMaxGas(int maxGas) {
        this.maxGas = maxGas;
    }
    
    public int getMaxGas() {
        return maxGas;
    }
    
    public void setMoved(boolean moved) {
        this.moved = moved;
    }
    
    public boolean getMoved() {
        return moved;
    }
    
    public void setMinRange(int minRange) {
        this.minRange = minRange;
    }
    
    public int getMinRange() {
        return minRange;
    }
    
    public void setMaxRange(int maxRange) {
        this.maxRange = maxRange;
    }
    
    public int getMaxRange() {
        return maxRange;
    }
    
    public Player getControlledBy() {
        return controlledBy;
    }
    
    public abstract Unit copy();
    
    public static int calculateDamageDone(double attack, double attackerHP, double defense, double defenderHP, double tileDefense) {
        return (int) Math.round(attack * 10 * (attackerHP/MAX_HP) / ((defense * tileDefense * defenderHP/MAX_HP) + 1));
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
    
    public void draw(Graphics g) {
        
    }

}
