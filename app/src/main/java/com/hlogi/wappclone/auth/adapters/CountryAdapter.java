package com.hlogi.wappclone.auth.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.auth.data.model.Country;
import com.hlogi.wappclone.databinding.CountryListItemBinding;

public class CountryAdapter extends ListAdapter<Country, CountryAdapter.ViewHolder> {

    private ItemAction mItemAction;
    private Country mSelectedCountry;

    public CountryAdapter(Country selectedCountry) {
        super(Country.DIFF_CALLBACK);
        this.mSelectedCountry = selectedCountry;
    }

    public void setOnItemAction( ItemAction itemAction) {
        this.mItemAction = itemAction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CountryListItemBinding binding = CountryListItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(getItem(position), mSelectedCountry);
        holder.setOnItemClickListener(mItemAction);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private CountryListItemBinding binding;
        private Country country;
        private ItemAction itemAction;

        ViewHolder(@NonNull CountryListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Country country, Country selectedCountry) {
            this.country = country;
            binding.countryName.setText(country.getName());
            binding.code.setText(String.format("+%s", country.getCode()));

            if (selectedCountry.getName().equals(country.getName())) {
                binding.checkImg.setVisibility(View.VISIBLE);
                binding.countryName.setTextColor(binding.getRoot().getResources().getColor(R.color.colorGreen, null));
            } else {
                binding.checkImg.setVisibility(View.INVISIBLE);
                binding.countryName.setTextColor(binding.getRoot().getResources().getColor(R.color.colorBlack, null));
            }

            binding.getRoot().setOnClickListener(v -> {
                itemAction.onClick(country);
            });
        }

        public Country getCountry() {
            return country;
        }

        void setOnItemClickListener(ItemAction itemClickListener) {
            this.itemAction = itemClickListener;
        }
    }

    public interface ItemAction {
        void onClick(Country country);
    }
}
