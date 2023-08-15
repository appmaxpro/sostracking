package com.tms.model.base;

import com.tms.TmsStore;
import com.tms.model.Model;
import com.tms.utils.Utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GeoZoneGroup extends Model {

    private String name;
    private String code;
    private boolean autoCreated;
    private long[] zones ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isAutoCreated() {
        return autoCreated;
    }

    public void setAutoCreated(boolean autoCreated) {
        this.autoCreated = autoCreated;
    }

    public long[] getZones() {
        return zones;
    }

    public void setZones(long[] zones) {
        this.zones = zones;
    }

    public Collection<GeoZone> getZones(TmsStore store) {
        return store.getObjects(GeoZone.class, zones);
    }

    public boolean contains(TmsStore store, double lat, double lng){
        return Utils.findOne(getZones(store), zone -> zone.contains(lat, lng)) != null;
    }

    public static GeoZoneGroup getGeoZoneGroup(TmsStore store, long[] ids) {
        Arrays.sort(ids);
        String code = String.join(",", List.of(ids).stream()
                .map(String::valueOf).collect(Collectors.toList()));


        var group = Utils.findOne(store.getAll(GeoZoneGroup.class),
                set -> code.equals(set.getCode()));

        if (group == null) {
            group = new GeoZoneGroup();
            group.setName("Auto Generated: "+ code);
            group.setCode(code);
            group.setZones(ids);
            group.setAutoCreated(true);
            group = store.save(group);
        }

        return group;

    }

}
