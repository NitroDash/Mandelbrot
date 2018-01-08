package game.main;

import framework.util.InputHandler;
import game.state.LoadState;
import game.state.State;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Game extends JPanel implements Runnable {
	private int gameWidth;
	private int gameHeight;
	private Image gameImage;
	
	private Thread gameThread;
	private volatile boolean running;
	private volatile State currentState;
	
	private InputHandler inputHandler;
	
	public Game(int gameWidth, int gameHeight) {
		this.gameWidth=gameWidth;
		this.gameHeight=gameHeight;
		setPreferredSize(new Dimension(gameWidth, gameHeight));
		setBackground(Color.BLACK);
		setFocusable(true);
		requestFocus();
	}
	
	public void setCurrentState(State newState) {
		System.gc();
		newState.init();
		currentState = newState;
		inputHandler.setCurrentState(currentState);
	}
	
	@Override
	public void addNotify() {
		super.addNotify();
		initInput();
		setCurrentState(new LoadState());
		initGame();
	}
	
	private void initGame() {
		running = true;
		gameThread = new Thread(this, "Game Thread");
		gameThread.start();
	}
	
	private void initInput() {
		inputHandler = new InputHandler();
		addKeyListener(inputHandler);
		addMouseListener(inputHandler);
	}

	@Override
	public void run() {
		long updateDurationMillis = 0;
		long sleepDurationMillis = 0;
		while (running) {
			long beforeUpdateRender = System.nanoTime();
			long deltaMillis = updateDurationMillis+sleepDurationMillis;
			updateAndRender(deltaMillis);
			updateDurationMillis = (System.nanoTime()-beforeUpdateRender)/1000000L;
			sleepDurationMillis = Math.max(1, 17-updateDurationMillis);
			try {
				Thread.sleep(sleepDurationMillis);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.exit(0);
	}

	private void updateAndRender(long deltaMillis) {
		currentState.update(deltaMillis/1000f);
		prepareGameImage();
		currentState.render(gameImage.getGraphics());
		renderGameImage(getGraphics());
	}
	
	private void prepareGameImage() {
		if (gameImage == null) {
			gameImage = createImage(gameWidth, gameHeight);
		}
		if (gameWidth!=this.getParent().getWidth()||gameHeight!=this.getParent().getHeight()) {
			gameWidth=this.getParent().getWidth();
			gameHeight=this.getParent().getHeight();
			GameMain.GAME_WIDTH=gameWidth;
			GameMain.GAME_HEIGHT=gameHeight;
			gameImage=createImage(gameWidth,gameHeight);
		}
		Graphics g = gameImage.getGraphics();
		g.clearRect(0, 0, gameWidth, gameHeight);
	}
	
	public void exit() {
		running = false;
	}
	
	private void renderGameImage(Graphics g) {
		if (gameImage != null) {
			g.drawImage(gameImage, 0, 0, null);
		}
		g.dispose();
	}
}
