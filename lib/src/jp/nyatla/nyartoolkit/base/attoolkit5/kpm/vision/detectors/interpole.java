/* 
 * PROJECT: NyARToolkit
 * --------------------------------------------------------------------------------
 * This work is based on the original ARToolKit developed by
 *  Copyright 2013-2015 Daqri, LLC.
 *  Author(s): Chris Broaddus
 *
 * The NyARToolkit is Java edition ARToolKit class library.
 *  Copyright (C)2016 Ryo Iizuka
 * 
 * NyARToolkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as publishe
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * NyARToolkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and to
 * copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms and
 * conditions of the license of that module. An independent module is a module
 * which is neither derived from nor based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but you
 * are not obligated to do so. If you do not wish to do so, delete this exception
 * statement from your version.
 * 
 */
package jp.nyatla.nyartoolkit.base.attoolkit5.kpm.vision.detectors;

import jp.nyatla.nyartoolkit.base.attoolkit5.kpm.KpmImage;

public class interpole {
    /**
     * Perform bilinear interpolation.
     *
     * @param[in] im Image
     * @param[in] width Widht of image
     * @param[in] height Height of image
     * @param[in] step Width step
     * @param[in] x x-location to interpolate
     * @param[in] y y-location to interpolate
     */
     public static double bilinear_interpolation(double[] im,
                                       int width,
                                       int height,
                                       double x,
                                       double y) {
        int xp, yp;
        int xp_plus_1, yp_plus_1;
        double w0, w1, w2, w3;
//        const Tin* p0;
//        const Tin* p1;
        double res;
        
//        // Integer casting and floor should be the same since (x,y) are always positive
//        ASSERT((int)std::floor(x) == (int)x, "floor() and cast not the same");
//        ASSERT((int)std::floor(y) == (int)y, "floor() and cast not the same");
        
        // Compute location of 4 neighbor pixels
        xp = (int)x;
        yp = (int)y;
        xp_plus_1 = xp+1;
        yp_plus_1 = yp+1;
        
        // Some sanity checks
//        ASSERT(yp >= 0 && yp < height, "yp out of bounds");
//        ASSERT(yp_plus_1 >= 0 && yp_plus_1 < height, "yp_plus_1 out of bounds");
//        ASSERT(xp >= 0 && xp < width, "xp out of bounds");
//        ASSERT(xp_plus_1 >= 0 && xp_plus_1 < width, "xp_plus_1 out of bounds");
        
        // Pointer to 2 image rows
        int p0 = width*yp;
        int p1 = p0+width;
        
        // Compute weights
        w0 = (xp_plus_1-x)*(yp_plus_1-y);
        w1 = (x-xp)*(yp_plus_1-y);
        w2 = (xp_plus_1-x)*(y-yp);
        w3 = (x-xp)*(y-yp);
        
//        ASSERT(w0 >= 0 && w0 <= 1.0001, "Out of range");
//        ASSERT(w1 >= 0 && w1 <= 1.0001, "Out of range");
//        ASSERT(w2 >= 0 && w2 <= 1.0001, "Out of range");
//        ASSERT(w3 >= 0 && w3 <= 1.0001, "Out of range");
//        ASSERT((w0+w1+w2+w3) <= 1.0001, "Out of range");
        
        // Compute weighted pixel
//        res = w0*p0[xp] + w1*p0[xp_plus_1] + w2*p1[xp] + w3*p1[xp_plus_1];
        res = w0*im[p0+xp] + w1*im[p0+xp_plus_1] + w2*im[p1+xp] + w3*im[p1+xp_plus_1];
        
        return res;
    }
//    template<typename T>
//    inline T bilinear_interpolation(const T* im,
//                                    size_t width,
//                                    size_t height,
//                                    size_t step,
//                                    float x,
//                                    float y) {
//        return bilinear_interpolation<T, T>(im, width, height, step, x, y); 
//    }
//    
//    /**
//     * Perform bilinear interpolation on an "unsigned char" image. The interpolation is done in 
//     * FLOAT precision and rounded.
//     */
//    inline unsigned char bilinear_interpolation(const unsigned char* im,
//                                                size_t width,
//                                                size_t height,
//                                                size_t step,
//                                                float x,
//                                                float y) {
//        float ret = bilinear_interpolation<unsigned char, float>(im, width, height, step, x, y);
//        ASSERT(ret >= 0, "Out of range");
//        ASSERT(ret <= 255, "Out of range");
//        return (unsigned char)(ret+0.5f);
//    }
//    
    /**
     * Bilinear interpolation. Integer pixel locations specify the center of the pixel.
     *
     * @param[in] im Image
     * @param[in] x
     * @param[in] y
     */
     public static double bilinear_interpolation(KpmImage im,
    		 double x,
    		 double y) {
    	return bilinear_interpolation((double[])im.getBuffer(), im.getWidth(), im.getHeight(), x, y);
    }
}
