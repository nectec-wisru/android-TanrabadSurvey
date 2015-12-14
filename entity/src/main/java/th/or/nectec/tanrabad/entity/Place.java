/*
 * Copyright (c) 2015 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package th.or.nectec.tanrabad.entity;

import th.or.nectec.tanrabad.entity.utils.Address;

import java.util.UUID;

public class Place implements LocationEntity {
    public static final int TYPE_VILLAGE_COMMUNITY = 1;
    public static final int TYPE_WORSHIP = 2;
    public static final int SUBTYPE_TEMPLE = 21;
    public static final int SUBTYPE_CHURCH = 22;
    public static final int SUBTYPE_MOSQUE = 23;
    public static final int TYPE_SCHOOL = 3;
    public static final int TYPE_HOSPITAL = 4;
    public static final int TYPE_FACTORY = 5;

    private UUID id;
    private String name;
    private int type;
    private int subType;
    private Location location;
    private Address address;

    public Place(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Place withName(String name) {
        UUID uuid = UUID.randomUUID();
        return new Place(uuid, name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public Place setType(int type) {
        this.type = type;
        return null;
    }

    public int getSubType() {
        return subType;
    }

    public void setSubType(int subType) {
        this.subType = subType;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + type;
        result = 31 * result + subType;
        result = 31 * result + (location != null ? location.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        if (type != place.type) return false;
        if (subType != place.subType) return false;
        if (!id.equals(place.id)) return false;
        if (!name.equals(place.name)) return false;
        return !(location != null ? !location.equals(place.location) : place.location != null);

    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", subType=" + subType +
                ", location=" + location +
                ", address=" + address +
                '}';
    }
}
