package com.lesports.stadium.adapter;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.lesports.stadium.R;
import com.lesports.stadium.app.LApplication;
import com.lesports.stadium.bean.MyYuyueBean;
import com.lesports.stadium.bean.SenceBean;
import com.lesports.stadium.bean.YuYueActivityBean;
import com.lesports.stadium.utils.ConstantValue;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ***************************************************************
 * 
 * @Desc : 我的预约的数据适配器
 * 
 * @Copr : 北京晶朝科技有限责任公司 版权所有 (c) 2016
 * 
 * @Author : liuwc
 * 
 * @data:
 * 
 * @Version : v1.0
 * 
 * 
 * @Modify : null
 * 
 *         ***************************************************************
 */

public class WodeyuyueListviewAdapter extends BaseAdapter {

	/**
	 * 上下文
	 */
	private Context mContext;
	/**
	 * 数据源
	 */
	private List<MyYuyueBean> mList;
	/**
	 * 保存着预约列表的数据集合
	 */
	private List<YuYueActivityBean> mYuyuelist=new ArrayList<YuYueActivityBean>();
	/**
	 * 用来记录被点击的或者是当前item的下表
	 */
	/**
	 * 预约数据的集合
	 */
	private List<YuYueActivityBean> list_yuyue; 
	/**
	 */
	public WodeyuyueListviewAdapter(Context context, List<MyYuyueBean> list) {
		this.mContext = context;
		this.mList = list;
		
	}

	/**
	 * 提供给界面中用于动态改变适配器内数据以及类别标记
	 * 
	 * @2016-2-26上午10:24:56
	 */
	public void setTagandData(int tag, List<MyYuyueBean> list) {
		this.mList = list;
		notifyDataSetChanged();
	}
	/**
	 * 提供给界面中用于动态改变适配器内数据以及类别标记
	 * 
	 * @2016-2-26上午10:24:56
	 */
	public void setData(List<MyYuyueBean> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		viewHolder vh = null;
		if (arg1 == null) {
			vh = new viewHolder();
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			arg1 = inflater
					.inflate(R.layout.sence_fragment_listview_item, null);
			vh.mBgImage = (ImageView) arg1
					.findViewById(R.id.second_sence_frament_listview_imagesss);
			vh.mBigtitle = (TextView) arg1
					.findViewById(R.id.tv_second_huodong_lable);
			vh.mTitle = (TextView) arg1
					.findViewById(R.id.tv_second_huodong_lable_subheader);
			vh.mTimeEnd = (TextView) arg1
					.findViewById(R.id.tv_second_huodong_time);
			vh.jinxinzhong = (TextView) arg1
					.findViewById(R.id.jinxinzhong);
			arg1.setTag(vh);
		} else {
			vh = (viewHolder) arg1.getTag();
		}
		LApplication.loader.DisplayImage(ConstantValue.BASE_IMAGE_URL
				+ mList.get(arg0).getmSenceBean().getFrontCoverImageURL()
				+ ConstantValue.IMAGE_END, vh.mBgImage,
				R.drawable.huodongshouye_zhanwei);
		useWaychuliBigtitle(vh.mBigtitle, mList.get(arg0).getmSenceBean().getTitle());
		useWaychuliSmailTitle(vh.mTitle, mList.get(arg0).getmSenceBean().getSubhead());
		int status = Integer.parseInt(mList.get(arg0).getmSenceBean().getcStatus());
		if (status == 1) {
			// 说明是进行中
			vh.jinxinzhong.setVisibility(View.VISIBLE);
			vh.mTimeEnd.setText(mList.get(arg0).getmSenceBean().getVenueName());
//			vh.mTimeEnd.setTextColor(Color.rgb(225, 219, 73));
		} else if (status == 0) {
			// 说明是未开始
			vh.mTimeEnd.setTextColor(Color.rgb(255, 255, 255));
			useWayCountTime(vh.mTimeEnd, mList.get(arg0).getmSenceBean());
			vh.jinxinzhong.setVisibility(View.GONE);
		} else if (status == 2) {
			vh.mTimeEnd.setVisibility(View.GONE);
			vh.jinxinzhong.setVisibility(View.GONE);
		}

		return arg1;
	}
	/**
	 * 用来处理小标题
	 * 
	 * @param mTitle
	 * @param subhead
	 */
	private void useWaychuliSmailTitle(TextView mTitle, String subhead) {
		if(!TextUtils.isEmpty(subhead)){
			if(vd(subhead)){
				//说明有汉字
				mTitle.setTextSize(24);
				mTitle.setText(subhead);
			}else{
				mTitle.setTextSize(24);
				mTitle.setText(subhead);
			}
		}
	}

