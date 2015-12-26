package chen.zheng.gifdecodeencode;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.update.UmengUpdateAgent;

import java.io.IOException;
import java.util.HashMap;

import chen.zheng.gifdecodeencode.Utils.CommonUtils;
import chen.zheng.gifdecodeencode.Utils.LogUtils;
import chen.zheng.gifdecodeencode.Utils.ToastUtils;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE_GET_FILE = 11;
    private static final int REQUEST_CONVERT_GIF = 12;
    private static final String CONVERT_RESULT_SIZE = "convert_result_size";
    private static final java.lang.String CONVERT_RESULT = "convert_result";


    private GifImageView mBigGif;
    private GifImageView mSmallGif;
    private TextView mConvertResult;
    private TextView mConvertSize;
    private Button mReconvert;

    private Button mGifChooseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBigGif = (GifImageView) findViewById(R.id.big_gif_image);
        mSmallGif = (GifImageView) findViewById(R.id.small_gif_image);
        mConvertResult = (TextView) findViewById(R.id.convert_result);
        mConvertSize = (TextView) findViewById(R.id.convert_size);
        mReconvert = (Button) findViewById(R.id.reconvert_button);

        mGifChooseButton = (Button) findViewById(R.id.gif_choose_button);
        mGifChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceToken();
                  /*选择GIF图片文件*/
                Intent intent = new Intent();
                intent.setType("image/gif");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_GET_FILE);
            }
        });

        setGifImage(null, null);

        /*开启友盟推送*/
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.enable();
        //打开自动更新
        UmengUpdateAgent.update(this);
    }

    private void getDeviceToken(){
        String device_token = UmengRegistrar.getRegistrationId(this);
        LogUtils.e("device_token: " +device_token );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_FILE:
                if (data != null && data.getData() != null) {
                    final Uri uri = data.getData();
                    LogUtils.d("uri:" + uri.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String filePath = CommonUtils.getFilePathFromURI(MainActivity.this, uri);
                            String fileName = CommonUtils.fileName(filePath).toLowerCase();
                            if (!TextUtils.isEmpty(filePath) && fileName.endsWith(".gif")) {
                                LogUtils.d("Choosed File Path : " + filePath);
                                //Start convert activity
                                Intent intent = new Intent(MainActivity.this, ConvertSettingActivity.class);
                                intent.putExtra(ConvertSettingActivity.GIF_FILE_PATH, filePath);
                                startActivityForResult(intent, REQUEST_CONVERT_GIF);
                            } else {
                                ToastUtils.showToast(MainActivity.this, MainActivity.this.getResources().getString(R.string.choose_gif_toast));
                            }
                        }
                    }).start();
                }
                break;
            case REQUEST_CONVERT_GIF:
                if (data != null && resultCode == RESULT_OK) { //转换完成，显示结果
                    final String newPath = data.getStringExtra(ConvertSettingActivity.NEW_PATH);
                    final String oldPath = data.getStringExtra(ConvertSettingActivity.OLD_PATH);

                    uploadSizeEvent(CommonUtils.getFileSize(this, newPath));
                    setGifImage(oldPath, newPath);
                    mConvertResult.setVisibility(View.VISIBLE);
                    mConvertSize.setVisibility(View.VISIBLE);
                    mConvertResult.setText(getString(R.string.saved_path) + newPath);
                    mConvertSize.setText(getString(R.string.saved_size) + CommonUtils.getReadableFileSize(this, newPath));
                    mReconvert.setVisibility(View.VISIBLE);
                    mReconvert.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CommonUtils.deleteFile(newPath);
                            //Start convert activity
                            Intent intent = new Intent(MainActivity.this, ConvertSettingActivity.class);
                            intent.putExtra(ConvertSettingActivity.GIF_FILE_PATH, oldPath);
                            startActivityForResult(intent, REQUEST_CONVERT_GIF);
                        }
                    });
                }
                break;
            default:
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadSizeEvent(long fileSize) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(CONVERT_RESULT_SIZE, String.valueOf(fileSize));
        MobclickAgent.onEvent(this, CONVERT_RESULT, map);
    }

    private void setGifImage(String oldPath, String newPath) {
        GifDrawable oldGifDrawable = null;
        GifDrawable newGifDrawable = null;
        if (!TextUtils.isEmpty(oldPath) && !TextUtils.isEmpty(newPath)) {
            /*返回结果*/
            try {
                oldGifDrawable = new GifDrawable(oldPath);
                newGifDrawable = new GifDrawable(newPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            /*默认图片*/
            try {
                oldGifDrawable = new GifDrawable(getResources(), R.drawable.big_gif);
                newGifDrawable = new GifDrawable(getResources(), R.drawable.small_gif);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final GifDrawable finalOldGifDrawable = oldGifDrawable;
        final GifDrawable finalNewGifDrawable = newGifDrawable;
        if (finalOldGifDrawable != null) {
            finalOldGifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    finalOldGifDrawable.reset();

                }
            });
        }
        if (finalNewGifDrawable != null) {
            finalNewGifDrawable.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    finalNewGifDrawable.reset();
                }
            });
        }
        mBigGif.setImageDrawable(finalOldGifDrawable);
        mSmallGif.setImageDrawable(finalNewGifDrawable);
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
