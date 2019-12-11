package com.airhockey.android;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.airhockey.android.airhockey1.AirHockey1Activity;
import com.airhockey.android.airhockey2.AirHockey2Activity;
import com.airhockey.android.airhockey3d.AirHockey3DActivity;
import com.airhockey.android.ortho.AirHockeyOrthoActivity;
import com.airhockey.android.texture.AirHockeyTextureActivity;

import java.util.ArrayList;
import java.util.List;

class MainRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context context;
    private List<Pair<Integer, Class>> data;

    public MainRVAdapter(Context context) {
        this.context = context;
        initData();
    }

    private void initData() {
        data = new ArrayList<>();
        data.add(new Pair<>(R.string.air_hockey_1, AirHockey1Activity.class));
        data.add(new Pair<>(R.string.air_hockey_2, AirHockey2Activity.class));
        data.add(new Pair<>(R.string.air_hockey_ortho, AirHockeyOrthoActivity.class));
        data.add(new Pair<>(R.string.air_hockey_3d, AirHockey3DActivity.class));
        data.add(new Pair<>(R.string.air_hockey_texured, AirHockeyTextureActivity.class));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VHolder(LayoutInflater.from(context).inflate(R.layout.rv_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VHolder vHolder = (VHolder) holder;
        vHolder.titleView.setText(context.getString(data.get(position).first));
        vHolder.titleView.setOnClickListener(v -> {
            Class clazz = data.get(position).second;
            Intent intent = new Intent(context, clazz);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class VHolder extends RecyclerView.ViewHolder {
        TextView titleView;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.title);
        }
    }
}
