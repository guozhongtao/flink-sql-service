package org.frank.flinksql.service;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import java.net.URL;

public class BaseTest {


    protected URL baseUrl;

    @LocalServerPort
    protected int port;

    @Autowired
    protected TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.baseUrl = new URL("http://localhost:" + port);
    }



}
