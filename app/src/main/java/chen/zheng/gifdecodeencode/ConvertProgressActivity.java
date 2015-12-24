package chen.zheng.gifdecodeencode;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import chen.zheng.gifdecodeencode.Utils.CommonUtils;
import chen.zheng.gifdecodeencode.Utils.LogUtils;
import chen.zheng.gifdecodeencode.decode.GifAction;
import chen.zheng.gifdecodeencode.decode.GifDecoder;

public class ConvertProgressActivity extends Activity implements GifAction{

    public static final String GIF_FILE_PATH = "file_path";
    public static final String GIF_SIZE = "size";
    public static final String GIF_QUALITY = "quality";
    public static final String GIF_FRAME_SAMPLE = "frame_sample";
    private String mFilePath;
    private float mSizeParam;
    private float mQualityParam;
    private int mFrameSampleParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_progress);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra(GIF_FILE_PATH);
        mSizeParam = intent.getFloatExtra(GIF_SIZE, (float) 0.7);
        mQualityParam = intent.getFloatExtra(GIF_QUALITY, (float) 0.7);
        mFrameSampleParam = intent.getIntExtra(GIF_FRAME_SAMPLE, 1);
    }


    private void convertGif(String filePath, float scale, float quality, int frame) {
        if (!TextUtils.isEmpty(filePath)) {
            File gifFile = new File(filePath);
            if (gifFile.exists()) {
                try {
                    FileInputStream inputStream = new FileInputStream(gifFile);
                    String parentPath = CommonUtils.parentPath(filePath);
                    String fileName = CommonUtils.fileName(filePath);
                    String outputFile = CommonUtils.createUniqueFileName(parentPath, fileName);
                    LogUtils.e("New file name:" + outputFile);
                    GifDecoder gifDecoder = new GifDecoder(inputStream, this, scale, quality, frame, outputFile);
                    gifDecoder.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void parseOk(boolean parseStatus, int frameIndex) {

    }
}
