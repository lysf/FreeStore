package com.snailgame.cjg.common.inter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.widget.TextView;
import android.widget.Toast;

import com.snailgame.cjg.InternalStorageContentProvider;
import com.snailgame.cjg.R;
import com.snailgame.cjg.common.ui.WebViewFragment;
import com.snailgame.cjg.common.widget.CommonWebView;
import com.snailgame.cjg.util.FileUtil;
import com.snailgame.cjg.util.IdentityHelper;
import com.snailgame.cjg.util.ToastUtils;
import com.snailgame.cjg.util.UploadUtil;
import com.snailgame.fastdev.image.BitmapUtil;
import com.snailgame.fastdev.util.LogUtils;

import java.io.File;
import java.util.ArrayList;

/**
 * webview上传图片 inteface
 * Created by TAJ_C on 2016/2/24.
 */
public class ImageUploadInterface {
    protected Activity mActivity;
    protected CommonWebView mWebView;


    private int width, height, size;
    private String savePath;
    private ArrayList<chooseImage> chooseImages = new ArrayList<>();
    private UploadAsyncTask uploadAsyncTask;
    private HandleImageAsyncTask handleImageAsyncTask;

    public final String localUrlHead = "http://localhost";
    public final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    private File mFileTemp;

    public ImageUploadInterface(CommonWebView mWebView, Activity mActivity) {
        this.mWebView = mWebView;
        this.mActivity = mActivity;
        savePath = mActivity.getCacheDir().toString() + "/snailwebimage/";
    }


