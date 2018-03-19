/*package com.example.admin.cryptowatcher;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

*//**
 * Created by Alex on 15.02.2018.
 *//*

public class DiffCallbackAlarmList extends DiffUtil.Callback {

    private final List<NotificationEntity> mOldAlarmList ;
    private final List<NotificationEntity> mNewAlarmList ;

    public DiffCallbackAlarmList (List<NotificationEntity> oldList, List<NotificationEntity> newList){
        this.mNewAlarmList = newList;
        this.mOldAlarmList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldAlarmList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewAlarmList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d("Cards" ,  "Items compare method " + (mOldAlarmList.get(oldItemPosition).getResistanceLevel() == ((mNewAlarmList.get(newItemPosition).getResistanceLevel())) &
                mOldAlarmList.get(oldItemPosition).getSupportLevel() == ((mNewAlarmList.get(newItemPosition).getSupportLevel()))));

        return (mOldAlarmList.get(oldItemPosition).getResistanceLevel() == ((mNewAlarmList.get(newItemPosition).getResistanceLevel())) &
                mOldAlarmList.get(oldItemPosition).getSupportLevel() == ((mNewAlarmList.get(newItemPosition).getSupportLevel()))) ;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Log.d("Cards" ,  "Contents compate method " + mOldAlarmList.get(oldItemPosition).equals(mNewAlarmList.get(newItemPosition)));
        return mOldAlarmList.get(oldItemPosition).equals(mNewAlarmList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}*/
