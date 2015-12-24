package chen.zheng.gifdecodeencode;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import chen.zheng.gifdecodeencode.Utils.CommonUtils;
import chen.zheng.gifdecodeencode.decode.GifDecoder;
import chen.zheng.gifdecodeencode.decode.GifParseCallback;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class ConvertSettingActivity extends Activity implements GifParseCallback, SeekBar.OnSeekBarChangeListener {

    public static final String GIF_FILE_PATH = "gif_file_path";

    private static final String GIF_INFO_DURATION = "duration";
    private static final String GIF_INFO_WIDTH = "width";
    private static final String GIF_INFO_HEIGHT = "height";
    private static final String GIF_INFO_FRAMECOUNT = "framecount";
    private static final int WIDTH_HEIGHT_TAG = 0;
    private static final int QUALITY_TAG = 1;
    private static final int FRAME_TAG = 2;
    private static final int PREFER_WIDTH_HEIGHT = 80;
    private static final int PREFER_QUALITY = 70;
    private static final int PREFER_FRAME_SAMPLE = 20;

    private String mFilePath;
    private Handler mHandler = new SettingHandler();

    private GifImageView mLocalGif;

    private TextView mLocalGifPath;
    private TextView mLocalGifSize;
    private TextView mLocalGifDuration;
    private TextView mLocalGifWidthHeight;
    private TextView mLocalGifFrameCount;

    private TextView mTargetImageWidthHeight;
    private TextView mTargetImageQuality;
    private TextView mTargetImageFrame;

    private SeekBar mTargetImageWidthHeightSeekBar;
    private SeekBar mTargetImageQualitySeekBar;
    private SeekBar mTargetImageFrameSeekBar;

    private Button mConvertButton;

    private float mWidthHeightParam = (float)PREFER_WIDTH_HEIGHT/100;
    private float mQualityParam = (float)PREFER_QUALITY/100;
    private int mFrameSampleParam = PREFER_FRAME_SAMPLE/10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_setting);
        Intent intent = getIntent();
        mFilePath = intent.getStringExtra(GIF_FILE_PATH);
        setupViews();
        initViews();
    }

    private void initViews() {
        mTargetImageWidthHeightSeekBar.setTag(WIDTH_HEIGHT_TAG);
        mTargetImageQualitySeekBar.setTag(QUALITY_TAG);
        mTargetImageFrameSeekBar.setTag(FRAME_TAG);

        mTargetImageWidthHeightSeekBar.setOnSeekBarChangeListener(this);
        mTargetImageQualitySeekBar.setOnSeekBarChangeListener(this);
        mTargetImageFrameSeekBar.setOnSeekBarChangeListener(this);

        mTargetImageWidthHeightSeekBar.setProgress(PREFER_WIDTH_HEIGHT);
        mTargetImageQualitySeekBar.setProgress(PREFER_QUALITY);
        mTargetImageFrameSeekBar.setProgress(PREFER_FRAME_SAMPLE);
        mConvertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConvertSettingActivity.this, ConvertProgressActivity.class);
                intent.putExtra(ConvertProgressActivity.GIF_FILE_PATH, mFilePath);
                intent.putExtra(ConvertProgressActivity.GIF_SIZE, mWidthHeightParam);
                intent.putExtra(ConvertProgressActivity.GIF_QUALITY, mQualityParam);
                intent.putExtra(ConvertProgressActivity.GIF_FRAME_SAMPLE, mFrameSampleParam);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGifFilePath(mFilePath);
        analyseGif(mFilePath);
    }

    private void setupViews() {
        mLocalGif = (GifImageView) findViewById(R.id.local_gif);
        mLocalGifPath = (TextView) findViewById(R.id.file_path);
        mLocalGifSize = (TextView) findViewById(R.id.file_size);
        mLocalGifDuration = (TextView) findViewById(R.id.image_time);
        mLocalGifWidthHeight = (TextView) findViewById(R.id.image_size);
        mLocalGifFrameCount = (TextView) findViewById(R.id.image_frame_count);

        mTargetImageWidthHeight = (TextView) findViewById(R.id.image_size_tv);
        mTargetImageQuality = (TextView) findViewById(R.id.image_quality_tv);
        mTargetImageFrame = (TextView) findViewById(R.id.image_frame_tv);

        mTargetImageWidthHeightSeekBar = (SeekBar) findViewById(R.id.image_size_bar);
        mTargetImageQualitySeekBar = (SeekBar) findViewById(R.id.image_quality_bar);
        mTargetImageFrameSeekBar = (SeekBar) findViewById(R.id.image_frame_bar);
        mConvertButton = (Button) findViewById(R.id.gif_convert_button);
    }

    private void analyseGif(String filePath) {
        mLocalGifPath.setText(mFilePath);
        String fileSize = CommonUtils.getFileSize(this, filePath);
        mLocalGifSize.setText(fileSize);
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


    private void setGifFilePath(String path) {
        //file path
        if (!TextUtils.isEmpty(path)) {
            try {
                final GifDrawable gifDrawable = new GifDrawable(path);
                gifDrawable.addAnimationListener(new AnimationListener() {
                    @Override
                    public void onAnimationCompleted(int loopNumber) {
                        gifDrawable.reset();
                    }
                });
                mLocalGif.setImageDrawable(gifDrawable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onParseFinish(boolean parseStatus, final int duration, final int width, final int height, final int frameCount) {
        if (parseStatus) {
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putInt(GIF_INFO_DURATION, duration);
            bundle.putInt(GIF_INFO_WIDTH, width);
            bundle.putInt(GIF_INFO_HEIGHT, height);
            bundle.putInt(GIF_INFO_FRAMECOUNT, frameCount);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Object tag = seekBar.getTag();
        if (tag != null) {
            int seekBarTag = (int) tag;
            switch (seekBarTag) {
                case WIDTH_HEIGHT_TAG:
                    mWidthHeightParam = (float) progress / 100;
                    mTargetImageWidthHeight.setText(this.getString(R.string.image_size)+mWidthHeightParam+"%");
                    break;
                case QUALITY_TAG:
                    mQualityParam = (float) progress / 100;
                    mTargetImageQuality.setText(this.getString(R.string.image_quality)+mQualityParam+"%");
                    break;
                case FRAME_TAG:
                    if(progress<10) {
                        mFrameSampleParam=1;
                    } else {
                        mFrameSampleParam = progress/10;
                    }
                    mTargetImageFrame.setText(this.getString(R.string.image_frame) + mFrameSampleParam + "");
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private class SettingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int duration = bundle.getInt(GIF_INFO_DURATION);
            int width = bundle.getInt(GIF_INFO_WIDTH);
            int height = bundle.getInt(GIF_INFO_HEIGHT);
            int frameCount = bundle.getInt(GIF_INFO_FRAMECOUNT);

            float durationInSecond = (float) duration / 1000;
            mLocalGifDuration.setText(String.valueOf(durationInSecond));
            mLocalGifWidthHeight.setText(String.valueOf(width) + " X " + String.valueOf(height));
            mLocalGifFrameCount.setText(String.valueOf(frameCount));
        }
    }
}
