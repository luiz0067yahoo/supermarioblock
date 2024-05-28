package aula03;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MarioLikeGame extends JPanel implements ActionListener {
    private Timer timer;
    private JLabel player;
    private JLabel[] coins;
    private JLabel[] stairs;
    private int coinsCount;
    private JLabel coinsCounter;

    public MarioLikeGame() {
        initGame();
    }

    private void initGame() {
        setFocusable(true);
        setPreferredSize(new Dimension(800, 400));
        setBackground(new Color(176, 224, 230));
        setLayout(null);

        player = new JLabel();
        player.setBounds(0, 360, 20, 40);
        player.setBackground(Color.RED);
        player.setOpaque(true);
        add(player);

        coins = new JLabel[] {
                createCoin(100, 200),
                createCoin(200, 200),
                createCoin(300, 200),
                createCoin(500, 200)
        };
        for (JLabel coin : coins) {
            add(coin);
        }

        stairs = new JLabel[] {
                createStair(300, 320),
                createStair(100, 360),
                createStair(140, 320),
                createStair(180, 360),
                createStair(220, 320)
        };
        for (JLabel stair : stairs) {
            add(stair);
        }

        coinsCounter = new JLabel("Moedas: 0");
        coinsCounter.setBounds(10, 10, 200, 30);
        coinsCounter.setForeground(Color.WHITE);
        add(coinsCounter);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyPressedHandler(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyReleasedHandler(e);
            }
        });

        timer = new Timer(1000 / 10, this);
        timer.start();
    }

    private JLabel createCoin(int x, int y) {
        JLabel coin = new JLabel();
        coin.setBounds(x, y, 20, 20);
        coin.setBackground(Color.YELLOW);
        coin.setOpaque(true);
        return coin;
    }

    private JLabel createStair(int x, int y) {
        JLabel stair = new JLabel();
        stair.setBounds(x, y, 40, 40);
        stair.setBackground(Color.GRAY);
        stair.setOpaque(true);
        return stair;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        applyGravity();
        applyMove();
        checkCollisions();
        repaint();
    }

    private void applyGravity() {
        int height = getHeight();
        if (positionBottom < limitJump && isJumping && !isDownning) {
            acelerate = Math.sqrt(2 * gravity * (limitJump - positionBottom));
            positionBottom = Math.min(positionBottom + (int) acelerate, limitJump);
        }else {
        	isDownning=true;
        	isUpning=false;
        	if (positionBottom > base && acelerate > 0 && isJumping && isDownning) {
                acelerate += gravity;
                positionBottom = Math.max(positionBottom - (int) acelerate, base);
            } else {
                acelerate = 0;
                positionBottom = base;
                isJumping = false;
                isDownning=false;
            	isUpning=false;
            }
        }
        player.setLocation(positionLeft, height - positionBottom - player.getHeight());
    }

    private void applyMove() {
        if (isMovingRight && !stopMovingRight) {
            positionLeft += moveSize;
        } else if (isMovingLeft && !stopMovingLeft) {
            positionLeft -= moveSize;
        }
        player.setLocation(positionLeft, player.getY());
    }

    private void checkCollisions() {
        Rectangle playerBounds = player.getBounds();

        for (JLabel coin : coins) {
            if (coin.isVisible() && playerBounds.intersects(coin.getBounds())) {
                coin.setVisible(false);
                coinsCount++;
                coinsCounter.setText("Moedas: " + coinsCount);
            }
        }

        boolean stairsFound = false;
        boolean stopMovingRight = false;
        boolean stopMovingLeft = false;
        int base = 0;
        this.limitJump=base+jump;
        for (JLabel stair : stairs) {
            Rectangle stairBounds = stair.getBounds();
            if (
            		playerBounds.y-playerBounds.height  <= stairBounds.y 
            		&& 
                    playerBounds.x + playerBounds.width > stairBounds.x 
                    &&
                    playerBounds.x < stairBounds.x + stairBounds.width
               )
            {
                stairsFound = true;
                if (playerBounds.y + playerBounds.height <= stairBounds.y + 10) {
                    base = getHeight() - stairBounds.y ;
                    this.base = base;
                }
                if (playerBounds.y > stairBounds.y + stairBounds.height - 10) {
                    this.limitJump = getHeight() - stairBounds.y - stairBounds.height - playerBounds.height;
                    System.out.print(this.limitJump);
                }
            }
            if(!stairsFound) {
            	this.base = 0;
            }

            if (
            		playerBounds.y  <= stairBounds.y 
            		&& 
                    playerBounds.y + playerBounds.height > stairBounds.y 
               ) {
                if (
            		playerBounds.x + playerBounds.width + (moveSize) > stairBounds.x 
                	&& 
                	playerBounds.x + playerBounds.width < stairBounds.x + stairBounds.width
            	) {
                    stopMovingRight = true;
                }
                else if (
            		playerBounds.x - (moveSize) < stairBounds.x + stairBounds.width 
            		&& 
            		playerBounds.x > stairBounds.x
        		) {
                    stopMovingLeft = true;
                }
            }
        }

        if (!stairsFound) base = 0;
        this.stopMovingRight = stopMovingRight;
        this.stopMovingLeft = stopMovingLeft;
    }

    private void keyPressedHandler(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && acelerate == 0) {
        	this.isJumping = true;
        	this.isDownning=false;
        	this.isUpning=true;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            isMovingLeft = true;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            isMovingRight = true;
        }
    }

    private void keyReleasedHandler(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            isMovingLeft = false;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            isMovingRight = false;
        }
    }

    private int positionBottom = 0;
    private int positionLeft = 0;
    private int base = 0;
    private int jump = 120;
    private int limitJump = jump;
    private boolean isJumping = false;
    private boolean isDownning = false;
    private boolean isUpning = false;
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private boolean stopMovingLeft = false;
    private boolean stopMovingRight = false;
    private double acelerate = 0;
    private double gravity = 9.8;
    private int moveSize = 20;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Jogo Similar ao Mario");
        MarioLikeGame game = new MarioLikeGame();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
