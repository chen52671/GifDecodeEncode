package chen.zheng.gifdecodeencode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import chen.zheng.gifdecodeencode.Utils.CommonUtils;
import chen.zheng.gifdecodeencode.Utils.LogUtils;
import chen.zheng.gifdecodeencode.Utils.ToastUtils;
import chen.zheng.gifdecodeencode.decode.GifAction;
import chen.zheng.gifdecodeencode.decode.GifDecoder;

public class ConvertProgressActivity extends Activity implements GifAction {

    public static final String GIF_FILE_PATH = "file_path";
    public static final String GIF_SIZE = "size";
    public static final String GIF_QUALITY = "quality";
    public static final String GIF_FRAME_SAMPLE = "frame_sample";
    public static final String GIF_TOTAL_FRAME_COUNT = "frame_count";
    private String mFilePath;
    private float mSizeParam;
    private float mQualityParam;
    private int mFrameSampleParam;

    private ImageView mConvertingImage;
    private ProgressBar mConvertingProgress;
    private Button mCancelButton;
    private GifDecoder mGifDecoder;
    private int mFrameCount;
    private String mOutputFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_progress);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra(GIF_FILE_PATH);
        mSizeParam = intent.getFloatExtra(GIF_SIZE, (float) 0.7);
        mQualityParam = intent.getFloatExtra(GIF_QUALITY, (float) 0.7);
        mFrameSampleParam = intent.getIntExtra(GIF_FRAME_SAMPLE, 1);
        mFrameCount = intent.getIntExtra(GIF_TOTAL_FRAME_COUNT, 20);

        mConvertingImage = (ImageView) findViewById(R.id.converting_image);
        mConvertingProgress = (ProgressBar) findViewById(R.id.convert_progressBar);
        mCancelButton = (Button) findViewById(R.id.cancel_button);
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        convertGif(mFilePath, mSizeParam, mQualityParam, mFrameSampleParam);
    }


    private void convertGif(String filePath, float scale, float quality, int frame) {
        if (!TextUtils.isEmpty(filePath)) {
            File gifFile = new File(filePath);
            if (gifFile.exists()) {
                try {
                    FileInputStream inputStream = new FileInputStream(gifFile);
                    String parentPath = CommonUtils.parentPath(filePath);
                    String fileName = CommonUtils.fileName(filePath);
                    mOutputFilePath = CommonUtils.createUniqueFileName(parentPath, fileName);
                    LogUtils.e("New file name:" + mOutputFilePath);
                    mGifDecoder = new GifDecoder(inputStream, this, scale, quality, frame, mOutputFilePath);
                    mGifDecoder.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void parseOk(boolean parseStatus, final int frameIndex, final Bitmap bitmap) {
        if (parseStatus && frameIndex == -1) {
            ToastUtils.showToast(this, parseStatus ? getString(R.string.decode_done) : getString(R.string.decode_fail));
        }
        LogUtils.e("frame count:" + frameIndex);
        if (parseStatus && mGifDecoder != null) {
            if (frameIndex == -1) {
                Intent intent = new Intent();
                intent.putExtra(ConvertSettingActivity.NEW_PATH, mOutputFilePath);
                setResult(RESULT_OK, intent);
                finish();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConvertingProgress.setProgress((frameIndex * 100) / mFrameCount);
                        mConvertingImage.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mGifDecoder != null && mGifDecoder.isAlive()) {
            mGifDecoder.interrupt();
        }
        super.onBackPressed();
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
