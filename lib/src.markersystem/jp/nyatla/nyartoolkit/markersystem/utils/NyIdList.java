package jp.nyatla.nyartoolkit.markersystem.utils;

import java.util.ArrayList;

import jp.nyatla.nyartoolkit.NyARException;
import jp.nyatla.nyartoolkit.core.raster.INyARGrayscaleRaster;
import jp.nyatla.nyartoolkit.core.types.NyARIntPoint2d;
import jp.nyatla.nyartoolkit.nyidmarker.*;
import jp.nyatla.nyartoolkit.nyidmarker.data.*;

public class NyIdList extends ArrayList<MarkerInfoNyId>
{
	private static final long serialVersionUID = -6446466460932931830L;
	/**輪郭推定器*/
	private NyIdMarkerPickup _id_pickup;
	private final NyIdMarkerPattern _id_patt=new NyIdMarkerPattern();
	private final NyIdMarkerParam _id_param=new NyIdMarkerParam();
	private final NyIdMarkerDataEncoder_RawBitId _id_encoder=new NyIdMarkerDataEncoder_RawBitId();
	private final NyIdMarkerData_RawBitId _id_data=new NyIdMarkerData_RawBitId();
	public NyIdList() throws NyARException
	{
		this._id_pickup = new NyIdMarkerPickup();
	}
	public void prepare()
	{
		for(int i=this.size()-1;i>=0;i--){
			MarkerInfoNyId target=this.get(i);
			if(target.life>0){
				target.lost_count++;
			}
			target.sq=null;
		}
	}
	public boolean update(INyARGrayscaleRaster i_raster,SquareStack.Item i_sq) throws NyARException
	{
		if(!this._id_pickup.pickFromRaster(i_raster.getGsPixelDriver(),i_sq.ob_vertex, this._id_patt, this._id_param))
		{
			return false;
		}
		if(!this._id_encoder.encode(this._id_patt,this._id_data)){
			return false;
		}
		//IDを検出
		long s=this._id_data.marker_id;
		for(int i=this.size()-1;i>=0;i--){
			MarkerInfoNyId target=this.get(i);
			if(target.nyid_range_s>s || s>target.nyid_range_e)
			{
				continue;
			}
			//既に認識済なら無視
			if(target.lost_count==0){
				continue;
			}
			//一致したよー。
			target.nyid=s;
			target.dir=this._id_param.direction;
			target.sq=i_sq;
			return true;
		}
		return false;
	}
	public void finish()
	{
		for(int i=this.size()-1;i>=0;i--)
		{
			MarkerInfoNyId target=this.get(i);
			if(target.sq==null){
				continue;
			}
			if(target.lost_count>0){
				//参照はそのままで、dirだけ調整する。
				target.lost_count=0;
				target.life++;
				target.sq.rotateVertexL(4-target.dir);
				NyARIntPoint2d.shiftCopy(target.sq.ob_vertex,target.tl_vertex,4-target.dir);
				target.tl_center.setValue(target.sq.center2d);
				target.tl_rect_area=target.sq.rect_area;
			}
		}
	}	
}