package com.navercorp.naverid;

import org.junit.runners.model.InitializationError;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.inject.Injector;

public class NaverIdTestRunner extends RobolectricTestRunner {

    public NaverIdTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    protected NaverIdTestRunner(Class<?> testClass, Injector injector) throws InitializationError {
        super(testClass, injector);
    }
}
