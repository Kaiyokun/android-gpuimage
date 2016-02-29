package com.ict.camera;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import jp.co.cyberagent.android.gpuimage.sample.R;

public class CameraActivity extends Activity implements OnClickListener {
	private CameraHandler cameraHandler;
	private CameraManagerFive cameraManagerFive;
	private boolean start = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		Button btnStart = (Button) findViewById(R.id.btn_start);
		btnStart.setOnClickListener(this);
		Button btnStop = (Button) findViewById(R.id.btn_stop);
		btnStop.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		if (start)
			cameraHandler.closeCamera();
		super.onPause();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_start:
			if(!start){
			cameraHandler = CameraFactory
					.getCameraHandler(getApplicationContext());
			DataProcessor dataProcessor = new DataModel(getApplicationContext());
			cameraHandler.setDataProcessor(dataProcessor);
			cameraHandler.openCamera();
			start = true;
			}
			break;

		case R.id.btn_stop:
			if (start) {
				cameraHandler.closeCamera();
				start = false;
			}
			break;
		default:
			break;
		}

	}

}
