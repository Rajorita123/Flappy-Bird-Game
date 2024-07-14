import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
  int boardWidth = 360;
  int boardHeight = 640;

  Image backgroundImg;
  Image birdImg;
  Image topPipeImg;
  Image bottomPipeImg;

  int birdX = boardWidth/8;
  int birdY = boardHeight/2;
  int birdWidth = 34;
  int birdHeight = 24;

  class bird{
    int x = birdX;
    int y = birdY;
    int width = birdWidth;
    int height = birdHeight;
    Image img;

    bird(Image img){
      this.img = img;
    }
  }
   
  // pipes
  int pipeX = boardWidth;
  int pipeY = 0;
  int pipeWidth = 64;
  int pipeHeight = 512;

  class pipes{
    int x = pipeX;
    int y = pipeY;
    int width = pipeWidth;
    int height = pipeHeight;
    Image img;
    boolean passed = false;

    pipes(Image img){
      this.img = img;
    }
  }

  // game logic
  bird Bird;
  int velocityX = -4;
  int velocityY = 0;
  int gravity = 1;

  ArrayList<pipes> pipe;
  Random random = new Random();

  Timer gameLoop;
  Timer placePipesTimer;

  boolean gameOver = false; 
  double score = 0; 

  FlappyBird(){
    setPreferredSize(new Dimension(boardWidth, boardHeight));
    // setBackground(Color.blue);
    setFocusable(true);
    addKeyListener(this);

    // load images
    backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
    birdImg = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
    topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
    bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

    // bird
    Bird = new bird(birdImg);
    pipe = new ArrayList<pipes>();

    // place pipes timer
    placePipesTimer = new Timer(1500, new ActionListener(){
      @Override
      public void actionPerformed(ActionEvent e){
        placePipes();
      }
    }); 

    placePipesTimer.start();

    // gameloop
    gameLoop = new Timer(1000/60, this);
    gameLoop.start();
  } 

  public void placePipes(){
    int randomPipeY = (int)(pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
    int openingSpace  = boardHeight/4;

    pipes topPipe = new pipes(topPipeImg);
    topPipe.y = randomPipeY;
    pipe.add(topPipe);

    pipes bottomPipe = new pipes(bottomPipeImg);
    bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
    pipe.add(bottomPipe);
  }

  public void paintComponent(Graphics g){
    super.paintComponent(g);
    draw(g);
  }

  public void move(){
    // bird
    velocityY += gravity;
    Bird.y += velocityY;
    Bird.y = Math.max(Bird.y, 0);

    // pipes
    for(int i = 0; i<pipe.size(); i++){
      pipes p = pipe.get(i);
      p.x += velocityX;
 
      if(!p.passed && Bird.x > p.x + p.width){
        p.passed = true;
        score += 0.5;
      }

      if(collision(Bird, p)){
        gameOver = true;    
      }
    }
   
    if(Bird.y > boardHeight){
      gameOver = true;
    }
  }

  public void draw(Graphics g){
    // background
    g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);

    // bird
    g.drawImage(birdImg, Bird.x, Bird.y, Bird.width, Bird.height, null);

    // pipes
    for(int i = 0; i<pipe.size(); i++){
      pipes p = pipe.get(i);
      g.drawImage(p.img, p.x, p.y, p.width, p.height, null);
    }

    // score
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.PLAIN, 32));
    if(gameOver){
      g.drawString("Game Over: "+ String.valueOf((int) score), 10, 35);
    }
    else{
      g.drawString(String.valueOf((int) score), 10, 35);
    }
  }

  public boolean collision(bird a, pipes b){
    return a.x < b.x + b.width &&
           a.x + a.width > b.x &&
           a.y < b.y + b.height &&
           a.y + a.height > b.y;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    move(); 
    repaint();
    if(gameOver){
      placePipesTimer.stop();
      gameLoop.stop();
    }
  }


  @Override
  public void keyPressed(KeyEvent e) {
    if(e.getKeyCode() == KeyEvent.VK_SPACE){
      velocityY = -9;

      if(gameOver){
        Bird.y = birdY;
        velocityY = 0;
        pipe.clear();
        score = 0;
        gameOver = false;
        gameLoop.start();
        placePipesTimer.start();
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }
  
  @Override
  public void keyReleased(KeyEvent e) {
  }
}