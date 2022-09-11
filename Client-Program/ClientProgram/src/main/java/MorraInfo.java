import java.io.Serializable;

public class MorraInfo implements Serializable {
    public boolean have2players;
    public int p1hand, p1guess, p2hand, p2guess;
    public boolean roundWinner, isDraw, isVictor, playingAgain; 

    public int p1points, p2points; 

    MorraInfo() {
        have2players = false;
        p1points = 0;
        p2points = 0;
        roundWinner = false; 
        isDraw = false; 
        isVictor = false;
        playingAgain = false; 
    }

    public void print() {
        System.out.println("have2players = " + have2players + " p1points = " + Integer.toString(p1points) + " p2 points = " + Integer.toString(p2points) + " playingAgain: " + playingAgain);
    }

    public boolean have2players() {
        if (have2players) return true; else return false; 
    }

    public void setHave2Players(boolean b) {
        have2players = b;
    }

    public int getp1hand() {
        return p1hand;
    }

    public void setp1hand(int h) {
        p1hand = h;
    }

    public int getp2hand() {
        return p2hand;
    }

    public void setp2hand(int h) {
        p2hand = h;
    }

    public int getp1guess() {
        return p1guess;
    }

    public void setp1guess(int g) {
        p1guess = g;
    }

    public int getp2guess() {
        return p2guess;
    }

    public void setp2guess(int g) {
        p2guess = g;
    }

    public int getp1Points() {
        return p1points;
    }

    public int getp2Points() {
        return p2points;
    }

    public void setp1Points(int points) {
        p1points = points;
    }
    
    public void setp2Points(int points) {
        p2points = points; 
    }

    public boolean isWinnner() {
        return roundWinner; 
    }

    public void setWinner(boolean b) {
        roundWinner = b; 
    }

    public boolean isDraw() {
        return isDraw;
    }

    public void setIsDraw(boolean b) {
        isDraw = b;
    }

    public void setIsVictor(boolean b) {
        isVictor = b;
    }

    public boolean isVictor() {
        return isVictor;
    }

    public boolean isPlayingAgain() {
        return playingAgain;
    }

    public void setIsPlayingAgain(boolean b)  {
        playingAgain = b; 
    }

    public void reset() {
        have2players = false; 
        p1hand = 0; p2hand = 0; 
        p1guess = 0; p2guess = 0;
        p1points = 0; p2points = 0; 
        roundWinner = false; isDraw = false; isVictor = false; playingAgain = false; 
    }
}