	/**
	 * 用来处理大标题
	 * 
	 * @param mBigtitle
	 * @param label
	 */
	private void useWaychuliBigtitle(TextView mBigtitle, String label) {
		if(!TextUtils.isEmpty(label)){
			if(vd(label)){
				//说明有汉字
				mBigtitle.setTextSize(27);
				mBigtitle.setText(label);
			}else{
				mBigtitle.setTextSize(32);
				mBigtitle.setText(label);
			}
		}
	}

	public boolean vd(String str) {

		char[] chars = str.toCharArray();
		boolean isGB2312 = false;
		for (int i = 0; i < chars.length; i++) {
			byte[] bytes = ("" + chars[i]).getBytes();
			if (bytes.length == 2) {
				int[] ints = new int[2];
				ints[0] = bytes[0] & 0xff;
				ints[1] = bytes[1] & 0xff;
				if (ints[0] >= 0x81 && ints[0] <= 0xFE && ints[1] >= 0x40
						&& ints[1] <= 0xFE) {
					isGB2312 = true;
					break;
				}
			}
		}
		return isGB2312;
	}
	/**
	 * 创建方法用来计算开始时间和结束时间
	 * 
	 * @param mTimeEnd
	 * @param senceBean
	 */
	private void useWayCountTime(TextView mTimeEnd, SenceBean senceBean) {
		String starttime = getChangeToTime(senceBean.getStarttime());
		String endtime = getChangeToTime(senceBean.getEndtime());
		if (!TextUtils.isEmpty(starttime) && !TextUtils.isEmpty(endtime)) {
			String startmonth = getMonthAndDay(starttime);
			String starthour = getHourAndMinute(starttime);
			String endhour = getHourAndMinute(endtime);
			String time = startmonth + "  " + starthour + "-" + endhour;
			mTimeEnd.setText(senceBean.getVenueName()+"　｜　"+time);
		}
	}

	/**
	 * 用来检查用户是否预约该活动
	 * @param id
	 * @return
	 */
	private boolean checkActivityIsYuyue(String id) {
		boolean ishave=false;
		if(list_yuyue!=null&&list_yuyue.size()!=0){
			for(int i=0;i<list_yuyue.size();i++){
				if(id.equals(list_yuyue.get(i).getActivityId())){
					ishave=true;
					break;
				}else{
					ishave=false;
				}
			}
		}
		return ishave;
	}

	/**
	 * 根据传入的活动id，用户来获取相对应的bid
	 * @param id
	 */
	private String getBid(String id) {
		String bid=null;
		for(int i=0;i<mYuyuelist.size();i++){
			YuYueActivityBean bean=mYuyuelist.get(i);
			if(bean.getActivityId().equals(id)){
				bid=bean.getBid();
				Log.i("bid是多少",bid);
				break;
			}
		}
		return bid;
	}

	/**
	 * 将结束时间转换后设置到控件上
	 * 
	 * @param mTimeEnd
	 * @param endtime
	 */
	private void setEndTimeToUI(TextView mTimeEnd, String endtime) {
		// TODO Auto-generated method stub
		String str = getChangeToTime(endtime);
		String hm = getHourAndMinute(str);
		mTimeEnd.setText(hm);
	}

	/**
	 * //根据传入开始时间，截取小时数，来进行设置
	 * 
	 * @param mTimeStart
	 * @param starttime
	 */
	private void setStartTimeToUI(TextView mTimeStart, String starttime) {
		String str = getChangeToTime(starttime);
		String hm = getHourAndMinute(str);
		mTimeStart.setText(hm);
	}

