package com.tooploox.aimtask.data.gateway;

import com.tooploox.aimtask.domain.entity.OnAirInfo;
import com.tooploox.aimtask.domain.entity.common.Result;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okio.BufferedSource;
import okio.Okio;

import static org.assertj.core.api.Assertions.assertThat;

public class OkHttpDataGatewayTest {

    private OkHttpDataGateway instance;
    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        instance = new OkHttpDataGateway(new OkHttpClient(), mockWebServer.url("/").toString());
    }

    @After
    public void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    /**
     * This test just verifies that parsing doesn't throw, but other tests can be added to verify that proper fields are being read,
     * time is parsed correctly, empty tracks list is handled etc.
     */
    @Test
    public void parsesCurrentlyPlayingStation_1() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(readFile("/feeds/currentlyPlayingResponse_1.xml"))
        );

        Result<OnAirInfo> stationInfo = instance.fetchStationInfo();

        assertThat(stationInfo.isSuccessful()).isTrue();
    }

    @Test
    public void parsesCurrentlyPlayingStation_2() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(readFile("/feeds/currentlyPlayingResponse_2.xml"))
        );

        Result<OnAirInfo> stationInfo = instance.fetchStationInfo();

        assertThat(stationInfo.isSuccessful()).isTrue();
    }

    private String readFile(String filePath) throws IOException {
        BufferedSource fileSource = Okio.buffer(Okio.source(this.getClass().getResourceAsStream(filePath)));
        String fileContents = fileSource.readUtf8();
        fileSource.close();
        return fileContents;
    }
}
