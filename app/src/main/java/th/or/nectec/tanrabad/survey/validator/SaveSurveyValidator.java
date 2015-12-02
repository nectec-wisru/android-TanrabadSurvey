/*
 * Copyright (c) 2015 NECTEC
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

package th.or.nectec.tanrabad.survey.validator;

import android.content.Context;

import th.or.nectec.tanrabad.domain.survey.SurveyValidator;
import th.or.nectec.tanrabad.entity.Survey;
import th.or.nectec.tanrabad.survey.R;

public class SaveSurveyValidator implements SurveyValidator {
    Context context;

    public SaveSurveyValidator(Context context) {
        this.context = context;
    }

    @Override
    public boolean validate(Survey survey) {
        if (survey.getUser() == null) {
            throw new ValidatorException(R.string.user_not_found);
        }

        if (survey.getSurveyBuilding() == null) {
            throw new ValidatorException(R.string.building_not_found);
        }

        if (survey.getResidentCount() < 1) {
            throw new ValidatorException(R.string.please_enter_resident);
        }

        return true;

    }
}
