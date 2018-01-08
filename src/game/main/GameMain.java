package game.main;

import javax.swing.JFrame;

public class GameMain {
	private static final String GAME_TITLE = "Mandelbrot";
	public static int GAME_WIDTH = 600;
	public static int GAME_HEIGHT = 600;
	public static Game sGame;
	public static void main(String[] args) {
		JFrame frame = new JFrame(GAME_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		sGame = new Game(GAME_WIDTH, GAME_HEIGHT);
		frame.add(sGame);
		frame.pack();
		frame.setVisible(true);
	}
}
