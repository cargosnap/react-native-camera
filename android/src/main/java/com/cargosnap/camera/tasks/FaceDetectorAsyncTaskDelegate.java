package com.cargosnap.camera.tasks;

import com.cargosnap.facedetector.RNFaceDetector;

import com.facebook.react.bridge.WritableArray;

public interface FaceDetectorAsyncTaskDelegate {
  void onFacesDetected(WritableArray faces);
  void onFaceDetectionError(RNFaceDetector faceDetector);
  void onFaceDetectingTaskCompleted();
}
