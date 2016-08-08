package com.lesports.stadium.danmu.library.danmaku.model;


public abstract class AbsDisplayer<T> implements IDisplayer {
    
    public abstract T getExtraData();
    
    public abstract void setExtraData(T data);

    @Override
    public boolean isHardwareAccelerated() {
        return false;
    }

}
