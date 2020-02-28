package com.example.skye_stagram.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.skye_stagram.BitmapScaler;
import com.example.skye_stagram.R;
import com.example.skye_stagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    Button _btnCaptureImage;
    ImageView _ivPostImage;
    EditText _etDescription;
    Button _btnSubmit;
    Button _btnLogout;
    File _photoFile;
    ProgressBar _pbLoading;

    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Link all the views
        _btnCaptureImage = findViewById(R.id.btnCaptureImage);
        _ivPostImage = findViewById(R.id.ivPostImage);
        _etDescription = findViewById(R.id.etDescription);
        _btnSubmit = findViewById(R.id.btnSubmit);
        _btnLogout = findViewById(R.id.btnLogout);
        _pbLoading = findViewById(R.id.pbLoading);

        // Add ClickListeners
        _btnCaptureImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                launchCamera();
            }
        });
        _btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String description = _etDescription.getText().toString();
                if (description.isEmpty() && (_photoFile == null || _ivPostImage.getDrawable() == null))
                {
                    Toast.makeText(MainActivity.this, "Cannot submit an empty post", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();

                savePost(description, currentUser, _photoFile);
            }
        });
        _btnLogout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ParseUser.logOut();
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        
        // queryPosts();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                // photo is on disk at this point
                Bitmap takenImage = BitmapFactory.decodeFile(_photoFile.getAbsolutePath());

                Bitmap resizedBitmap = BitmapScaler.scaleToFitWidth(takenImage, 417);

                // load image into preview
                _ivPostImage.setImageBitmap(resizedBitmap);
            }
            else
            {
                Toast.makeText(MainActivity.this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchCamera()
    {
        // Create an implicit intent for some camera app on the system to launch
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create file reference
        _photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider for API 24+
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.example.fileprovider", _photoFile);
        // send content provider to the app handling the intent
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // check that an app is handling the intent, or else this app will crash
        if (i.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName)
    {
        // get safe storage directory for photos
        // user getExternalFilesDir so we don't need extra runtime permissions
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // create storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs())
        {
            Log.d(TAG, "failed to create directory");
        }

        // return file target for the photo based on file name
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void savePost(String description, ParseUser currentUser, File photoFile)
    {
        _pbLoading.setVisibility(ProgressBar.VISIBLE);

        Post post = new Post();
        post.setDescription(description);
        if (photoFile != null)
        {
            post.setImage(new ParseFile(photoFile));
        }
        post.setUser(currentUser);

        post.saveInBackground(new SaveCallback()
        {
            @Override
            public void done(ParseException e)
            {
                if (e != null)
                {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(MainActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Post submitted successfully");
                Toast.makeText(MainActivity.this, "Post submitted successfully", Toast.LENGTH_SHORT).show();

                // Reset form
                _etDescription.setText(null);
                _ivPostImage.setImageResource(0);

                _pbLoading.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    private void queryPosts()
    {
        // Specify which class is being retrieved
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        // .getInBackground() for a specific post
        query.findInBackground(new FindCallback<Post>()
        {
            @Override
            public void done(List<Post> objects, ParseException e)
            {
                if (e != null)
                {
                    Log.e(TAG, "Issue with getting posts", e);
                    Toast.makeText(MainActivity.this, "Unable to load posts.", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (Post post : objects)
                {
                    Log.i(TAG, "Post: " + post.getDescription() + ", user: " + post.getUser().getUsername());
                }
            }
        });
    }
}
