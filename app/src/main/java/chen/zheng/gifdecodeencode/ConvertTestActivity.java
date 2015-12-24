package chen.zheng.gifdecodeencode;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import chen.zheng.gifdecodeencode.Utils.CommonUtils;
import chen.zheng.gifdecodeencode.Utils.LogUtils;
import chen.zheng.gifdecodeencode.Utils.ToastUtils;
import chen.zheng.gifdecodeencode.decode.GifAction;
import chen.zheng.gifdecodeencode.decode.GifDecoder;

public class ConvertTestActivity extends Activity implements GifAction {
    private static final int GIF_FILE_PATH = 1;
    private static final int REQUEST_CODE_GET_FILE = 11;
    private TextView mFilePathTv;
    private Button mChooseButton;
    private EditText mScaleParam;
    private EditText mQualityParam;
    private EditText mFrameParam;
    private Button mConvertButton;
    private Button mDecodeButton;
    private Button mEncodeButton;
    private Handler mHandler;
    private String mFilePath;

    private float mScale = 1l; //缩放比率，0.5意味着缩放宽高为原来的1/2
    private float mQuality = 1l; //图片质量，不改变图片大小的情况下，降低图片质量数值越低，质量越低
    private int mFrame = 1; //缩减帧数，值为2意味着2帧变一帧，删掉一帧，3意味着3帧变1帧，删掉2帧

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_test);
        mFilePathTv = (TextView) findViewById(R.id.file_path);
        mChooseButton = (Button) findViewById(R.id.choose_button);
        mScaleParam = (EditText) findViewById(R.id.scale_et);
        mQualityParam = (EditText) findViewById(R.id.quality_et);
        mFrameParam = (EditText) findViewById(R.id.frame_et);
        mConvertButton = (Button) findViewById(R.id.convert_button);
        mDecodeButton = (Button) findViewById(R.id.decode_button);
        mEncodeButton = (Button) findViewById(R.id.encode_button);

        mChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*选择GIF图片文件*/
                Intent intent = new Intent();
                intent.setType("image/gif");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_GET_FILE);
            }
        });
        mConvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String scale = mScaleParam.getText().toString();
                if (!TextUtils.isEmpty(scale)) {
                    float scaleF = Float.valueOf(scale);
                    if (scaleF >= 0 && scaleF <= 1) {
                        mScale = scaleF;
                    }
                }
                String qulity = mQualityParam.getText().toString();
                if (!TextUtils.isEmpty(qulity)) {
                    float qulityF = Float.valueOf(qulity);
                    if (qulityF >= 0 && qulityF <= 1) {
                        mQuality = qulityF;
                    }
                }
                String frame = mFrameParam.getText().toString();
                if (!TextUtils.isEmpty(frame)) {
                    int frameF = Integer.valueOf(frame);
                    if (frameF >= 1) {
                        mFrame = frameF;
                    }
                }
                convertGif(mFilePath, mScale, mQuality, mFrame);
            }
        });
        mDecodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*GIF解码*/
                decodeFile(mFilePath);
            }
        });
        mHandler = new MyHandler();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_GET_FILE:
                if (data != null && data.getData() != null) {
                    final Uri uri = data.getData();
                    LogUtils.d("uri:" + uri.toString());
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mFilePath = CommonUtils.getFilePathFromURI(ConvertTestActivity.this, uri);
                            if (!TextUtils.isEmpty(mFilePath)) {
                                LogUtils.d("Choosed File Path : " + mFilePath);
                                mHandler.obtainMessage(GIF_FILE_PATH, mFilePath).sendToTarget();
                            }
                        }
                    }).start();
                }
                break;
            default:
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }




    private void decodeFile(String filePath) {
        if (!TextUtils.isEmpty(filePath)) {
            File gifFile = new File(filePath);
            if (gifFile.exists()) {
                try {
                    FileInputStream inputStream = new FileInputStream(gifFile);
                    GifDecoder gifDecoder = new GifDecoder(inputStream, this);
                    gifDecoder.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void parseOk(boolean parseStatus, int frameIndex) {
        if (frameIndex == -1) {
            ToastUtils.showToast(this, parseStatus ? "Decode done" : "Decode Fail");
        }
        LogUtils.e("frame count:" + frameIndex);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GIF_FILE_PATH:
                    String filePaht = (String) msg.obj;
                    mFilePathTv.setText(filePaht);
                    break;
            }
        }
    }
}
