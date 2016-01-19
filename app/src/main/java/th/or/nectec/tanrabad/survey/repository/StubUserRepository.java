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

package th.or.nectec.tanrabad.survey.repository;


import th.or.nectec.tanrabad.domain.UserRepository;
import th.or.nectec.tanrabad.entity.User;

public class StubUserRepository implements UserRepository {

    private final User sara;

    public StubUserRepository() {
        sara = new User("sara");
        sara.setFirstname("ซาร่า");
        sara.setLastname("คิดส์");
        sara.setEmail("sara.k@gmail.com");
        sara.setOrganizationId(1);
    }

    @Override
    public User findByUsername(String userName) {
        if (sara.getUsername().equals(userName)) {
            return sara ;
        } else {
            return null;
        }
    }

}
