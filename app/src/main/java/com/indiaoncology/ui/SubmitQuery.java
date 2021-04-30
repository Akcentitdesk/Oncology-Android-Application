package com.indiaoncology.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.share.Share;
import com.indiaoncology.R;
import com.indiaoncology.adaptar.prescription.PrescriptionAdapter;
import com.indiaoncology.databinding.ActivitySubmitQueryBinding;
import com.indiaoncology.databinding.DialogBinding;
import com.indiaoncology.databinding.LayoutTakepictureBinding;
import com.indiaoncology.databinding.PopupAddReminderBinding;
import com.indiaoncology.model.PrescriptionData;
import com.indiaoncology.model.PrescriptionResponse;
import com.indiaoncology.model.type.Data;
import com.indiaoncology.model.type.TypeResponse;
import com.indiaoncology.service.Api;
import com.indiaoncology.service.BaseCallback;
import com.indiaoncology.service.BaseResponse;
import com.indiaoncology.service.RequestController;
import com.indiaoncology.ui.patient.SelectPatient;
import com.indiaoncology.utils.ActivityController;
import com.indiaoncology.utils.AppConstant;
import com.indiaoncology.utils.CommonUtils;
import com.indiaoncology.utils.DialogUtils;
import com.indiaoncology.utils.ProgressDialogUtils;
import com.indiaoncology.utils.SharedPref;
import com.indiaoncology.utils.TakePictureUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.indiaoncology.utils.TakePictureUtils.PICK_GALLERY;
import static com.indiaoncology.utils.TakePictureUtils.TAKE_PICTURE;

