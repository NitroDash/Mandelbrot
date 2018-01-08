package game.main;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Resources {
	
	public static BufferedImage welcome, iconimage;
	
	public static void load() {
		welcome = loadImage("welcome.png");
		iconimage = loadImage("iconimage.png");
	}
	
	private static BufferedImage loadImage(String filename) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(Resources.class.getResourceAsStream("/resources/"+filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return img;
	}
}
