package com.example.admin.cryptowatcher;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 14.02.2018.
 */

public class AlarmViewAdapter extends RecyclerView.Adapter<AlarmViewAdapter.AlarmViewHolder>{

    List<NotificationEntity> alarms;
    private OnItemClickListener onItemClickListener;
    public AlarmViewAdapter(List<NotificationEntity> alarmL) {
        this.alarms = alarmL;

    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView pairName;
        TextView marketName;
        TextView resLvl;
        TextView supLvl;
        AlarmViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.alarm_card);
            pairName = (TextView)itemView.findViewById(R.id.alarm_card_pair);
            marketName = (TextView)itemView.findViewById(R.id.alarm_card_market);
            resLvl = (TextView)itemView.findViewById(R.id.alarm_card_res);
            supLvl = (TextView)itemView.findViewById(R.id.alarm_card_sup);
        }
    }


    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.alarm_view_card, viewGroup, false);
        AlarmViewHolder pvh = new AlarmViewHolder(v);
        return pvh;
    }

   /* public void updateEmployeeListItems(List<NotificationEntity> alarmList) {
        Log.d("Cards" ,  "Update list called");
        final DiffCallbackAlarmList diffCallback = new DiffCallbackAlarmList(this.alarms, alarmList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        this.alarms.clear();
        this.alarms.addAll(alarmList);
        diffResult.dispatchUpdatesTo(this);

    }*/
    @Override
    public void onBindViewHolder(final AlarmViewHolder alarmViewHolder, final int i) {


        alarmViewHolder.pairName.setText(alarms.get(i).getPairName());
        alarmViewHolder.marketName.setText(alarms.get(i).getMarketName());
        alarmViewHolder.resLvl.setText(String.valueOf(alarms.get(i).getResistanceLevel()));
        alarmViewHolder.supLvl.setText(String.valueOf(alarms.get(i).getSupportLevel()));


        View.OnLongClickListener listener = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(final View v) {
               new AlertDialog.Builder(v.getContext(), R.style.MaterialBaseTheme_Light_Dialog)
                        .setTitle(R.string.attention_msg)
                        .setMessage(R.string.delete_notification_qstn_msg)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                try{

                                    NotificationIO temp = new NotificationIO(v.getContext(), alarms.get(i),"delete");

                                }finally {


                                    onItemClickListener.onItemLongClicked(alarms.get(i));
                                    //call interface method that will fire up classes that have an
                                    //implementation of the interface onItemClickListener
                                }
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

                return true;
            }
        };
        alarmViewHolder.pairName.setOnLongClickListener(listener);
        alarmViewHolder.marketName.setOnLongClickListener(listener);
        alarmViewHolder.cv.setOnLongClickListener(listener);
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {//register CallBack
        this.onItemClickListener = onItemClickListener;
    }
    AlarmViewAdapter(ArrayList<NotificationEntity> list){
        this.alarms = list;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


}
