/*package com.example.myproreha;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.content.Context;
import android.widget.Toast;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;



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

              if (dataList.isEmpty()) {
            // dataList ist leer
            // Fügen Sie hier Ihren Code ein, um darauf zu reagieren

            // Zum Beispiel können Sie eine Meldung anzeigen, dass keine Daten vorhanden sind
            Toast.makeText(context, "Keine Daten vorhanden", Toast.LENGTH_SHORT).show();

            // Oder Sie können eine spezielle Ansicht anzeigen, die angibt, dass keine Daten vorhanden sind
            // z.B. TextView mit der Nachricht "Keine Daten vorhanden" anzeigen



        } else {
            // dataList enthält Daten
            // Fügen Sie hier Ihren Code ein, um darauf zu reagieren

            // Zum Beispiel können Sie die RecyclerView sichtbar machen


            // Oder Sie können zusätzlichen Code einfügen, um die Daten zu verarbeiten oder anzuzeigen
            // z.B. Daten in eine andere Liste kopieren, Filterung oder Sortierung durchführen usw.

            // Hier ist ein Beispiel, wie Sie die Daten in der dataList durchlaufen können
            for (DataClass data : dataList) {
                // Führen Sie Aktionen für jedes Datenobjekt aus
                // z.B. Loggen Sie die Daten, zeigen Sie sie in einer Toast-Nachricht an, usw.
                Log.d("MyAdapter", "Data: " + data.getDataTherapie());
                Toast.makeText(context, "Data: " + data.getDataTherapie(), Toast.LENGTH_SHORT).show();
            }
        }



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
 */
