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

package org.tanrabad.survey.service.json;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;

@JsonObject
public class JsonEntomology {
    @JsonField(name = "place_id", typeConverter = UuidTypeConverter.class)
    public UUID placeId;

    @JsonField(name = "place_type")
    public int placeType;

    @JsonField(name = "place_name")
    public String placeName;

    @JsonField
    public GeoJsonPoint location;

    @JsonField(name = "tambon_name")
    public String tambonName;

    @JsonField(name = "amphur_name")
    public String amphurName;

    @JsonField(name = "province_name")
    public String provinceName;

    @JsonField(name = "num_surveyed_houses")
    public int numSurveyedHouses;

    @JsonField(name = "num_found_houses")
    public int numFoundHouses;

    @JsonField(name = "num_no_container_houses")
    public int numNoContainerHouses;

    @JsonField(name = "num_surveyed_containers")
    public int numSurveyedContainer;

    @JsonField(name = "num_found_containers")
    public int numFoundContainers;

    @JsonField(name = "duplicate_survey")
    public int numDuplicateSurvey;

    @JsonField(name = "report_update")
    public String reportUpdate;

    @JsonField(name = "survey_start", typeConverter = UnixThaiDateTimeConverter.class)
    public DateTime surveyStartDate;

    @JsonField(name = "survey_end", typeConverter = UnixThaiDateTimeConverter.class)
    public DateTime surveyEndDate;

    @JsonField(name = "hi_value")
    public double hiValue;

    @JsonField(name = "bi_value")
    public double biValue;

    @JsonField(name = "ci_value")
    public double ciValue;

    @JsonField(name = "key_container_in")
    public List<JsonKeyContainer> keyContainerIn;

    @JsonField(name = "key_container_out")
    public List<JsonKeyContainer> keyContainerOut;

    @JsonField(name = "sum_person_count")
    public int sumPersonCount;

    @Override
    public String toString() {
        return "JsonEntomology{"
                + "placeId=" + placeId
                + ", placeType=" + placeType
                + ", placeName='" + placeName + '\''
                + ", location=" + location
                + ", tambonName='" + tambonName + '\''
                + ", amphurName='" + amphurName + '\''
                + ", provinceName='" + provinceName + '\''
                + ", numSurveyedHouses=" + numSurveyedHouses
                + ", numFoundHouses=" + numFoundHouses
                + ", numNoContainerHouses=" + numNoContainerHouses
                + ", numSurveyedContainer=" + numSurveyedContainer
                + ", numFoundContainers=" + numFoundContainers
                + ", numDuplicateSurvey=" + numDuplicateSurvey
                + ", reportUpdate='" + reportUpdate + '\''
                + ", surveyStartDate=" + surveyStartDate
                + ", surveyEndDate=" + surveyEndDate
                + ", hiValue=" + hiValue
                + ", biValue=" + biValue
                + ", ciValue=" + ciValue
                + ", keyContainerIn=" + keyContainerIn
                + ", keyContainerOut=" + keyContainerOut
                + ", sumPersonCount=" + sumPersonCount
                + '}';
    }
}
