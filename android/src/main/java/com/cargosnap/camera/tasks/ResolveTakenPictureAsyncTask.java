package com.cargosnap.camera.tasks;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.exifinterface.media.ExifInterface;
import android.util.Base64;

import com.cargosnap.camera.CSCameraViewHelper;
import com.cargosnap.camera.utils.RNFileUtils;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

public class ResolveTakenPictureAsyncTask extends AsyncTask<Void, Void, WritableMap> {
    private static final String ERROR_TAG = "E_TAKING_PICTURE_FAILED";
    private Promise mPromise;
    private Bitmap mBitmap;
    private byte[] mImageData;
    private ReadableMap mOptions;
    private File mCacheDirectory;
    private int mDeviceOrientation;
    private PictureSavedDelegate mPictureSavedDelegate;

    public ResolveTakenPictureAsyncTask(byte[] imageData, Promise promise, ReadableMap options, File cacheDirectory, int deviceOrientation, PictureSavedDelegate delegate) {
        mPromise = promise;
        mOptions = options;
        mImageData = imageData;
        mCacheDirectory = cacheDirectory;
        mDeviceOrientation = deviceOrientation;
        mPictureSavedDelegate = delegate;
    }

    public ResolveTakenPictureAsyncTask(Bitmap bitmap, Promise promise, ReadableMap options, File cacheDirectory, int deviceOrientation, PictureSavedDelegate delegate) {
        mPromise = promise;
        mOptions = options;
        mBitmap = bitmap;
        mCacheDirectory = cacheDirectory;
        mDeviceOrientation = deviceOrientation;
        mPictureSavedDelegate = delegate;
    }

    private int getQuality() {
        return (int) (mOptions.getDouble("quality") * 100);
    }

    @Override
    protected WritableMap doInBackground(Void... voids) {
        WritableMap response = Arguments.createMap();

        response.putInt("deviceOrientation", mDeviceOrientation);
        response.putInt("pictureOrientation", mOptions.hasKey("orientation") ? mOptions.getInt("orientation") : mDeviceOrientation);

        try{
            // get response dimensions right from the bitmap if we have it
            response.putInt("width", mBitmap.getWidth());
            response.putInt("height", mBitmap.getHeight());

            // Cache compressed image in imageStream
            ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, getQuality(), imageStream);

            String filePath = writeStreamToFile(imageStream);

            File imageFile = new File(filePath);
            String fileUri = Uri.fromFile(imageFile).toString();
            response.putString("uri", fileUri);

            // Write base64-encoded image to the response if requested
            if (mOptions.hasKey("base64") && mOptions.getBoolean("base64")) {
                response.putString("base64", Base64.encodeToString(imageStream.toByteArray(), Base64.NO_WRAP));
            }

            return response;
        }
        catch (Resources.NotFoundException e) {
            mPromise.reject(ERROR_TAG, "Documents directory of the app could not be found.", e);
            e.printStackTrace();
        }
        catch (IOException e) {
            mPromise.reject(ERROR_TAG, "An unknown I/O exception has occurred.", e);
            e.printStackTrace();
        }

        return null;
    }

    private Bitmap rotateBitmap(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private Bitmap resizeBitmap(Bitmap bm, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleRatio = (float) newWidth / (float) width;

        return Bitmap.createScaledBitmap(bm, newWidth, (int) (height * scaleRatio), true);
    }

    private Bitmap flipHorizontally(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    // Get rotation degrees from Exif orientation enum

    private int getImageRotation(int orientation) {
        int rotationDegrees = 0;
        switch (orientation) {
        case ExifInterface.ORIENTATION_ROTATE_90:
            rotationDegrees = 90;
            break;
        case ExifInterface.ORIENTATION_ROTATE_180:
            rotationDegrees = 180;
            break;
        case ExifInterface.ORIENTATION_ROTATE_270:
            rotationDegrees = 270;
            break;
        }
        return rotationDegrees;
    }

    private String writeStreamToFile(ByteArrayOutputStream inputStream) throws IOException {
        String outputPath = null;
        IOException exception = null;
        FileOutputStream outputStream = null;

        try {
            outputPath = RNFileUtils.getOutputFilePath(mCacheDirectory, ".jpg");
            outputStream = new FileOutputStream(outputPath);
            inputStream.writeTo(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
            exception = e;
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (exception != null) {
            throw exception;
        }

        return outputPath;
    }

    @Override
    protected void onPostExecute(WritableMap response) {
        super.onPostExecute(response);

        // If the response is not null everything went well and we can resolve the promise.
        if (response != null) {
            if (mOptions.hasKey("fastMode") && mOptions.getBoolean("fastMode")) {
                WritableMap wrapper = Arguments.createMap();
                wrapper.putInt("id", mOptions.getInt("id"));
                wrapper.putMap("data", response);
                mPictureSavedDelegate.onPictureSaved(wrapper);
            } else {
                mPromise.resolve(response);
            }
        }
    }

}
