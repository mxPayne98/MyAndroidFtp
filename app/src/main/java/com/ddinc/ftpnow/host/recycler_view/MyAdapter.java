package com.ddinc.ftpnow.host.recycler_view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ddinc.ftpnow.R;
import com.ddinc.ftpnow.host.database.UserInfo;

import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<UserInfo> userInfos;
    private Context context;

    public MyAdapter(ArrayList<UserInfo> userInfos, Context context) {
        this.userInfos = userInfos;
        this.context = context;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(viewType, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserInfo u = userInfos.get(position);
        String name = u.name + " - " + (u.writePermission ? context.getString(R.string.text_view_write_permission_true) : context.getString(R.string.text_view_write_permission_false));
        holder.textViewName.setText(name);

        String tempStr;
        if (u.uploadRate != 0)
            tempStr = " : " + String.valueOf(u.uploadRate / 1024) + "KB/s";
        else
            tempStr = "Unlimited";
        holder.textViewUploadRate.setText(context.getString(R.string.text_view_upload_rate) + " : " + tempStr);

        if (u.downloadRate != 0)
            tempStr = " : " + String.valueOf(u.uploadRate / 1024) + "KB/s";
        else
            tempStr = "Unlimited";

        holder.textViewDownloadRate.setText(context.getString(R.string.text_view_download_rate) + " : " + tempStr);
        holder.textViewUserEnabled.setText(u.userEnabled ? context.getString(R.string.text_view_user_enabled_true) : context.getString(R.string.text_view_user_enabled_false));

    }

    @Override
    public int getItemCount() {
        return userInfos.size();
    }

    public int getItemViewType(int position) {
        return R.layout.recycler_view_item_card_view;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewUser;
        TextView textViewName, textViewUploadRate, textViewDownloadRate, textViewUserEnabled;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewUser = itemView.findViewById(R.id.image_view_user);
            textViewName = itemView.findViewById(R.id.text_view_card_user_name);
            textViewUploadRate = itemView.findViewById(R.id.text_view_card_upload_rate);
            textViewDownloadRate = itemView.findViewById(R.id.text_view_card_download_rate);
            textViewUserEnabled = itemView.findViewById(R.id.text_view_card_user_enabled);
        }
    }
}