import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderBoard {
    final static String textName = "LeaderBoard.txt";

    static String line;

    public LeaderBoard() {

    }

    public static void addToLeaderBoard(Player winner, Player loser)
            throws FileNotFoundException, IOException {
        List<String> score = new ArrayList<String>();
        List<String> restOfScore = new ArrayList<String>();
        boolean replacingHighScore = false;
        int counter = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(textName));
            while ((line = in.readLine()) != null) {
                int startIndex = line.indexOf(" Funds spent: ");
                int endIndex = line.indexOf(" Buildings taken: ");
                System.out.println(startIndex + " --------------------" + endIndex + "----1");
                startIndex += 14;
                String string = line.substring(startIndex, endIndex);
                int fundsSpent = Integer.parseInt(string);
                System.out.println(fundsSpent + "=--------3");
                if (!replacingHighScore) {
                    if (winner.getFundsSpent() >= fundsSpent) {
                        replacingHighScore = true;
                        restOfScore.add(line);
                    } else {
                        score.add(line);
                    }
                } else {
                    if(counter == 4 && replacingHighScore) {
                        
                    } else {
                        restOfScore.add(line);
                    }
                }
                counter++;
            }
            if (counter == 5) {
                writeToFile(winner, loser, score, restOfScore, replacingHighScore);
            } else if (counter < 5) {
                replacingHighScore = true;
                writeToFile(winner, loser, score, restOfScore, replacingHighScore);
            }
            in.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException ex) {
            throw new IOException();
        }
    }

    private static void writeToFile(Player winner, Player loser, List<String> score,
            List<String> restOfScore, boolean replacingHighScore) throws IOException {
        int counter = 0;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(textName));
            for (String string : score) {
                out.write(string);
                if(counter < 4) {
                    out.newLine();
                }
                counter++;
            }
            if (replacingHighScore) {
                out.write("Player won: " + winner.getName() + " Funds spent: "
                        + winner.getFundsSpent() + " Buildings taken: " + winner.getBuildingsTaken()
                        + " Units bought: " + winner.getUnitsBought() + " " + "Player lost: "
                        + loser.getName() + " Funds spent: " + loser.getFundsSpent()
                        + " Buildings taken: " + loser.getBuildingsTaken() + " Units bought: "
                        + loser.getUnitsBought());
                if(counter < 4) {
                    out.newLine();
                }
                counter++;
            }
            for (String string : restOfScore) {
                out.write(string);
                if (counter < 4) {
                    out.newLine();
                }
            }
            out.close();
        } catch (IOException e) {
            throw new IOException();
        }

    }
    
    public static String getLeaderBoard() throws FileNotFoundException, IOException {
        List<String> score = new ArrayList<String>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(textName));
            while ((line = in.readLine()) != null) {
                score.add(line);
            }
            in.close();
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException ex) {
            throw new IOException();
        }
        String leaderBoardString = "<html>Scores!<br>";
        for(String string: score) {
            leaderBoardString += string + "<br>";
        }
        leaderBoardString += "</html>";
        return leaderBoardString;
    }
}
