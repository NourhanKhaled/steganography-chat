import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class computes the peak signal to noise ratio between two multiband
 * images
 * 
 * TODO: check the definition of PSNR
 * 
 * @author Abdullah
 */
public class PSNR {


	public static String MSE(BufferedImage im1, BufferedImage im2) {
		assert (im1.getType() == im2.getType() && im1.getHeight() == im2.getHeight()
				&& im1.getWidth() == im2.getWidth());

		double mse = 0;
		int sum = 0;
		int width = im1.getWidth();
		int height = im1.getHeight();
		Raster r1 = im1.getRaster();
		Raster r2 = im2.getRaster();
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				mse += Math.pow(r1.getSample(i, j, 0) - r2.getSample(i, j, 0), 2);
				if (r1.getSample(i, j, 0) == r2.getSample(i, j, 0)) {
					sum++;
				}
			}
		}

		mse /= (double) (width * height);
		float x = (100 * sum) / (width * height);
		double output = 20.0 * Math.log10(255 / Math.sqrt(mse));
		System.out.println("Peak signal-to-noise ratio: " + String.format("%.2f", output));
		return String.format("%.2f", output);
	}
	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty("user.dir"));
		BufferedImage image1 = ImageIO.read(new File("/home/radwa/eclipse-workspace/Security/src/image.jpg"));
		BufferedImage image2 = ImageIO.read(new File("/home/radwa/eclipse-workspace/Security/src/encoded_image.jpg.png"));
		String ratio = PSNR.MSE(image1, image2);
		System.out.println(ratio);
	}
}
