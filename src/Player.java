import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;

    private PlayerColor color;

    private int funds;

    private Tile base;
    
    private int fundsSpent;
    
    private int buildingsTaken;
    
    private int unitsBought;
    
    private int unitsLost;

    public Player(String name, PlayerColor color) {
        this.name = name;
        this.color = color;

        base = null;
    }

    public Player() {
        this.color = PlayerColor.NONE;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    public void setFunds(int funds) {
        this.funds = funds;
    }

    public int getFunds() {
        return funds;
    }

    public void setBase(Tile base) {
        this.base = base;
    }

    public Tile getBase(Tile base) {
        return base;
    }
    
    public void addToFundsSpent(int funds) {
        fundsSpent += funds;
    }

    public int getFundsSpent() {
        return fundsSpent;
    }    
    
    public void addToUnitsBought() {
        unitsBought++;
    }    
    
    public int getUnitsBought() {
        return unitsBought;
    }
    
    public void addToUnitsLost() {
        unitsLost++;
    }    
    
    public int getUnitsLost() {
        return unitsLost;
    }
    
    public void addTobuildingsTaken() {
        buildingsTaken++;
    }
    
    public int getbuildingsTaken() {
        return buildingsTaken;
    }
    
    public int getBuildingsTaken() {
        return buildingsTaken;
    }
    
    public void resetScores() {
        fundsSpent = 0;
        buildingsTaken = 0;
        unitsBought = 0;
        unitsLost = 0;

    }
    
    public PlayerColor getPlayerColor() {
        return color;
    }
    
}
