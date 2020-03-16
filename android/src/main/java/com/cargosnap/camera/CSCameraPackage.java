package com.cargosnap.camera;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.lwansbrough.RCTCamera.RCTCameraModule;
import com.lwansbrough.RCTCamera.RCTCameraViewManager;

import com.cargosnap.facedetector.FaceDetectorModule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by jgfidelis on 02/02/18.
 */

public class CSCameraPackage implements ReactPackage {
    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactApplicationContext) {
        return Arrays.<NativeModule>asList(
                new RCTCameraModule(reactApplicationContext),
                new CameraModule(reactApplicationContext),
                new FaceDetectorModule(reactApplicationContext)
        );
    }

    // Deprecated in RN 0.47
    public List<Class<? extends JavaScriptModule>> createJSModules() {
        return Collections.emptyList();
    }

    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactApplicationContext) {
        return Arrays.<ViewManager>asList(
                new RCTCameraViewManager(),
                new CameraViewManager()
        );
    }
}
