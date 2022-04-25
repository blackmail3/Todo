package com.example.comp7506_1.todolist.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.example.comp7506_1.todolist.Bean.User;
import com.example.comp7506_1.todolist.DBHelper.MyDatabaseHelper;
import com.example.comp7506_1.todolist.Interface.ItemTouchHelperAdapter;
import com.example.comp7506_1.todolist.R;
import com.example.comp7506_1.todolist.Bean.Tomato;
import com.example.comp7506_1.todolist.Utils.BitmapUtils;
import com.example.comp7506_1.todolist.Utils.SPUtils;
import com.example.comp7506_1.todolist.Utils.TomatoUtils;

import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;
import me.drakeet.materialdialog.MaterialDialog;

/**
 * RecyclerViewAdapter
 */
public class ClockRecyclerViewAdapter extends RecyclerView.Adapter<ClockRecyclerViewAdapter.ViewHolder>
        implements ItemTouchHelperAdapter{

    private List<Tomato> tomatoList;
    private Context context;
    private MaterialDialog dialog;
    private int truePosition,itemPosition;
    private MyDatabaseHelper dbHelper;


    public ClockRecyclerViewAdapter(List<Tomato> tomato, Context context) {
        this.tomatoList = tomato;
        this.context=context;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView clock_title;
        TextView work_time;
        ImageView clock_card_bg;




        public ViewHolder(View itemView) {
            super(itemView);
            clock_title = (TextView) itemView.findViewById(R.id.clock_title);
            work_time = (TextView) itemView.findViewById(R.id.work_time);
            clock_card_bg = (ImageView) itemView.findViewById(R.id.clock_card_bg);

        }


    }
    @Override
    public ClockRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(context).inflate(R.layout.item_clock,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ClockRecyclerViewAdapter.ViewHolder ViewHolder, int i) {

        RequestOptions options2 =new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(true)
                .signature(new ObjectKey(SPUtils.get(context,"head_signature","")))
                .placeholder(R.drawable.ic_img1);

        ViewHolder.clock_title.setText(tomatoList.get(tomatoList.size()-1-i).getTitle());
        ViewHolder.work_time.setText(tomatoList.get(tomatoList.size()-1-i).getWorkLength() + " 分钟");
        ViewHolder.clock_card_bg.setImageBitmap(BitmapUtils.readBitMap(context,tomatoList.get(tomatoList.size()-1-i).getImgId()));

    }

    @Override
    public int getItemCount() {
        return tomatoList.size();
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(tomatoList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(fromPosition,toPosition);
        return true;
    }

    public void removeItem(int position){
        truePosition = tomatoList.size()-1-position;
        itemPosition = position;
        popAlertDialog();
//        tomatoList.remove(tomatoList.size()-1-position);
//        notifyItemRemoved(position);
//        notifyItemRangeChanged(position, tomatoList.size()-position);
    }

    private void popAlertDialog() {

        if (dialog == null) {

            dialog = new MaterialDialog(context);
            dialog.setMessage("Delete it?")
                    .setPositiveButton("Confirm", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Tomato tomato = tomatoList.get(truePosition);
                            String clockTitle = tomatoList.get(truePosition).getTitle();
                            dbHelper = new MyDatabaseHelper(context, "Data.db", null, 2);
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            db.delete("Clock","clocktitle = ?",
                                    new String[]{clockTitle});

                            if (User.getCurrentUser(User.class) != null){
                                TomatoUtils.deleteNetTomato(context, tomato, new TomatoUtils.DeleteTomatoListener() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(int errorCord, String msg) {
                                        Log.i("ClockFragment", "msg ");
                                        Toasty.warning(context, msg, Toast.LENGTH_SHORT, true).show();
                                    }
                                });
                            }
                            tomatoList.remove(truePosition);
                            notifyItemRemoved(itemPosition);
                            notifyItemRangeChanged(itemPosition,tomatoList.size());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new View.OnClickListener() {
                        public void onClick(View view) {
                            notifyItemChanged(itemPosition);
                            dialog.dismiss();
                        }
                    });

        }

        dialog.show();

    }


}
