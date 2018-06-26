package integration.google.sunder.cropimageinandroidusinglibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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

public class MainActivity extends AppCompatActivity {

    ImageButton imageButton;
    Uri imageUri;

    /**
     * Persist URI image to crop URI if specific permissions are required
     */
    private Uri mCropImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageButton = (ImageButton) findViewById(R.id.quick_start_cropped_image);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectImageClick(v);
            }
        });
    }


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
//            Log.i("Image_details222",""+data.getData().toString());
            /*Path path = Paths.get(data.getData().toString());
            try {
                byte[] data1 = Files.readAllBytes(path);
                Log.i("Image_details22",""+data1.length);

            String encodedImageonCapture = Base64.encodeToString(data1, Base64.DEFAULT);
            Log.i("Image_details2",""+encodedImageonCapture);
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            /*File file = new File(data.getData().toString());
           /// Uri.fromFile(file);
            int size = (int) file.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

           String encodedImageonCapture = Base64.encodeToString(bytes, Base64.DEFAULT);
            Log.i("Image_details2",""+encodedImageonCapture);
*/
            // For API >= 23 we need to check specifically that we have permissions to read external storage.
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                // request permissions and handle the result in onRequestPermissionsResult()
                mCropImageUri = imageUri;
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            } else {
                // no permissions required or already grunted, can start crop image activity
                startCropImageActivity(imageUri);
            }
        }

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ((ImageButton) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                Toast.makeText(this, "Cropping successful, Sample: " + result.getUri(), Toast.LENGTH_LONG).show();

                try {
                    URL url=new URL(result.getUri().toString());
                    try {
                        Bitmap bitmap= BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        byte[] b1 = bytes.toByteArray();
                        String encodedImageonCapture = Base64.encodeToString(b1, Base64.DEFAULT);
                        Log.i("Image_details",""+encodedImageonCapture);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               /* try {
                    InputStream iStream =   getContentResolver().openInputStream(imageUri);

                    byte[] inputData = new byte[0];
                    try {
                        inputData = getBytes(iStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String encodedImageonCapture = Base64.encodeToString(inputData, Base64.DEFAULT);
                    Log.i("Image_details1",""+result.toString());


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (mCropImageUri != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // required permissions granted, start crop image activity
            startCropImageActivity(mCropImageUri);
        } else {
            Toast.makeText(this, "Cancelling, required permissions are not granted", Toast.LENGTH_LONG).show();
        }
    }
  /*  public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }*/
    /**
     * Start crop image activity for the given image.
     */
    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }
}
