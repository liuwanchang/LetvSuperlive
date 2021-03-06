package com.lesports.stadium.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * ***************************************************************
 * 
 * @ClassName: VisualizerView
 * 
 * @Desc :自定义音波控件 显示当前声音分贝大小
 * 
 * @Copr : 北京晶朝科技有限责任公司 版权所有 (c) 2016
 * 
 * @Author : 王新年
 * 
 * @data:2016-2-2 下午2:49:19
 * 
 * @Version : v1.0
 * 
 * 
 * @Modify : null
 * 
 *         ***************************************************************
 */
public class VisualizerView extends View {
	/**
	 * view宽度与单个音频块占比 - 正常480 需微调
	 */
	private static final int DN_W = 470;
	/**
	 * view高度与单个音频块占比
	 */
	private static final int DN_H = 180;
	/**
	 * 单个音频块宽度
	 */
	private static final int DN_SL = 15;
	/**
	 * 单个音频块高度
	 */
	private static final int DN_SW = 5;

	/**
	 * 音量柱·音频块 - 最大个数
	 */
	protected final static int MAX_LEVEL = 15;
	/**
	 * 音量柱 - 最大个数
	 */
	protected final static int CYLINDER_NUM = 28;
	protected final static int CYLINDER_NUM_TYPE = 15;
	/**
	 * 频谱器
	 */
	protected Visualizer mVisualizer = null;
	/**
	 * 画笔
	 */
	protected Paint mPaint = null;
	/**
	 * 音量柱 数组
	 */
	protected byte[] mData = new byte[CYLINDER_NUM];

	boolean mDataEn = true;
	private int hgap = 0;
	private int vgap = 0;
	private int levelStep = 18;
	private float strokeWidth = 0;
	private float strokeLength = 0;

	// 构造函数初始化画笔
	public VisualizerView(Context context) {
		super(context);
		mPaint = new Paint();// 初始化画笔工具
		mPaint.setAntiAlias(true);// 抗锯齿
		mPaint.setColor(Color.BLACK);// 画笔颜色
	}

	public VisualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	// 执行 Layout 操作
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		float w, h, xr, yr;

		w = right - left;
		h = bottom - top;
		xr = w / (float) DN_W;
		yr = h / (float) DN_H;

		strokeWidth = DN_SW * yr;
		strokeLength = DN_SL * xr;
		hgap = (int) ((w - strokeLength * CYLINDER_NUM) / (CYLINDER_NUM + 1));
		vgap = (int) (h / (MAX_LEVEL + 2));

		mPaint.setStrokeWidth(strokeWidth); // 设置频谱块宽度
	}

	// 绘制频谱块和倒影
	protected void drawCylinder(Canvas canvas, float x, byte value) {
		if (value <= 0)
			value = 1;// 最少有一个频谱块

		for (int i = 0; i < value; i++) { // 每个能量柱绘制value个能量块
			if(value>12)
				value = 12;
			float y = (getHeight() - i * vgap - vgap) - 40;// 计算y轴坐标
			int ranColor = 0xff073047;
			if(value>4){
				if (value <= i + 3) {
					ranColor = 0xff14C0EF;
				}
			}else if (value >3 || value ==3) {
				if (value <= i + 1) {
					ranColor = 0xff14C0EF;
				}
			}else if(value==2){
				if (value <= i + 1) {
					ranColor = 0xff14C0EF;
				}
			}
			// 绘制频谱块
			mPaint.setColor(ranColor);// 画笔颜色
			canvas.drawLine(x, y, (x + strokeLength), y, mPaint);// 绘制频谱块

			// 绘制音量柱倒影
			/*
			 * if (i <= 6 && value > 0) { mPaint.setColor(Color.WHITE);// 画笔颜色
			 * mPaint.setAlpha(100 - (100 / 6 * i));// 倒影颜色 canvas.drawLine(x,
			 * -y + 210, (x + strokeLength), -y + 210, mPaint);// 绘制频谱块 }
			 */
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		for (int i = 0; i < CYLINDER_NUM; i++) { // 绘制28个能量柱
			drawCylinder(canvas, strokeWidth / 2 + hgap + i
					* (hgap + strokeLength) - 15, mData[i]);
		}
	}
	

	/**
	 * 
	 * @Title: onFftDataCapture
	 * @Description: 这个回调应该采集的是快速傅里叶变换有关的数据
	 * @param: @param fft
	 * @return: void
	 * @throws
	 */
	public void onFftDataCapture(byte[] fft) {
		byte[] model = new byte[fft.length / 2 + 1];
		if (mDataEn) {
			model[0] = (byte) Math.abs(fft[1]);
			int j = 1;
			for (int i = 2; i < fft.length;) {
				model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
				i += 2;
				j++;
			}
		} else {
			for (int i = 0; i < CYLINDER_NUM; i++) {
				model[i] = 0;
			}
		}
		for (int i = 0; i < CYLINDER_NUM; i++) {
			final byte a = (byte) (Math.abs(model[CYLINDER_NUM - i]) / levelStep);

			final byte b = mData[i];
			if (a > b) {
				mData[i] = a;
			} else {
				if (b > 0) {
					mData[i]--;
				}
			}
		}
		postInvalidate();// 刷新界面
	}
	
}
