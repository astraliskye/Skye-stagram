package com.example.skye_stagram.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.skye_stagram.BitmapScaler;
import com.example.skye_stagram.ImageResizer;
import com.example.skye_stagram.R;
import com.example.skye_stagram.activities.LoginActivity;
import com.example.skye_stagram.activities.MainActivity;
import com.example.skye_stagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ComposeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment
{
    public static final String TAG = ComposeFragment.class.getSimpleName();
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Button _btnCaptureImage;
    ImageView _ivPostImage;
    ImageView _ivRotateCCL;
    ImageView _ivRotateCL;
    EditText _etDescription;
    Button _btnSubmit;
    ProgressBar _pbLoading;
    File _photoFile;
    Bitmap _currentBitmap;

    public String photoFileName = "photo.jpg";

    public ComposeFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2)
    {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    // Called when the fragment should create its own View object hierarchy
    // either dynamically or via XML layout inflation
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    // Called soon after onCreateView
    // View setup should occur here. View lookups and attaching view listeners
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        _btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        _ivPostImage = view.findViewById(R.id.ivPostImage);
        _ivRotateCCL = view.findViewById(R.id.ivRotateCCL);
        _ivRotateCL = view.findViewById(R.id.ivRotateCL);
        _etDescription = view.findViewById(R.id.etDescription);
        _btnSubmit = view.findViewById(R.id.btnSubmit);
        _pbLoading = view.findViewById(R.id.pbLoading);

        // Add ClickListeners
        _ivRotateCCL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BitmapDrawable bmpDraw = (BitmapDrawable)_ivPostImage.getDrawable();
                _currentBitmap = bmpDraw.getBitmap();
                _currentBitmap = rotateImage(_currentBitmap, -90);

                _ivPostImage.setImageBitmap(_currentBitmap);
            }
        });

        _ivRotateCL.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                BitmapDrawable bmpDraw = (BitmapDrawable)_ivPostImage.getDrawable();
                _currentBitmap = bmpDraw.getBitmap();
                _currentBitmap = rotateImage(_currentBitmap, 90);

                _ivPostImage.setImageBitmap(_currentBitmap);
            }
        });

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
                    Toast.makeText(getContext(), "Cannot submit an empty post", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();

                try (FileOutputStream out = new FileOutputStream(_photoFile.getAbsolutePath())) {
                    _currentBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                    e.printStackTrace();
                }

                savePost(description, currentUser, _photoFile);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                // photo is on disk at this point
                Bitmap takenImage = BitmapFactory.decodeFile(_photoFile.getAbsolutePath());
                _currentBitmap = BitmapScaler.scaleToFitWidth(takenImage, 417);

                // load image into preview
                _ivPostImage.setImageBitmap(_currentBitmap);
            }
            else
            {
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
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
                    Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Post submitted successfully");
                Toast.makeText(getContext(), "Post submitted successfully", Toast.LENGTH_SHORT).show();

                // Reset form
                _etDescription.setText(null);
                _ivPostImage.setImageResource(0);

                _pbLoading.setVisibility(ProgressBar.INVISIBLE);
            }
        });
    }

    private void launchCamera()
    {
        // Create an implicit intent for some camera app on the system to launch
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create file reference
        _photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider for API 24+
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.example.fileprovider", _photoFile);
        // send content provider to the app handling the intent
        i.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // check that an app is handling the intent, or else this app will crash
        if (i.resolveActivity(getContext().getPackageManager()) != null)
        {
            startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName)
    {
        // get safe storage directory for photos
        // user getExternalFilesDir so we don't need extra runtime permissions
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // create storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs())
        {
            Log.d(TAG, "failed to create directory");
        }

        // return file target for the photo based on file name
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
