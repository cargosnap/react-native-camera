package com.cargosnap.camera;

import androidx.annotation.Nullable;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.google.android.cameraview.AspectRatio;
import com.google.android.cameraview.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CameraViewManager extends ViewGroupManager<CSCameraView> {
  public enum Events {
    EVENT_CAMERA_READY("onCameraReady"),
    EVENT_ON_MOUNT_ERROR("onMountError"),
    EVENT_ON_BAR_CODE_READ("onBarCodeRead"),
    EVENT_ON_FACES_DETECTED("onFacesDetected"),
    EVENT_ON_BARCODES_DETECTED("onGoogleVisionBarcodesDetected"),
    EVENT_ON_FACE_DETECTION_ERROR("onFaceDetectionError"),
    EVENT_ON_BARCODE_DETECTION_ERROR("onGoogleVisionBarcodeDetectionError"),
    EVENT_ON_TEXT_RECOGNIZED("onTextRecognized"),
    EVENT_ON_PICTURE_TAKEN("onPictureTaken"),
    EVENT_ON_PICTURE_SAVED("onPictureSaved"),
    EVENT_ON_RECORDING_START("onRecordingStart"),
    EVENT_ON_RECORDING_END("onRecordingEnd");

    private final String mName;

    Events(final String name) {
      mName = name;
    }

    @Override
    public String toString() {
      return mName;
    }
  }

  private static final String REACT_CLASS = "CSCamera";

  @Override
  public void onDropViewInstance(CSCameraView view) {
    view.onHostDestroy();
    super.onDropViewInstance(view);
  }


  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  protected CSCameraView createViewInstance(ThemedReactContext themedReactContext) {
    return new CSCameraView(themedReactContext);
  }

  @Override
  @Nullable
  public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
    MapBuilder.Builder<String, Object> builder = MapBuilder.builder();
    for (Events event : Events.values()) {
      builder.put(event.toString(), MapBuilder.of("registrationName", event.toString()));
    }
    return builder.build();
  }

  @ReactProp(name = "type")
  public void setType(CSCameraView view, int type) {
    view.setFacing(type);
  }

  @ReactProp(name = "cameraId")
  public void setCameraId(CSCameraView view, String id) {
    view.setCameraId(id);
  }

  @ReactProp(name = "ratio")
  public void setRatio(CSCameraView view, String ratio) {
    view.setAspectRatio(AspectRatio.parse(ratio));
  }

  @ReactProp(name = "flashMode")
  public void setFlashMode(CSCameraView view, int torchMode) {
    view.setFlash(torchMode);
  }

  @ReactProp(name = "exposure")
  public void setExposureCompensation(CSCameraView view, float exposure){
    view.setExposureCompensation(exposure);
  }

  @ReactProp(name = "autoFocus")
  public void setAutoFocus(CSCameraView view, boolean autoFocus) {
    view.setAutoFocus(autoFocus);
  }

  @ReactProp(name = "focusDepth")
  public void setFocusDepth(CSCameraView view, float depth) {
    view.setFocusDepth(depth);
  }

  @ReactProp(name = "autoFocusPointOfInterest")
  public void setAutoFocusPointOfInterest(CSCameraView view, ReadableMap coordinates) {
    if(coordinates != null){
      float x = (float) coordinates.getDouble("x");
      float y = (float) coordinates.getDouble("y");
      view.setAutoFocusPointOfInterest(x, y);
    }
  }

  @ReactProp(name = "zoom")
  public void setZoom(CSCameraView view, float zoom) {
    view.setZoom(zoom);
  }

  @ReactProp(name = "whiteBalance")
  public void setWhiteBalance(CSCameraView view, int whiteBalance) {
    view.setWhiteBalance(whiteBalance);
  }

  @ReactProp(name = "pictureSize")
  public void setPictureSize(CSCameraView view, String size) {
    view.setPictureSize(size.equals("None") ? null : Size.parse(size));
  }

  @ReactProp(name = "barCodeTypes")
  public void setBarCodeTypes(CSCameraView view, ReadableArray barCodeTypes) {
    if (barCodeTypes == null) {
      return;
    }
    List<String> result = new ArrayList<>(barCodeTypes.size());
    for (int i = 0; i < barCodeTypes.size(); i++) {
      result.add(barCodeTypes.getString(i));
    }
    view.setBarCodeTypes(result);
  }

  @ReactProp(name = "barCodeScannerEnabled")
  public void setBarCodeScanning(CSCameraView view, boolean barCodeScannerEnabled) {
    view.setShouldScanBarCodes(barCodeScannerEnabled);
  }

  @ReactProp(name = "useCamera2Api")
  public void setUseCamera2Api(CSCameraView view, boolean useCamera2Api) {
    view.setUsingCamera2Api(useCamera2Api);
  }

  @ReactProp(name = "camera1ScanMode")
  public void setCamera1ScanMode(CSCameraView view, String camera1ScanMode) {
    view.setCamera1ScanMode(camera1ScanMode);
  }

  @ReactProp(name = "playSoundOnCapture")
  public void setPlaySoundOnCapture(CSCameraView view, boolean playSoundOnCapture) {
    view.setPlaySoundOnCapture(playSoundOnCapture);
  }

  @ReactProp(name = "faceDetectorEnabled")
  public void setFaceDetecting(CSCameraView view, boolean faceDetectorEnabled) {
    view.setShouldDetectFaces(faceDetectorEnabled);
  }

  @ReactProp(name = "faceDetectionMode")
  public void setFaceDetectionMode(CSCameraView view, int mode) {
    view.setFaceDetectionMode(mode);
  }

  @ReactProp(name = "faceDetectionLandmarks")
  public void setFaceDetectionLandmarks(CSCameraView view, int landmarks) {
    view.setFaceDetectionLandmarks(landmarks);
  }

  @ReactProp(name = "faceDetectionClassifications")
  public void setFaceDetectionClassifications(CSCameraView view, int classifications) {
    view.setFaceDetectionClassifications(classifications);
  }

  @ReactProp(name = "trackingEnabled")
  public void setTracking(CSCameraView view, boolean trackingEnabled) {
    view.setTracking(trackingEnabled);
  }

  @ReactProp(name = "googleVisionBarcodeDetectorEnabled")
  public void setGoogleVisionBarcodeDetecting(CSCameraView view, boolean googleBarcodeDetectorEnabled) {
    view.setShouldGoogleDetectBarcodes(googleBarcodeDetectorEnabled);
  }

  @ReactProp(name = "googleVisionBarcodeType")
  public void setGoogleVisionBarcodeType(CSCameraView view, int barcodeType) {
    view.setGoogleVisionBarcodeType(barcodeType);
  }

  @ReactProp(name = "googleVisionBarcodeMode")
  public void setGoogleVisionBarcodeMode(CSCameraView view, int barcodeMode) {
    view.setGoogleVisionBarcodeMode(barcodeMode);
  }

  @ReactProp(name = "textRecognizerEnabled")
  public void setTextRecognizing(CSCameraView view, boolean textRecognizerEnabled) {
    view.setShouldRecognizeText(textRecognizerEnabled);
  }
}
