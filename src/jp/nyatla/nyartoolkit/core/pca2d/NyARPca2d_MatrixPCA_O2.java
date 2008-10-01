/* 
 * PROJECT: NyARToolkit
 * --------------------------------------------------------------------------------
 * This work is based on the original ARToolKit developed by
 *   Hirokazu Kato
 *   Mark Billinghurst
 *   HITLab, University of Washington, Seattle
 * http://www.hitl.washington.edu/artoolkit/
 *
 * The NyARToolkit is Java version ARToolkit class library.
 * Copyright (C)2008 R.Iizuka
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this framework; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * For further information please contact.
 *	http://nyatla.jp/nyatoolkit/
 *	<airmail(at)ebony.plala.or.jp>
 * 
 */
package jp.nyatla.nyartoolkit.core.pca2d;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.param.NyARCameraDistortionFactor;
import jp.nyatla.nyartoolkit.core.types.*;
import jp.nyatla.nyartoolkit.core.types.matrix.*;

/**
 * ARToolkitのPCA関数を二次元に特化させて単純化したもの
 *
 */
public class NyARPca2d_MatrixPCA_O2 implements INyARPca2d
{
	private double[] _x;

	private double[] _y;

	private int _number_of_data;

	private static final double PCA_EPS = 1e-6; // #define EPS 1e-6

	private static final int PCA_MAX_ITER = 100; // #define MAX_ITER 100

	private static final double PCA_VZERO = 1e-16; // #define VZERO 1e-16
	public NyARPca2d_MatrixPCA_O2(int i_max_points)
	{
		this._x=new double[i_max_points];
		this._y=new double[i_max_points];
		this._number_of_data=0;
		return;
	}


	/**
	 * static int QRM( ARMat *a, ARVec *dv )の代替関数
	 * 
	 * @param a
	 * @param dv
	 * @throws NyARException
	 */
	private static void PCA_QRM(NyARDoubleMatrix22 o_matrix, NyARDoublePoint2d dv) throws NyARException
	{
		double w, t, s, x, y, c;
		double ev1;
		double dv_x,dv_y;
		double mat00,mat01,mat10,mat11;
		// <this.vecTridiagonalize2d(i_mat, dv, ev);>
		dv_x = o_matrix.m00;// this.m[dim - 2][dim - 2];// d.v[dim-2]=a.m[dim-2][dim-2];//d->v[dim-2]=a->m[(dim-2)*dim+(dim-2)];
		ev1 = o_matrix.m01;// this.m[dim - 2][dim - 1];// e.v[dim-2+i_e_start]=a.m[dim-2][dim-1];//e->v[dim-2] = a->m[(dim-2)*dim+(dim-1)];
		dv_y = o_matrix.m11;// this.m[dim - 1][dim - 1];// d.v[dim-1]=a_array[dim-1][dim-1];//d->v[dim-1] =a->m[(dim-1)*dim+(dim-1)];
		// 単位行列にする。
		mat00 = mat11 = 1;
		mat01 = mat10 = 0;
		// </this.vecTridiagonalize2d(i_mat, dv, ev);>

		// int j = 1;
		// // while(j>0 && fabs(ev->v[j])>EPS*(fabs(dv->v[j-1])+fabs(dv->v[j])))
		// while (j > 0 && Math.abs(ev1) > PCA_EPS * (Math.abs(dv.x) + Math.abs(dv.y))) {
		// j--;
		// }
		// if (j == 0) {
		int iter = 0;
		do {
			iter++;
			if (iter > PCA_MAX_ITER) {
				break;
			}
			w = (dv_x - dv_y) / 2;// w = (dv->v[h-1] -dv->v[h]) / 2;//ここ？
			t = ev1 * ev1;// t = ev->v[h] * ev->v[h];
			s = Math.sqrt(w * w + t);
			if (w < 0) {
				s = -s;
			}
			x = dv_x - dv_y + t / (w + s);// x = dv->v[j] -dv->v[h] +t/(w+s);
			y = ev1;// y = ev->v[j+1];

			if (Math.abs(x) >= Math.abs(y)) {
				if (Math.abs(x) > PCA_VZERO) {
					t = -y / x;
					c = 1 / Math.sqrt(t * t + 1);
					s = t * c;
				} else {
					c = 1.0;
					s = 0.0;
				}
			} else {
				t = -x / y;
				s = 1.0 / Math.sqrt(t * t + 1);
				c = t * s;
			}
			w = dv_x - dv_y;// w = dv->v[k] -dv->v[k+1];
			t = (w * s + 2 * c * ev1) * s;// t = (w * s +2 * c *ev->v[k+1]) *s;
			dv_x -= t;// dv->v[k] -= t;
			dv_y += t;// dv->v[k+1] += t;
			ev1 += s * (c * w - 2 * s * ev1);// ev->v[k+1]+= s * (c* w- 2* s *ev->v[k+1]);

			x = mat00;// x = a->m[k*dim+i];
			y = mat10;// y = a->m[(k+1)*dim+i];
			mat00 = c * x - s * y;// a->m[k*dim+i] = c * x - s* y;
			mat10 = s * x + c * y;// a->m[(k+1)*dim+i] = s* x + c * y;
			
			x = mat01;// x = a->m[k*dim+i];
			y = mat11;// y = a->m[(k+1)*dim+i];
			mat01 = c * x - s * y;// a->m[k*dim+i] = c * x - s* y;
			mat11 = s * x + c * y;// a->m[(k+1)*dim+i] = s* x + c * y;
		} while (Math.abs(ev1) > PCA_EPS * (Math.abs(dv_x) + Math.abs(dv_y)));
		// }

		t = dv_x;// t = dv->v[h];
		if (dv_y > t) {// if( dv->v[i] > t ) {
			t = dv_y;// t = dv->v[h];
			dv_y = dv_x;// dv->v[h] = dv->v[k];
			dv_x = t;// dv->v[k] = t;
			// 行の入れ替え
			o_matrix.m00 = mat10;
			o_matrix.m01 = mat11;
			o_matrix.m10 = mat00;		
			o_matrix.m11 = mat01;
			
		} else {
			// 行の入れ替えはなし
			o_matrix.m00 = mat00;
			o_matrix.m01 = mat01;
			o_matrix.m10 = mat10;		
			o_matrix.m11 = mat11;
		}
		dv.x=dv_x;
		dv.y=dv_y;
		return;
	}

