package com.ayildiz.grad;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.core.Mat;

public class CamActivity extends Activity implements CvCameraViewListener2 {
	
	private CameraBridgeViewBase openCvCameraView;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		setContentView(R.layout.activity_cam);
		openCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cam_view);
		openCvCameraView.setCvCameraViewListener(this);
		openCvCameraView.enableView();
	}
	
	@Override
	public void onPause(){
		super.onPause();
		openCvCameraView.disableView();
	}
	
    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void onDestroy() {
        super.onDestroy();
        if (openCvCameraView != null)
            openCvCameraView.disableView();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	Mat grayFrame = inputFrame.gray();
    	Mat rgbaFrame = inputFrame.rgba();
    	Detect(grayFrame.getNativeObjAddr(), rgbaFrame.getNativeObjAddr());
    	return rgbaFrame;
    }

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}
	
	public native void Detect(long grayAddr, long rgbaAddr);

}
