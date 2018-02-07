package com.tooploox.aimtask.data.gateway;

import android.util.Xml;

import com.tooploox.aimtask.domain.entity.OnAirInfo;
import com.tooploox.aimtask.domain.entity.StationInfo;
import com.tooploox.aimtask.domain.entity.Track;
import com.tooploox.aimtask.domain.entity.common.Result;
import com.tooploox.aimtask.domain.interfaces.DataGateway;

import org.threeten.bp.Duration;
import org.threeten.bp.OffsetDateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Simple DataGateway implementation using OkHttp and Android's XML pull parser.
 */
public class OkHttpDataGateway implements DataGateway {

    private static final String DEFAULT_BASE_URL = "http://aim.appdata.abc.net.au.edgesuite.net/data/abc/triplej/";
    private static final String ON_AIR_PATH = "onair.xml";

    private final OkHttpClient okHttpClient;
    private final HttpUrl baseUrl;

    public OkHttpDataGateway() {
        this(new OkHttpClient(), DEFAULT_BASE_URL);
    }

    OkHttpDataGateway(OkHttpClient okHttpClient, String baseUrl) {
        this.okHttpClient = okHttpClient;
        this.baseUrl = HttpUrl.parse(baseUrl);
    }

    @Override
    public Result<OnAirInfo> fetchStationInfo() {
        try {
            Response response = okHttpClient.newCall(new Request.Builder()
                    .url(Objects.requireNonNull(baseUrl.resolve(ON_AIR_PATH)))
                    .build())
                    .execute();

            if (response.isSuccessful()) {
                return Result.value(parseOnAirResponse(Objects.requireNonNull(response.body()).byteStream()));
            } else {
                return Result.error(new Exception(String.format("Error while reading on air information: %s", response.message())));
            }
        } catch (IOException | XmlPullParserException exception) {
            return Result.error(exception);
        }
    }

    /**
     * Parsing is pretty naive since I'm not sure about which fields are optional etc. It works for the basic case, but
     * can be expanded once there's more information about the schema.
     */
    private OnAirInfo parseOnAirResponse(InputStream inputStream) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inputStream, null);

        StationInfo stationInfo = null;
        List<Track> tracksList = null;

        int event;
        while ((event = parser.next()) != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                if (parser.getName().equals("epgItem")) {
                    stationInfo = parseStationInfo(parser);
                } else if (parser.getName().equals("playoutData")) {
                    tracksList = parseTracksList(parser);
                }
            }
        }

        if (stationInfo == null || tracksList == null) {
            throw new RuntimeException("Exception while parsing on air info");
        }

        return new OnAirInfo(stationInfo, tracksList);
    }

    private StationInfo parseStationInfo(XmlPullParser parser) throws IOException, XmlPullParserException {
        String name = parser.getAttributeValue(null, "name");
        String description = parser.getAttributeValue(null, "description");
        String time = parser.getAttributeValue(null, "time");
        String duration = parser.getAttributeValue(null, "duration");
        String presenter = parser.getAttributeValue(null, "presenter");

        String image640 = null;
        String displayTime = null;

        int event;
        while (!((event = parser.nextTag()) == XmlPullParser.END_TAG && parser.getName().equals("epgItem"))) {
            if (event == XmlPullParser.START_TAG && parser.getName().equals("customField")) {
                String fieldName = parser.getAttributeValue(null, "name");
                if (fieldName.equals("image640")) {
                    image640 = parser.getAttributeValue(null, "value");
                } else if (fieldName.equals("displayTime")) {
                    displayTime = parser.getAttributeValue(null, "value");
                }
            }
        }
        return new StationInfo(
                name,
                description,
                OffsetDateTime.parse(time).toLocalDateTime(),
                parseDuration(duration),
                presenter,
                Objects.requireNonNull(image640),
                Objects.requireNonNull(displayTime)
        );
    }

    private List<Track> parseTracksList(XmlPullParser parser) throws IOException, XmlPullParserException {
        List<Track> tracks = new ArrayList<>(20); // Seems like there are 20 tracks in a station -- always?
        int event;
        while (!((event = parser.nextTag()) == XmlPullParser.END_TAG && parser.getName().equals("playoutData"))) {
            if (event == XmlPullParser.START_TAG && parser.getName().equals("playoutItem")) {
                tracks.add(new Track(
                        parser.getAttributeValue(null, "title"),
                        parser.getAttributeValue(null, "artist"),
                        parser.getAttributeValue(null, "album"),
                        OffsetDateTime.parse(parser.getAttributeValue(null, "time")).toLocalDateTime(),
                        parseDuration(parser.getAttributeValue(null, "duration")),
                        parser.getAttributeValue(null, "imageUrl"),
                        parseStatus(parser.getAttributeValue(null, "status").toLowerCase()),
                        parseType(parser.getAttributeValue(null, "type").toLowerCase())
                ));
            }
        }
        return tracks;
    }

    private Duration parseDuration(String playDuration) {
        String[] parts = playDuration.split(":");
        return Duration.parse(String.format("PT%sH%sM%sS", parts[0], parts[1], parts[2]));
    }

    private Track.Type parseType(String type) {
        switch (type) {
            case "song":
                return Track.Type.SONG;
            default:
                throw new IllegalArgumentException("Unknown track type");
        }
    }

    private Track.Status parseStatus(String status) {
        switch (status) {
            case "history":
                return Track.Status.HISTORY;
            case "playing":
                return Track.Status.PLAYING;
            default:
                throw new IllegalArgumentException("Unknown track status");
        }
    }
}
