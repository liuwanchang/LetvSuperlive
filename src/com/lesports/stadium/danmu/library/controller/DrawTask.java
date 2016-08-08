package com.lesports.stadium.danmu.library.controller;


import android.graphics.Canvas;

import com.lesports.stadium.danmu.library.danmaku.model.AbsDisplayer;
import com.lesports.stadium.danmu.library.danmaku.model.BaseDanmaku;
import com.lesports.stadium.danmu.library.danmaku.model.DanmakuTimer;
import com.lesports.stadium.danmu.library.danmaku.model.GlobalFlagValues;
import com.lesports.stadium.danmu.library.danmaku.model.IDanmakuIterator;
import com.lesports.stadium.danmu.library.danmaku.model.IDanmakus;
import com.lesports.stadium.danmu.library.danmaku.model.android.DanmakuGlobalConfig;
import com.lesports.stadium.danmu.library.danmaku.model.android.DanmakuGlobalConfig.ConfigChangedCallback;
import com.lesports.stadium.danmu.library.danmaku.model.android.DanmakuGlobalConfig.DanmakuConfigTag;
import com.lesports.stadium.danmu.library.danmaku.model.android.Danmakus;
import com.lesports.stadium.danmu.library.danmaku.parser.BaseDanmakuParser;
import com.lesports.stadium.danmu.library.danmaku.parser.DanmakuFactory;
import com.lesports.stadium.danmu.library.danmaku.renderer.IRenderer;
import com.lesports.stadium.danmu.library.danmaku.renderer.IRenderer.RenderingState;
import com.lesports.stadium.danmu.library.danmaku.renderer.android.DanmakuRenderer;
import com.lesports.stadium.danmu.library.danmaku.renderer.android.DanmakusRetainer;

public class DrawTask implements IDrawTask {
    
    protected AbsDisplayer<?> mDisp;

    protected IDanmakus danmakuList;

    protected BaseDanmakuParser mParser;

    TaskListener mTaskListener;

    IRenderer mRenderer;

    DanmakuTimer mTimer;

    private IDanmakus danmakus = new Danmakus(Danmakus.ST_BY_LIST);

    protected boolean clearRetainerFlag;

    private long mStartRenderTime = 0;

    private RenderingState mRenderingState = new RenderingState();

    protected boolean mReadyState;

    private long mLastBeginMills;

    private long mLastEndMills;

    private boolean mIsHidden;
    private ConfigChangedCallback mConfigChangedCallback = new ConfigChangedCallback() {
        @Override
        public boolean onDanmakuConfigChanged(DanmakuGlobalConfig config, DanmakuConfigTag tag, Object... values) {
            return DrawTask.this.onDanmakuConfigChanged(config, tag, values);
        }
    };

    public DrawTask(DanmakuTimer timer, AbsDisplayer<?> disp,
            TaskListener taskListener) {
        mTaskListener = taskListener;
        mRenderer = new DanmakuRenderer();
        mRenderer.setVerifierEnabled(DanmakuGlobalConfig.DEFAULT.isPreventOverlappingEnabled() || DanmakuGlobalConfig.DEFAULT.isMaxLinesLimited());
        mDisp = disp;
        initTimer(timer);
        Boolean enable = DanmakuGlobalConfig.DEFAULT.isDuplicateMergingEnabled();
        if (enable != null) {
            if(enable) {
                DanmakuFilters.getDefault().registerFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
            } else {
                DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
            }
        }
    }

    protected void initTimer(DanmakuTimer timer) {
        mTimer = timer;
    }

    @Override
    public synchronized void addDanmaku(BaseDanmaku item) {
        if (danmakuList == null)
            return;
        boolean added = false;
        if (item.isLive) {
            removeUnusedLiveDanmakusIn(10);
        }
        item.index = danmakuList.size();
        if (mLastBeginMills <= item.time && item.time <= mLastEndMills) {
            synchronized (danmakus) {
                added = danmakus.addItem(item);
            }
        } else if (item.isLive) {
            mLastBeginMills = mLastEndMills = 0;
        }
        synchronized (danmakuList) {
            added = danmakuList.addItem(item);
        }
        if (added && mTaskListener != null) {
            mTaskListener.onDanmakuAdd(item);
        }
    }
    
    @Override
    public synchronized void removeAllDanmakus() {
        if (danmakuList == null || danmakuList.isEmpty())
            return;
        danmakuList.clear();
    }

    protected void onDanmakuRemoved(BaseDanmaku danmaku) {
        // TODO call callback here
    }

    @Override
    public synchronized void removeAllLiveDanmakus() {
        if (danmakus == null || danmakus.isEmpty())
            return;
        synchronized (danmakus) {
            IDanmakuIterator it = danmakus.iterator();
            while (it.hasNext()) {
                BaseDanmaku danmaku = it.next();
                if (danmaku.isLive) {
                    it.remove();
                    onDanmakuRemoved(danmaku);
                }
            }
        }
    }

    protected synchronized void removeUnusedLiveDanmakusIn(int msec) {
        if (danmakuList == null || danmakuList.isEmpty())
            return;
        long startTime = System.currentTimeMillis();
        IDanmakuIterator it = danmakuList.iterator();
        while (it.hasNext()) {
            BaseDanmaku danmaku = it.next();
            boolean isTimeout = danmaku.isTimeOut();
            if (isTimeout && danmaku.isLive) {
                it.remove();
                onDanmakuRemoved(danmaku);
            }
            if (!isTimeout || System.currentTimeMillis() - startTime > msec) {
                break;
            }
        }
    }

