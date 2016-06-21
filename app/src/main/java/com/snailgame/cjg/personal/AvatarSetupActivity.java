package com.snailgame.cjg.personal;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.snailgame.cjg.InternalStorageContentProvider;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.server.UserInfoGetService;
import com.snailgame.cjg.common.widget.PopupDialog;
import com.snailgame.cjg.event.AvatarChangeEvent;
import com.snailgame.cjg.global.AppConstants;
import com.snailgame.cjg.util.AccountUtil;
import com.snailgame.cjg.util.ComUtil;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.JsonUrl;
import com.snailgame.cjg.util.LoginSDKUtil;
import com.snailgame.cjg.util.MainThreadBus;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.UploadUtil;
import com.snailgame.fastdev.image.BitmapUtil;
import com.snailgame.fastdev.util.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import third.simplecropimage.CropImageActivity;

/**
 * Created by sunxy on 2014/12/31.
 */
public class AvatarSetupActivity extends Activity {
    private String path;
    public static final int REQUEST_PHOTO_HRAPH = 2;
    public static final int REQUESET_PHOTO_ZOOM = 3;
    public static final int REQUEST_CODE_CROP_IMAGE = 4;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private Dialog mPhotoDialog;
    private Bitmap photoBitmap;
    private static File mFileTemp;
    private PhotoTask photoTask;

    private boolean dialogOutsideDismiss = true;

    public static Intent newIntent(Context context) {
        return new Intent(context, AvatarSetupActivity.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        createPhotoSelectDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!LoginSDKUtil.isLogined(this)) {
            finish();
        }
        if (requestCode == REQUEST_PHOTO_HRAPH) {
            if (resultCode == RESULT_OK) {
                startCropImage();
            } else {
                finish();
            }
        }

        // 调用 garlly
        if (requestCode == REQUESET_PHOTO_ZOOM) {
            if (data == null) {
                finish();
                return;
            }

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();

                startCropImage();
            } catch (Exception e) {
                LogUtils.e("Error while creating temp file" + e.getLocalizedMessage());
            }
        }

        if (requestCode == REQUEST_CODE_CROP_IMAGE) {
            if (data == null) {
                finish();
                return;
            }

            String path = data.getStringExtra(CropImageActivity.IMAGE_PATH);
            if (path == null) {
                return;
            }
            if (photoTask != null)
                photoTask.cancel(true);
            photoTask = new PhotoTask(data.getType());
            photoTask.execute();
        }
    }


    /**
     * 上传头像
     */
    public class PhotoTask extends AsyncTask<Void, Void, Boolean> {
        String fileType;

        public PhotoTask(String fileType) {
            this.fileType = fileType;
            if (TextUtils.isEmpty(fileType)) {
                this.fileType = "image/pjpeg";
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            handleLoading(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            photoBitmap = BitmapFactory.decodeFile(mFileTemp.getPath());
            path = FileUtil.SD_IMAGE_PATH + "photo.jpg";
            BitmapUtil.saveMyBitmap(path, photoBitmap);
            File file = new File(path);

            String result = UploadUtil.uploadFile(file, fileType, null,
                    JsonUrl.getJsonUrl().JSON_URL_PHOTO_UPLOAD + AccountUtil.getLoginParams(), null);
            if (false == TextUtils.isEmpty(result)) {
                startService(UserInfoGetService.newIntent(AvatarSetupActivity.this, AppConstants.ACTION_UPDATE_USR_INFO));
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            handleLoading(true);
            if (aBoolean) {
                if (photoBitmap != null) {
                    MainThreadBus.getInstance().post(new AvatarChangeEvent(photoBitmap));

                }
                ToastUtils.showMsg(getApplication(), getString(R.string.personal_update_avatar_succ));
            } else {
                ToastUtils.showMsg(getApplication(), getString(R.string.personal_update_avatar_fail));
            }
            if (path != null) {
                File file = new File(path);
                if (file.exists())
                    file.delete();
            }
            finish();
        }
    }

    private PopupDialog loadingDialog;

    private void handleLoading(boolean off) {

        if (loadingDialog == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.common_loading, null);
            loadingDialog = new PopupDialog(this, R.style.PopupDialog);
            loadingDialog.setContentView(view);
            loadingDialog.setCanceledOnTouchOutside(false);
        }

        if (off) {
            loadingDialog.dismiss();
        } else {
            loadingDialog.show();
        }

    }


    private void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    private void startCropImage() {
        startActivityForResult(CropImageActivity.newIntent(this, mFileTemp.getPath(), true, 2, 2), REQUEST_CODE_CROP_IMAGE);
    }




    private void createPhotoSelectDialog() {
        mPhotoDialog = new Dialog(this, R.style.Dialog);
        mPhotoDialog.setContentView(R.layout.personal_photo_dialog);
        mPhotoDialog.setCanceledOnTouchOutside(true);
        mPhotoDialog.findViewById(R.id.btn_personal_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                mPhotoDialog.dismiss();
                dialogOutsideDismiss = false;
            }
        });

        mPhotoDialog.findViewById(R.id.btn_personal_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                mPhotoDialog.dismiss();
                dialogOutsideDismiss = false;
            }
        });
        mPhotoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (dialogOutsideDismiss)
                    finish();
            }
        });
        mPhotoDialog.show();

        createFileDir();
    }

    private void createFileDir() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dir = new File(FileUtil.SD_IMAGE_PATH);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    ToastUtils.showMsg(this, "create dir failed");
                    return;
                }
            }
            mFileTemp = new File(dir, TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            Uri mImageCaptureUri = null;
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                mImageCaptureUri = Uri.fromFile(mFileTemp);
            } else {
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, REQUEST_PHOTO_HRAPH);
        } catch (ActivityNotFoundException e) {
            LogUtils.d("cannot take picture" + e.getLocalizedMessage());
        }
    }

    private void openGallery() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        if (ComUtil.isIntentSafe(this, photoPickerIntent)) {
            startActivityForResult(photoPickerIntent, REQUESET_PHOTO_ZOOM);
        } else {
            ToastUtils.showMsgLong(this, getString(R.string.no_pic_app));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (photoTask != null)
            photoTask.cancel(true);
    }
}