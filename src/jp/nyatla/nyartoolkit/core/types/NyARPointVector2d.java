package jp.nyatla.nyartoolkit.core.types;


/**
 * 点の座標と、そのベクトルを格納します。
 *
 */
public class NyARPointVector2d
{
	public double x;
	public double y;
	public double dx;
	public double dy;
	public static NyARPointVector2d[] createArray(int i_length)
	{
		NyARPointVector2d[] r=new NyARPointVector2d[i_length];
		for(int i=0;i<i_length;i++){
			r[i]=new NyARPointVector2d();
		}
		return r;
	}
	/* 法線ベクトルを計算する。
	 */
	public void OrthogonalVec(NyARPointVector2d i_src)
	{
		double w=this.dx;
		this.dx=i_src.dy;
		this.dy=-w;
	}
}