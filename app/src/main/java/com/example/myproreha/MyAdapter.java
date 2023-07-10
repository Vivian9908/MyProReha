package com.example.myproreha;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.content.Context;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.clans.fab.FloatingActionButton;


public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> dataList;




    public MyAdapter(Context context, List<DataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.recTitle.setText(dataList.get(position).getDataTherapie());
        holder.recDate.setText(dataList.get(position).getDataDate());
        holder.recDuration.setText(dataList.get(position).getDataDuration());
        holder.recNotes.setText(dataList.get(position).getDataNotes());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UploadActivity.class);
                intent.putExtra("Title", dataList.get(holder.getAdapterPosition()).getDataTherapie());
                intent.putExtra("Date", dataList.get(holder.getAdapterPosition()).getDataDate());
                intent.putExtra("Duration", dataList.get(holder.getAdapterPosition()).getDataDuration());
                intent.putExtra("Notes", dataList.get(holder.getAdapterPosition()).getDataNotes());
                intent.putExtra("key", dataList.get(holder.getAdapterPosition()).getKey());


                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder {

    TextView recTitle, recDate, recDuration, recNotes;
    CardView recCard;

    public MyViewHolder(@NonNull View itemView) {

        super(itemView);

        recTitle = itemView.findViewById(R.id.recTitle);
        recDate = itemView.findViewById(R.id.recDate);
        recDuration = itemView.findViewById(R.id.recDuration);
        recNotes = itemView.findViewById(R.id.recNotes);
        recCard = itemView.findViewById(R.id.recCard);


    }
}