    @Override
    public synchronized RenderingState draw(AbsDisplayer<?> displayer) {
        return drawDanmakus(displayer,mTimer);
    }

    @Override
    public void reset() {
        if (danmakus != null)
            danmakus.clear();
        if (mRenderer != null)
            mRenderer.clear();
    }

    @Override
    public void seek(long mills) {
        reset();
//        requestClear();
        GlobalFlagValues.updateVisibleFlag();
        mStartRenderTime = mills < 1000 ? 0 : mills;
    }

    @Override
    public void clearDanmakusOnScreen(long currMillis) {
        reset();
        GlobalFlagValues.updateVisibleFlag();
        mStartRenderTime = currMillis;
    }

    @Override
    public void start() {
        DanmakuGlobalConfig.DEFAULT.registerConfigChangedCallback(mConfigChangedCallback);
    }

    @Override
    public void quit() {
        DanmakuGlobalConfig.DEFAULT.unregisterAllConfigChangedCallbacks();
        if (mRenderer != null)
            mRenderer.release();
    }

    public void prepare() {
        assert (mParser != null);
        loadDanmakus(mParser);
        if (mTaskListener != null) {
            mTaskListener.ready();
            mReadyState = true;
        }
    }
    //获取要展示的数据
    protected void loadDanmakus(BaseDanmakuParser parser) {
        danmakuList = parser.setDisplayer(mDisp).setTimer(mTimer).getDanmakus();
        GlobalFlagValues.resetAll();
    }

    public void setParser(BaseDanmakuParser parser) {
        mParser = parser;
        mReadyState = false;
    }

    protected RenderingState drawDanmakus(AbsDisplayer<?> disp, DanmakuTimer timer) {
        if (clearRetainerFlag) {
            DanmakusRetainer.clear();
            clearRetainerFlag = false;
        }
        if (danmakuList != null) {
            Canvas canvas = (Canvas) disp.getExtraData();
            DrawHelper.clearCanvas(canvas);
            if (mIsHidden) {
                return mRenderingState;
            }
            long beginMills = timer.currMillisecond - DanmakuFactory.MAX_DANMAKU_DURATION - 100;
            long endMills = timer.currMillisecond + DanmakuFactory.MAX_DANMAKU_DURATION;
            if(mLastBeginMills > beginMills || timer.currMillisecond > mLastEndMills) {
                IDanmakus subDanmakus = danmakuList.sub(beginMills, endMills);
                if(subDanmakus != null) {
                    danmakus = subDanmakus;
                } else {
                    danmakus.clear();
                }
                mLastBeginMills = beginMills;
                mLastEndMills = endMills;
            } else {
                beginMills = mLastBeginMills;
                endMills = mLastEndMills;
            }
            if (danmakus != null && !danmakus.isEmpty()) {
                RenderingState renderingState = mRenderingState = mRenderer.draw(mDisp, danmakus, mStartRenderTime);
                if (renderingState.nothingRendered) {
                    if (renderingState.beginTime == RenderingState.UNKNOWN_TIME) {
                        renderingState.beginTime = beginMills;
                    }
                    if (renderingState.endTime == RenderingState.UNKNOWN_TIME) {
                        renderingState.endTime = endMills;
                    }
                }
                return renderingState;
            } else {
                mRenderingState.nothingRendered = true;
                mRenderingState.beginTime = beginMills;
                mRenderingState.endTime = endMills;
                return mRenderingState;
            }
        }
        return null;
    }

    public void requestClear() {
        mLastBeginMills = mLastEndMills = 0;
        mIsHidden = false;
    }

    public void requestClearRetainer() {
        clearRetainerFlag = true;
    }

    public boolean onDanmakuConfigChanged(DanmakuGlobalConfig config, DanmakuConfigTag tag,
            Object... values) {
        boolean handled = handleOnDanmakuConfigChanged(config, tag, values);
        if (mTaskListener != null) {
            mTaskListener.onDanmakuConfigChanged();
        }
        return handled;
    }

    protected boolean handleOnDanmakuConfigChanged(DanmakuGlobalConfig config, DanmakuConfigTag tag, Object[] values) {
        boolean handled = false;
        if (tag == null || DanmakuConfigTag.MAXIMUM_NUMS_IN_SCREEN.equals(tag)) {
            handled = true;
        } else if (DanmakuConfigTag.DUPLICATE_MERGING_ENABLED.equals(tag)) {
            Boolean enable = (Boolean) values[0];
            if (enable != null) {
                if (enable) {
                    DanmakuFilters.getDefault().registerFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
                } else {
                    DanmakuFilters.getDefault().unregisterFilter(DanmakuFilters.TAG_DUPLICATE_FILTER);
                }
                handled = true;
            }
        } else if (DanmakuConfigTag.SCALE_TEXTSIZE.equals(tag) || DanmakuConfigTag.SCROLL_SPEED_FACTOR.equals(tag)) {
            requestClearRetainer();
            handled = false;
        } else if (DanmakuConfigTag.MAXIMUN_LINES.equals(tag) || DanmakuConfigTag.OVERLAPPING_ENABLE.equals(tag)) {
            if (mRenderer != null) {
                mRenderer.setVerifierEnabled(DanmakuGlobalConfig.DEFAULT.isPreventOverlappingEnabled() || DanmakuGlobalConfig.DEFAULT.isMaxLinesLimited());
            }
            handled = true;
        }
        return handled;
    }

    @Override
    public void requestHide() {
        mIsHidden = true;
    }

}
