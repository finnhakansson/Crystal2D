package finnhakansson;

import java.io.*;
import java.util.*;

public class BmpFile {

	public BmpFile() {
		// ?
	}

    public static byte[] CreateBitMapFileHeader(int width, int height) {
        int size = height * width * 3 + 14 + 40;
        byte[] h = new byte[14];
        h[0] = 0x42;
        h[1] = 0x4d;
        h[2] = (byte)((size << 24) >>> 24);
        h[3] = (byte)((size << 16) >>> 24);
        h[4] = (byte)((size << 8) >>> 24);
        h[5] = (byte)((size << 0) >>> 24);
        h[6] = 0; // bfReserved1
        h[7] = 0;
        h[8] = 0; // bfReserved2
        h[9] = 0;
        h[10] = 14 + 40; //
        h[11] = 0;
        h[12] = 0;
        h[13] = 0;
        return h;
    }

    public static byte[] CreateBitMapInfoHeader(int width, int height) {
        byte[] h = new byte[40];
        h[0] = 40;
        h[1] = 0;
        h[2] = 0;
        h[3] = 0;
        
        h[4] = (byte)((width << 24) >>> 24);
        h[5] = (byte)((width << 16) >>> 24);
        h[6] = (byte)((width << 8) >>> 24);
        h[7] = (byte)((width << 0) >>> 24);
        h[8] = (byte)((height << 24) >>> 24);
        h[9] = (byte)((height << 16) >>> 24);
        h[10] = (byte)((height << 8) >>> 24);
        h[11] = (byte)((height << 0) >>> 24);
        
        h[12] = 1; // Number of planes
        h[13] = 0;
        
        h[14] = 24;
        h[15] = 0;
        
        // compression
        h[16] = 0;
        h[17] = 0;
        h[18] = 0;
        h[19] = 0;
        
        // size of image
        h[20] = 0;
        h[21] = 0;
        h[22] = 0;
        h[23] = 0;
        
        h[24] = 0;
        h[25] = 0;
        h[26] = 0;
        h[27] = 0;
        
        h[28] = 0;
        h[29] = 0;
        h[30] = 0;
        h[31] = 0;
        
        h[32] = 0;
        h[33] = 0;
        h[34] = 0;
        h[35] = 0;
        
        h[36] = 0;
        h[37] = 0;
        h[38] = 0;
        h[39] = 0;

        return h;
    }

    public static byte[] CreateBitMapImage(int width, int height) {
        Random rand = new Random();
        byte[] image = new byte[width * height * 3];
        int i = 0;
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                int r = rand.nextInt();
                int g = rand.nextInt();
                int b = rand.nextInt();
                
                if (r < 0) {
                    r = -r;
                }
                if (g < 0) {
                    g = -g;
                }
                if (b < 0) {
                    b = -b;
                }
                
                r %= 256;
                g %= 256;
                b %= 256;
                
                if (r < 50) {
                    r += 50;
                }
                
                //System.out.println("(" + h + ", " + w + "): " + "r = " + r + " g = " + g + " b = " + b);
                
                image[i++] = (byte)b; // blue
                image[i++] = (byte)g; // green
                image[i++] = (byte)r; // red
            }
        }
        return image;
    }
    
    public static void createbmpfile(String filename, int width, int height) {
        byte[] fileheader = CreateBitMapFileHeader(width, height);
        byte[] infoheader = CreateBitMapInfoHeader(width, height);
        byte[] image = CreateBitMapImage(width, height);
        File f = new File(filename);
        //System.out.println("createbmpfile:  image_size = " + image_size);
        
        try {
            // Create an output stream to the file.
            FileOutputStream file_output = new FileOutputStream(f);
            // Wrap the FileOutputStream with a DataOutputStream
            DataOutputStream data_out = new DataOutputStream(file_output);
            
            data_out.write(fileheader, 0, fileheader.length);
            data_out.write(infoheader, 0, infoheader.length);
            data_out.write(image, 0, image.length);
            
            // Close file when finished with it..
            file_output.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e );
        }
    }

}
