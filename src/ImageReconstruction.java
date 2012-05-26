
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;

import javax.imageio.stream.FileImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ImageReconstruction extends JFrame
{
	JFileChooser fc;
	FileImageInputStream fis;
	byte[] arr;
	int[] sourcePixels;
	int[] pix1;
	int[] pix2;
	int [][] pix2D;
	int width;
	int height;
	Image img;
	BufferedImage bufImage;
	public ImageReconstruction()
	{
		try
		{
			fc = new JFileChooser();
			fc.showOpenDialog(this);
			File file=fc.getSelectedFile();
			fis = new FileImageInputStream(file);
			int len =(int)fis.length();
			arr = new byte[len];	
			fis.read(arr,0,len);
			img = Toolkit.getDefaultToolkit().createImage(arr);
			ImageIcon ic = new ImageIcon(img);
			JLabel label = new JLabel(ic);
			Container c = this.getContentPane();
			c.setLayout(new GridLayout(1,2));
			c.add(label);
			this.setSize(300,450);
			this.setVisible(true);
			width = img.getWidth(null);
			height =img.getHeight(null);
			sourcePixels =new int[width * height];
			PixelGrabber pg=new PixelGrabber(img,0,0,width,height,sourcePixels,0,width);
			pg.grabPixels();
			pix1 = new int[width*height];
			pix2 = new int[width*height];
			
			for(int i=0;i<width*height;i++)
			{
				int a= sourcePixels[i];
				int r=(0xff & (a>>16));
				int g=(0xff & (a>>8));
				int b=(0xff & a);
				int avg=(int)(0.33*r+0.56*g+0.11*b);
				pix1[i]=avg; //(0xff000000|a<<16|a<<8|a);
				pix1[i] = ((0xff000000)| pix1[i]<<16 | pix1[i]<<8 |pix1[i]);
				//pix2[i]= a;
			}
			bufImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			//MemoryImageSource m = new MemoryImageSource(width,height,pix1,0,width);
			//Image img1=Toolkit.getDefaultToolkit().createImage(m);
			convertTo2D();
			
			ic=new ImageIcon(bufImage);
			JLabel l=new JLabel(ic);
			c=this.getContentPane();
			c.add(l);
			this.setSize(300,450);
			this.setVisible(true);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void convertTo2D()
	{
		int count = 0;
		pix2D = new int [width][height];
		
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				pix2D[i][j] = pix1[count];
				++count;
			}
		}
	}
	
	public void convertTo1D()
	{
		int count = 0;
		pix2 = new int [width * height];
		
		for (int i = 0; i < width; i++)
		{
			for (int j = 0; j < height; j++)
			{
				pix2[count] = pix2D[i][j];
				++count;
			}
		}
	}
	
	public static void main(String[] args)
	{
		ImageReconstruction imgRecon = new ImageReconstruction();
		imgRecon.convertTo2D();
		imgRecon.drawCircle(0, 0, 0);
		imgRecon.convertTo1D();
	//	imgRecon.displayCircleImage();
	}
	
	public void drawCircle(int x, int y, int radius)
	{
		radius  = 17;
		x = 18;
		y = 108;
		
		int pixell[][] = new int [width][height];
		
		for (int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				if (((int)Math.sqrt(((Math.pow((i-x), 2)) + (Math.pow((j-y), 2)))) - radius) == 0)
				{
					pix2D[j][i] = 0;
					pixell[i][j] = 255;
					System.out.println(i + "," + j);
				}
				else
				{
					pixell[i][j] = 0;
				}		
			}
		}
		drawCircleOnly(pix2D);
	}
	/*
	public void displayCircleImage()
	{
		for (int i = 0; i < width * height; i++)
		{
			pix2[i]=((0xff000000)| pix2[i]<<16 | pix2[i]<<8 |pix2[i]);
		}
		
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		MemoryImageSource m1=new MemoryImageSource(width ,height ,pix2,0, width);
		Image destImg =Toolkit.getDefaultToolkit().createImage(m1);
		ImageIcon ic =new ImageIcon(destImg);
		JLabel label3=new JLabel(ic);
		JFrame f2=new JFrame();
		Container c3=f2.getContentPane();
		c3.add(label3);
		f2.setTitle("Circle Draw");
		f2.setVisible(true);
		f2.setSize(300,450);
	}*/
	
	private void drawCircleOnly(int pixell[][])
	{
		int count = 0;
		//int pixi[] = new int [width * height];
		
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
		for (int i = 0; i < width; i++)
		{
			for(int j = 0; j < height; j++)
			{
				bufferedImage.setRGB(i,j,pixell[i][j]);  
				//		pixi[count] = pixell[i][j];
			//	bufferedImage.setRGB(i,j,pixi[count]); 
				//++count;
			}
		}
		/*	
		for (int i = 0; i < width * height; i++)
		{
			pixi[i]=((0xff000000)| pixi[i]<<16 | pixi[i]<<8 |pixi[i]);
//		}*/
		//grabPixel()
		//MemoryImageSource m1=new MemoryImageSource(width ,height ,pixi,0, width);
		//Image destImg = Toolkit.getDefaultToolkit().createImage(m1);
		ImageIcon ic =new ImageIcon(bufferedImage);
		JLabel label3=new JLabel(ic);
		JFrame f2=new JFrame();
		Container c3=f2.getContentPane();
		c3.add(label3);
		f2.setTitle("Circle Only Draw");
		f2.setVisible(true);
		f2.setSize(300,450);
	}
}


