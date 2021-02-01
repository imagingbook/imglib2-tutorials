
import java.awt.Color;
import java.io.File;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.process.ImageProcessor;
import io.scif.img.IO;
import net.imglib2.algorithm.componenttree.mser.Mser;
import net.imglib2.algorithm.componenttree.mser.MserTree;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.imageplus.ByteImagePlus;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * Taken from imglib2-tutorials
 */
public class WB_MSER_1 {

	public <T extends NumericType<T> & NativeType<T>> WB_MSER_1() {
		// define the file to open
//		File file = new File( "DrosophilaWing.tif" );
//		File file = new File("D://images/boats-tiny.png");
		File file = new File("D://images/boats.png");

		// open a file with ImageJ
		final ImagePlus imp = new Opener().openImage(file.getAbsolutePath());
		ByteImagePlus<UnsignedByteType> byteImagePlus = new ByteImagePlus<UnsignedByteType>(imp);

		// display it via ImageJ
		//imp.show();

		// wrap it into an ImgLib image (no copying)
		//final Img<T> image = ImagePlusAdapter.wrap(imp);
		// display it via ImgLib using ImageJ
		//ImageJFunctions.show(image);

		Img<UnsignedByteType> img = IO.openImgs(file.getAbsolutePath(), new UnsignedByteType()).get(0);
		ImageJFunctions.show(img);
		
		int width = (int) img.dimension(0);
		int height = (int) img.dimension(1);
		int size = width * height;
		IJ.log("size = " + size);
		
		int delta = 15;
		int minSize = (int) (0.001 * size);
		int maxSize = (int) (0.25 * size);
		double maxVariation = 3;
		double minDiversity = 0.4;
		boolean darkToBright= true;

		// --------------------------------------------------------------------------------------------------------
		MserTree<UnsignedByteType> newtree = 
				MserTree.buildMserTree(img, delta, minSize, maxSize, maxVariation, minDiversity, darkToBright);
		// --------------------------------------------------------------------------------------------------------
		IJ.log("done");
		
		ImagePlus imPlus = ImageJFunctions.wrapUnsignedByte(img, "title");
        ImageProcessor ip = imPlus.getProcessor().convertToColorProcessor();
        
        //IJ.log("ip = " + ip);
		
        
		int k = 0;
		for (Mser<UnsignedByteType> mser :  newtree) {
			{
				double[] mean = mser.mean();
				int x = (int) Math.rint(mean[0]);
				int y = (int) Math.rint(mean[1]);
				int rad = (int) (0.25 * Math.sqrt(mser.size()));
				ip.setColor(Color.red);
				ip.drawOval(x - rad, y - rad, 2*rad, 2*rad);
				//IJ.log(k + ": " + Arrays.toString(mean));
				IJ.log(String.format("%d: value=%d size=%d score=%.2f childs=%d", 
						k, mser.value().getInteger(), mser.size(), mser.score(), mser.getChildren().size()));
			}
			
//			ip.setColor(Color.green);
//			for (Mser<UnsignedByteType> child : mser.getChildren()) {
//				double[] mean = mser.mean();
//				int x = (int) Math.rint(mean[0]);
//				int y = (int) Math.rint(mean[1]);
//				int rad = (int) (0.25 * Math.sqrt(mser.size()));
//				
//				ip.drawOval(x - rad, y - rad, 2*rad, 2*rad);
//				IJ.log(String.format("     %d: value=%d size=%d score=%.2f childs=%d", 
//						k, child.value().getInteger(), child.size(), child.score(), child.getChildren().size()));
//			}
			k++;
		}
		
		new ImagePlus("MSER", ip).show();
		
		
//		Img<UnsignedByteType> img2 = img.factory().create(img);
//        ImageJFunctions.show(img2);

        //ImagePlus imPlus = ImageJFunctions.showUnsignedByte(img);
     
        //System.exit(0);
	}

	public static void main(String[] args) {
		// open an ImageJ window
		new ImageJ();

		// run the example
		new WB_MSER_1();
	}
}
