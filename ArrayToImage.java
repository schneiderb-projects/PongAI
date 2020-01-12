import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.swing.*;
import javax.swing.border.LineBorder;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ArrayToImage {
	static JFrame f;
	static AutoResize currentImage;
	static JLabel currentLabel;
	int[] colors; 

	boolean isUp = false;
	boolean isDown = false;
	boolean isS = false;
	boolean isL = false;
	boolean gameOver = false;
	boolean removeLabel = false;

	Pong parent;

	ArrayToImage(Pong parent, int maze[][], int[] colors) {
		this.parent = parent;
		f = new JFrame();
		this.colors = colors;
		BufferedImage img = toBufferedImage(maze, colors);
		currentImage = new AutoResize(img);
		f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		f.setFocusable(true);
		f.add(currentImage);
		f.setSize(400,400);
		f.setLocation(400,0);
		f.addKeyListener(new Listener(this));
		f.setVisible(true);
	}

	public void refreshImage(int[][] arr)
	{
		if(removeLabel) {
			f.remove(currentLabel);
			removeLabel = false;
		}
		else {
			f.remove(currentImage);
		}
		BufferedImage img = toBufferedImage(arr,colors);
		currentImage = new AutoResize(img);
		f.add(currentImage);
		f.repaint();
		f.setVisible(true);
	}

	public void refreshChanged(int[][] toChange)
	{
		f.remove(currentImage);
		if(removeLabel) {
			f.remove(currentLabel);
			removeLabel = false;
		}
		currentImage.editImage(toChange,colors);
		f.add(currentImage);
		f.repaint();
		f.setVisible(true);
	}

	private static BufferedImage toBufferedImage(int[][] rawRGB, int[] colors) {
		int h = rawRGB.length;
		int w = rawRGB[0].length;

		BufferedImage image = new BufferedImage(h, w, BufferedImage.TYPE_INT_ARGB);
		for (int x=0; x<h; ++x) {
			for (int y=0; y<w; ++y) {
				int argb = rawRGB[x][y];
				image.setRGB(x, y, colors[argb]);
			}
		}
		return image;
	}

	public static BufferedImage getImageFromArray(int[] pixels, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		return image;
	}

	KeyListener getListener() {
		return f.getKeyListeners()[0];
	}

	void gameOver(String str) {
		f.remove(currentImage);
		removeLabel = true;
		currentLabel = new JLabel(str + "Press space to begin a new game");
		currentLabel.setHorizontalAlignment(JLabel.CENTER);
		currentLabel.setVerticalAlignment(JLabel.CENTER);
		f.add(currentLabel); 
		f.repaint();
		f.setVisible(true);
	}

	
	
	public static class Listener implements KeyListener {
		ArrayToImage parent;

		Listener(ArrayToImage rent) {
			parent = rent;
		}
		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_UP) 
				parent.isUp = true;
			if(e.getKeyCode() == KeyEvent.VK_DOWN) 
				parent.isDown = true;
			if(e.getKeyCode() == KeyEvent.VK_S) 
				parent.isS = true;
			if(e.getKeyCode() == KeyEvent.VK_L) 
				parent.isL = true;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_UP) 
				parent.isUp = false;
			if(e.getKeyCode() == KeyEvent.VK_DOWN) 
				parent.isDown = false;
			if(e.getKeyCode() == KeyEvent.VK_S) 
				parent.isS = false;
			if(e.getKeyCode() == KeyEvent.VK_L) 
				parent.isL = false;
		}
	}
}



class AutoResize extends JPanel {
	private static final long serialVersionUID = 1L;
	BufferedImage image;

	public AutoResize(BufferedImage image) {
		this.image = image;
		setFocusable(true);
		requestFocus();
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		int w = getWidth();
		int h = getHeight();
		int iw = image.getWidth();
		int ih = image.getHeight();
		double xScale = (double)w/iw;
		double yScale = (double)h/ih;
		double scale = Math.min(xScale, yScale);    // scale to fit
		int width = (int)(scale*iw);
		int height = (int)(scale*ih);
		int x = (w - width)/2;
		int y = (h - height)/2;
		g2.drawImage(image, x, y, width, height, this);
	}

	void editImage(int[][] toEdit,int[] colors) {
		for(int[] set: toEdit) 
			image.setRGB(set[0], set[1], colors[set[2]]);
	}
}