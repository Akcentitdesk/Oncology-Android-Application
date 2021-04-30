package com.indiaoncology.adaptar.schedule;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;


import com.indiaoncology.R;
import com.indiaoncology.databinding.RowReminderTimingsBinding;
import com.indiaoncology.model.doctor.location.TimeArray;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.patient.SelectPatient;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;

import java.util.List;

import retrofit2.Call;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {
    private Activity context;
    private List<TimeArray> timeDataList;
    private String doc_id, day, date, loc_id, type, fees;

    public TimeSlotAdapter(Activity context, List<TimeArray> list) {
        this.context = context;
        this.timeDataList = list;
    }

    public void fetchData(String doc_id, String day, String date, String loc_id, String fees) {
        this.doc_id = doc_id;
        this.day = day;
        this.date = date;
        this.loc_id = loc_id;
        this.fees = fees;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_reminder_timings, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final TimeArray list = timeDataList.get(position);
        holder.binding.tvTiming.setText(list.getFrom());
        if (list.getIsAvailable().equals("0")) {
            holder.binding.llRoot.setEnabled(false);
            holder.binding.llRoot.setBackground(context.getResources().getDrawable(R.drawable.border_gray));
            holder.binding.tvTiming.setTextColor(context.getResources().getColor(R.color.lightGray));
        } else {
            holder.binding.llRoot.setEnabled(true);
            holder.binding.llRoot.setBackground(context.getResources().getDrawable(R.drawable.border_main));
            holder.binding.tvTiming.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }

        holder.binding.llRoot.setOnClickListener(v -> {
            /*Bundle bundle = new Bundle();
            bundle.putString("Selected_Time", list.getFrom());
            bundle.putString("Selected_Day", day);
            bundle.putString("Selected_Date", date);
            bundle.putString("fees", fees);
            bundle.putString("Selected_Doctor_Id", doc_id);
            bundle.putString("Selected_Location_Id", loc_id);
            bundle.putString(AppConstant.FROM, AppConstant.FROM_APPOINTMENT);
            ActivityController.startActivity(context, SelectPatient.class, bundle, false, false);
*/
        });

    }

    /* private void updateAppointment(String time) {
         if (CommonUtils.isOnline(context)) {
             try {
                 ProgressDialogUtils.show(context);
                 Api api = RequestController.createService(context, Api.class);
                 api.updateAppointment(SharedPref.getStringPreferences(context, AppConstant.USER_ID)
                         , doc_id, loc_id, day, date, time).enqueue(new BaseCallback<BaseResponse>(context) {
                     @Override
                     public void onSuccess(BaseResponse response) {
                         ProgressDialogUtils.dismiss();
                         if (response != null) {
                             if (response.getStatus().equals("1")) {
                                 CommonUtils.showToastShort(context, response.getMessage());
                                // ActivityController.startActivity(context, MyOrder.class, AppConstant.MY_APPOINTMENT, true);
                             }
                         }
                     }

                     @Override
                     public void onFail(Call<BaseResponse> call, BaseResponse baseResponse) {
                         ProgressDialogUtils.dismiss();
                     }
                 });
             } catch (Exception e) {
                 e.printStackTrace();
             }
         } else {
             CommonUtils.showToastShort(context, context.getResources().getString(R.string.no_internet));
         }
     }
 */
    @Override
    public int getItemCount() {
        return timeDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private RowReminderTimingsBinding binding;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}