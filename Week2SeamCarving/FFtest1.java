package Week2SeamCarving;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.Arrays;

/**
 * Created by feliciafay on 3/30/16.
 */
public class FFtest1 {
    public static void main(String[] args) {
        if (args.length != 1) {
            StdOut.println("Usage:\njava FFtest1 [image filename] ");
            return;
        }

        Picture inputImg = new Picture(args[0]);

        StdOut.printf("image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
        SeamCarver sc = new SeamCarver(inputImg);

        int[] horizontalSeam = sc.findHorizontalSeam();
//        int[] horizontalSeam = new int [] { 2, 2, 1, 2, 1, 2};
        System.out.println("horizontal optimal seam: " + Arrays.toString(horizontalSeam));
        sc.removeHorizontalSeam(horizontalSeam);

//        int[] verticalSeam = sc.findVerticalSeam();
//        //int[] verticalSeam = new int []{ -1, 0, 0, 0, 0, 1, 0, 1, 2, 2};
//        System.out.println("vertical optimal seam: " + Arrays.toString(verticalSeam));
//        sc.removeVerticalSeam(verticalSeam);

        Picture outputImg = sc.picture();
        StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());
        inputImg.show();
        outputImg.show();
    }

}
