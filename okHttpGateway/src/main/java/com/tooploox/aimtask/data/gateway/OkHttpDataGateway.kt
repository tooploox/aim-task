package com.tooploox.aimtask.data.gateway

import android.util.Xml
import com.tooploox.aimtask.domain.entity.OnAirInfo
import com.tooploox.aimtask.domain.entity.StationInfo
import com.tooploox.aimtask.domain.entity.Track
import com.tooploox.aimtask.domain.entity.common.Result
import com.tooploox.aimtask.domain.interfaces.DataGateway
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.threeten.bp.Duration
import org.threeten.bp.OffsetDateTime
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.Objects

/**
 * Simple DataGateway implementation using OkHttp and Android's XML pull parser.
 */
class OkHttpDataGateway(
        private val okHttpClient: OkHttpClient = OkHttpClient(),
        baseUrl: String = DEFAULT_BASE_URL
) : DataGateway {

    private val baseUrl: HttpUrl? = HttpUrl.parse(baseUrl)

    override fun fetchStationInfo(): Result<OnAirInfo> =
            try {
                val response = okHttpClient.newCall(Request.Builder()
                        .url(Objects.requireNonNull<HttpUrl>(baseUrl!!.resolve(ON_AIR_PATH)))
                        .build())
                        .execute()

                if (response.isSuccessful) {
                    Result.Value(parseOnAirResponse(response.body()!!.byteStream()))
                } else {
                    Result.Error(Exception(String.format("Error while reading on air information: %s", response.message())))
                }
            } catch (exception: IOException) {
                Result.Error(exception)
            } catch (exception: XmlPullParserException) {
                Result.Error(exception)
            }

    /**
     * Parsing is pretty naive since I'm not sure about which fields are optional etc. It works for the basic case, but
     * can be expanded once there's more information about the schema.
     */
    private fun parseOnAirResponse(inputStream: InputStream): OnAirInfo {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, null)

        var stationInfo: StationInfo? = null
        var tracksList: List<Track>? = null

        var event: Int = parser.next()
        while (event != XmlPullParser.END_DOCUMENT) {
            if (event == XmlPullParser.START_TAG) {
                if (parser.name == "epgItem") {
                    stationInfo = parseStationInfo(parser)
                } else if (parser.name == "playoutData") {
                    tracksList = parseTracksList(parser)
                }
            }
            event = parser.next()
        }

        if (stationInfo == null || tracksList == null) {
            throw RuntimeException("Exception while parsing on air info")
        }

        return OnAirInfo(stationInfo, tracksList)
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun parseStationInfo(parser: XmlPullParser): StationInfo {
        val name = parser.getAttributeValue(null, "name")
        val description = parser.getAttributeValue(null, "description")
        val time = parser.getAttributeValue(null, "time")
        val duration = parser.getAttributeValue(null, "duration")
        val presenter = parser.getAttributeValue(null, "presenter")

        var image640: String? = null
        var displayTime: String? = null

        var event: Int = parser.nextTag()
        while (!(event == XmlPullParser.END_TAG && parser.name == "epgItem")) {
            if (event == XmlPullParser.START_TAG && parser.name == "customField") {
                val fieldName = parser.getAttributeValue(null, "name")
                if (fieldName == "image640") {
                    image640 = parser.getAttributeValue(null, "value")
                } else if (fieldName == "displayTime") {
                    displayTime = parser.getAttributeValue(null, "value")
                }
            }
            event = parser.nextTag()
        }
        return StationInfo(
                name,
                description,
                OffsetDateTime.parse(time).toLocalDateTime(),
                parseDuration(duration),
                presenter,
                Objects.requireNonNull<String>(image640),
                Objects.requireNonNull<String>(displayTime)
        )
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun parseTracksList(parser: XmlPullParser): List<Track> {
        val tracks = ArrayList<Track>(20) // Seems like there are 20 tracks in a station -- always?
        var event: Int = parser.nextTag()
        while (!(event == XmlPullParser.END_TAG && parser.name == "playoutData")) {
            if (event == XmlPullParser.START_TAG && parser.name == "playoutItem") {
                tracks.add(Track(
                        parser.getAttributeValue(null, "title"),
                        parser.getAttributeValue(null, "artist"),
                        parser.getAttributeValue(null, "album"),
                        OffsetDateTime.parse(parser.getAttributeValue(null, "time")).toLocalDateTime(),
                        parseDuration(parser.getAttributeValue(null, "duration")),
                        parser.getAttributeValue(null, "imageUrl"),
                        parseStatus(parser.getAttributeValue(null, "status").toLowerCase()),
                        parseType(parser.getAttributeValue(null, "type").toLowerCase())
                ))
            }
            event = parser.nextTag()
        }
        return tracks
    }

    private fun parseDuration(playDuration: String): Duration =
            playDuration.split(":").let {
                Duration.parse(String.format("PT%sH%sM%sS", it[0], it[1], it[2]))
            }

    private fun parseType(type: String): Track.Type =
            when (type) {
                "song" -> Track.Type.SONG
                else -> throw IllegalArgumentException("Unknown track type")
            }

    private fun parseStatus(status: String): Track.Status =
            when (status) {
                "history" -> Track.Status.HISTORY
                "playing" -> Track.Status.PLAYING
                else -> throw IllegalArgumentException("Unknown track status")
            }

    companion object {

        private const val DEFAULT_BASE_URL = "http://aim.appdata.abc.net.au.edgesuite.net/data/abc/triplej/"
        private const val ON_AIR_PATH = "onair.xml"
    }
}
