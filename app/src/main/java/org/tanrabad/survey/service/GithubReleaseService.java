/*
 * Copyright (c) 2019 NECTEC
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.tanrabad.survey.service.json.GithubReleaseJson;

public class GithubReleaseService {

    private static final String LATEST_URL = "https://api.tanrabad.org/v2/info/releases/latest";

    protected final OkHttpClient client = new OkHttpClient();

    public GithubReleaseJson getLatest() {
        Request request = new Request.Builder()
            .url(LATEST_URL)
            .build();

        try (Response response =  client.newCall(request).execute()){
            return LoganSquare.parse(response.body().string(), GithubReleaseJson.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
