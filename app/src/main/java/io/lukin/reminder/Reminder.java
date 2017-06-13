package io.lukin.reminder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;


public class Reminder extends Object implements Parcelable {

    private int hour = -1;
    private int minute = -1;
    private String type;
    private String name = "";
    private LatLng place = null;
    private String address;
    private boolean enabled = true;

    public LatLng getPlace() {
        return place;
    }

    public void setPlace(LatLng place) {
        this.place = place;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




    public Reminder() {
        super();
        enabled = true;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setState(boolean state){
        this.enabled = state;
    }

    public void toggle(){
        this.enabled = !this.enabled;
    }

    public String getAddress(){
        if(place != null){
            return  address;
        }
        return "";
    }

    public boolean timeIsSet(){
        return minute != -1 && hour != -1;
    }


    protected Reminder(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
        type = in.readString();
        name = in.readString();
        place = (LatLng) in.readValue(LatLng.class.getClassLoader());
        address = in.readString();
        enabled = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeValue(place);
        dest.writeString(address);
        dest.writeByte((byte) (enabled ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };
}
