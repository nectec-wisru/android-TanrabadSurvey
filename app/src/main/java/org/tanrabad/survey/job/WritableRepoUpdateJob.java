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

package org.tanrabad.survey.job;

import org.tanrabad.survey.service.RestService;
import org.tanrabad.survey.domain.WritableRepository;

import java.io.IOException;
import java.util.List;

public class WritableRepoUpdateJob<T> implements Job {

    public static final int ID = 192384;
    private WritableRepository<T> repository;
    private RestService<T> restService;

    public WritableRepoUpdateJob(RestService<T> restService, WritableRepository<T> repository) {
        this.restService = restService;
        this.repository = repository;
    }

    @Override
    public int id() {
        return ID;
    }

    @Override
    public void execute() throws IOException {
        do {
            List<T> update = restService.getUpdate();
            if (!update.isEmpty()) {
                repository.updateOrInsert(update);
            }

            List<T> delete = restService.getDelete();
            for (T deleteData : delete) {
                repository.delete(deleteData);
            }

        } while (restService.hasNextRequest());
    }
}
