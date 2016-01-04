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

package th.or.nectec.tanrabad.survey.presenter.job;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

public abstract class AbsJobRunner implements JobRunner {

    List<Job> jobList = new ArrayList();
    boolean running = false;
    int jobFinishCount = 0;
    int jobErrorCount = 0;

    @Override
    public AbsJobRunner addJob(Job job) {
        jobList.add(job);
        return this;
    }

    @Override
    public void start() {
        running = true;
        jobErrorCount = 0;
        jobFinishCount = 0;
        new AsyncJob().execute(jobList.toArray(new Job[jobList.size()]));
    }

    @Override
    public int totalJobs() {
        return jobList.size();
    }

    @Override
    public int finishedJobs() {
        return jobFinishCount;
    }

    @Override
    public int errorJobs() {
        return jobErrorCount;
    }

    protected void onJobError(Job errorJob, JobException exception) {
        jobErrorCount++;
    }

    private void onJobDone(Job job) {
        jobFinishCount++;
    }

    abstract protected void onJobStart(Job startingJob);

    abstract protected void onRunFinish();


    private enum UpdateMode {
        START, DONE, ERROR
    }

    private class AsyncJob extends AsyncTask<Job, Object, Void> {

        @Override
        protected Void doInBackground(Job... jobs) {
            for (Job job : jobs) {
                publishProgress(UpdateMode.START, job);
                try {
                    job.execute();
                } catch (JobException jEx) {
                    publishProgress(UpdateMode.ERROR, job, jEx);
                }
                publishProgress(UpdateMode.DONE, job);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            onRunFinish();
        }

        @Override
        protected void onProgressUpdate(Object... jobs) {
            super.onProgressUpdate(jobs);
            UpdateMode mode = (UpdateMode) jobs[0];
            Job job = (Job) jobs[1];
            switch (mode) {
                case START:
                    onJobStart(job);
                    break;
                case DONE:
                    onJobDone(job);
                    break;
                case ERROR:
                    onJobError(job, (JobException) jobs[1]);
                    break;
            }
        }

    }


}