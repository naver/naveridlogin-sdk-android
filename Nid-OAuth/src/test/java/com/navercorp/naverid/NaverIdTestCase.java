package com.navercorp.naverid;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


@Config(
        minSdk = 21,
        maxSdk = 29
)
@RunWith(NaverIdTestRunner.class)
public abstract class NaverIdTestCase {

}
