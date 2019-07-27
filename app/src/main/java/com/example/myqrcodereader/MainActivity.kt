package com.example.myqrcodereader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.karumi.dexter.Dexter
import github.nisrulz.qreader.QREader
import android.Manifest.permission.CAMERA
import android.widget.Toast
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import github.nisrulz.qreader.QRDataListener
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var qrEader: QREader?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Dexter.withActivity(this@MainActivity)
                .withPermission(CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        setUpCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Toast.makeText(this@MainActivity, "You Must Enable This Permission", Toast.LENGTH_SHORT).show()
                    }

                }).check()
    }

    private fun setUpCamera() {
        btn_on_off.setOnClickListener {
            if (qrEader!!.isCameraRunning){
                btn_on_off.text = "Start"
                qrEader!!.stop()
            } else {
                btn_on_off.text = "Stop"
                qrEader!!.start()
            }
        }

        setUpQREader()
    }

    private fun setUpQREader() {
        qrEader = QREader.Builder(this,camera_view, QRDataListener {
            data ->
                code_info.post { code_info.text = data }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(camera_view.height)
                .width(camera_view.width)
                .build()
    }

    override fun onResume() {
        super.onResume()
        if (qrEader != null)
        qrEader!!.initAndStart(camera_view)
    }

    override fun onPause() {
        super.onPause()
        if (qrEader != null)
        qrEader!!.releaseAndCleanup()
    }
}
