import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Tsp2 {

	static Graphics2D g2;
	static long seed; 
	static Random randGenerator;
	final static double tI = 100.00;
	final static double tF = 0.1;
	static int iters;
	final static double A = 11000.00;
	final static double e = 2.7182818284;
	static double coolingFactor = 0.99;
	final static int pxXLeft = 10;
	final static int pxXRight = 830;
	final static int pxYTop = 10;
	final static int pxYBottom = 640;
	final static double specXLeft = -0.719;
	final static double specXRight = 2.592;
	final static double specYTop = 10.843;
	final static double specYBottom = 35.415;
	final static double xSF = 0.5;
	final static double ySF = 0.1;
	final static double hSF = 0.0001;
	static int vals;
	final static double distPenalty = 0.0111111111111;

	public static class Point { 

		private int myID;
		private double myXppm;
		private double myYppm;
		private float myHeight;

		static ArrayList<Integer> currentPath = new ArrayList<Integer>();
		static ArrayList<Integer> newPath     = new ArrayList<Integer>();
		static ArrayList<Integer> bestPath    = new ArrayList<Integer>();
		static ArrayList<Point>   tableACoords  = new ArrayList<Point>();
		static ArrayList<Point>   tableBCoords = new ArrayList<Point>();

		public Point () 
		{		
			myID=0;
			myXppm=0.000;
			myYppm=0.000;
			myHeight = +0;
		}

		public Point (int id, double Xppm, double Yppm, float height) 
		{
			myID = id;
			myXppm = Xppm;
			myYppm = Yppm;
			myHeight = height;
		}

		public int getID()
		{
			return myID;
		}

		public double getXppm()
		{
			return myXppm;
		}

		public double getYppm()
		{
			return myYppm;
		}

		public float getHeight()
		{
			return myHeight;
		}

		public static void fillArrays () throws FileNotFoundException
		{ 
			File f = new File("/Users/cbovell/tsp/allTables/txt/table001.txt");
			File f2 = new File("/Users/cbovell/tsp/allTables/txt/table007.txt");
			Scanner sc = new Scanner(f);
			Scanner sc2 = new Scanner(f2);

			while(sc.hasNextLine() && sc2.hasNextLine())
			{ 

				String line = sc.nextLine();
				String line2 = sc2.nextLine();
				String[] details = line.split("\\s+");
				String[] details2 = line2.split("\\s+");

				int ID = Integer.parseInt(details[1]);
				double XPPM = Double.parseDouble(details[2]);
				double YPPM = Double.parseDouble(details[3]);
				float HEIGHT = Float.parseFloat(details[4]);

				int ID2 = Integer.parseInt(details2[1]);
				double XPPM2 = Double.parseDouble(details2[2]);
				double YPPM2 = Double.parseDouble(details2[3]);
				float HEIGHT2 = Float.parseFloat(details[4]);

				Point tableA = new Point(ID,XPPM,YPPM,HEIGHT);
				Point tableB = new Point(ID2,XPPM2,YPPM2,HEIGHT2);
				tableACoords.add(tableA);
				tableBCoords.add(tableB);
			} 
		} 

		public static int convertXPPMtoPixel(double XPPM)
		{
			int xPx = pxXLeft + (int)((pxXRight - pxXLeft)*(specXLeft - XPPM)/(specXLeft - specXRight));				
			return xPx;
		}
		public static int convertYPPMtoPixel (double YPPM)
		{
			int yPx = pxYTop + (int)((pxYBottom - pxYTop)*(specYTop - YPPM)/(specYTop - specYBottom));
			return yPx;
		}

		public static double distance (Point firstPoint, Point secondPoint) 
		{
			double x = xSF*(secondPoint.getXppm() - firstPoint.getXppm());
			double y = ySF*(secondPoint.getYppm() - firstPoint.getYppm());
			double z = hSF*(secondPoint.getHeight() - firstPoint.getHeight());

			double distance = Math.sqrt(((x*x)) + ((y*y)) + ((z*z)));
			return distance;
		}

		public static double getPathDistance (ArrayList<Integer> thisPath)

		{
			int len = vals;
			double dist = 0;

			for(int i=0; i<len; i++)
			{
				int j = thisPath.get(i);

				if (tableACoords.get(i).getID()==0 || tableBCoords.get(j).getID()==0)
				{
					dist += distPenalty;
				}
				else
				{
					dist += distance(tableACoords.get(i), tableBCoords.get(j));
				}
			}
			return dist;
		}

		public static void drawCenteredCircle(int x, int y, int r) {
			x = x-(r/2);
			y = y-(r/2);
			g2.drawOval(x,y,r,r);
		}

		public static class ImagePanel extends JPanel
		{ 

			private static final long serialVersionUID = 1L;
			private BufferedImage img;

			public ImagePanel() {  

				try {
					img = ImageIO.read(new File("/Users/cbovell/tsp/allTables/gif/001_007.gif"));
				} catch (IOException ex) {
					System.out.println("Image not found.");
				}
			}
			@Override
			public void paintComponent(Graphics g)
			{
				super.paintComponent (g);
				g2 = (Graphics2D) g.create();
				g2.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
				int len = vals;

				int pxXA = 0;
				int pxYA = 0;
				int pxXB = 0;
				int pxYB = 0;

				for (int i = 0; i<len; i++)
				{
					int j = bestPath.get(i);

					pxXA = convertXPPMtoPixel(tableACoords.get(i).getXppm());
					pxYA = convertYPPMtoPixel(tableACoords.get(i).getYppm());

					pxXB = convertXPPMtoPixel(tableBCoords.get(j).getXppm());
					pxYB = convertYPPMtoPixel(tableBCoords.get(j).getYppm());

					if  (tableACoords.get(i).getID() > 0 && tableBCoords.get(j).getID() > 0)
					{
						g2.setColor(Color.green);
						g2.setStroke(new BasicStroke(1));
						g2.drawLine(pxXA,pxYA,pxXB,pxYB);
					}
					else if (tableACoords.get(i).getID() > 0) 
					{
						Color transparentRed = new Color(255,69,0);
						g2.setColor(transparentRed);
						drawCenteredCircle(pxXA,pxYA,20);
					}
					else if (tableBCoords.get(j).getID() > 0)
					{
						Color transparentYellow = new Color(255,255,102);
						g2.setColor(transparentYellow);
						drawCenteredCircle(pxXB,pxYB,20);

					}
				}
			}
		}


		public static class NewFrame extends JFrame 
		{
			private static final long serialVersionUID = 1L;

			public NewFrame () throws IOException
			{
				BufferedImage img = ImageIO.read(new File("/Users/cbovell/tsp/allTables/gif/001_007.gif"));

				this.setResizable (true);
				this.setSize (img.getWidth(), img.getHeight());

				ImagePanel panel = new ImagePanel ();
				this.getContentPane().add (panel);
				this.setVisible (true);
			}

		}
		//path exchange addition
				//swap a group of elements 
		private static void pathTransport (ArrayList<Integer> destPath, ArrayList<Integer> srcPath)
		{

			copyPath(destPath, srcPath);

			int pathSize = vals;
			int srcLoc1  = (int)((randGenerator.nextDouble() * (int)(pathSize)));
			int srcLocN  = (int)((randGenerator.nextDouble() * (int)(pathSize)));
			int destLoc1 = (int)((randGenerator.nextDouble() * (int)(pathSize)));

			if (srcLoc1 > srcLocN)
			{
				int tmp = srcLoc1;
				srcLoc1 = srcLocN;
				srcLocN = tmp;
			}

			int j = destLoc1;

			for( int i = 0; i < vals; i++ )
			{
				destPath.set( i, -1 );
			}

			for (int i = srcLoc1; i <= srcLocN; i++)
			{	
				destPath.set(j, srcPath.get(i));
				j++;

				if (j > vals-1)
				{
					j = 0;
				}
			}

			int destLoc = 0;

			for( int srcLoc = 0; srcLoc < vals; srcLoc++ )
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
			int len = vals;

			int loc1 = (int)((randGenerator.nextDouble() * (int)(len)));
			int locN = (int)((randGenerator.nextDouble() * (int)(len)));

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
			double pTransport = 0.05;
			double pReversal  = 0.05;
			double pSwap = 1.0 - (pTransport + pReversal);
			double randChooser = (randGenerator.nextDouble());	
			int len = vals;

			if (randChooser < pSwap)
			{
				copyPath(destPath, srcPath);
				int i = (int)((randGenerator.nextDouble() * (int)(len)));
				int j = (int)((randGenerator.nextDouble() * (int)(len)));


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
			}

		}

		public static void copyPath(ArrayList<Integer> destPath, ArrayList<Integer>srcPath)
		{
			int n = vals;

			for( int i = 0; i < n; i++) 
			{
				destPath.set(i, srcPath.get(i));
			}
		}



		public static int testValidPath (ArrayList<Integer> thisPath)
		{
			int len = (vals);
			for (int i = 0; i < len; i++ )
			{
				int n = 0;

				for(int j = 0; j < len; j++ )
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
		

		public static void findLargestAddPeaks (ArrayList<Point> tableA, ArrayList<Point> tableB, int peaksA, int peaksB)
		{

			Point zeroEntry = new Point(0,0,0,0);
			for (int i = 0; i<peaksA; i++)
			{
				tableACoords.add(zeroEntry);
			}	
			for (int x = 0; x<peaksB; x++)
			{
				tableBCoords.add(zeroEntry);
			}

			int firstSize = tableA.size();
			int secondSize = tableB.size();

			if (firstSize > secondSize)
			{
				vals = secondSize;
			}

			else {
				vals = firstSize;
			}
			System.out.println("Table A size: " + firstSize + " " + "Table B size: " + secondSize);
		}

		public static void main(String[] args) throws InterruptedException, IOException  {

			fillArrays();
			findLargestAddPeaks(tableACoords,tableBCoords, 30, 30);
			int len = (vals);

			seed = 993467197;
			//400 is the original
			final int numIterTemp = 400;

			//Create integer arrays, with values of 0 to N where N = number of cities.

			for (int i = 0; i < len; i++)
			{
				currentPath.add(i);
				newPath.add(i);
				bestPath.add(i);
			}
			
			NewFrame pathFrame = new NewFrame ();
			double currentDist = getPathDistance(currentPath);
			double bestDist = currentDist;

			int nAccept = 0;
			int nIter   = 0;
			
				randGenerator = new Random(seed);
				randGenerator.setSeed(seed);
				
				for (double tC = tI; tC > tF; tC *= coolingFactor)
				{
					for (int i = 0; i < numIterTemp; i++)
					{
						nIter++;
						modifyPath(newPath,currentPath);
						double newDist = getPathDistance(newPath);

						if (newDist < bestDist)
						{		
							bestDist    = newDist;
							currentDist = newDist;
							copyPath(currentPath,newPath);
							copyPath(bestPath,newPath);
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
							double randProbChooser = (randGenerator.nextDouble());

							if (randProbChooser < Math.pow(e,(-A*(newDist-currentDist)/(tC*len))))
							{
								copyPath(currentPath,newPath);
								currentDist = newDist;
								nAccept++;
							}

						}

					}     // iteration loop
				}         // temperature loop

				testValidPath(bestPath);
				System.out.println("Best distance: " + bestDist);
			
		} 

		

		@Override
		public String toString() 
		{
			return "  " + myID + " (" + myXppm + "," + myYppm + ")";

		}
	}
}
