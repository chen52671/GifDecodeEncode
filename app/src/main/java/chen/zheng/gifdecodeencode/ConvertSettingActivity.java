package chen.zheng.gifdecodeencode;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

public class ConvertSettingActivity extends AppCompatActivity {

    public static final String GIF_FILE_PATH = "gif_file_path";
    private String mFilePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_setting);
        Intent intent =getIntent();
        mFilePath = intent.getStringExtra(GIF_FILE_PATH);



        setGifFilePath(mFilePath);
    }


    private void setGifFilePath(String path){
        //file path
        if(!TextUtils.isEmpty(path)){
            try {
                GifDrawable gifDrawable = new GifDrawable( path);


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
