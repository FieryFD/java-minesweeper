import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Tile extends JPanel {

    //false = closed, true = open
    public boolean state = false;

    //false = no Bomb, true = has a Bomb
    public boolean hasBomb;

    //Displays how many bombs around it
    public JLabel bombCount;

    public boolean flagged = false;

    public Tile(boolean hasBomb){
        this.hasBomb = hasBomb;
        this.setBorder(new BevelBorder(BevelBorder.RAISED));
        this.setBackground(Color.lightGray);
        this.setPreferredSize(new Dimension(35, 35));
        bombCount = new JLabel("", SwingConstants.CENTER);
        bombCount.setHorizontalAlignment(JLabel.CENTER);
        bombCount.setFont(new Font("Roboto", Font.BOLD, 18));
        this.add(bombCount);

        this.setVisible(true);
    }

    public void openTile(){
        state = true;
        this.setBorder(new LineBorder(Color.gray));

        if (hasBomb) {
            setBackground(Color.RED);
        }
        else {
            setBackground(Color.white);
        }

    }

    public void flagTile(){
        setBackground(Color.orange);
        flagged = true;
    }

    public void unflagTile(){
        setBackground(Color.lightGray);
        flagged = false;
    }
}
