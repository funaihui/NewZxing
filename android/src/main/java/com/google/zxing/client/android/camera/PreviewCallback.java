/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.camera;

import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.client.android.utils.CV4JImage;
import com.google.zxing.client.android.utils.QRCodeScanner;
import com.google.zxing.client.android.utils.Rect;

import java.io.ByteArrayOutputStream;

@SuppressWarnings("deprecation") // camera APIs
final class PreviewCallback implements Camera.PreviewCallback {

  private static final String TAG = PreviewCallback.class.getSimpleName();

  private final CameraConfigurationManager configManager;
  private Handler previewHandler;
  private int previewMessage;

  PreviewCallback(CameraConfigurationManager configManager) {
    this.configManager = configManager;
  }

  void setHandler(Handler previewHandler, int previewMessage) {
    this.previewHandler = previewHandler;
    this.previewMessage = previewMessage;
  }

  @Override
  public void onPreviewFrame(byte[] data, Camera camera) {
//    Log.e(TAG, "onPreviewFrame: " );
//    Camera.Size previewSize = camera.getParameters().getPreviewSize();
//    YuvImage yuvimage=new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
//    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//    yuvimage.compressToJpeg(new android.graphics.Rect(0, 0, previewSize.width, previewSize.height), 80, baos);  //这里 80 是图片质量，取值范围 0-100，100为品质最高
//    byte[] jdata = baos.toByteArray();
//
//
//
//    CV4JImage cv4JImage = new CV4JImage(jdata);
//    QRCodeScanner qrCodeScanner = new QRCodeScanner();
//    Rect rect = qrCodeScanner.findQRCodeBounding(cv4JImage.getProcessor(),1,6);
//    if (rect == null || rect.height==0 ) {
//      Log.e(TAG, "onPreviewFrame: 咩没有识别到二维码" );
//      camera.setOneShotPreviewCallback(this);
//      return;
//    }
    Log.e(TAG, "onPreviewFrame: 识别到二维码" );

    Point cameraResolution = configManager.getCameraResolution();
    Handler thePreviewHandler = previewHandler;
    if (cameraResolution != null && thePreviewHandler != null) {
      Point screenResolution = configManager.getScreenResolution();
      Message message;
      if (screenResolution.x < screenResolution.y){
        // portrait
        message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.y,
                cameraResolution.x, data);
      } else {
        // landscape
        message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.x,
                cameraResolution.y, data);
      }
//      Message message = thePreviewHandler.obtainMessage(previewMessage, cameraResolution.x,
//          cameraResolution.y, data);
      message.sendToTarget();
      previewHandler = null;
    } else {
      Log.d(TAG, "Got preview callback, but no handler or resolution available");
    }
  }

}