public class SubmitQuery extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private Activity activity;
    String condition, stage, cancer_type_id;
    private ActivitySubmitQueryBinding binding;
    private String filePath;
    public static String image = "";
    private Dialog dialog, dialogSubmit;
    ArrayList<String> arrayList = new ArrayList<>();
    private List<Data> typeDataList = new ArrayList<>();
    private PrescriptionAdapter prescriptionAdapter;
    ArrayList<String> idd = new ArrayList<>();
    ArrayList<String> treatment_array = new ArrayList<>();
    private List<PrescriptionData> prescriptionDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_submit_query);
        context = SubmitQuery.this;
        activity = SubmitQuery.this;
        getTypes();
        setViews();
        CommonUtils.setToolBar(binding.menuBar, "", activity);
        binding.menuBar.ivRight.setVisibility(View.GONE);
        binding.menuBar.ivSecond.setVisibility(View.GONE);
    }

    private void getTypes() {
        if (CommonUtils.isOnline(context)) {
            try {
                Api api = RequestController.createService(context, Api.class);
                api.getTypeList(1).enqueue(new BaseCallback<TypeResponse>(context) {
                    @Override
                    public void onSuccess(TypeResponse response) {
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                if (response.getDataList() != null && !response.getDataList().isEmpty()) {
                                    typeDataList.clear();
                                    Data data = new Data();
                                    data.setTitle("select");
                                    typeDataList.add(0, data);
                                    typeDataList.addAll(response.getDataList());

                                    for (int i = 0; i < typeDataList.size(); i++) {
                                        arrayList.add(typeDataList.get(i).getTitle());
                                        System.out.println(" data " + typeDataList.get(i).getTitle());
                                        setSpinnerData(arrayList, binding.spinnerType, cancer_type_id);
                                    }
                                } else {
                                    typeDataList.clear();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFail(Call<TypeResponse> call, BaseResponse baseResponse) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setViews() {
        binding.llupload.setOnClickListener(this);
        binding.btnSubmit.setOnClickListener(this);
        binding.btnContactUs.setOnClickListener(this);

        ArrayList<String> arrayListstage = new ArrayList<>();
        arrayListstage.add("Select");
        arrayListstage.add("Early");
        arrayListstage.add("Locally Advance");
        arrayListstage.add("Metastatic");
        arrayListstage.add("Recurrence");

        ArrayList<String> arrayListcondition = new ArrayList<>();
        arrayListcondition.add("select");
        arrayListcondition.add("Active");
        arrayListcondition.add("Weak");
        arrayListcondition.add("Bedridden");

        setSpinnerData(arrayListcondition, binding.spinnerPatientCondition, condition);
        setSpinnerData(arrayListstage, binding.spinnerStage, stage);

        System.out.println(" \n" + cancer_type_id + "\n" + condition + "\n" + stage);


        binding.checkNone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.checkChemo.setChecked(false);
                binding.checkRadiation.setChecked(false);
                binding.checkSurgery.setChecked(false);
            }
        });

        setCheckedFalse(binding.checkSurgery);


    }

    private void setCheckedFalse(CheckBox checkbox) {
        checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.checkNone.setChecked(false);
            }
        });
    }

    private void setSpinnerData(ArrayList<String> arrayList, Spinner spinner, String data) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String data = parent.getItemAtPosition(position).toString();
                // Toast.makeText(parent.getContext(), "Selected: " + tutorialsName, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == PICK_GALLERY) {
            if (resultCode == Activity.RESULT_OK) {
                Uri picUri = intent.getData();
                filePath = getPath(picUri);
                runOnUiThread(() -> {
                    try {
                        TakePictureUtils.copyFile(filePath, image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                new ImageCompression().execute(image);
            }
        } else if (requestCode == TAKE_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                new ImageCompression().execute(image);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.llupload:
                opendialog();
                break;
            case R.id.btnContactUs:
                ActivityController.startActivity(context, Contact.class);
                break;
            case R.id.btnSubmit:
                if (validData()) {

                    Bundle bundle = new Bundle();
                    bundle.putString("patient_condition", String.valueOf(binding.spinnerPatientCondition.getSelectedItem()));
                    bundle.putString("cancer_stage", String.valueOf(binding.spinnerStage.getSelectedItem()));
                    bundle.putString("cancer_type", String.valueOf(binding.spinnerType.getSelectedItem()));
                    idd.clear();
                    for (int i = 0; i < prescriptionDataList.size(); i++) {
                        idd.add(prescriptionDataList.get(i).getId());
                        System.out.println("final image's  : " + idd);
                    }
                    bundle.putStringArrayList("report_id", idd);
                    treatment_array.clear();
                    if (binding.checkNone.isChecked()) {
                        treatment_array.add("No treatment");
                    }
                    if (binding.checkChemo.isChecked()) {
                        treatment_array.add("Chemotherapy");
                    }
                    if (binding.checkRadiation.isChecked()) {
                        treatment_array.add("Radiation");
                    }
                    if (binding.checkSurgery.isChecked()) {
                        treatment_array.add("Surgery");
                    }
                    bundle.putStringArrayList("treatments", treatment_array);
                    bundle.putString(AppConstant.FROM, AppConstant.FROM_ENQUIRY);
                    ActivityController.startActivity(activity, SelectPatient.class, bundle, false, false);
                    prescriptionDataList.clear();
                    idd.clear();
                    // submitData();
                }
                break;
            default:
                break;
        }
    }

    private void submitData() {

        if (CommonUtils.isOnline(context)) {
            try {
                HashMap<String, Object> map = new HashMap<>();
                map.put("user_id", SharedPref.getStringPreferences(context, AppConstant.USER_ID));
                map.put("patient_condition", binding.spinnerPatientCondition.getSelectedItem());
                map.put("cancer_stage", binding.spinnerStage.getSelectedItem());
                map.put("cancer_type", binding.spinnerType.getSelectedItem());
                idd.clear();
                for (int i = 0; i < prescriptionDataList.size(); i++) {
                    idd.add(prescriptionDataList.get(i).getId());
                    System.out.println("final image's  : " + idd);
                }
                map.put("report_id", idd);
                treatment_array.clear();
                if (binding.checkNone.isChecked()) {
                    treatment_array.add("No treatment");
                }
                if (binding.checkChemo.isChecked()) {
                    treatment_array.add("Chemotherapy");
                }
                if (binding.checkRadiation.isChecked()) {
                    treatment_array.add("Radiation");
                }
                if (binding.checkSurgery.isChecked()) {
                    treatment_array.add("Surgery");
                }

                map.put("treatments", treatment_array);
                Api api = RequestController.createService(context, Api.class);
                api.enquiry(map).enqueue(new BaseCallback<BaseResponse>(context) {
                    @Override
                    public void onSuccess(BaseResponse response) {
                        if (response != null) {
                            if (response.getStatus().equals("1")) {
                                prescriptionDataList.clear();
                                idd.clear();
                                openResponseDialog(response.getMessage());

                            }
                        }
                    }

                    @Override
                    public void onFail(Call<BaseResponse> call, BaseResponse baseResponse) {

                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void openResponseDialog(String heading) {
        final PopupAddReminderBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.popup_add_reminder, null, false);
        dialogSubmit = DialogUtils.createDialog(context, dataBinding.getRoot(), 0);
        dialogSubmit.setCancelable(false);
        dataBinding.subHeading.setText(heading);

        dataBinding.btnOk.setOnClickListener(v -> {
            dialogSubmit.dismiss();
            ActivityController.startActivity(activity, Dashboard.class, true);
        });


    }

    private boolean validData() {
        if (binding.spinnerType.getSelectedItem().equals("Select"))
            return false;
        if (binding.spinnerStage.getSelectedItem().equals("Select"))
            return false;
        else if (binding.spinnerPatientCondition.getSelectedItem().equals("Select"))
            return false;
        else if (!isSelected()) {
            return false;
        } else
            return true;
    }

    private boolean isSelected() {
        System.out.println("treatment array : " + treatment_array);
        return binding.checkSurgery.isChecked() || binding.checkRadiation.isChecked()
                || binding.checkChemo.isChecked() || binding.checkNone.isChecked();
    }

    private void opendialog() {
        final LayoutTakepictureBinding picturebinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_takepicture, null, false);
        dialog = DialogUtils.createDialog(context, picturebinding.getRoot(), 0);
        dialog.setCancelable(false);
        picturebinding.tvcancel.setOnClickListener(v -> dialog.dismiss());
        picturebinding.llcamera.setOnClickListener(v -> {
            image = TakePictureUtils.getFilename();
            TakePictureUtils.takePicture(activity, image);
            dialog.dismiss();
        });
        picturebinding.llGallery.setOnClickListener(v -> {
            image = TakePictureUtils.getFilename();
            TakePictureUtils.openGallery(activity, image);
            dialog.dismiss();
        });
    }


    public static MultipartBody.Part getMultipartFromFile(String partName, File imageFile) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile);
        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, imageFile.getName(), requestFile);
    }

    private String getPath(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    private void setSelectedPrescriptionAdapter(List<PrescriptionData> prescriptionDataList) {
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        binding.rvreports.setLayoutManager(gridLayoutManager);
        binding.rvreports.hasFixedSize();
        prescriptionAdapter = new PrescriptionAdapter(context, prescriptionDataList, (view, position) -> {
            prescriptionDataList.remove(position);
            prescriptionAdapter.notifyDataSetChanged();
            if (prescriptionDataList.size() <= 0) {
            }
        });
        prescriptionAdapter.show(true);
        if (prescriptionDataList.size() == 0) {
            binding.rvreports.setVisibility(View.GONE);
        } else {
            binding.rvreports.setVisibility(View.VISIBLE);
            binding.rvreports.setAdapter(prescriptionAdapter);
            prescriptionAdapter.notifyDataSetChanged();
        }

    }


    private void upload(File imageFile) {
        if (CommonUtils.isOnline(context)) {
            try {
                ProgressDialogUtils.show(context);
                MultipartBody.Part imagePart = null;
                if (imageFile != null) {
                    imagePart = getMultipartFromFile("image", imageFile);
                }
                RequestBody userId = CommonUtils.parseString(SharedPref.getStringPreferences(context, AppConstant.USER_ID));
                Api api = RequestController.createService(context, Api.class);
                api.uploadPrescriptions(userId, imagePart).enqueue(new Callback<PrescriptionResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PrescriptionResponse> call, @NonNull Response<PrescriptionResponse> response) {
                        ProgressDialogUtils.dismiss();
                        if (response.body() != null) {
                            prescriptionDataList.addAll(response.body().getPrescriptionDataList());
                            setSelectedPrescriptionAdapter(prescriptionDataList);
                            prescriptionAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PrescriptionResponse> call, @NonNull Throwable t) {
                        ProgressDialogUtils.dismiss();
                        //  Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();

                    }
                });


            } catch (Exception e) {
                Log.e("TAG", e.getMessage());
                e.printStackTrace();
            }
        } else {
            CommonUtils.showAlertPopup(context, getResources().getString(R.string.internet_title), getResources().getString(R.string.no_internet));

        }

    }

    public class ImageCompression extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (strings.length == 0 || strings[0] == null)
                return null;
            return TakePictureUtils.compressImage(strings[0]);
        }

        protected void onPostExecute(String imagePath) {
            // imagePath is path of new compressed image.
            upload(new File(imagePath));
        }
    }


}