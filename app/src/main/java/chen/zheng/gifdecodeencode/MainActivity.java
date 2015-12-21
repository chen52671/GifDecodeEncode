package chen.zheng.gifdecodeencode;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import chen.zheng.gifdecodeencode.Utils.CommonUtils;
import chen.zheng.gifdecodeencode.Utils.LogUtils;
import chen.zheng.gifdecodeencode.Utils.ToastUtils;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GET_FILE = 11;
    private Button mGifChooseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGifChooseButton = (Button) findViewById(R.id.gif_choose_button);
        mGifChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  /*选择GIF图片文件*/
                Intent intent = new Intent();
                intent.setType("image/gif");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_GET_FILE);
            }
        });
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
                                intent.putExtra(ConvertSettingActivity.GIF_FILE_PATH,filePath);
                                startActivity(intent);
                            } else {
                                ToastUtils.showToast(MainActivity.this,MainActivity.this.getResources().getString(R.string.choose_gif_toast));
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
}
