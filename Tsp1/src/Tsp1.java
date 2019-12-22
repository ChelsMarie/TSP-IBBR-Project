import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Tsp1 {

	private static final long serialVersionUID = 1L;
	static Graphics g2;
	static Random randGenerator;
	final static double tI = 100.00;
	final static double tF = 0.1;
	static int iters;
	final static double A = 8.00;
	final static double e = 2.7182818284;
	static double coolingFactor = 0.999;



	public static class Point { 

		private int myX;
		private int myY;
		private String myCity;

		static ArrayList<Integer> currentPath = new ArrayList<Integer>();
		static ArrayList<Integer> newPath     = new ArrayList<Integer>();
		static ArrayList<Integer> bestPath    = new ArrayList<Integer>();
		static ArrayList<Point>   info        = new ArrayList<Point>();

		public Point () 
		{
			myX=0;
			myY=0;
			myCity=" ";
		}

		public Point (int x, int y, String city) 
		{
			myX = x;
			myY = y;
			myCity = city;
		}

		public int getX()
		{
			return myX;
		}

		public int getY()
		{
			return myY;
		}

		public String getCity()
		{
			return myCity;
		}

		public void setStuff(int newX, int newY, String newCity)
		{
			myX = newX;
			myY = newY;
		}


		public static void fillInfo () throws FileNotFoundException
		{ 
			File f = new File("/Users/cbovell/tsp/coords.csv");
			Scanner sc = new Scanner(f);

			while(sc.hasNextLine())
			{ 

				String line = sc.nextLine();
				String[] details = line.split(",");
				int xCoord = Integer.parseInt(details[0]);
				int yCoord = Integer.parseInt(details[1]);
				String city = details[2];
				Point cityInfo = new Point(xCoord, yCoord, city);
				info.add(cityInfo);
			} 
		} 

		public static double distance (Point firstPoint, Point secondPoint) 
		{
			double x = (firstPoint.getX() - secondPoint.getX());
			double y = (secondPoint.getY() - firstPoint.getY());

			double distance = 3.09*Math.sqrt((x*x) + (y*y));

			return distance;
		}

		public static double getTotalDistance(ArrayList<Integer> aPath) throws FileNotFoundException
		{
			int len = (info.size() - 2);
			double dist = 0;

			for(int i=0; i<len + 1; i++)
			{
				dist+=distance(info.get(aPath.get(i)), info.get(aPath.get(i+1)));
			}
			return dist;
		}

		public static class ImagePanel extends JPanel
		{ 

			private static final long serialVersionUID = 1L;
			private BufferedImage img;

			public ImagePanel() {  

				try {
					img = ImageIO.read(new File("/Users/cbovell/tsp/map.gif"));
				} catch (IOException ex) {
					System.out.println("Image not found.");
				}
			}
			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent (g);
				g2 = g.create();
				g2.drawImage(img, 0, 0, getWidth(), getHeight(), null);
				int len = (info.size() - 2);
				for(int i=0; i<len + 1; i++)
				{
					g2.setColor(Color.white);
					g2.drawLine(info.get(currentPath.get(i)).getX(),info.get(currentPath.get(i)).getY(),info.get(currentPath.get(i+1)).getX(),info.get(currentPath.get(i+1)).getY());
				}
			}
		}

		public static class NewFrame extends JFrame 
		{
			private static final long serialVersionUID = 1L;

			public NewFrame () throws IOException
			{
				BufferedImage img = ImageIO.read(new File("/Users/cbovell/tsp/map.gif"));

				this.setResizable (true);
				this.setSize (img.getWidth(), img.getHeight());

				ImagePanel panel = new ImagePanel ();
				this.getContentPane().add (panel);
				this.setVisible (true);
			}

		}

		private static void pathTransport (ArrayList<Integer> destPath, ArrayList<Integer> srcPath)
		{

			copyPath(destPath, srcPath);

			int pathSize = (info.size() - 2);
			int srcLoc1  = (int)((Math.random() * pathSize) + 1);
			int srcLocN  = (int)((Math.random() * pathSize) + 1);
			int destLoc1 = (int)((Math.random() * pathSize) + 1);

			if (srcLoc1 > srcLocN)
			{
				int tmp = srcLoc1;
				srcLoc1 = srcLocN;
				srcLocN = tmp;
			}

			int j = destLoc1;

			for( int i = 1; i <= pathSize; i++ )
			{
				destPath.set( i, -1 );
			}

			for (int i = srcLoc1; i <= srcLocN; i++)
			{	
				destPath.set( j, srcPath.get(i));
				j++;

				if (j > pathSize)
				{
					j = 1;
				}
			}

			int destLoc = 1;

			for( int srcLoc = 1; srcLoc <= pathSize; srcLoc++ )
			{
				if (srcLoc < srcLoc1 || srcLoc > srcLocN)
				{
					while (destPath.get(destLoc) != -1)
					{
						destLoc++;
					}

					destPath.set(destLoc, srcPath.get(srcLoc));
				}
			}
		}

		private static void pathReverse ( ArrayList<Integer>destPath, ArrayList<Integer>srcPath )
		{
			copyPath( destPath, srcPath );
			int len = info.size() - 2;

			int loc1 = (int)((Math.random() * len) + 1);
			int locN = (int)((Math.random() * len) + 1);

			if (loc1 > locN)
			{
				int itemp = loc1;
				loc1 = locN;
				locN = itemp;
			}

			int j = locN;

			for (int i = loc1; i <= locN; i++ )
			{
				destPath.set(j, srcPath.get(i));
				j--;
			}
		}




		public static void modifyPath (ArrayList<Integer> destPath, ArrayList<Integer>srcPath)
		{
			double pTransport = 0.10;
			double pReversal  = 0.05;
			double pSwap = 1.0 - (pTransport + pReversal);
			double randChooser = (Math.random());	
			int len = (info.size() - 2);

			if (randChooser < pSwap)
			{
				copyPath(destPath, srcPath);
				int i = (int)((Math.random() * len) + 1);
				int j = (int)((Math.random() * len) + 1);
				destPath.set( i, srcPath.get(j) );
				destPath.set( j,  srcPath.get(i) );
			}

			else if (randChooser < (pSwap + pReversal))
			{
				pathReverse( destPath, srcPath );
			}
			else 
			{
				pathTransport( destPath, srcPath );
				testValidPath( destPath );
			}

		}

		public static void copyPath(ArrayList<Integer> destPath, ArrayList<Integer>srcPath)
		{
			int n = info.size();

			for( int i = 0; i < n; i++) 
			{
				destPath.set( i, srcPath.get(i) );
			}
		}

		public static double getPathDistance (ArrayList<Integer> thisPath)

		{


			int len = (info.size() - 2);
			double dist = 0;

			for(int i=0; i<(len+1); i++)
			{
				dist+=distance(info.get(thisPath.get(i)), info.get(thisPath.get(i+1)));
			}
			return dist;
		}

		public static int testValidPath (ArrayList<Integer> thisPath)
		{
			int len = (info.size() - 2);
			for (int i = 1; i <= len; i++ )
			{
				int n = 0;

				for(int j = 1; j <= len; j++ )
				{
					if (thisPath.get(j) == i)
					{
						n++;
					}
				}

				if (n != 1)
				{
					System.out.print( "Path is wrong");
					return 0 ;
				}
			}

			return 1;
		}

		public static void main (String [] args) throws InterruptedException, IOException 
		{ 
			fillInfo();
			int len = (info.size() - 2);

			final Scanner keyboard = new Scanner (System.in);
			System.out.println("Enter a seed number: ");
			int seed = 0; 
			seed = keyboard.nextInt();
			final int numIterTemp = 400;
			int failCount = 0;

			//Create integer arrays, with values of 0 to N where N = number of cities.

			for (int i = 0; i <= len + 1; i++ )
			{
				currentPath.add(i);
				newPath.add(i);
				bestPath.add(i);
			}

			NewFrame pathFrame = new NewFrame ();

			randGenerator = new Random();
			randGenerator.setSeed(seed);

			double currentDist = getPathDistance(currentPath);
			double bestDist = currentDist;

			for (double tC = tI; tC > tF; tC *= coolingFactor)
			{
				for (int i = 0; i < numIterTemp; i++)
				{
					modifyPath(newPath,currentPath);
					double newDist = getPathDistance(newPath);

					if (newDist < bestDist)
					{		
						bestDist    = newDist;
						currentDist = newDist;
						copyPath(currentPath,newPath);
						copyPath(bestPath,newPath);
						System.out.println("Best distance is: " + bestDist);
						Thread.sleep(150);
						pathFrame.repaint();
					}
					else if (newDist < currentDist)
					{
						currentDist = newDist;
						copyPath(currentPath,newPath);	
					}
					else if (newDist > currentDist)
					{
						double randProbChooser = (Math.random());
						
						if (randProbChooser < Math.pow(e,(-A*(newDist-currentDist)/(tC*len))))
						{
 					     copyPath(currentPath,newPath);
						 currentDist = newDist;
						}
					}
					
				}     // iteration loop
			}         // temperature loop
			
		testValidPath(bestPath);
		

		for( int i = 0; i <= len + 1; i++ )
		{
		 System.out.println( info.get(bestPath.get(i)) );
		}
		
		System.out.println( "\n Final Distance: " + bestDist);

		} 

		private static int random (int iter) {
			return 0;
		}

		@Override
		public String toString() 
		{
			return "  " + myCity + "(" + myX + "," + myY + ")";

		}
	}
}
