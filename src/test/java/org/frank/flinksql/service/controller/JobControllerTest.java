package org.frank.flinksql.service.controller;


import org.frank.flinksql.service.BaseTest;


import org.frank.flinksql.service.model.Result;
import org.frank.flinksql.service.model.param.SubmitSqlJobArgs;
import org.frank.flinksql.service.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JobControllerTest extends BaseTest {

	@Test
	public void testCancelJob() throws IOException {
		String url = String.format("%s/api/v1/job/cancel?jobId=%s&appId=%s", baseUrl, "bb84bb99dc520308f06e7419fe117aac", "application_1623142224865_0001");
		Utils.requestPost(template, url, Result.class);
	}


	@Test
	public void testStopJob() throws IOException {
		String url = String.format("%s/api/v1/job/stop?appId=%s", baseUrl, "application_1623142224865_0001");
		Utils.requestPost(template, url, Result.class);
	}

	@Test
	public void testSubmitSql() throws IOException {
		String url = String.format("%s/api/v1/job/sql/submit", baseUrl);
		SubmitSqlJobArgs submitSqlJobArgs = new SubmitSqlJobArgs();
		submitSqlJobArgs.setSql("insert into xxx select * from sss");
		submitSqlJobArgs.setJm("1G");
		submitSqlJobArgs.setTm("1G");
		submitSqlJobArgs.setParallelism(1);
		submitSqlJobArgs.setYarnQueue("root");
		submitSqlJobArgs.setSlot(1);
		submitSqlJobArgs.setAppName("test");
		Utils.requestPost(template, url, submitSqlJobArgs, Result.class);
	}
}
