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
import okhttp3.Request;
import okhttp3.Response;
import org.tanrabad.survey.TanrabadApp;
import org.tanrabad.survey.entity.Survey;
import org.tanrabad.survey.service.json.JsonSurvey;

import java.io.IOException;
import java.util.List;

import static org.tanrabad.survey.utils.http.Header.ACCEPT;
import static org.tanrabad.survey.utils.http.Header.ACCEPT_CHARSET;
import static org.tanrabad.survey.utils.http.Header.USER_AGENT;

public class SurveyRestService extends AbsUploadRestService<Survey> implements DeleteRestService<Survey> {

    public static final String PATH = "/survey";

    public SurveyRestService() {
        this(ImpRestServiceConfig.getInstance().getApiBaseUrl(),
                new ApiSyncInfoPreference(TanrabadApp.getInstance(), PATH));
    }

    public SurveyRestService(String baseApi, ServiceLastUpdate serviceLastUpdate) {
        super(baseApi, serviceLastUpdate);
    }

    @Override
    protected String entityToJsonString(Survey data) throws IOException {
        return LoganSquare.serialize(JsonSurvey.parse(data));
    }

    @Override
    protected String getId(Survey data) {
        return data.getId().toString();
    }

    @Override
    protected String getPath() {
        return PATH;
    }

    @Override
    protected List<Survey> jsonToEntityList(String responseBody) {
        throw new IllegalArgumentException("Survey rest service not support convert to entity.");
    }

    @Override
    public boolean delete(Survey data) throws IOException {
        Request request = deleteRequest(data);
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            TanrabadApp.log(response.body().string());
            throw new RestServiceException(response);
        }
        return true;
    }

    @Override
    public String getDeleteQueryString() {
        return "?userid=".concat(getUser().getUsername());
    }

    protected final Request deleteRequest(Survey data) {
        Request.Builder requestBuilder = new Request.Builder()
                .delete()
                .url(getUrl().concat("/").concat(data.getId().toString()).concat(getDeleteQueryString()))
                .addHeader(USER_AGENT, TRB_USER_AGENT)
                .addHeader(ACCEPT, "application/json")
                .addHeader(ACCEPT_CHARSET, "utf-8");
        return requestBuilder.build();
    }
}
