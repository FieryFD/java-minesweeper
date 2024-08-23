import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class GamePanel extends JPanel {
    private final int rowSize;
    private final int colSize;
    private final int difficulty;
    private int cellsToOpen = 0;
    boolean gameLost = false;
    boolean firstClick = true;
    int amountOfBombs = 0;

    public Tile[][] tiles;
    JLabel timerLabel;
    JLabel bombAmountLabel;

    Queue<int[]> tileIndexQueue = new LinkedList<>();
    Random random = new Random();
    LabelTimer timer = new LabelTimer();

    public GamePanel(int width, int height, int difficulty, JLabel timerLabel, JLabel bombAmountLabel){
        this.rowSize = width;
        this.colSize = height;
        this.difficulty = difficulty;
        this.timerLabel = timerLabel;
        this.bombAmountLabel = bombAmountLabel;
        this.setLayout(new GridLayout(this.rowSize, this.colSize));
        this.setBackground(Color.BLACK);

        tiles = new Tile[this.rowSize][this.colSize];

        startGame();
        this.setVisible(true);
    }

    private void startGame(){
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){

                boolean isBomb = random.nextInt(100) < difficulty;
                tiles[i][j] = new Tile(isBomb);

                if (isBomb){
                    amountOfBombs++;
                }
                Tile currentTile = tiles[i][j];
                int J = j;
                int I = i;

                currentTile.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(firstClick){
                            timer.startTimer(timerLabel);
                            firstClick = false;
                        }

                        //if tile is open then...
                        if (currentTile.state) return;

                        if (leftClickAndNotFlagged(e, currentTile)){
                            //if the tile has a bomb...
                            if (currentTile.hasBomb) {
                                //Game over
                                currentTile.openTile();
                                endGame();// ends the game, and reveals all other bombs
                                return;
                            }
                            //open the tile and count how many bombs around it.
                            revealTiles(I, J);
                            if (countBombs(I, J) == null){
                                //if no bombs around, add to the zero spread queue
                                addTilesToQueue(I, J);
                            }
                        }
                        else if (SwingUtilities.isRightMouseButton(e)) {
                            //if right click,
                            //if tile is not flagged, mark it.
                            if (!currentTile.flagged) {
                                currentTile.flagTile();
                                amountOfBombs--;
                            } else {// else, unflagging the tile.
                                currentTile.unflagTile();
                                amountOfBombs++;
                            }
                        }
                    }
                });

                this.add(tiles[i][j]);
            }
        }
        bombAmountLabel.setText("BombCount: "+amountOfBombs);
        cellsToOpen = (rowSize*colSize)-amountOfBombs;

    }

    //counts bombs around the tile

    /**
     * Counts the bomb around the tile, given the x and y position of the tile
     * @param iPosition x coordinate of the tile
     * @param jPosition y coordinate of the tile
     * @return text of how many bombs are around the tile
     */
    private String countBombs(int iPosition, int jPosition){
        int bombCount = 0;
        //iterates through the surrounding tiles to...
        for (int i = iPosition - 1; i <= iPosition + 1 ;i++){
            for (int j = jPosition - 1; j <= jPosition + 1; j++){
                if (isWithinBoardBoundary(i, j)){
                    //check if it is the clicked tile, or checks if it is an open tile then skips it
                    if (isTheClickedOrOpenedTile(i, j, iPosition, jPosition)) {
                        continue;
                    }
                    //check if the tile has a bomb...
                    if (tiles[i][j].hasBomb){
                        bombCount++;// then adds to the counter
                    }
                }
            }
        }
        if (bombCount == 0){
            return null;
        }
        colorsBombCount(bombCount, iPosition, jPosition);
        return Integer.toString(bombCount);
    }

    /**
     * Sets the colors for the bombCount
     * @param bombCount the count of bombs around it
     * @param iPosition the x coordinate of the bomb
     * @param jPosition the y coordinate of the bomb
    */
    private void colorsBombCount(int bombCount, int iPosition, int jPosition){
        switch (bombCount){
            case 1:
                tiles[iPosition][jPosition].bombCount.setForeground(Color.blue);
                break;
            case 2:
                tiles[iPosition][jPosition].bombCount.setForeground(Color.green);
                break;
            case 3:
                tiles[iPosition][jPosition].bombCount.setForeground(Color.red);
                break;
            case 4:
                tiles[iPosition][jPosition].bombCount.setForeground(new Color(0, 39,151));
                break;
            case 5:
                tiles[iPosition][jPosition].bombCount.setForeground(new Color(140, 0, 0));
                break;
            case 6:
                tiles[iPosition][jPosition].bombCount.setForeground(new Color(0, 175, 145));
                break;
            case 7:
                tiles[iPosition][jPosition].bombCount.setForeground(Color.BLACK);
                break;
            case 8:
                tiles[iPosition][jPosition].bombCount.setForeground(Color.lightGray);
                break;
        }
    }

    /**
     * Reveals the tile clicked
     * @param i x coordinate of the tile
     * @param j y coordinate of the tile
    */
    private void revealTiles(int i, int j){
        tiles[i][j].openTile();
        tiles[i][j].bombCount.setText(countBombs(i, j));
        cellsToOpen--;
        checkWinGame();

    }

    /**
     * Adds surrounding tiles to the Queue of tiles to check bomb count
     * @param iPosition x coordinate of the tile
     * @param jPosition y coordinate of the tile
     */
    private void addTilesToQueue(int iPosition, int jPosition){
        for (int i = iPosition - 1; i <= iPosition + 1 ;i++) {
            for (int j = jPosition - 1; j <= jPosition + 1; j++) {
                if (!isWithinBoardBoundary(i, j)){
                    continue;
                }
                if (isTheClickedOrOpenedTile(i, j, iPosition, jPosition)){
                    continue;
                }
                tileIndexQueue.offer(new int[]{i,j});
                zeroSpread();
            }
        }
    }

    /**
     * Reveals all tiles in the Queue
     */
    private void zeroSpread(){
        while (!tileIndexQueue.isEmpty()) {
            int[] pair = tileIndexQueue.poll(); // Retrieve and remove the head of the queue
            revealTiles(pair[0], pair[1]);

            /*
              After revealing the current tile in the queue,
              check its surrounding tiles and adds them to the queue
            */
            if (countBombs(pair[0], pair[1]) == null){
                addTilesToQueue(pair[0], pair[1]);
            }
        }
    }

    /**
     * Ends the game by opening all tiles that has a bomb in it
     */
    private void endGame(){
        timer.stopTimer();
        gameLost = true;
        for (int i = 0; i < rowSize; i++){
            for (int j = 0; j < colSize; j++){
                if (isFlagged(i, j) && !hasBomb(i, j)) {
                    tiles[i][j].setBackground(new Color(255, 96, 0));
                }
                if (!isFlagged(i, j) && hasBomb(i, j)) {
                    tiles[i][j].openTile();
                }
            }
        }
        bombAmountLabel.setText("You Lost");
    }

    /**
     * Checks the game for a winning condition
     */
    private void checkWinGame(){
        if (cellsToOpen == 0 && !gameLost){
            bombAmountLabel.setText("You Win!");
            timer.stopTimer();
        }
    }

    private boolean leftClickAndNotFlagged(MouseEvent e, Tile currentTile){
        return SwingUtilities.isLeftMouseButton(e) && !currentTile.flagged;
    }

    private boolean isTheClickedOrOpenedTile(int i, int j, int iPosition, int jPosition){
        return  (i == iPosition && j == jPosition) || tiles[i][j].state;
    }

    private boolean isWithinBoardBoundary(int i, int j){
        return i >= 0 &&  i < rowSize && j >= 0 && j < colSize;
    }

    private boolean isFlagged(int i, int j){
        return tiles[i][j].flagged;
    }

    private boolean hasBomb(int i, int j){
        return tiles[i][j].hasBomb;
    }
}
