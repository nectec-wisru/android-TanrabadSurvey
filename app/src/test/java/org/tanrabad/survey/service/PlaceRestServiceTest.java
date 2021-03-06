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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.tanrabad.survey.WireMockTestBase;
import org.tanrabad.survey.domain.place.PlaceSubTypeRepository;
import org.tanrabad.survey.domain.user.UserRepository;
import org.tanrabad.survey.entity.Place;
import org.tanrabad.survey.entity.field.Location;
import org.tanrabad.survey.entity.lookup.PlaceType;
import org.tanrabad.survey.service.json.JsonPlace;
import org.tanrabad.survey.utils.ResourceFile;
import org.tanrabad.survey.utils.http.Header;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.tanrabad.survey.service.PlaceRestService.PATH;
import static org.tanrabad.survey.utils.http.Header.CONTENT_TYPE;
import static org.tanrabad.survey.utils.http.Header.USER_AGENT;

public class PlaceRestServiceTest extends WireMockTestBase {

    public static final String MON_30_NOV_2015_17_00_00_GMT = "Mon, 30 Nov 2015 17:00:00 GMT";
    private UserRepository userRepository = Mockito.mock(UserRepository.class);
    private PlaceSubTypeRepository placeSubTypeRepository = Mockito.mock(PlaceSubTypeRepository.class);
    private ServiceLastUpdate lastUpdate = Mockito.mock(ServiceLastUpdate.class);
    private PlaceRestService restService;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Mockito.when(userRepository.findByUsername("dpc-user")).thenReturn(stubUser());
        Mockito.when(placeSubTypeRepository.getDefaultPlaceSubTypeId(PlaceType.HOSPITAL)).thenReturn(1);
        restService = new PlaceRestService(
                localHost(),
                lastUpdate,
                placeSubTypeRepository);
    }

    @Test
    public void testGetUrl() throws Exception {
        assertEquals(localHost() + PATH + restService.getQueryString(), restService.getUrl());
    }

    @Test(expected = RestServiceException.class)
    public void test404Response() throws Exception {
        stubFor(get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withBody("")));

        restService.getUpdate();
        Mockito.verify(lastUpdate, never()).save(anyString());
    }

    @Test
    public void testNotModifiedResponse() throws Exception {
        Mockito.when(lastUpdate.get()).thenReturn(MON_30_NOV_2015_17_00_00_GMT);
        stubFor(get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                        .withStatus(304)
                        .withBody("")));

        List<Place> places = restService.getUpdate();

        assertEquals(0, places.size());
        verify(getRequestedFor(urlPathEqualTo(PATH))
                .withHeader(Header.IF_MODIFIED_SINCE, equalTo(MON_30_NOV_2015_17_00_00_GMT)));
        Mockito.verify(lastUpdate, never()).save(anyString());
    }

    @Test
    public void testSuccessResponse() throws Exception {
        stubFor(get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(Header.LAST_MODIFIED, MON_30_NOV_2015_17_00_00_GMT)
                        .withBody(ResourceFile.read("placeList.json"))));

        List<Place> placeList = restService.getUpdate();
        Place place = placeList.get(0);

        assertEquals(1, placeList.size());
        assertEquals(uuid("b7a9d934-04fc-a22e-0539-6c17504f732e"), place.getId());
        assertEquals("รพ.สต.ตำบลนาทราย", place.getName());
        assertEquals(null, place.getLocation());
        Mockito.verify(lastUpdate).save(MON_30_NOV_2015_17_00_00_GMT);
        verify(getRequestedFor(urlPathEqualTo(PATH))
                .withHeader(Header.ACCEPT, equalTo("application/json"))
                .withHeader(Header.ACCEPT_CHARSET, equalTo("utf-8"))
                .withHeader(Header.USER_AGENT, equalTo(AbsRestService.TRB_USER_AGENT))
                .withoutHeader(Header.IF_MODIFIED_SINCE));
    }

    private UUID uuid(String uuid) {
        return UUID.fromString(uuid);
    }

    @Test
    public void testSuccessResponseMultipleItem() throws Exception {
        stubFor(get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(Header.LAST_MODIFIED, MON_30_NOV_2015_17_00_00_GMT)
                        .withBody(ResourceFile.read("placeList10Item.json"))));

        List<Place> buildingList = restService.getUpdate();

        assertEquals(10, buildingList.size());
        Place place1 = buildingList.get(0);
        assertEquals(uuid("b7a9d934-04fc-a22e-0539-6c17504f732e"), place1.getId());
        assertEquals("รพ.สต.ตำบลนาทราย", place1.getName());
        assertEquals(null, place1.getLocation());
        Place place5 = buildingList.get(4);
        assertEquals(uuid("68f55d61-a9e9-3166-78bd-3470dbf5e434"), place5.getId());
        assertEquals("รพ.สต.บ้านห้วยไคร้", place5.getName());
        assertEquals(null, place5.getLocation());
        Place place10 = buildingList.get(9);
        assertEquals(uuid("f56e0c74-5b4f-b72f-a993-0227cad188fb"), place10.getId());
        assertEquals("รพ.สต.บ้านห้วยใส", place10.getName());
        assertEquals(null, place10.getLocation());
    }

    @Test
    public void testSuccessResponseWithNextPage() throws Exception {
        stubFor(get(urlPathEqualTo(PATH))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(Header.LAST_MODIFIED, MON_30_NOV_2015_17_00_00_GMT)
                        .withHeader(Header.LINK,
                                "<" + localHost() + PATH + "?page=2&per_page=10>; rel=\"next\","
                                        + "<" + localHost() + PATH + "?page=2&per_page=10>; rel=\"last\"")
                        .withBody(ResourceFile.read("placeList10Item.json"))));
        stubFor(get(urlEqualTo(PATH + "?page=2&per_page=10"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(Header.LAST_MODIFIED, MON_30_NOV_2015_17_00_00_GMT)
                        .withBody(ResourceFile.read("placeNextList5Item.json"))));

        ArrayList<Place> placeArrayList = new ArrayList<>();
        do {
            placeArrayList.addAll(restService.getUpdate());
        } while (restService.hasNextRequest());

        assertEquals(15, placeArrayList.size());
        Place place1 = placeArrayList.get(0);
        assertEquals(uuid("b7a9d934-04fc-a22e-0539-6c17504f732e"), place1.getId());
        assertEquals("รพ.สต.ตำบลนาทราย", place1.getName());
        assertEquals(null, place1.getLocation());
        Place place5 = placeArrayList.get(4);
        assertEquals(uuid("68f55d61-a9e9-3166-78bd-3470dbf5e434"), place5.getId());
        assertEquals("รพ.สต.บ้านห้วยไคร้", place5.getName());
        assertEquals(null, place5.getLocation());
        Place place15 = placeArrayList.get(14);
        assertEquals(uuid("648e41e1-2ccd-00f2-f065-ba111c384c5b"), place15.getId());
        assertEquals("รพ.สต.บ้านหลวง ต.นาพู่", place15.getName());
        assertEquals(new Location(17.6048028519667, 102.755400339956), place15.getLocation());
        Mockito.verify(lastUpdate).save(MON_30_NOV_2015_17_00_00_GMT);
    }

    @Test
    public void testPostData() throws Exception {
        stubFor(post((urlEqualTo(PATH)))
                .willReturn(aResponse()
                        .withStatus(201)));

        Place testPlace = stubPlace();
        boolean uploadStatus = restService.post(testPlace);

        assertEquals(true, uploadStatus);
        verify(postRequestedFor(urlPathMatching(PATH))
                .withRequestBody(equalToJson(toJson(testPlace)))
                .withHeader(CONTENT_TYPE, equalTo("application/json; charset=utf-8"))
                .withHeader(USER_AGENT, equalTo(AbsRestService.TRB_USER_AGENT)));
    }

    private Place stubPlace() {
        Place place = Place.withName("555");
        place.setUpdateBy("dpc-user");
        place.setLocation(new Location(1, 1));
        place.setUpdateTimestamp(DateTime.now().toString());
        place.setSubdistrictCode("100520");
        return place;
    }

    private String toJson(Place place) throws java.io.IOException {
        return LoganSquare.serialize(JsonPlace.parse(place));
    }

    @Test(expected = RestServiceException.class)
    public void testPostData404() throws Exception {
        stubFor(post((urlEqualTo(PATH)))
                .willReturn(aResponse()
                        .withStatus(404)));

        restService.post(stubPlace());
    }

    @Test(expected = RestServiceException.ErrorResponseException.class)
    public void testResponseError() throws Exception {
        stubFor(post((urlEqualTo(PATH)))
                .willReturn(aResponse()
                        .withBody(ResourceFile.read("errorResponses.json")).withStatus(400))
        );

        restService.post(stubPlace());
    }

    @Test
    public void testPut() throws Exception {
        Place place = stubPlace();
        stubFor(put(urlEqualTo(PATH + "/" + place.getId().toString()))
                .willReturn(aResponse()
                        .withStatus(200)));

        boolean success = restService.put(place);

        assertEquals(true, success);
        verify(putRequestedFor(urlEqualTo(PATH + "/" + place.getId().toString()))
                .withHeader(CONTENT_TYPE, equalTo("application/json; charset=utf-8"))
                .withHeader(USER_AGENT, equalTo(AbsRestService.TRB_USER_AGENT))
                .withRequestBody(equalToJson(toJson(place))));
    }

    @Test(expected = RestServiceException.class)
    public void testPutConflict() throws Exception {
        Place place = stubPlace();
        stubFor(put(urlEqualTo(PATH + "/" + place.getId().toString()))
                .willReturn(aResponse()
                        .withStatus(409)));

        restService.put(place);
    }

    @Test
    public void testDelete() throws Exception {
        Place place = stubPlace();
        String deleteUrl = PATH.concat("/").concat(place.getId().toString())
                .concat(restService.getDeleteQueryString());
        stubFor(delete(urlPathEqualTo(deleteUrl))
                .willReturn(aResponse()
                        .withStatus(200)));

        PlaceRestService placeRestService = new PlaceRestService(localHost(), lastUpdate, placeSubTypeRepository);
        boolean result = placeRestService.delete(place);

        assertTrue(result);
        verify(deleteRequestedFor(urlEqualTo(deleteUrl)));
    }

    @Test(expected = RestServiceException.class)
    public void testDeleteNotSuccess() throws Exception {
        Place place = stubPlace();
        String deleteUrl = PATH.concat("/").concat(place.getId().toString())
                .concat(restService.getDeleteQueryString());
        stubFor(delete(urlPathEqualTo(deleteUrl))
                .willReturn(aResponse()
                        .withStatus(404)));

        PlaceRestService placeRestService = new PlaceRestService(localHost(), lastUpdate, placeSubTypeRepository);
        placeRestService.delete(place);

    }
}
