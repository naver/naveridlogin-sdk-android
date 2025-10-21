package com.navercorp.nid.core.util

/**
 *
 * Created on 2021.10.19
 * Updated on 2021.10.19
 *
 * @author Namhoon Kim. (namhun.kim@navercorp.com)
 *         Naver Authentication Platform Development.
 *
 * Android Version 관리를 위한 유틸
 */
object AndroidVer {

    /**
     * 미지원 api (Less than min sdk version)
     */
//    const val API_1_BASE = 1
//    const val API_2_BASE_1_1 = 2
//    const val API_3_CUPCAKE = 3
//    const val API_4_DONUT = 4
//    const val API_5_ECLAIR = 5
//    const val API_6_ECLAIR_0_1 = 6
//    const val API_7_ECLAIR_MR1 = 7
//    const val API_8_FROYO = 8
//    const val API_9_GINGERBREAD = 9
//    const val API_10_GINGERBREAD_MR1 = 10
//    const val API_11_HONEYCOMB = 11
//    const val API_12_HONEYCOMB_MR1 = 12
//    const val API_13_HONEYCOMB_MR2 = 13
//    const val API_14_ICE_CREAM_SANDWICH = 14
//    const val API_15_ICE_CREAM_SANDWICH_MR1 = 15
//    const val API_16_JELLY_BEAN = 16
//    const val API_17_JELLY_BEAN_MR1 = 17
//    const val API_18_JELLY_BEAN_MR2 = 18
//    const val API_19_KITKAT = 19
//    const val API_20_KITKAT_WATCH = 20

    /**
     * 지원 api (More than min sdk version)
     */
    const val API_21_LOLLIPOP = 21
    const val API_22_LOLLIPOP_MR1 = 22
    const val API_23_MARSHMALLOW = 23
    const val API_24_NOUGAT = 24
    const val API_25_NOUGAT_MR1 = 25
    const val API_26_OREO = 26
    const val API_27_OREO_MR1 = 27
    const val API_28_PIE = 28
    const val API_29_Q = 29
    const val API_29_ANDROID_10 = API_29_Q
    const val API_30_ANDROID_11: Int = 30
}