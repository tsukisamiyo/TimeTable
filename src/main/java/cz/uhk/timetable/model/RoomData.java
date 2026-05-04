package cz.uhk.timetable.model;

import com.google.gson.annotations.SerializedName;

public class RoomData {
    @SerializedName("zkrBudovy")
    private String buildingCode;
    @SerializedName("cisloMistnosti")
    private String roomCode;

    public String getBuildingCode() {
        return buildingCode;
    }

    public String getRoomCode() {
        return roomCode;
    }

    @Override
    public String toString() {
        return "RoomData{" +
                "buildingCode='" + buildingCode + '\'' +
                ", roomCode='" + roomCode + '\'' +
                '}';
    }
}
