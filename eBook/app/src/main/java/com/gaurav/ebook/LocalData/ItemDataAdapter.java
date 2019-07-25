package com.gaurav.ebook.LocalData;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gaurav.ebook.R;

import java.util.ArrayList;

public class ItemDataAdapter extends RecyclerView.Adapter<ItemDataAdapter.ItemDataHolder> {

    Context context;
    ArrayList<LocalDataModel> arrayList;

    public ItemDataAdapter(Context context, ArrayList<LocalDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public void onBindViewHolder(@NonNull ItemDataHolder viewHolder, final int i) {

        viewHolder.tv1.setText(arrayList.get(i).PermissionId);
        viewHolder.tv2.setText(arrayList.get(i).UserID);
    }

    @Override
    public ItemDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_local_data, parent, false);
        return new ItemDataHolder(view);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ItemDataHolder extends RecyclerView.ViewHolder {

        TextView tv1, tv2;

        public ItemDataHolder(View itemView) {
            super(itemView);

            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
        }
    }
}