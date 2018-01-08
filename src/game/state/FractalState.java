package game.state;

import game.main.GameMain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class FractalState extends State {
	int[][] calculatedPixels;
	double zoomPerPixel=0.005;
	double centerX=-0.6;
	double centerY=0;
	boolean[] keys=new boolean[4];
	boolean zoomingIn,zoomingOut,shift,tab;
	final Color[] repetitionBlockColors={Color.WHITE,Color.RED,Color.YELLOW,Color.ORANGE,Color.GREEN,Color.BLACK};
	@Override
	public void init() {
		calculatedPixels=new int[GameMain.GAME_WIDTH][GameMain.GAME_HEIGHT];
		for (int xPixel=0; xPixel<calculatedPixels.length; xPixel++) {
			for (int yPixel=0; yPixel<calculatedPixels[0].length; yPixel++) {
				double startX=centerX-GameMain.GAME_WIDTH*zoomPerPixel/2+xPixel*zoomPerPixel;
				double startY=centerY+GameMain.GAME_HEIGHT*zoomPerPixel/2-yPixel*zoomPerPixel;
				calculatedPixels[xPixel][yPixel]=findValue(startX, startY);
			}
		}
	}

	@Override
	public void update(float delta) {
		int pixelsMoved=3;
		if (shift) {
			pixelsMoved*=3;
		}
		if (keys[0]&&!keys[1]) {
			centerY+=zoomPerPixel*pixelsMoved;
			for (int x=0; x<calculatedPixels.length; x++) {
				for (int y=calculatedPixels[0].length-1; y>=0; y--) {
					if (y<pixelsMoved) {
						double startX=centerX-GameMain.GAME_WIDTH*zoomPerPixel/2+x*zoomPerPixel;
						double startY=centerY+GameMain.GAME_HEIGHT*zoomPerPixel/2-y*zoomPerPixel;
						calculatedPixels[x][y]=findValue(startX,startY);
					} else {
						calculatedPixels[x][y]=calculatedPixels[x][y-pixelsMoved];
					}
				}
			}
		}
		if (keys[1]&&!keys[0]) {
			centerY-=zoomPerPixel*pixelsMoved;
			for (int x=0; x<calculatedPixels.length; x++) {
				for (int y=0; y<calculatedPixels[0].length; y++) {
					if (y>=calculatedPixels[0].length-pixelsMoved) {
						double startX=centerX-GameMain.GAME_WIDTH*zoomPerPixel/2+x*zoomPerPixel;
						double startY=centerY+GameMain.GAME_HEIGHT*zoomPerPixel/2-y*zoomPerPixel;
						calculatedPixels[x][y]=findValue(startX,startY);
					} else {
						calculatedPixels[x][y]=calculatedPixels[x][y+pixelsMoved];
					}
				}
			}
		}
		if (keys[2]&&!keys[3]) {
			centerX-=zoomPerPixel*pixelsMoved;
			for (int y=0; y<calculatedPixels[0].length; y++) {
				for (int x=calculatedPixels.length-1; x>=0; x--) {
					if (x<pixelsMoved) {
						double startX=centerX-GameMain.GAME_WIDTH*zoomPerPixel/2+x*zoomPerPixel;
						double startY=centerY+GameMain.GAME_HEIGHT*zoomPerPixel/2-y*zoomPerPixel;
						calculatedPixels[x][y]=findValue(startX,startY);
					} else {
						calculatedPixels[x][y]=calculatedPixels[x-pixelsMoved][y];
					}
				}
			}
		}
		if (keys[3]&&!keys[2]) {
			centerX+=zoomPerPixel*pixelsMoved;
			for (int y=0; y<calculatedPixels[0].length; y++) {
				for (int x=0; x<calculatedPixels.length; x++) {
					if (x>=calculatedPixels.length-pixelsMoved) {
						double startX=centerX-GameMain.GAME_WIDTH*zoomPerPixel/2+x*zoomPerPixel;
						double startY=centerY+GameMain.GAME_HEIGHT*zoomPerPixel/2-y*zoomPerPixel;
						calculatedPixels[x][y]=findValue(startX,startY);
					} else {
						calculatedPixels[x][y]=calculatedPixels[x+pixelsMoved][y];
					}
				}
			}
		}
		if (zoomingIn&&!zoomingOut) {
			if (shift) {
				zoomPerPixel/=8.0;
			} else {
				zoomPerPixel/=2.0;
			}
			zoomingIn=false;
			for (int xPixel=0; xPixel<calculatedPixels.length; xPixel++) {
				for (int yPixel=0; yPixel<calculatedPixels[0].length; yPixel++) {
					double startX=centerX-GameMain.GAME_WIDTH*zoomPerPixel/2+xPixel*zoomPerPixel;
					double startY=centerY+GameMain.GAME_HEIGHT*zoomPerPixel/2-yPixel*zoomPerPixel;
					calculatedPixels[xPixel][yPixel]=findValue(startX, startY);
				}
			}
		}
		if (zoomingOut&&!zoomingIn) {
			if (shift) {
				zoomPerPixel*=8.0;
			} else {
				zoomPerPixel*=2.0;
			}
			zoomingOut=false;
			for (int xPixel=0; xPixel<calculatedPixels.length; xPixel++) {
				for (int yPixel=0; yPixel<calculatedPixels[0].length; yPixel++) {
					double startX=centerX-GameMain.GAME_WIDTH*zoomPerPixel/2+xPixel*zoomPerPixel;
					double startY=centerY+GameMain.GAME_HEIGHT*zoomPerPixel/2-yPixel*zoomPerPixel;
					calculatedPixels[xPixel][yPixel]=findValue(startX, startY);
				}
			}
		}
	}

	@Override
	public void render(Graphics g) {
		if (calculatedPixels.length!=GameMain.GAME_WIDTH||calculatedPixels[0].length!=GameMain.GAME_HEIGHT) {
			init();
		}
		int[] numberFrequencies = new int[repetitionBlockColors.length+1];
		for (int x=0; x<GameMain.GAME_WIDTH; x++) {
			for (int y=0; y<GameMain.GAME_HEIGHT; y++) {
				numberFrequencies[calculatedPixels[x][y]+1]++;
			}
		}
		int biggestValue=0;
		for (int i = 0; i < numberFrequencies.length; i++) {
			if (numberFrequencies[i]>numberFrequencies[biggestValue]) {
				biggestValue=i;
			}
		}
		biggestValue--;
		if (biggestValue<0) {
			g.setColor(Color.BLACK);
		} else {
			g.setColor(repetitionBlockColors[biggestValue]);
		}
		g.fillRect(0,0,GameMain.GAME_WIDTH,GameMain.GAME_HEIGHT);
		recursiveRender(0,0,GameMain.GAME_WIDTH,GameMain.GAME_HEIGHT,biggestValue,g);
		if (tab) {
			g.setColor(Color.LIGHT_GRAY);
			g.drawLine(0, GameMain.GAME_HEIGHT/2, GameMain.GAME_WIDTH, GameMain.GAME_HEIGHT/2);
			g.drawLine(GameMain.GAME_WIDTH/2,0,GameMain.GAME_WIDTH/2,GameMain.GAME_HEIGHT);
		}
		g.setColor(Color.LIGHT_GRAY);
		g.drawString("Distance across screen: "+zoomPerPixel*GameMain.GAME_WIDTH, 10, 20);
	}

	@Override
	public void onClick(MouseEvent e) {
		
	}

	@Override
	public void onKeyPress(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			keys[0]=true;
			break;
		case KeyEvent.VK_DOWN:
			keys[1]=true;
			break;
		case KeyEvent.VK_LEFT:
			keys[2]=true;
			break;
		case KeyEvent.VK_RIGHT:
			keys[3]=true;
			break;
		case KeyEvent.VK_A:
			zoomingIn=true;
			break;
		case KeyEvent.VK_Z:
			zoomingOut=true;
			break;
		case KeyEvent.VK_SHIFT:
			shift=true;
			break;
		case KeyEvent.VK_Q:
			tab=true;
			break;
		}
	}

	@Override
	public void onKeyRelease(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_UP:
			keys[0]=false;
			break;
		case KeyEvent.VK_DOWN:
			keys[1]=false;
			break;
		case KeyEvent.VK_LEFT:
			keys[2]=false;
			break;
		case KeyEvent.VK_RIGHT:
			keys[3]=false;
			break;
		case KeyEvent.VK_SHIFT:
			shift=false;
			break;
		case KeyEvent.VK_Q:
			tab=false;
			break;
		}
	}
	
	int findValue(double startX, double startY) {
		double x=startX;
		double y=startY;
		int result=1000;
		for (int i = 1; i < 200; i++) {
			double nextX=x*x-y*y+startX;
			double nextY=2*x*y+startY;
			x=nextX;
			y=nextY;
			if (x*x+y*y>=4.0) {
				result=i;
				break;
			}
		}
		for (int i=0; i<repetitionBlockColors.length; i++) {
			if (result<=(i+1)*40) {
				return i;
			}
		}
		return -1;
	}
	
	private void recursiveRender(int x, int y, int width,int height,int dontDraw,Graphics g) {
		int benchmarkColor=calculatedPixels[x][y];
		if (benchmarkColor==dontDraw) {
			if (width>1) {
				recursiveRender(x,y,width/2,height/2,dontDraw,g);
				recursiveRender(x+width/2,y+height/2,width-width/2,height-height/2,dontDraw,g);
				recursiveRender(x+width/2,y,width-width/2,height/2,dontDraw,g);
				recursiveRender(x,y+height/2,width/2,height-height/2,dontDraw,g);
			}
		} else {
			for (int testX=x; testX<width+x; testX++) {
				for (int testY=y; testY<height+y; testY++) {
					if (calculatedPixels[testX][testY]!=benchmarkColor) {
						recursiveRender(x,y,width/2,height/2,dontDraw,g);
						recursiveRender(x+width/2,y+height/2,width-width/2,height-height/2,dontDraw,g);
						recursiveRender(x+width/2,y,width-width/2,height/2,dontDraw,g);
						recursiveRender(x,y+height/2,width/2,height-height/2,dontDraw,g);
						return;
					}
				}
			}
			if (benchmarkColor==-1) {
				g.setColor(Color.BLACK);
			} else {
				g.setColor(repetitionBlockColors[benchmarkColor]);
			}
			g.fillRect(x,y,width,height);
		}
	}
}
