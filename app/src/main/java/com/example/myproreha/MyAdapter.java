package com.example.myproreha;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClass> datalist;

    public MyAdapter(Context context, List<DataClass> datalist) {
        this.context = context;
        this.datalist = datalist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.recTitle.setText(datalist.get(position).getDataTitle());
        holder.recDate.setText(datalist.get(position).getDataDate());


        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Title", datalist.get(holder.getAdapterPosition()).getDataTitle());
                intent.putExtra("Date", datalist.get(holder.getAdapterPosition()).getDataDate());
                intent.putExtra("Duration", datalist.get(holder.getAdapterPosition()).getDataDuration());
                intent.putExtra("Notes", datalist.get(holder.getAdapterPosition()).getDataNotes());
                intent.putExtra("Key", datalist.get(holder.getAdapterPosition()).getKey());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
}

class MyViewHolder extends RecyclerView.ViewHolder{
    TextView recTitle, recDate;
    CardView recCard;


    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        recCard = itemView.findViewById(R.id.recCard);
        recTitle = itemView.findViewById(R.id.recTitle);
        recDate = itemView.findViewById(R.id.recDate);


    }
}
