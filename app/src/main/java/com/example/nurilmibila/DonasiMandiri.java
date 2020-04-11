package com.example.nurilmibila;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;


public class DonasiMandiri extends AppCompatActivity {

    EditText et_nominal;
    TextView tv_path;
    ImageButton btn_selectImage;
    ImageView iv_buktiDonasi;
    String nominal;
    Bitmap imgBukti;
    Button btn_submit;
    Button btn_next;

    //folder path for firebase storage
    String mStoragePath = "buktiDonasi/";
    //root database for firebase database
    String mDatabasePath = "Donasi";
    //creating uri
    Uri mFilePathUri;
    //creating storagereference and database reference
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    //progressdialog
    ProgressDialog mProgressDialog;
    //image request code for choosing image
    int IMAGE_REQUEST_CODE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donasi_mandiri);
        et_nominal = findViewById(R.id.et_inputNominal);
        tv_path = findViewById(R.id.tv_path);
        btn_selectImage = findViewById(R.id.btn_selectImage);
        btn_submit = findViewById(R.id.btn_submit);
        btn_next = findViewById(R.id.btn_next);
        iv_buktiDonasi = findViewById(R.id.iv_buktiDonasi);

        nominal = et_nominal.getText().toString();

        btn_selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select image"), IMAGE_REQUEST_CODE);
            }
        });
        //button click to upload data to firebase
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //call method to upload data to firebase
                uploadDataToFirebase();
            }
        });
        //assign firebasestorage instance to storage reference object
        mStorageReference = FirebaseStorage.getInstance().getReference();
        //assign firebasedatabase instance with root database name
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(mDatabasePath);
        //progress dialog
        mProgressDialog = new ProgressDialog(DonasiMandiri.this);

        /*
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(this, "This button is clickable", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),DonasiBerhasil.class);
                intent.putExtra("nominal", nominal);
                intent.putExtra("imgBukti", imgBukti);
            }
        });
         */
    }

    private void uploadDataToFirebase() {
        //check whether filepathuri is empty or not
        if (mFilePathUri != null) {
            //setting progress bar title
            mProgressDialog.setTitle("Image is uploading...");
            mProgressDialog.show();
            //create second storagereference
            StorageReference storageReference2nd = mStorageReference.child(mStoragePath
                    + System.currentTimeMillis()+ "." + mFilePathUri);
            //adding addonsuccesslistener to storagereference2nd
            storageReference2nd.putFile(mFilePathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //get nominal donasi
                    nominal = et_nominal.getText().toString().trim();
                    //hide progress dialogue
                    mProgressDialog.dismiss();
                    //show toast that image is uploaded
                    Toast.makeText(DonasiMandiri.this, "Upload image success", Toast.LENGTH_SHORT).show();
                    ImageUploadInfo imageUploadInfo = new ImageUploadInfo(nominal, taskSnapshot.toString());
                    //getting image upload id
                    String imageUploadId = mDatabaseReference.push().getKey();
                    mDatabaseReference.child(imageUploadId).setValue(imageUploadInfo);
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressDialog.dismiss();
                            //show error toast
                            Toast.makeText(DonasiMandiri.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mProgressDialog.setTitle("Image is uploading");
                        }
                    });
        }
        else {
            Toast.makeText(this, "Please select image or add image name", Toast.LENGTH_SHORT).show();
        }
    }

    //method to get the selected image file extension from file path uri
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        //returning the file extension
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK
                && data!=null && data.getData()!=null) {
            mFilePathUri = data.getData();
            try {
                //getting selected image path into path textview
                String path_photo_id = data.getData().getPath();
                tv_path.setText(path_photo_id);
                //getting selected image into bitmap and show it in imageview
                imgBukti = MediaStore.Images.Media.getBitmap(getContentResolver(),mFilePathUri);
                iv_buktiDonasi.setImageBitmap(imgBukti);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void next(View view) {
        //Toast.makeText(this, "This button is clickable", Toast.LENGTH_SHORT).show();
        try {
            nominal = et_nominal.getText().toString();
            Bitmap imgBukti = MediaStore.Images.Media.getBitmap(getContentResolver(),mFilePathUri);
            Intent intent = new Intent(getApplicationContext(),DonasiBerhasil.class);
            startActivity(intent);
        } catch (IOException e) {
            Toast.makeText(this,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
