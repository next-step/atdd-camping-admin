package com.camping.admin.support.context;

public class ReservationWorld {
    public Long reservationId;
    public String status;
    public final World common;

    public ReservationWorld(World world) {
        this.common = world;
    }
}