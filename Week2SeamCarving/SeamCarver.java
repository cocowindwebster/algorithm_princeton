package Week2SeamCarving;

import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

/**
 * Created by feliciafay on 3/27/16.
 * */
public class SeamCarver {
    // create a seam carver object based on the given picture
    private Picture picture;
    private int currentWidth;
    private int currentHeight;
    private double [][]energyArray;
    private boolean isTransposed;

    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new java.lang.NullPointerException();
        }
        this.picture = new Picture(picture);
        this.currentWidth = picture.width();
        this.currentHeight = picture.height();
        this.energyArray = new double [Math.max(currentHeight, currentWidth)][Math.max(currentHeight, currentWidth)];
        this.isTransposed = false;
    }

    // current picture
    public Picture picture() {
        return new Picture(this.picture);
    }


    // width of current picture
    public int width() {
        return this.currentWidth;
    }

    // height of current picture
    public int height() {
        return this.currentHeight;
    }

    private int energyHelper(Color c1, Color c2) {
        return (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed()) +
                (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen()) +
                (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue());
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x >= currentWidth || y < 0 || y >= currentHeight) {
            //System.out.println("Index Out, " + x + ">= " + currentWidth + ", " + y + ">=" + currentHeight);
            throw new java.lang.IndexOutOfBoundsException();
        }
        if (x == 0 || x == currentWidth - 1 || y == 0 || y == currentHeight- 1) {
            return 1000;
        }
        Color colorX1 = picture.get(x - 1, y);
        Color colorX2 = picture.get(x + 1, y);
        Color colorY1 = picture.get(x, y - 1);
        Color colorY2 = picture.get(x, y + 1);
        return Math.sqrt((double) energyHelper(colorX1, colorX2) + energyHelper(colorY1, colorY2));
    }

    private void transpose() {
        //System.out.println("1-1transposed:" + this.currentWidth + ", " + this.currentHeight);
        Picture transPic = new Picture(currentHeight, currentWidth);
        for (int i = 0; i < currentHeight; ++i) {
            for (int j = 0; j < currentWidth; ++j) {
                transPic.set(i, j, this.picture.get(j, i));
            }
        }
        int temp = this.currentHeight;
        this.currentHeight = this.currentWidth;
        this.currentWidth = temp;
        this.picture = transPic;
        if (this.isTransposed) {
            this.isTransposed = false;
        } else {
            this.isTransposed = true;
        }
        //this.isTransposed = this.isTransposed ? false : true;
    }

    private void initEnergyArray() {
        for (int i = 0; i < this.currentHeight; ++i) {
            for (int j = 0; j < this.currentWidth; ++j) {
                energyArray[i][j] = energy(j, i);
            }
        }
    }

    // sequence of indices for vertical seam
    private int [] findVerticalSeamHelper() {
        int [] result = new int [currentHeight];
        if (currentWidth == 1) { //corner case : 1×8.png
            return result;
        }
        int [][] precursor = new int [currentHeight][currentWidth];
        double [][] dp = new double[currentHeight][currentWidth];
        for (int i = 0; i < currentWidth; ++i) {
            precursor[0][i] = -1;
            dp[0][i] = energyArray[0][i];
        }

        for (int i = 1; i < currentHeight; ++i) {
            for (int j = 0; j < currentWidth; ++j) {
                if (j == 0)  {
                    if (dp[i - 1][j] < dp[i - 1][j + 1]) {
                        dp[i][j] = energyArray[i][j] + dp[i - 1][j];
                        precursor[i][j] = j;
                    } else {
                        dp[i][j] = energyArray[i][j] + dp[i - 1][j + 1];
                        precursor[i][j] = j + 1;
                    }
                } else if (j == currentWidth - 1) {
                    if (dp[i - 1][j] < dp[i - 1][j - 1]) {
                        dp[i][j] = energyArray[i][j] + dp[i - 1][j];
                        precursor[i][j] =  j;
                    } else {
                        dp[i][j] = energyArray[i][j] + dp[i - 1][j - 1];
                        precursor[i][j] = j - 1;
                    }
                } else {
                    if (dp[i - 1][j - 1] < dp[i - 1][j]) {
                        dp[i][j] = energyArray[i][j] + dp[i - 1][j - 1];
                        precursor[i][j] = j - 1;
                    } else {
                        dp[i][j] = energyArray[i][j] + dp[i - 1][j];
                        precursor[i][j] = j;
                    }
                    if (dp[i - 1][j + 1] + energyArray[i][j] < dp[i][j]) {
                        dp[i][j] = energyArray[i][j] + dp[i - 1][j + 1];
                        precursor[i][j] = j + 1;
                    }
                }
            }
        }

        // find min path
        double  minRes = dp[currentHeight - 1][0];
        int lastPos = 0, index = currentHeight - 1;
        for (int j = 0; j < currentWidth; ++j) {
            if (minRes > dp[currentHeight - 1][j]) {
                minRes = dp[currentHeight - 1][j];
                lastPos = j;
            }
        }
        while (lastPos != -1) {
            result[index] = lastPos;
            lastPos = precursor[index--][lastPos];
        }
        //System.out.println("A1 - findVerticalSeam():" + Arrays.toString(result));
        return result;
    }

    public int[] findVerticalSeam() {
        if (isTransposed) {
            transpose();
        }
        initEnergyArray();
        return findVerticalSeamHelper();
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        //System.out.println("transposed:" + this.currentWidth+ ", " + this.currentHeight);
        if (!isTransposed) {
            transpose();
        }
        //System.out.println("transposed:" + this.currentWidth+ ", " + this.currentHeight);
        initEnergyArray();
        int [] res = findVerticalSeamHelper();
        if (isTransposed) {
            transpose();
        }
        return res;
    }

    private void removeVerticalSeamHelper(int []seam) {
        //remove pixel from image
        for (int row = 0; row < seam.length; ++row) {
            for (int col = seam[row]; col < currentWidth - 1; ++col) {
                picture.set(col, row, picture.get(col + 1, row));
            }
        }
        //update energy array, based on new image, new width.
        for (int row = 1; row < seam.length - 1; ++row) {
            for (int col = Math.max(0, seam[row] - 1); col <= Math.min(seam[row] + 1, currentWidth - 1); ++col) {
                energyArray[row][col] = energy(col, row);
            }
            for (int col = seam[row] + 2; col < currentWidth - 1; ++col) {
                energyArray[row][col] = energyArray[row][col + 1];
            }
            //todo: test system.arrayCopy()
//            System.arraycopy(energyArray[row], seam[row] + 3,
// energyArray[row], seam[row] + 2, currentWidth - seam[row] - 3 );
        }
        --currentWidth;
        //create a new picture
        Picture newPicture = new Picture(currentWidth, currentHeight);
        for (int row = 0; row < currentHeight; ++row) {
            for (int col = 0; col < currentWidth; ++col) {
                newPicture.set(col, row, picture.get(col, row));
            }
        }
        this.picture = newPicture;
    }
    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null) {
            throw new java.lang.NullPointerException();
        }
        if (seam.length != currentHeight) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < seam.length; ++i) {
            if (seam[i] < 0 || seam[i] >= currentWidth) {
                throw new java.lang.IllegalArgumentException();
            }
            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }
        if (isTransposed) {
            transpose();
        }
        removeVerticalSeamHelper(seam);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null) {
            throw new java.lang.NullPointerException();
        }
        if (seam.length != currentWidth) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < seam.length; ++i) {
            if (seam[i] < 0 || seam[i] >= currentHeight) {
                throw new java.lang.IllegalArgumentException();
            }
            if (i > 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
        }
        if (!isTransposed) {
            transpose();
        }
        removeVerticalSeamHelper(seam);
        transpose();
    }

}
