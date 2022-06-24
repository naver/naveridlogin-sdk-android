package com.navercorp.nid.log

import com.navercorp.naverid.NaverIdTestCase
import com.nhn.android.oauth.BuildConfig
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert
import org.junit.Test
import org.powermock.reflect.Whitebox

class NidLogTest: NaverIdTestCase() {

    @Test
    fun init_테스트() {
        NidLog.init()

        val instance = Whitebox.getInternalState<INidLog>(NidLog::class.java, "instance")

        if (BuildConfig.BUILD_TYPE.equals("debug", ignoreCase = true)) {
            Assert.assertTrue(instance is DebugNidLog)
        } else {
            Assert.assertTrue(instance is ReleaseNidLog)
        }
    }

    @Test
    fun showLog_테스트() {
        NidLog.showLog(true)

        val instance = Whitebox.getInternalState<INidLog>(NidLog::class.java, "instance")

        Assert.assertTrue(instance is DebugNidLog)
    }

    @Test
    fun setPrefix_테스트() {
        val prefixMessage = "prefixMessage"

        NidLog.init()
        NidLog.setPrefix(prefixMessage)

        val instance = Whitebox.getInternalState<INidLog>(NidLog::class.java, "instance")
        val prefix = Whitebox.getInternalState<String>(instance, "prefix")

        Assert.assertEquals(prefixMessage, prefix)
    }

    @Test
    fun v_테스트() {
        val tag = "tag"
        val message = "message"

        val mockDebugLog = mockk<DebugNidLog>()
        every { mockDebugLog.v(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockDebugLog)

        NidLog.v(tag, message)
        verify { mockDebugLog.v(tag, message) }

        val mockReleaseLog = mockk<ReleaseNidLog>()
        every { mockReleaseLog.v(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockReleaseLog)

        NidLog.v(tag, message)
        verify { mockReleaseLog.v(tag, message) }
    }

    @Test
    fun d_테스트() {
        val tag = "tag"
        val message = "message"

        val mockDebugLog = mockk<DebugNidLog>()
        every { mockDebugLog.d(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockDebugLog)

        NidLog.d(tag, message)
        verify { mockDebugLog.d(tag, message) }

        val mockReleaseLog = mockk<ReleaseNidLog>()
        every { mockReleaseLog.d(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockReleaseLog)

        NidLog.d(tag, message)
        verify { mockReleaseLog.d(tag, message) }
    }

    @Test
    fun i_테스트() {
        val tag = "tag"
        val message = "message"

        val mockDebugLog = mockk<DebugNidLog>()
        every { mockDebugLog.i(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockDebugLog)

        NidLog.i(tag, message)
        verify { mockDebugLog.i(tag, message) }

        val mockReleaseLog = mockk<ReleaseNidLog>()
        every { mockReleaseLog.i(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockReleaseLog)

        NidLog.i(tag, message)
        verify { mockReleaseLog.i(tag, message) }
    }

    @Test
    fun w_테스트() {
        val tag = "tag"
        val message = "message"

        val mockDebugLog = mockk<DebugNidLog>()
        every { mockDebugLog.w(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockDebugLog)

        NidLog.w(tag, message)
        verify { mockDebugLog.w(tag, message) }

        val mockReleaseLog = mockk<ReleaseNidLog>()
        every { mockReleaseLog.w(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockReleaseLog)

        NidLog.w(tag, message)
        verify { mockReleaseLog.w(tag, message) }
    }

    @Test
    fun e_테스트() {
        val tag = "tag"
        val message = "message"

        val mockDebugLog = mockk<DebugNidLog>()
        every { mockDebugLog.e(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockDebugLog)

        NidLog.e(tag, message)
        verify { mockDebugLog.e(tag, message) }

        val mockReleaseLog = mockk<ReleaseNidLog>()
        every { mockReleaseLog.e(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockReleaseLog)

        NidLog.e(tag, message)
        verify { mockReleaseLog.e(tag, message) }
    }

    @Test
    fun wtf_테스트() {
        val tag = "tag"
        val message = "message"

        val mockDebugLog = mockk<DebugNidLog>()
        every { mockDebugLog.wtf(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockDebugLog)

        NidLog.wtf(tag, message)
        verify { mockDebugLog.wtf(tag, message) }

        val mockReleaseLog = mockk<ReleaseNidLog>()
        every { mockReleaseLog.wtf(tag, message) } returns Unit
        Whitebox.setInternalState(NidLog::class.java, "instance", mockReleaseLog)

        NidLog.wtf(tag, message)
        verify { mockReleaseLog.wtf(tag, message) }
    }

}