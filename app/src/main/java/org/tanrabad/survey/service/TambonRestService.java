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

package org.tanrabad.survey.service;

import com.bluelinelabs.logansquare.LoganSquare;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.tanrabad.survey.TanrabadApp;
import org.tanrabad.survey.entity.lookup.Subdistrict;
import org.tanrabad.survey.service.json.JsonTambon;
import org.tanrabad.survey.utils.http.QueryStringBuilder;

public class TambonRestService extends AbsRestService<Subdistrict> {

    private static final String PATH = "/tambon";

    public TambonRestService() {
        this(ImpRestServiceConfig.getInstance().getApiBaseUrl(),
                new ApiSyncInfoPreference(TanrabadApp.getInstance(), PATH));
    }

    public TambonRestService(String apiBaseUrl, ServiceLastUpdate serviceLastUpdate) {
        super(apiBaseUrl, serviceLastUpdate);
    }

    protected String getPath() {
        return PATH;
    }

    @Override
    public String getQueryString() {
        return new QueryStringBuilder("geostd=4326", getApiFilterParam(), DEFAULT_PER_PAGE).build();
    }

    protected List<Subdistrict> jsonToEntityList(String responseBody) throws IOException {
        ArrayList<Subdistrict> subdistrictList = new ArrayList<>();
        List<JsonTambon> jsonTambonList = LoganSquare.parseList(responseBody, JsonTambon.class);
        for (JsonTambon eachJsonTambon : jsonTambonList)
            subdistrictList.add(eachJsonTambon.getEntity());
        return subdistrictList;
    }
}