    /**
     * 打开本地相册
     */
    @JavascriptInterface
    public void openImageChooser() {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                createPhotoSelectDialog();
            }
        });
    }

    /**
     * 上传图片
     */
    @JavascriptInterface
    public void uploadImage(final int id, final String url) {
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (uploadAsyncTask != null) {
                    uploadAsyncTask.cancel(true);
                }
                uploadAsyncTask = new UploadAsyncTask(id);
                uploadAsyncTask.execute(url);
            }
        });
    }


    /**
     * 设置图片大小分辨率
     */
    @JavascriptInterface
    public void initBitmapAttribute(int width, int height, int size) {
        this.width = width;
        this.height = height;
        if (size > 0) {
            this.size = size;
        }
    }


    @SuppressLint("NewApi")
    public void evaluateJavascript(String javascript) {
        if (mWebView != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mWebView.evaluateJavascript(javascript, null);
            } else {
                mWebView.loadUrl(javascript);
            }
        }
    }


    /**
     * 上传图片
     */
    class UploadAsyncTask extends AsyncTask<String, Integer, String> implements UploadUtil.OnUploadListener {
        int id;
        int progress = 0;
        String cookie;

        public UploadAsyncTask(int id) {
            super();
            this.id = id;
            CookieSyncManager.createInstance(mActivity);
            android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
            cookie = cookieManager.getCookie(WebViewFragment.getDomain(mWebView.getUrl()));
        }

        @Override
        protected String doInBackground(String... params) {
            if (isCancelled()) return null;
            if (params.length > 0 && id >= 0 && id < chooseImages.size()) {
                try {
                    chooseImage chooseImage = chooseImages.get(id);
                    if (chooseImage != null) {
                        String filepath = chooseImage.getPath();
                        String fileType = chooseImage.getFileType();
                        String url = params[0];
                        String result = UploadUtil
                                .uploadFile(new File(filepath), fileType, cookie,
                                        url + (url.contains("?") ? "&" : "?") +
                                                "nUserId="
                                                + IdentityHelper.getUid(mActivity)
                                                + "&cIdentity="
                                                + IdentityHelper.getIdentity(mActivity)
                                                + "&nAppId=" + IdentityHelper.getAppId(),
                                        this);
                        return result;
                    } else {
                        return null;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (!isCancelled() && !TextUtils.isEmpty(result)) {
                LogUtils.d(result);
                evaluateJavascript("javascript:OnUploadSuccess(" + id + ",'"
                        + result + "')");
            } else {
                evaluateJavascript("javascript:OnUploadFailed(" + id + ")");
                Toast.makeText(mActivity, mActivity.getString(R.string.webview_upload_failed), Toast.LENGTH_SHORT)
                        .show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (values.length > 0 && !isCancelled()) {
                if (progress != values[0]) {
                    progress = values[0];
                    evaluateJavascript("javascript:OnUploadProgress(" + id
                            + "," + progress + ")");
                }
            }
        }

        @Override
        public void onUploadProgress(int progress) {
            publishProgress(progress);
        }
    }

    /**
     * 调用openImageChooser 后，通过WebviewActivity 回调的方法 处理图片
     *
     * @param requestCode
     * @param data
     */
    public void afterChooseImage(int requestCode, Intent data) {
        try {
            String fileType;
            String path = null;
            if (requestCode == WebViewFragment.FILECHOOSER_RESULTCODE) {
                Uri result = data == null ? null : data.getData();
                if (data != null && !TextUtils.isEmpty(data.getType())) {
                    fileType = data.getType();
                } else {
                    fileType = "image/pjpeg";
                }

                if (result != null)
                    path = FileUtil.getPath(mActivity, result);
            } else {
                fileType = "image/pjpeg";
                if (mFileTemp != null)
                    path = mFileTemp.getPath();
            }


            if (!TextUtils.isEmpty(path)) {
                //处理图片 缩放和压缩
                if (handleImageAsyncTask != null) {
                    handleImageAsyncTask.cancel(true);
                }
                handleImageAsyncTask = new HandleImageAsyncTask();
                handleImageAsyncTask.execute(path, fileType);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        evaluateJavascript("javascript:OnImageChooser(" + -1 + ",'')");
    }


    /**
     * 处理图片 缩放和压缩
     */
    public class HandleImageAsyncTask extends AsyncTask<String, Void, BitmapUtil.ImageModel> {

        @Override
        protected BitmapUtil.ImageModel doInBackground(String... params) {
            if (params.length > 0) {
                String path = params[0];
                String fileType = params[1];
                BitmapUtil.ImageModel imageModel = BitmapUtil.getImageFromPath(path,
                        width, height, size, savePath, fileType);
                return imageModel;
            }
            return null;
        }

        protected void onPostExecute(BitmapUtil.ImageModel result) {
            super.onPostExecute(result);
            if (result != null && !TextUtils.isEmpty(result.getNewPath())) {
                chooseImage chooseImage = new chooseImage();
                chooseImage.setPath(result.getNewPath());
                chooseImage.setFileType(result.getFileType());
                chooseImages.add(chooseImage);
                int id = chooseImages.size() - 1;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    evaluateJavascript("javascript:OnImageChooser(" + id + ",'"
                            + localUrlHead + result.getNewPath() + "')");
                } else {
                    evaluateJavascript("javascript:OnImageChooser(" + id + ",'')");
                }
            } else {
                evaluateJavascript("javascript:OnImageChooser(" + -1 + ",'')");
            }
        }
    }

    public void cancelTask() {
        if (handleImageAsyncTask != null) {
            handleImageAsyncTask.cancel(true);
        }

        if (uploadAsyncTask != null) {
            uploadAsyncTask.cancel(true);
        }
    }

    /**
     * 显示图片来源
     */
    private void createPhotoSelectDialog() {
        final Dialog dialog = new Dialog(mActivity, R.style.Dialog);
        dialog.setContentView(R.layout.personal_photo_dialog);
        ((TextView) dialog.findViewById(R.id.txt_title)).setText(R.string.photo_chose_title);
        dialog.setCanceledOnTouchOutside(true);
        dialog.findViewById(R.id.btn_personal_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.btn_personal_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
                dialog.dismiss();
            }
        });
        dialog.show();

        createFileDir();
    }


    private void createFileDir() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File dir = new File(FileUtil.SD_IMAGE_PATH);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    ToastUtils.showMsg(mActivity, "create dir failed");
                    return;
                }
            }
            mFileTemp = new File(dir, TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(mActivity.getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
    }

    /**
     * 拍照
     */
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
            mActivity.startActivityForResult(intent, WebViewFragment.TAKEPICTURE_RESULTCODE);
        } catch (ActivityNotFoundException e) {
            LogUtils.d("cannot take picture" + e.getLocalizedMessage());
        }
    }


    /**
     * 相册
     */
    private void openGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            mActivity.startActivityForResult(
                    Intent.createChooser(intent, null),
                    WebViewFragment.FILECHOOSER_RESULTCODE);
        } catch (Exception e) {

        }
    }

    /**
     * 上传图片实体类
     */
    class chooseImage {
        String path; //路径
        String FileType; //类型

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getFileType() {
            return FileType;
        }

        public void setFileType(String fileType) {
            FileType = fileType;
        }
    }
}
