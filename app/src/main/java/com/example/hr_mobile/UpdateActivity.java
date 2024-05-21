package com.example.hr_mobile;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
public class UpdateActivity extends AppCompatActivity {
    ImageView updateImage;
    Button updateBtn;
    EditText updateFio, updateDesc, updateRole;
    String fio, desc, role;
    String imageUrl;
    String key, oldImageUrl;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        updateBtn = findViewById(R.id.editButton);
        updateRole = findViewById(R.id.updateRole);
        updateDesc = findViewById(R.id.updateDesc);
        updateFio = findViewById(R.id.updateFio);
        updateImage = findViewById(R.id.updateImage);

        ActivityResultLauncher<Intent> activityResultLauncherAgain = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateActivity.this, "No image", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Glide.with(UpdateActivity.this).load(bundle.getString("Image")).into(updateImage);
            updateFio.setText(bundle.getString("Name"));
            updateDesc.setText(bundle.getString("Description"));
            updateRole.setText(bundle.getString("Role"));
            key = bundle.getString("Key");
            oldImageUrl = bundle.getString("Image");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("Android Tutorials").child(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");

                try {
                    activityResultLauncherAgain.launch(photoPicker);
                }
                catch (Exception ex){
                    Toast.makeText(UpdateActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void saveData() {

        if(uri == null){
            updateData();
            return;
        }

        storageReference = FirebaseStorage.getInstance().getReference()
                .child("Android Images").child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);

        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();

                while(!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();

                imageUrl = urlImage.toString();
                updateData();

                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });

    }

    public void updateData(){
        fio = updateFio.getText().toString().trim();
        role = updateRole.getText().toString().trim();
        desc = updateDesc.getText().toString().trim();

        if (imageUrl == null) {
            imageUrl = oldImageUrl;
        }

        DataClass data = new DataClass(fio, desc, role, imageUrl);

        databaseReference.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    if (oldImageUrl != null && !oldImageUrl.equals(data.getDataImage())) {
                        StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                        reference.delete();
                    }
                    Toast.makeText(UpdateActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