	/**
	 * 根据当前传入时间，来计算当前时间是pm还是am
	 * 
	 * @param mTimeType
	 * @param starttime
	 */
	private void countTimesType(TextView mTimeType, String starttime) {
		String str = getChangeToTime(starttime);
		String hm = getHourAndMinute(str);
		/**
		 * 截取小时数
		 */
		String hour = hm.substring(0, 2);
		int hours = Integer.parseInt(hour);
		if (hours < 12) {
			// 说明是上午
			mTimeType.setText("AM");
		} else {
			mTimeType.setText("PM");
		}
	}

	/**
	 * 根据传入控件和时间，将时间数值设置到空间上
	 * 
	 * @param mBigtitle
	 * @param starttime
	 */
	private void setTimeToBigTitle(TextView mBigtitle, String starttime) {
		String str = getChangeToTime(starttime);
		String md = getMonthAndDay(str);
		mBigtitle.setText(md);
	}

	/**
	 * 将传入的时间字符串，转换成相应的时间，并且将之设置到控件上
	 * 
	 * @param mTitle
	 * @param string
	 */
	private void initStartTime(TextView mTitle, String string) {
		// TODO Auto-generated method stub
		String str = getChangeToTime(string);
		Log.i("得到的开始时间是多少", str);
		// 截取月日 2015.12.14.00.00.00
		String md = getMonthAndDay(str);
		Log.i("现在的月日是多少", md);
		String hm = getHourAndMinute(str);
		Log.i("现在获取的小时分钟数是", hm);

	}

	/**
	 * 将一个传入的字符串毫秒值转换成一个字符串时间值
	 * 
	 * @param str
	 * @return
	 */
	public String getChangeToTime(String str) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH:mm:ss",
				Locale.getDefault());
		long time = Long.parseLong(str);
		Date date = new Date();
		date.setTime(time);
		String timestring = sdf.format(date);

		return timestring;

	}

	/**
	 * 根据传入的字符串时间值，截取当前月日时间字符串
	 * 
	 * @param str
	 * @return
	 */
	public String getMonthAndDay(String str) {
		String md = str.substring(5, 10);
		return md;

	}

	/**
	 * 根据传入的时间值，截取小时和分钟数
	 * 
	 * @param str
	 * @return
	 */
	public String getHourAndMinute(String str) {
		String hm = str.substring(11, 16);
		return hm;

	}

	static class viewHolder {
		/**
		 * 大标题
		 */
		TextView mBigtitle;
		/**
		 * 说明
		 */
		TextView mExplain;
		/**
		 * 标题
		 */
		TextView mTitle;
		/**
		 * time类型，上午还是下午，只用在未开始的item中，开始或者结束的需要隐藏
		 */
		TextView mTimeType;
		/**
		 * 开始时间
		 */
		TextView mTimeStart;
		/**
		 * 结束时间
		 */
		TextView mTimeEnd;
		/**
		 * 时间隔离符号
		 */
		TextView mTimeGl;
		/**
		 * 地理位置信息
		 */
		TextView mLocation;
		/**
		 * 参加人数
		 */
		TextView mPersonNum;
		/**
		 * 活动的状态，分为三种，比赛中，未开始，已结束
		 */
		TextView mStatus;
		/**
		 * 活动状态的背景图片
		 */
		ImageView mStatusBg;
		/**
		 * 展示背景的image
		 */
		ImageView mBgImage;
		/**
		 * 用户是否报名
		 */
		ImageView isBaoming;
		/**
		 * 状态
		 */
		TextView jinxinzhong;
	}

	/**
	 * 用来处理图片，目的是为了节省内存
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {

		BitmapFactory.Options opt = new BitmapFactory.Options();

		opt.inPreferredConfig = Bitmap.Config.RGB_565;

		opt.inPurgeable = true;

		opt.inInputShareable = true;

		// 获取资源图片

		InputStream is = context.getResources().openRawResource(resId);

		return BitmapFactory.decodeStream(is, null, opt);

	}

}
