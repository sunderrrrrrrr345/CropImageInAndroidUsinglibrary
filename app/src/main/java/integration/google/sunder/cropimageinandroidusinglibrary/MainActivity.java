package integration.google.sunder.cropimageinandroidusinglibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity {

    ImageButton imageButton;
    Uri imageUri;

    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    private Uri mCropImageUri;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButton = findViewById(R.id.quick_start_cropped_image);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                        onSelectImageClick(v);



            }
        });
    }

  /*  private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
*/

    /**
     * Start pick image activity with chooser.
     */
    public void onSelectImageClick(View view) {
        CropImage.startPickImageActivity(this);
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of pick image chooser
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            imageUri = CropImage.getPickImageResultUri(this, data);
            Log.i("Image_details11111111111111111111111", "" + data.getData());
            //convert file data to bystes
       /*  */

            String stringlength = imageUri.toString();
            if (stringlength.lastIndexOf(".") != -1 && stringlength.lastIndexOf(".") != 0) {
                switch (stringlength.substring(stringlength.lastIndexOf("."))) {
                    case ".apk":
                        Log.i("Image_details22222", "sd0");
                        Toast.makeText(this, "Upload file", Toast.LENGTH_SHORT).show();
                        Bytes_File(data.getData());
                        break;
                    case ".pdf":
                        Log.i("Image_details22222", "sd1");
                        Toast.makeText(this, "Upload file", Toast.LENGTH_SHORT).show();
                        Bytes_File(data.getData());
                        break;
                    case ".doc":
                        Log.i("Image_details22222", "sd2");
                        Toast.makeText(this, "Upload file", Toast.LENGTH_SHORT).show();
                        Bytes_File(data.getData());
                        break;
                    case ".txt":
                        Log.i("Image_details22222", "sd3");
                        Toast.makeText(this, "Upload file", Toast.LENGTH_SHORT).show();
                        Bytes_File(data.getData());
                        break;
                    case "null":
                        Log.i("Image_details22222", "sd5");
                        Toast.makeText(this, "Upload file", Toast.LENGTH_SHORT).show();
                        Bytes_File(data.getData());
                        break;
                    default:
                        Log.i("Image_details22222", "sd4");
                        // For API >= 23 we need to check specifically that we have permissions to read external storage.
                        if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                            // request permissions and handle the result in onRequestPermissionsResult()
                            mCropImageUri = imageUri;
                            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                        } else {
                            // no permissions required or already grunted, can start crop image activity
                            startCropImageActivity(imageUri);
                        }
                        break;
                }
            } else {
                Toast.makeText(MainActivity.this, "No length", Toast.LENGTH_SHORT).show();
                if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                    // request permissions and handle the result in onRequestPermissionsResult()
                    mCropImageUri = imageUri;
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                } else {
                    // no permissions required or already grunted, can start crop image activity
                    startCropImageActivity(imageUri);
                }
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ((ImageButton) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                Toast.makeText(this, "Cropping successful, Sample: " + result.getUri(), Toast.LENGTH_LONG).show();

                try {
                    URL url = new URL(result.getUri().toString());
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        byte[] b1 = bytes.toByteArray();
                        String encodedImageonCapture = Base64.encodeToString(b1, Base64.DEFAULT);
                        Log.i("Image_details", "" + encodedImageonCapture);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }


     private void Bytes_File(Uri data1) {
         //Uri data1 = data.getData();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         FileInputStream fis;
         try {
             assert data1 != null;
             fis = new FileInputStream(new File(data1.getPath()));
             byte[] buf = new byte[1024];
             int n;
             while (-1 != (n = fis.read(buf)))
                 baos.write(buf, 0, n);
         } catch (Exception e) {
             e.printStackTrace();
         }
         byte[] bbytes = baos.toByteArray();
         String encodedImageonCapture = Base64.encodeToString(bbytes, Base64.DEFAULT);
         Log.i("Image_details11111111111111111111111", "" + encodedImageonCapture);
     }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

            if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // required permissions granted, start crop image activity
                startCropImageActivity(mCropImageUri);
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
            }

    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
}
