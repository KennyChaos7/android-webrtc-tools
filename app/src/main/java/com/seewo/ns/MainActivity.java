package com.seewo.ns;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.wzc.ns.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    //ns 与 nxs
    private static final boolean DO_NS = true;
    //输入文件路径 手机根目录下ns_out.pcm
    private static final String OUT_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ns_out.pcm";
    private static final String OUT_FILE_PATH2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/origin_out.pcm";

    private AudioTrack audioTrack;
    private boolean isPlaying = false;
    private boolean isStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                doWork();
            }
        }

        final int bufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO,AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);


        findViewById(R.id.btnOrigin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInputStream fis = null;
                try {
                    audioTrack.play();
                    fis = new FileInputStream(OUT_FILE_PATH2);
                    Log.e(TAG, " btnOrigin");
                    byte[] buffer = new byte[bufferSize];
                    int len = 0;
                    isPlaying = true;
                    while ((len = fis.read(buffer)) != -1 && !isStop) {
                        audioTrack.write(buffer, 0, len);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "playPCMRecord: e : " + e);
                } finally {
                    if (audioTrack != null) {
                        audioTrack.stop();
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        findViewById(R.id.btnAfter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInputStream fis = null;
                try {
                    audioTrack.play();
                    fis = new FileInputStream(OUT_FILE_PATH);
                    Log.e(TAG, " btnOrigin");
                    byte[] buffer = new byte[bufferSize];
                    int len = 0;
                    isPlaying = true;
                    while ((len = fis.read(buffer)) != -1 && !isStop) {
                        audioTrack.write(buffer, 0, len);
                    }

                } catch (Exception e) {
                    Log.e(TAG, "playPCMRecord: e : " + e);
                } finally {
                    if (audioTrack != null) {
                        audioTrack.stop();
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    private void doWork() {
        if (DO_NS) {
            doNs();
        }
    }

    private void doNs() {
        try {
            int sample = 8000;
            int bufferSize = 160 * (sample / 8000);

            NoiseSuppression noiseSuppression = new NoiseSuppression();
            long handler = noiseSuppression.WebRtcNsCreate();
            noiseSuppression.WebRtcNsInit(handler, sample);
            noiseSuppression.WebRtcNsSetPolicy(handler, 2);

            Toast.makeText(this, "开始测试", Toast.LENGTH_LONG).show();

            InputStream fInt = getResources().openRawResource(R.raw.test_input);
            FileOutputStream fOut = new FileOutputStream(OUT_FILE_PATH);
            byte[] buffer = new byte[bufferSize];
            int bytes;

            while (fInt.read(buffer) != -1) {
                short[] inputData = new short[buffer.length/2];
                short[] outData = new short[buffer.length/2];
                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(inputData);
                noiseSuppression.WebRtcNsAnalyze(handler,inputData);
                noiseSuppression.WebRtcNsProcess(handler,inputData, 1, outData);

                fOut.write(shortArrayToByteArray(outData));
            }

            fInt.close();
            fOut.close();

            fInt = getResources().openRawResource(R.raw.test_input);
            fOut = new FileOutputStream(OUT_FILE_PATH2);
            buffer = new byte[bufferSize];
            while (fInt.read(buffer) != -1) {
                short[] inputData = new short[buffer.length/2];
                ByteBuffer.wrap(buffer).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(inputData);

                fOut.write(shortArrayToByteArray(inputData));
            }

            fInt.close();
            fOut.close();


            Toast.makeText(this, "测试结束，输出文件位于手机根目录下/ns_out.pcm", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // shortArray to byteArray
    public byte[] shortArrayToByteArray(short[] data) {
        byte[] byteVal = new byte[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            byteVal[i * 2] = (byte) (data[i] & 0xff);
            byteVal[i * 2 + 1] = (byte) ((data[i] & 0xff00) >> 8);
        }
        return byteVal;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasPermissionDismiss = false;
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == -1) {
                    hasPermissionDismiss = true;   //发现有未通过权限
                    break;
                }
            }
        }
        if (hasPermissionDismiss) {
        } else {
            doWork();
        }
    }

}
