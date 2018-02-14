package com.tooploox.aimtask.data.gateway

import com.tooploox.aimtask.domain.entity.common.Result
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class OkHttpDataGatewayTest {

    private lateinit var instance: OkHttpDataGateway
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        instance = OkHttpDataGateway(OkHttpClient(), mockWebServer.url("/").toString())
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    /**
     * This test just verifies that parsing doesn't throw, but other tests can be added to verify that proper fields are being read,
     * time is parsed correctly, empty tracks list is handled etc.
     */
    @Test
    fun parsesCurrentlyPlayingStation_1() {
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody(readFile("/feeds/currentlyPlayingResponse_1.xml"))
        )

        val stationInfo = instance.fetchStationInfo()

        assertThat(stationInfo).isInstanceOf(Result.Value::class.java)
    }

    @Test
    fun parsesCurrentlyPlayingStation_2() {
        mockWebServer.enqueue(MockResponse()
                .setResponseCode(200)
                .setBody(readFile("/feeds/currentlyPlayingResponse_2.xml"))
        )

        val stationInfo = instance.fetchStationInfo()

        assertThat(stationInfo).isInstanceOf(Result.Value::class.java)
    }

    private fun readFile(filePath: String): String =
            Okio.buffer(Okio.source(this.javaClass.getResourceAsStream(filePath))).use {
                it.readUtf8()
            }
}
