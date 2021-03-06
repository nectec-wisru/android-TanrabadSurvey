/*
 * Copyright (c) 2016 NECTEC
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

package org.tanrabad.survey.entity;

public class Organization {

    public static final int AREA_LEVEL_ID_PROVINCE = 1;
    public static final int AREA_LEVEL_ID_AMPHUR = 2;
    public static final int AREA_LEVEL_ID_TUMBON = 3;

    private final int organizationId;
    private final String name;
    private int areaLevelId;
    private String address;
    private String subdistrictCode;
    private String healthRegionCode;

    public Organization(int organizationId, String name) {
        this.organizationId = organizationId;
        this.name = name;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public String getName() {
        return name;
    }

    public int getAreaLevelId() {
        return areaLevelId;
    }

    public void setAreaLevelId(int areaLevelId) {
        this.areaLevelId = areaLevelId;
    }

    public String getHealthRegionCode() {
        return healthRegionCode;
    }

    public void setHealthRegionCode(String healthRegionCode) {
        this.healthRegionCode = healthRegionCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubdistrictCode() {
        return subdistrictCode;
    }

    public void setSubdistrictCode(String subdistrictCode) {
        this.subdistrictCode = subdistrictCode;
    }

    @Override
    public int hashCode() {
        int result = organizationId;
        result = 31 * result + name.hashCode();
        result = 31 * result + areaLevelId;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Organization that = (Organization) o;

        return organizationId == that.organizationId
                && areaLevelId == that.areaLevelId
                && name.equals(that.name);
    }
}