	/**
	 * static int PCA( ARMat *input, ARMat *output, ARVec *ev )
	 * 
	 * @param output
	 * @param o_ev
	 * @throws NyARException
	 */
	private void PCA_PCA(NyARDoubleMatrix22 o_matrix, NyARDoublePoint2d o_ev,NyARDoublePoint2d o_mean) throws NyARException
	{
		// double[] mean_array=mean.getArray();
		// mean.zeroClear();

		//PCA_EXの処理
		double sx = 0;
		double sy = 0;
		final int number_of_data = this._number_of_data;
		for (int i = 0; i < number_of_data; i++) {
			sx += this._x[i];
			sy += this._y[i];
		}
		sx = sx / number_of_data;
		sy = sy / number_of_data;
		
		//PCA_CENTERとPCA_xt_by_xを一緒に処理
		final double srow = Math.sqrt((double) this._number_of_data);
		double w00, w11, w10;
		w00 = w11 = w10 = 0.0;// *out = 0.0;
		for (int i = 0; i < number_of_data; i++) {
			final double x = (this._x[i] - sx) / srow;
			final double y = (this._y[i] - sy) / srow;
			w00 += (x * x);// *out += *in1 * *in2;
			w10 += (x * y);// *out += *in1 * *in2;
			w11 += (y * y);// *out += *in1 * *in2;
		}
		o_matrix.m00=w00;
		o_matrix.m01=o_matrix.m10=w10;
		o_matrix.m11=w11;
		
		//PCA_PCAの処理
		PCA_QRM(o_matrix, o_ev);
		// m2 = o_output.m;// m2 = output->m;
		if (o_ev.x < PCA_VZERO) {// if( ev->v[i] < VZERO ){
			o_ev.x = 0.0;// ev->v[i] = 0.0;
			o_matrix.m00 = 0.0;// *(m2++) = 0.0;
			o_matrix.m01 = 0.0;// *(m2++) = 0.0;
		}

		if (o_ev.y < PCA_VZERO) {// if( ev->v[i] < VZERO ){
			o_ev.y = 0.0;// ev->v[i] = 0.0;
			o_matrix.m10 = 0.0;// *(m2++) = 0.0;
			o_matrix.m11 = 0.0;// *(m2++) = 0.0;
		}
		o_mean.x=sx;
		o_mean.y=sy;
		// }
		return;
	}
	public void pca(double[] i_x,double[] i_y,int i_start,int i_number_of_point,NyARDoubleMatrix22 o_evec, NyARDoublePoint2d o_ev,NyARDoublePoint2d o_mean) throws NyARException
	{
		NyARException.trap("未チェックの関数");
		assert(this._x.length>i_number_of_point);		
		this._number_of_data = i_number_of_point;
		System.arraycopy(i_x, 0, this._x, i_start, i_number_of_point);
		System.arraycopy(i_y, 0, this._y, i_start, i_number_of_point);
		
		PCA_PCA(o_evec, o_ev,o_mean);

		final double sum = o_ev.x + o_ev.y;
		// For順変更禁止
		o_ev.x /= sum;// ev->v[i] /= sum;
		o_ev.y /= sum;// ev->v[i] /= sum;
		return;	
	}
	
	public void pcaWithDistortionFactor(int[] i_x,int[] i_y,int i_start,int i_number_of_point,NyARCameraDistortionFactor i_factor,NyARDoubleMatrix22 o_evec, NyARDoublePoint2d o_ev,NyARDoublePoint2d o_mean) throws NyARException
	{
		assert(this._x.length>i_number_of_point);
		this._number_of_data = i_number_of_point;
		i_factor.observ2IdealBatch(i_x, i_y, i_start, i_number_of_point, this._x, this._y);

		PCA_PCA(o_evec,o_ev,o_mean);

		final double sum = o_ev.x + o_ev.y;
		// For順変更禁止
		o_ev.x /= sum;// ev->v[i] /= sum;
		o_ev.y /= sum;// ev->v[i] /= sum;
		return;	
	}
}
