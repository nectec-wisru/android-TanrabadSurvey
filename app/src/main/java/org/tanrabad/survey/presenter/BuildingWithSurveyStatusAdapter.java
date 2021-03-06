/*
 * Copyright (c) 2016 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tanrabad.survey.presenter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.tanrabad.survey.R;
import org.tanrabad.survey.domain.building.BuildingWithSurveyStatus;
import org.tanrabad.survey.entity.Building;
import org.tanrabad.survey.repository.persistence.BuildingWithChange;
import org.tanrabad.survey.utils.alert.Alert;
import org.tanrabad.survey.utils.android.InternetConnection;

import java.util.ArrayList;
import java.util.List;

public class BuildingWithSurveyStatusAdapter extends RecyclerView.Adapter<BuildingWithSurveyStatusAdapter.ViewHolder>
        implements ListViewAdapter<BuildingWithSurveyStatus> {

    private final Context context;
    private final int buildingIcon;

    private List<BuildingWithSurveyStatus> building = new ArrayList<>();
    private List<Building> surveyedBuilding = new ArrayList<>();
    private AdapterView.OnItemClickListener onItemClickListener;
    private AdapterView.OnItemLongClickListener onItemLongClickListener;
    private boolean isEditButtonVisible;
    private OnDeleteBuildingListener onDeleteBuildingListener;

    public BuildingWithSurveyStatusAdapter(Context context, @DrawableRes int buildingIcon) {
        this.context = context;
        this.buildingIcon = buildingIcon;
    }

    @Override
    public void updateData(List<BuildingWithSurveyStatus> buildings) {
        this.building = buildings;
        notifyDataSetChanged();
    }

    @Override
    public void clearData() {
        this.building = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public BuildingWithSurveyStatus getItem(int i) {
        return building.get(i);
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setEditButtonVisibility(boolean isEditButtonVisible) {
        this.isEditButtonVisible = isEditButtonVisible;
        notifyDataSetChanged();
    }

    @Override
    public BuildingWithSurveyStatusAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_building, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BuildingWithSurveyStatusAdapter.ViewHolder holder, int position) {
        BuildingWithSurveyStatus buildingWithSurveyStatus = building.get(position);
        holder.buildingTextView.setText(buildingWithSurveyStatus.building.getName());
        holder.buildingIcon.setImageResource(buildingIcon);
        if (buildingWithSurveyStatus.isSurvey) {
            holder.rootView.setEnabled(false);
            holder.surveyedStatus.setVisibility(View.VISIBLE);
        } else {
            holder.rootView.setEnabled(true);
            holder.surveyedStatus.setVisibility(View.INVISIBLE);
        }

        if (isEditButtonVisible) {
            holder.editBuilding.setVisibility(View.VISIBLE);
            holder.deleteBuilding.setVisibility(View.VISIBLE);
        } else {
            holder.editBuilding.setVisibility(View.GONE);
            holder.deleteBuilding.setVisibility(View.GONE);
        }

        setSyncStatus(holder, buildingWithSurveyStatus.building);
    }

    private void setSyncStatus(ViewHolder holder, Building currentBuilding) {
        BuildingWithChange bwc = (BuildingWithChange) currentBuilding;
        holder.notSync.setVisibility(bwc.isNotSynced() ? View.VISIBLE : View.GONE);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return building.size();
    }

    private void onItemHolderClick(ViewHolder itemHolder) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    private void onItemHolderLongClick(ViewHolder itemHolder) {
        if (onItemLongClickListener != null) {
            onItemLongClickListener.onItemLongClick(null, itemHolder.itemView,
                    itemHolder.getAdapterPosition(), itemHolder.getItemId());
        }
    }

    public void setOnDeleteBuildingListener(OnDeleteBuildingListener onDeleteBuildingListener) {
        this.onDeleteBuildingListener = onDeleteBuildingListener;
    }

    interface OnDeleteBuildingListener {
        void onDeleteBuilding(Building building);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView buildingTextView;
        private ImageButton editBuilding;
        private ImageButton deleteBuilding;
        private ImageView buildingIcon;
        private ImageView notSync;
        private View surveyedStatus;
        private View rootView;

        public ViewHolder(View itemView) {
            super(itemView);
            rootView = itemView;
            buildingTextView = (TextView) itemView.findViewById(R.id.building_name);
            buildingIcon = (ImageView) itemView.findViewById(R.id.building_icon);
            editBuilding = (ImageButton) itemView.findViewById(R.id.edit_building);
            deleteBuilding = (ImageButton) itemView.findViewById(R.id.delete_building);
            surveyedStatus = itemView.findViewById(R.id.surveyed);
            notSync = (ImageView) itemView.findViewById(R.id.not_sync);
            editBuilding.setOnClickListener(this);
            deleteBuilding.setOnClickListener(this);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.edit_building) {
                Building building = BuildingWithSurveyStatusAdapter.this.building.get(getAdapterPosition()).building;
                BuildingFormActivity.startEdit((Activity) context,
                        building.getPlace().getId().toString(),
                        building.getId().toString());
            } else if (view.getId() == R.id.delete_building) {
                if (!InternetConnection.isAvailable(context)) {
                    Alert.highLevel().show(R.string.please_enable_internet_before_delete);
                    return;
                }

                if (onDeleteBuildingListener == null)
                    return;

                Building building = BuildingWithSurveyStatusAdapter.this.building.get(getAdapterPosition()).building;
                onDeleteBuildingListener.onDeleteBuilding(building);
            } else {
                onItemHolderClick(this);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            onItemHolderLongClick(this);
            return true;
        }
    }
}
