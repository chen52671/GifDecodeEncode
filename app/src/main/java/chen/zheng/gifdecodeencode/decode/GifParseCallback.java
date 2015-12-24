package chen.zheng.gifdecodeencode.decode;

public interface GifParseCallback {
    public void onParseFinish(boolean parseStatus, int duration, int width, int height, int frameCount);
}
