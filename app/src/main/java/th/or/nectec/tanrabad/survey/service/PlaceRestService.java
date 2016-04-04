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

package th.or.nectec.tanrabad.survey.service;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;
import th.or.nectec.tanrabad.domain.place.PlaceSubTypeRepository;
import th.or.nectec.tanrabad.entity.Place;
import th.or.nectec.tanrabad.survey.TanrabadApp;
import th.or.nectec.tanrabad.survey.repository.BrokerPlaceSubTypeRepository;
import th.or.nectec.tanrabad.survey.service.json.JsonPlace;

import static th.or.nectec.tanrabad.survey.service.http.Header.*;

public class PlaceRestService extends AbsUploadRestService<Place> implements DeleteRestService<Place> {

    public static final String PATH = "/place";
    private PlaceSubTypeRepository placeSubTypeRepository;

    public PlaceRestService() {
        this(BASE_API, new ServiceLastUpdatePreference(TanrabadApp.getInstance(), PATH),
                BrokerPlaceSubTypeRepository.getInstance());
    }

    public PlaceRestService(String apiBaseUrl,
                            ServiceLastUpdate serviceLastUpdate,
                            PlaceSubTypeRepository placeSubTypeRepository) {
        super(apiBaseUrl, serviceLastUpdate);
        this.placeSubTypeRepository = placeSubTypeRepository;
    }

    @Override
    protected List<Place> jsonToEntityList(String responseBody) throws IOException {
        ArrayList<Place> places = new ArrayList<>();
        List<JsonPlace> jsonPlaces = LoganSquare.parseList(responseBody, JsonPlace.class);
        for (JsonPlace eachJsonPlace : jsonPlaces) {
            Place place = eachJsonPlace.getEntity(placeSubTypeRepository);
            if (eachJsonPlace.active) {
                places.add(place);
            } else {
                addDeleteData(place);
            }
        }
        return places;
    }

    @Override
    protected String getPath() {
        return PATH;
    }

    @Override
    public String getDefaultParams() {
        return new QueryStringBuilder("geostd=4326", getApiFilterParam()).build();
    }

    @Override
    protected String entityToJsonString(Place data) {
        try {
            return LoganSquare.serialize(JsonPlace.parse(data));
        } catch (IOException io) {
            throw new RestServiceException(io);
        }
    }

    @Override
    protected String getId(Place data) {
        return data.getId().toString();
    }

    @Override
    public boolean delete(Place data) throws IOException {
        Request request = deleteRequest(data);
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            TanrabadApp.log(response.body().string());
            throw new RestServiceException(response);
        }
        return true;
    }

    protected final Request deleteRequest(Place data) {
        Request.Builder requestBuilder = new Request.Builder()
                .delete()
                .url(getUrl().replace(getDefaultParams(), "").concat("/").concat(data.getId().toString()))
                .addHeader(USER_AGENT, TRB_USER_AGENT)
                .addHeader(ACCEPT, "application/json")
                .addHeader(ACCEPT_CHARSET, "utf-8");
        return requestBuilder.build();
    }
}
