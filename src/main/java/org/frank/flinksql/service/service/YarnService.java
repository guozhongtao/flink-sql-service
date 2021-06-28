package org.frank.flinksql.service.service;

import com.alibaba.fastjson.JSON;
import org.frank.flinksql.service.config.GatewayConfig;
import org.frank.flinksql.service.enums.YarnStateEnum;
import org.frank.flinksql.service.exception.ErrorCode;
import org.frank.flinksql.service.exception.GWException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;


@Service
@Slf4j
public class YarnService {

    private String APPLICATION_KILL = "{\"state\":\"KILLED\"}";

    private String killUrl = "/ws/v1/cluster/apps/";

    private String yarnHAUrl = "/ws/v1/cluster/info";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private GatewayConfig gatewayConfig;

    public void stopJobByAppId(String appId) {
        log.info("stop job by appId = {}", appId);
        if (StringUtils.isEmpty(appId)) {
            throw new GWException(ErrorCode.PARAMETER_ERROR);
        }
        String rmAddress = gatewayConfig.getRmMasterAddress();
        if (!detectRMHa(rmAddress)){
            rmAddress = gatewayConfig.getRmSlaveAddress();
        }
        String url = String.format("http://%s%s%s/state", rmAddress, killUrl, appId);
        log.info("stop URL is = {}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity(APPLICATION_KILL, headers);
        restTemplate.put(url, httpEntity);
    }

    public YarnStateEnum getJobStateByJobId(String appId) {
        log.info("stop job by appId = {}", appId);
        if (StringUtils.isEmpty(appId)) {
            throw new GWException(ErrorCode.PARAMETER_ERROR);
        }

        String rmAddress = gatewayConfig.getRmMasterAddress();
        if (!detectRMHa(rmAddress)) {
            rmAddress = gatewayConfig.getRmSlaveAddress();
        }
        String url = String.format("http://%s%s/state", rmAddress, appId);
        String res = restTemplate.getForObject(url, String.class);
        if (StringUtils.isEmpty(res)) {
            log.error("request failed, res is null url={}", url);
            throw new GWException(ErrorCode.HTTP_REQUEST_IS_NULL);
        }
        return YarnStateEnum.getYarnStateEnum(String.valueOf(JSON.parseObject(res).get("state")));
    }


    public void cancelJobForYarnByAppId(String appId, String jobId) {
        log.info("cancel Job ForYarn By AppId = {}, and by jobId = {}", appId, jobId);
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(jobId)) {
            throw new GWException(ErrorCode.PARAMETER_ERROR);
        }
        String rmAddress = gatewayConfig.getRmMasterAddress();
        if (!detectRMHa(rmAddress)) {
            rmAddress = gatewayConfig.getRmSlaveAddress();
        }
        String url = String.format("http://%s/proxy/%s/jobs/%s/yarn-cancel", rmAddress, appId,jobId);
        log.info("[cancelJobByAppId]请求参数 appId={} jobId={} url={}", appId, jobId, url);
        String res = restTemplate.getForObject(url, String.class);
        log.info("[cancelJobByAppId]请求参数结果: res={}", res);
    }


    private boolean detectRMHa(String rmAddr) {
        try{
            String url = String.format("http://%s%s", rmAddr, yarnHAUrl);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity(headers);
            YarnClusterHaInfo yarnClusterHaInfo = restTemplate.getForObject(url, YarnClusterHaInfo.class, httpEntity);
            if (yarnClusterHaInfo != null &&
                    yarnClusterHaInfo.getClusterInfo() != null &&
                    "ACTIVE".equals(yarnClusterHaInfo.getClusterInfo().get("haState"))){
                return true;
            }
        }catch(Exception e){
            log.error("detect name node is error {}", e);
        }
        return false;
    }

    private static class YarnClusterHaInfo {
        private Map clusterInfo;

        public Map getClusterInfo() {
            return clusterInfo;
        }

        public void setClusterInfo(Map clusterInfo) {
            this.clusterInfo = clusterInfo;
        }
    }

}
