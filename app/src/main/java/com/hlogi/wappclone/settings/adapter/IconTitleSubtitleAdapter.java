package com.hlogi.wappclone.settings.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.IcTitleSubtitleListItemBinding;

import java.util.List;

public class IconTitleSubtitleAdapter extends RecyclerView.Adapter<IconTitleSubtitleAdapter.ViewHolder> {

    private List<Integer> titles;
    private List<Integer> subtitles;
    private List<Integer> icons;
    private IconTitleSubtitleAdapter.ItemActionListener mItemActionListener;

    public IconTitleSubtitleAdapter(List<Integer> titles, List<Integer> subtitles, List<Integer> icons) {
        this.titles = titles;
        this.subtitles = subtitles;
        this.icons = icons;
    }

    public interface ItemActionListener {
        void onClickView(Integer title);
    }

    public void setItemActionListener(IconTitleSubtitleAdapter.ItemActionListener mItemActionListener) {
        this.mItemActionListener = mItemActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        IcTitleSubtitleListItemBinding binding = IcTitleSubtitleListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false );
        return new IconTitleSubtitleAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItemActionListener(mItemActionListener);
        holder.bindTo(titles.get(position), subtitles.get(position), icons.get(position));
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private IcTitleSubtitleListItemBinding binding;
        private IconTitleSubtitleAdapter.ItemActionListener itemActionListener;

        public ViewHolder(@NonNull IcTitleSubtitleListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Integer title, Integer subtitle, Integer icon) {
            binding.setNullInt(R.string.null_t);
            binding.setTitle(title);
            binding.setSubtitle(subtitle);
            binding.setIcon(binding.getRoot().getResources().getDrawable(icon, null));

            binding.getRoot().setOnClickListener(v -> {
                itemActionListener.onClickView(title);
            });
        }

        public void setItemActionListener(IconTitleSubtitleAdapter.ItemActionListener listener) {
            this.itemActionListener = listener;
        }
    }

}
