package cz.uhk.timetable.utils;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import cz.uhk.timetable.model.RoomData;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;

public class StagRoomRequestBuilder {
    private static final String BASE_STAG_URL = "https://stag-demo.uhk.cz/ws/services/rest2/mistnost/getMistnostiInfo?outputFormat=JSON";
    private String url;
    private Gson gson;

    public StagRoomRequestBuilder() {
        this.url = BASE_STAG_URL;
        gson = new Gson();
    }

    public StagRoomRequestBuilder filterByBuildingCode(String buildingCode) {
        url = url + "&zkrBudovy=" + buildingCode;
        return this;
    }

    public StagRoomRequestBuilder filterByRoomCode(String roomCode) {
        url = url + "&cisloMistnosti=" + roomCode;
        return this;
    }

    public List<RoomData> execute() {
        try {
            var uri = URI.create(url);
            System.out.println(url);
            var reader = new InputStreamReader(uri.toURL().openStream());
            RoomResponse response = gson.fromJson(reader, RoomResponse.class);
            if (response == null || response.mistnostInfo == null) {
                return List.of();
            }
            return response.mistnostInfo;
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUrl() {
        return url;
    }

    private static class RoomResponse {
        @SerializedName("mistnostInfo")
        List<RoomData> mistnostInfo;
    }
}
