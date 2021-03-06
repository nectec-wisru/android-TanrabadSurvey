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

package org.tanrabad.survey.entity.lookup;

import org.tanrabad.survey.entity.ReferenceEntity;

public class PlaceType implements ReferenceEntity {

    public static final int VILLAGE_COMMUNITY = 1;
    public static final int WORSHIP = 2;
    public static final int SCHOOL = 3;
    public static final int HOSPITAL = 4;
    public static final int FACTORY = 5;
    private final int id;
    private final String name;

    public PlaceType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        PlaceType otherPlaceType = (PlaceType) other;

        return id == otherPlaceType.id && name.equals(otherPlaceType.name);

    }
}
